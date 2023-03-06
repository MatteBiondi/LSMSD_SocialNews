package it.unipi.lsmsd.socialnews.dao.redundancy;

import com.mongodb.client.ClientSession;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsRedundancyTaskException;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoPostDAO;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoReporterDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextListener;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;


class AppendingObjectOutputStream extends ObjectOutputStream {

    public AppendingObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        // do not write a header, but reset
        reset();
    }

}

public class RedundancyUpdater {
    private static final Logger logger = LoggerFactory.getLogger(ServletContextListener.class);
    private static volatile RedundancyUpdater instance;
    private final ScheduledExecutorService executor;

    private static final String COMMENTS = "Comments";
    private static final String REPORTS = "Reports";

    private static final String COMMENT_FILE_PATH = "comment_redundancy_log.dat";
    private static final String REPORT_FILE_PATH = "report_redundancy_log.dat";

    private final Map<String, Integer> postCommentCounts; // postId -> commentCounter
    private final Map<String, Integer> postReportCounts; // reporterId -> reportCounter

    private final MongoPostDAO mongoPostDAO;
    private final MongoReporterDAO mongoReporterDAO;

    private final Object commentFileLock;
    private final Object reportFileLock;


    private RedundancyUpdater() {
        postCommentCounts = new HashMap<>();
        postReportCounts = new HashMap<>();

        commentFileLock = new Object();
        reportFileLock = new Object();

        mongoPostDAO = new MongoPostDAO();
        mongoReporterDAO = new MongoReporterDAO();

        applyRedundanciesFromLog();

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::applyRedundanciesFromLog, 1, 1, TimeUnit.HOURS);
        logger.info("Redundancy updater started");
    }

    public static RedundancyUpdater getInstance() {
        if(instance == null){
            synchronized (RedundancyUpdater.class){
                if(instance == null){
                    instance = new RedundancyUpdater();
                }
            }
        }
        logger.info("Redundancy updater instance taken");
        return instance;
    }

    private void applyRedundanciesFromLog(){
        logger.info("inizio");
        List<RedundancyTask> pendentTasks = new ArrayList<>();

        // Take lock on comments file
        synchronized (commentFileLock){
            // Read operations in log files
            try {
                pendentTasks.addAll(extractFromLog(COMMENT_FILE_PATH));
            }catch (SocialNewsRedundancyTaskException e){
                logger.error("Error in reading comments log file: " + e.getMessage());
                return;
            }

            // Execute all the tasks read from the file
            for (RedundancyTask task : pendentTasks) {
                executeTask(task);
            }

            // Apply redundancy changes to DB
            applyCommentsChangesToDB();
        }
        logger.info("Data redundancies about comments successfully applied");

        pendentTasks.clear();

        synchronized (reportFileLock){
            // Read operations in log files
            try {
                pendentTasks.addAll(extractFromLog(REPORT_FILE_PATH));
            }catch (SocialNewsRedundancyTaskException e){
                logger.error("Error in reading reports log file: " + e.getMessage());
                return;
            }

            // Execute all the tasks read from the file
            for (RedundancyTask task : pendentTasks) {
                executeTask(task);
            }

            // Apply redundancy changes to DB
            applyReportsChangesToDB();
        }

        logger.info("Data redundancies about reports successfully applied");
    }

    private void executeTask(RedundancyTask task) throws IllegalArgumentException{
        switch (task.getOperationType()) {
            case ADD_COMMENT    ->  addComment(task.getIdentifier(), task.getCounter());
            case REMOVE_COMMENT ->  removeComment(task.getIdentifier(), task.getCounter());
            case ADD_REPORT     ->  addReport(task.getIdentifier(), task.getCounter());
            case REMOVE_REPORT  ->  removeReport(task.getIdentifier(), task.getCounter());
            default             ->  throw new IllegalArgumentException("Fail to identify operation type in task execution");
        }
        logger.info("Redundancy updater: task executed");

    }

    public void addTask(RedundancyTask task){
        try {
            switch (task.getOperationType()) {
                case ADD_COMMENT, REMOVE_COMMENT -> {
                    synchronized(commentFileLock){
                        writeLog(COMMENT_FILE_PATH, task);
                    }
                }
                case ADD_REPORT, REMOVE_REPORT -> {
                    synchronized(reportFileLock){
                        writeLog(REPORT_FILE_PATH, task);
                    }
                }
                default                 ->  throw new IllegalArgumentException("Fail to identify operation type in task execution");
            }
        } catch (SocialNewsRedundancyTaskException appException){
            logger.error("Error in writing in log files: "+ appException.getMessage());
        }catch (Exception e){
            logger.error("Unexpected exception: "+e.getMessage());
        }
        logger.info("Redundancy updater: task added");
    }

    private void applyCommentsChangesToDB() {
        // Open transaction
        ClientSession session = mongoPostDAO.openSession();
        try{
            session.startTransaction();

            // DB redundancy update
            for (String key : postCommentCounts.keySet()) {
                try {
                    Integer value = postCommentCounts.get(key);
                    if (mongoPostDAO.updateNumOfComment(session, key, value) == 0) {
                        logger.warn("Post " + key + " -> "+value+" not modified. Maybe it has been deleted.");
                    }
                    else{
                        logger.info("Post comment redundancies applied");
                    }
                    postCommentCounts.remove(key);
                } catch (Exception e) {
                    logger.error("Error in applying changes to DB: " + e.getMessage());
                }
            }

            // Renew content of log file
            renewCommentsLogFileContent();

            // Flush local maps
            postCommentCounts.clear();

            session.commitTransaction();
        } catch (Exception ex){
            logger.error("Unable to apply comments changes to database for one or more posts: "+ex.getMessage());
            printMapContent(COMMENTS);
            session.abortTransaction();
        } finally {
            session.close();
        }
        logger.info("Redundancy updater: redundancies changes applied");
    }

    private void applyReportsChangesToDB() {
        // Open transaction
        ClientSession session = mongoReporterDAO.openSession();
        try{
            session.startTransaction();

            // DB redundancy update
            for (String key : postReportCounts.keySet()) {
                try {
                    Integer value = postReportCounts.get(key);
                    if(mongoReporterDAO.updateNumOfReport(session, key, value) == 0){
                        logger.warn("Reporter "+key+" -> "+value+" not modified. Maybe it has been deleted");
                    }
                    else {
                        logger.info("Post report redundancies applied");
                    }
                    postReportCounts.remove(key);
                } catch (Exception e){
                    logger.error("Error in applying report changes to database for "+key+": "+ e.getMessage());
                }
            }

            // Renew content of log file
            renewReportsLogFileContent();

            // Flush local maps
            postReportCounts.clear();
            
            session.commitTransaction();
        } catch (Exception ex){
            logger.error("Unable to apply reports changes to database for one or more reporter: "+ex.getMessage());
            printMapContent(REPORTS);
            session.abortTransaction();
        } finally {
            session.close();
        }
        logger.info("Redundancy updater: redundancies changes applied");
    }

    private void renewCommentsLogFileContent() throws SocialNewsRedundancyTaskException{
        // Write in the log file the task for failed redundancies operation on database
        try {
            emptyFile(COMMENT_FILE_PATH);
            for (String key : postCommentCounts.keySet()) {
                Integer value = postCommentCounts.get(key);
                TaskType taskType = value < 0? TaskType.REMOVE_COMMENT : TaskType.ADD_COMMENT;

                writeLog(COMMENT_FILE_PATH,new RedundancyTask(taskType, key, value));
            }
        } catch (Exception e){
            logger.error("Error in overwrite log file content: "+ e.getMessage());
            throw new SocialNewsRedundancyTaskException("Error in writing comments log file");
        }
        logger.info("Redundancy updater: comments log file updated");
    }

    private void renewReportsLogFileContent() throws SocialNewsRedundancyTaskException{
        // Write in the log file the task for failed redundancies operation on database
        try {
            emptyFile(REPORT_FILE_PATH);
            for (String key : postReportCounts.keySet()) {
                Integer value = postReportCounts.get(key);
                TaskType taskType = value < 0? TaskType.REMOVE_REPORT : TaskType.ADD_REPORT;

                writeLog(REPORT_FILE_PATH,new RedundancyTask(taskType, key, value));
            }
        } catch (Exception e){
            logger.error("Error in overwrite log file content: "+ e.getMessage());
            throw new SocialNewsRedundancyTaskException("Error in writing reports log file");
        }
        logger.info("Redundancy updater: reports log file updated");
    }

    private void addComment (String postId, int counter) {
        postCommentCounts.put(postId, postCommentCounts.getOrDefault(postId, 0) + counter);
    }

    private void removeComment (String postId, int counter){
        postCommentCounts.put(postId, postCommentCounts.getOrDefault(postId, 0) - counter);
    }

    private void addReport (String reporterId, int counter){
        postReportCounts.put(reporterId, postReportCounts.getOrDefault(reporterId, 0) + counter);
    }

    private void removeReport (String reporterId, int counter){
        postReportCounts.put(reporterId, postReportCounts.getOrDefault(reporterId, 0) - counter);
    }

    private void printMapContent(String type) {
        if (type.equals(COMMENTS)) {
            logger.info("Post comments redundancy content: ");
            for (String key : postCommentCounts.keySet()) {
                Integer value = postCommentCounts.get(key);
                logger.info(key + " : " + value);
            }
        }
        else {
            logger.info("Post report redundancy content: ");
            for (String key : postReportCounts.keySet()) {
                Integer value = postReportCounts.get(key);
                logger.info(key + " : " + value);
            }
        }
    }

    private void writeLog (String filePath, RedundancyTask task) throws SocialNewsRedundancyTaskException {
        if(new File(filePath).length() == 0) {
            // Write file for the first time (header included)
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                 ObjectOutputStream stream = new ObjectOutputStream(fileOutputStream)) {
                // Lock on file has been already taken from caller method
                stream.writeObject(task);
            } catch (Exception e) {
                logger.warn("Write log file error");
                throw new SocialNewsRedundancyTaskException("Exception in log write operation");
            }
        }else{
            // Append content to file (header excluded)
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath,true);
                 AppendingObjectOutputStream stream = new AppendingObjectOutputStream(fileOutputStream)) {
                // Lock on file has been already taken from caller method
                stream.writeObject(task);
            } catch (Exception e) {
                logger.warn("Write log file error: appending content");
                throw new SocialNewsRedundancyTaskException("Exception in log write operation: appending content");
            }
        }
    }

    private List<RedundancyTask> extractFromLog (String filePath) throws SocialNewsRedundancyTaskException {
        List<RedundancyTask> tasks = new ArrayList<>();

        try (   FileInputStream fileInputStream = new FileInputStream(filePath);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            // Lock on file has been already taken from caller method
            while (true) {
                RedundancyTask task = (RedundancyTask) objectInputStream.readObject();
                if (task == null)
                    break;
                tasks.add(task);
            }
        }catch (EOFException e) {
            // Reached the end of the file
        }catch (FileNotFoundException e) {
            // The file does not exist yet, no changes need to be applied
            logger.warn("Not existing log file: "+filePath);
        } catch (IOException | ClassNotFoundException e){
            // Error while reading the file
            logger.warn("Extract from log file error:"+filePath);
            throw new SocialNewsRedundancyTaskException("Exception in log read operation");
        }finally {
            // Delete file content
            emptyFile(filePath);
        }

        return tasks;
    }

    public void stopRedundanciesUpdate (){
        logger.info("RedundancyUpdater exiting.");
        executor.shutdown();
        applyRedundanciesFromLog();
    }

    private void emptyFile(String filePath) throws SocialNewsRedundancyTaskException {
        try {
            new FileOutputStream(filePath).close();
        }catch (Exception e){
            throw new SocialNewsRedundancyTaskException("Impossible to empty file: "+filePath);
        }
    }

    private boolean deleteFile(String filePath){
        return new File(filePath).delete();
    }

    // Utility function for debug purpose only
    private void printFileInfo(){

        File commentFile = new File(COMMENT_FILE_PATH);
        try {
            if (commentFile.createNewFile()) {
                System.out.println("File created: " + commentFile.getName());
            } else {
                System.out.println("File " + commentFile.getName() + " already exists");
            }
        } catch (IOException e) {
            System.out.println("Error in file creation " + commentFile.getName());
            e.printStackTrace();
        }

        File reportFile = new File(REPORT_FILE_PATH);
        try {
            if (reportFile.createNewFile()) {
                System.out.println("File created: " + reportFile.getName());
            } else {
                System.out.println("File " + reportFile.getName() + " already exists");
            }
        } catch (IOException e) {
            System.out.println("Error in file creation " + reportFile.getName());
            e.printStackTrace();
        }

        System.out.println("Path: " + commentFile.getAbsolutePath() +
                " Space: "+ commentFile.length()+
                " Last modification:" + commentFile.lastModified());
        System.out.println("Path: " + reportFile.getAbsolutePath() +
                " Space: "+ reportFile.length()+
                " Last modification:" + reportFile.lastModified());
    }
}
