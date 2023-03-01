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
        executor.scheduleAtFixedRate(this::applyRedundanciesFromLog, 0, 1, TimeUnit.HOURS);
    }

    public static RedundancyUpdater getInstance() {
        if(instance == null){
            synchronized (RedundancyUpdater.class){
                if(instance == null){
                    instance = new RedundancyUpdater();
                }
            }
        }
        return instance;
    }

    private void applyRedundanciesFromLog(){
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
    }

    public boolean addTask(RedundancyTask task){
        try {
            switch (task.getOperationType()) {
                case ADD_COMMENT, REMOVE_COMMENT -> {
                    synchronized(commentFileLock){
                        writeLog(COMMENT_FILE_PATH, task, true);
                    }
                }
                case ADD_REPORT, REMOVE_REPORT -> {
                    synchronized(reportFileLock){
                        writeLog(REPORT_FILE_PATH, task, true);
                    }
                }
                case STOP_RUNNING       ->  stopRedundanciesUpdate();
                default                 ->  throw new IllegalArgumentException("Fail to identify operation type in task execution");
            }
            return true;
        } catch (SocialNewsRedundancyTaskException appException){
            logger.error("Error in writing in log files: "+ appException.getMessage());
        }catch (Exception e){
            logger.error("Unexpected exception: "+e.getMessage());
        }
        return false;
    }

    private void applyCommentsChangesToDB() {
        // Open transaction
        ClientSession session = mongoPostDAO.openSession();
        try{
            session.startTransaction();

            storeCommentsIntoDB();

            // Renew content of log file
            if(renewCommentsLogFileContent())
                // Flush local maps
                postCommentCounts.clear();
            else
                printMapContent(COMMENTS);

            session.commitTransaction();
        } catch (Exception ex){
            logger.error("Unable to apply comments changes to database for one or more posts: "+ex.getMessage());
            session.abortTransaction();
        } finally {
            session.close();
        }
    }

    private void applyReportsChangesToDB() {
        // Open transaction
        ClientSession session = mongoReporterDAO.openSession();
        try{
            session.startTransaction();

            storeReportsIntoDB();

            // Renew content of log file
            if(renewReportsLogFileContent())
                // Flush local maps
                postReportCounts.clear();
            else
                printMapContent(REPORTS);

            session.commitTransaction();
        } catch (Exception ex){
            logger.error("Unable to apply reports changes to database for one or more reporter: "+ex.getMessage());
            session.abortTransaction();
        } finally {
            session.close();
        }
    }

    private void storeCommentsIntoDB(){
        // DB redundancy update
        for (String key : postCommentCounts.keySet()) {
            try {
                Integer value = postCommentCounts.get(key);
                if (mongoPostDAO.updateNumOfComment(key, value) == 0) {
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
    }

    private void storeReportsIntoDB(){
        // DB redundancy update
        for (String key : postReportCounts.keySet()) {
            try {
                Integer value = postReportCounts.get(key);
                if(mongoReporterDAO.updateNumOfReport(key, value) == 0){
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
    }

    private boolean renewCommentsLogFileContent(){
        // Write in the log file the task for failed redundancies operation on database
        try {
            writeLog(COMMENT_FILE_PATH,null,false);
            for (String key : postCommentCounts.keySet()) {
                Integer value = postCommentCounts.get(key);
                TaskType taskType = value < 0? TaskType.REMOVE_COMMENT : TaskType.ADD_COMMENT;

                writeLog(COMMENT_FILE_PATH,new RedundancyTask(taskType, key, value),true);
            }
            return true;
        } catch (Exception e){
            logger.error("Error in overwrite log file content: "+ e.getMessage());
            return false;
        }
    }

    private boolean renewReportsLogFileContent(){
        // Write in the log file the task for failed redundancies operation on database
        try {
            writeLog(REPORT_FILE_PATH,null,false);
            for (String key : postReportCounts.keySet()) {
                Integer value = postReportCounts.get(key);
                TaskType taskType = value < 0? TaskType.REMOVE_REPORT : TaskType.ADD_REPORT;

                writeLog(REPORT_FILE_PATH,new RedundancyTask(taskType, key, value),true);
            }
            return true;
        } catch (Exception e){
            logger.error("Error in overwrite log file content: "+ e.getMessage());
            return false;
        }
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

    private void writeLog (String filePath, RedundancyTask task, boolean appendMode) throws SocialNewsRedundancyTaskException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath, appendMode))) {
            // Lock on file has been already taken from caller method
            oos.writeObject(task);
        }catch (Exception e){
            logger.warn("Write log file error");
            throw new SocialNewsRedundancyTaskException("Exception in log write operation");
        }
    }

    private List<RedundancyTask> extractFromLog (String filePath) throws SocialNewsRedundancyTaskException {
        List<RedundancyTask> tasks = new ArrayList<>();

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath))) {

            // Lock on file has been already taken from caller method
            while (objectInputStream.available() > 0) {
                tasks.add((RedundancyTask) objectInputStream.readObject());
            }

            // Delete file content
            writeLog(filePath, null, false);

        }catch (FileNotFoundException e) {
            // The file does not exist yet, no changes need to be applied
            logger.warn("Not existing log file: "+filePath);
        } catch (IOException | ClassNotFoundException e){
            // Error while reading the file
            logger.warn("Extract from log file error:"+filePath);
            throw new SocialNewsRedundancyTaskException("Exception in log read operation");
        }

        return tasks;
    }

    private void stopRedundanciesUpdate (){
        logger.info("RedundancyUpdater exiting.");
        executor.shutdown();
        applyRedundanciesFromLog();
    }
}
