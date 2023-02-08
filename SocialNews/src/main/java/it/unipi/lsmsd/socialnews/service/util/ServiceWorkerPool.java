package it.unipi.lsmsd.socialnews.service.util;

import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public final class ServiceWorkerPool {
    /**
     * Thread pool used to execute multiple query requested by some services
     * Note that an appropriate configuration of the pool is crucial to obtains advantages respect to the execution
     * in series of the operations
     */
    private final ExecutorService pool;

    private static volatile ServiceWorkerPool instance;

    private ServiceWorkerPool(){
        pool = Executors.newCachedThreadPool();
    }

    public static ServiceWorkerPool getPool(){
        if(instance == null){
            synchronized (ServiceWorkerPool.class){
                if(instance == null){
                    instance = new ServiceWorkerPool();
                }
            }
        }
        return instance;
    }

    public List<Future<?>> submitTask(List<Callable<?>> tasks){
        List<Future<?>> futures = new ArrayList<>();
        for(Callable<?> task: tasks)
            futures.add(pool.submit(task));
        return futures;
    }

    public Future<?> submitTask(Callable<?> task){
        return pool.submit(task);
    }

    public void shutdown() throws SocialNewsServiceException {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(Util.getIntProperty("shutdownTimeout", 60), TimeUnit.SECONDS)) {
               pool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
            throw new SocialNewsServiceException("Thread pool not shutdown properly" + ex.getMessage());
        }
    }
}
