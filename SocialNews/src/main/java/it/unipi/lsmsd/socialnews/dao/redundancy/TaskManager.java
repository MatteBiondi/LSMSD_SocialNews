package it.unipi.lsmsd.socialnews.dao.redundancy;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsRedundancyTaskException;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoPostDAO;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoReporterDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

class TaskExecutor extends Thread{
    private static final String FILE_PATH = "redundancy_log.dat";
    private static final Logger logger = LoggerFactory.getLogger(ServletContextListener.class);
    private final ScheduledExecutorService executor;

    //BlockingQueue implementations are thread-safe. All queuing methods achieve their effects atomically
    private final BlockingQueue<RedundancyTask> taskQueue;
    private final Map<String, Integer> postCommentCounts; // postId -> commentCounter
    private final Map<String, Integer> postReportCounts; // reporterId -> reportCounter

    private boolean running;

    private final Object mutex;

    public TaskExecutor(BlockingQueue<RedundancyTask> taskQueue) {
        postCommentCounts = new HashMap<>();
        postReportCounts = new HashMap<>();
        this.taskQueue = taskQueue;
        executor = Executors.newSingleThreadScheduledExecutor();
        running = true;
        mutex = new Object();
    }

    @Override
    public void run() {
        try{
            restartByLog();
            executor.scheduleAtFixedRate(this::applyChangesToDB, 0, 1, TimeUnit.HOURS);

            while (running) {
                synchronized (mutex) {
                    RedundancyTask task = taskQueue.poll(5, TimeUnit.MINUTES);
                    if(task != null) {
                        executeTask(task);
                        writeLog(task);
                    }
                }
            }

            executor.shutdown();
        } catch (InterruptedException e) {
            logger.error("TaskExecutor thread interrupted");
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException e) {
            logger.error("Task execution exception");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Unexpected exception");
            Thread.currentThread().interrupt();
        } finally {
            logger.info("ThreadExecutor exiting.");
            applyChangesToDB();
        }
    }

    private void restartByLog() throws SocialNewsRedundancyTaskException {
        // Read operations in log file
        List<RedundancyTask> pendentTasks = readLog();

        // Execute all the tasks read from the file
        for (RedundancyTask task : pendentTasks) {
            executeTask(task);
        }

        // Apply redundancy changes to DB
        applyChangesToDB();
    }

    private void applyChangesToDB() {
        synchronized (mutex){
            // DB redundancy update
            try {
                MongoPostDAO mongoPostDAO = new MongoPostDAO();
                for (String key : postCommentCounts.keySet()) {
                    Integer value = postCommentCounts.get(key);
                    if(mongoPostDAO.updateNumOfComment(key, value) == 0)
                        throw new SocialNewsRedundancyTaskException("Error: No post modified");
                }
                logger.info("Post comment redundancies applied");

                MongoReporterDAO mongoReporterDAO = new MongoReporterDAO();
                for (String key : postReportCounts.keySet()) {
                    Integer value = postReportCounts.get(key);
                    if(mongoReporterDAO.updateNumOfReport(key, value) == 0)
                        throw new SocialNewsRedundancyTaskException("Error: No reporter modified");
                }
                logger.info("Post report redundancies applied");
            } catch (Exception e){
                logger.error("Error in applying changes to DB: "+ e.getMessage());
                return;
            }

            // Flush log file
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
                // Write null to overwrite log file
                oos.writeObject(null);
            } catch (IOException e) {
                logger.warn("Flush log file error: ", e);
                // In case of error try to remove and recreate file to empty it
                File file = new File(FILE_PATH);
                try {
                    if (file.exists()) {
                        if (!file.delete()) {
                            throw new SocialNewsRedundancyTaskException("Error in removing file " + FILE_PATH);
                        }
                    }
                    if (!file.createNewFile()) {
                        throw new SocialNewsRedundancyTaskException("Error in recreating file " + FILE_PATH);
                    }
                } catch (Exception ex) {
                    logger.error("Error during changes application to DB: " + e.getMessage());
                    printMapsContent();
                    return;
                }
            }

            // Flush local maps
            postCommentCounts.clear();
            postReportCounts.clear();
        }
    }

    private void printMapsContent() {
        logger.info("Post comments redundancy content: ");
        for (String key : postCommentCounts.keySet()) {
            Integer value = postCommentCounts.get(key);
            logger.info(key + " : " + value);
        }

        logger.info("Post report redundancy content: ");
        for (String key : postReportCounts.keySet()) {
            Integer value = postReportCounts.get(key);
            logger.info(key + " : " + value);
        }
    }

    private void executeTask(RedundancyTask task) throws IllegalArgumentException{
        switch (task.getOperationType()) {
            case ADD_COMMENT    -> addComment(task.getIdentifier());
            case REMOVE_COMMENT -> removeComment(task.getIdentifier());
            case ADD_REPORT     -> addReport(task.getIdentifier());
            case REMOVE_REPORT  -> removeReport(task.getIdentifier());
            case STOP_RUNNING   -> stopThread();
            default             -> throw new IllegalArgumentException("Fail to identify operation type in task execution");
        }
    }

    private void stopThread() {
        running=false;
    }

    private void addComment (String postId) {
        postCommentCounts.put(postId, postCommentCounts.getOrDefault(postId, 0) + 1);
    }

    private void removeComment (String postId){
        postCommentCounts.put(postId, postCommentCounts.getOrDefault(postId, 0) - 1);
    }

    private void addReport (String reporterId){
        postReportCounts.put(reporterId, postReportCounts.getOrDefault(reporterId, 0) + 1);
    }

    private void removeReport (String reporterId){
        postReportCounts.put(reporterId, postReportCounts.getOrDefault(reporterId, 0) - 1);
    }

    public int getCommentCount(String postId) {
        return postCommentCounts.getOrDefault(postId, 0);
    }

    public int getReportCount(String reporterId) {
        return postReportCounts.getOrDefault(reporterId, 0);
    }

    private void writeLog (RedundancyTask task) throws SocialNewsRedundancyTaskException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH, true))) {
            oos.writeObject(task);
        }catch (Exception e){
            logger.warn("Write log file error");
            throw new SocialNewsRedundancyTaskException("Exception in log write operation");
        }
    }

    private List<RedundancyTask> readLog () throws SocialNewsRedundancyTaskException {
        List<RedundancyTask> tasks = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            while (ois.available() > 0) {
                tasks.add((RedundancyTask) ois.readObject());
            }
        } catch (FileNotFoundException e) {
            // The file does not exist yet, no changes need to be applied
            logger.warn("Read log file error");
        } catch (IOException | ClassNotFoundException e){
            // Error while reading the file
            logger.warn("Read log file error");
            throw new SocialNewsRedundancyTaskException("Exception in log read operation");
        }

        return tasks;
    }
}

public class TaskManager {

    private static volatile TaskManager instance;
    private final BlockingQueue<RedundancyTask> taskQueue;
    private final TaskExecutor taskExecutor;

    private TaskManager() {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.taskExecutor = new TaskExecutor(taskQueue);
        this.taskExecutor.start();
    }

    public static synchronized TaskManager getInstance() {
        if(instance == null){
            synchronized (TaskManager.class){
                if(instance == null){
                    instance = new TaskManager();
                }
            }
        }
        return instance;
    }

    public void addTask(RedundancyTask task){
        try{
            if(task.getOperationType() != TaskType.STOP_RUNNING)
                taskQueue.put(task);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
