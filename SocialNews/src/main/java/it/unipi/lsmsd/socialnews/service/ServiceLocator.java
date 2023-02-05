package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.service.implement.AdminServiceImpl;
import it.unipi.lsmsd.socialnews.service.implement.PostServiceImpl;
import it.unipi.lsmsd.socialnews.service.implement.ReaderServiceImpl;
import it.unipi.lsmsd.socialnews.service.implement.ReporterServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

import static it.unipi.lsmsd.socialnews.service.ServiceLocator.Service.*;

public final class ServiceLocator {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLocator.class);
    private static final Map<Service, Object> cache;

    /**
     * Private constructor to prevent instantiation
     */
    private ServiceLocator(){ }

    /**
     * Initial context class, it is in charge to look up an instance of the service interface passed as argument
     * Due to its simplicity the task may be implemented as simple method of the ServiceLocator, however we keep this
     * class as starting point for future development
     */
    static class InitialContext{
        /**
         * Instantiates an object implementing the service interface
         * @param service service interface
         * @return instance implementing the interface service
         */
        Object lookup(ServiceLocator.Service service) {
            return switch (service) {
                case ADMIN -> new AdminServiceImpl();
                case READER -> new ReaderServiceImpl();
                case REPORTER -> new ReporterServiceImpl();
                case POST -> new PostServiceImpl();
                case STATISTICS -> null;
            };
        }
    }

    /**
     * Services handled by ServiceLocator
     */
    enum Service{
        ADMIN(AdminService.class),
        READER(ReaderService.class),
        REPORTER(ReporterService.class),
        POST(PostService.class),
        STATISTICS(null);

        private final Class<?> clazz;

        Service(Class<?> clazz) { this.clazz = clazz; }

        @Override
        public String toString() {
            return this.clazz.getName();
        }
    }

    static {// Cache initialization
        cache = new HashMap<>();
    }

    /**
     * If already instantiated retrieves the object from local cache, otherwise lookup it via InitalContext
     * @param service service interface
     * @return instance implementing the interface service
     */
    private static Object getService(Service service){
        if(!cache.containsKey(service)){
            logger.info("Lookup " + service);
            cache.put(service, new ServiceLocator.InitialContext().lookup(service));
        }
        else {
            logger.debug("Cached " + service);
        }
        return cache.get(service);
    }

    /**
     * Retrieves an object implementing AdminService interface
     * @return instance implementing AdminService interface
     */
     public static AdminService getAdminService(){
         return (AdminService) getService(ADMIN);
     }

    /**
     * Retrieves an object implementing ReaderService interface
     * @return instance implementing ReaderService interface
     */
    public static ReaderService getReaderService(){
        return (ReaderService) getService(READER);
    }

    /**
     * Retrieves an object implementing ReporterService interface
     * @return instance implementing ReporterService interface
     */
    public static ReporterService getReporterService(){
        return (ReporterService) getService(REPORTER);
    }

    /**
     * Retrieves an object implementing PostService interface
     * @return instance implementing PostService interface
     */
    public static PostService getPostService(){
        return (PostService) getService(POST);
    }

}
