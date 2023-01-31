package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.implement.ReaderImplDAO;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static it.unipi.lsmsd.socialnews.dao.DAOLocator.Service.*;

public abstract class DAOLocator {
    private static final Logger logger = LoggerFactory.getLogger(MongoConnection.class);
    private static final Map<Service, Object> cache;

    /**
     * Initial context class, it is in charge to look up an instance of the service interface passed as argument
     * Due to its simplicity the task may be implemented as simple method of the DAOLocator, however we keep this
     * class as starting point for future development
     */
    static class InitialContext{
        /**
         * Instantiates an object implementing the service interface
         * @param service service interface
         * @return instance implementing the interface service
         */
        Object lookup(Service service) {//TODO: create missing DAOs
            return switch (service) {
                case ADMIN -> null;
                case READER -> new ReaderImplDAO();
                case REPORTER -> null;
                case POST -> null;
                case COMMENT -> null;
            };
        }
    }

    /**
     * Services handled by DAOLocator
     */
    enum Service{
        ADMIN(AdminDAO.class),
        READER(ReaderImplDAO.class),
        REPORTER(ReporterDAO.class),
        POST(PostDAO.class),
        COMMENT(CommentDAO.class);

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
    private static Object getDAO(Service service){
        if(!cache.containsKey(service)){
            logger.info("Lookup " + service);
            cache.put(service, new InitialContext().lookup(service));
        }
        else {
            logger.debug("Cached " + service);
        }
        return cache.get(service);
    }

    /**
     * Retrieves an object implementing AdminDAO interface
     * @return instance implementing AdminDAO interface
     */
    public static AdminDAO getAdminDAO(){
        return (AdminDAO) getDAO(ADMIN);
    }

    /**
     * Retrieves an object implementing ReaderDAO interface
     * @return instance implementing ReaderDAO interface
     */
    public static ReaderDAO getReaderDAO(){
        return (ReaderDAO) getDAO(READER);
   }

    /**
     * Retrieves an object implementing ReporterDAO interface
     * @return instance implementing ReporterDAO interface
     */
    public static ReporterDAO getReporterDAO(){
        return (ReporterDAO) getDAO(REPORTER);
   }

    /**
     * Retrieves an object implementing PostDAO interface
     * @return instance implementing PostDAO interface
     */
    public static PostDAO getPostDAO(){
        return (PostDAO) getDAO(POST);
   }

    /**
     * Retrieves an object implementing CommentDAO interface
     * @return instance implementing CommentDAO interface
     */
    public static CommentDAO getCommentDAO(){
        return (CommentDAO) getDAO(COMMENT);
   }
}
