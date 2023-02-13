package it.unipi.lsmsd.socialnews.listener;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebListener
public class SessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionListener.class);

    public SessionListener() { }

    @Override
    public void sessionCreated(HttpSessionEvent se) { }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        if(se.getSession().getAttribute("userType").equals("reporter")){
            String email = (String) se.getSession().getAttribute("email");
            try {
                if(DAOLocator.getReporterDAO().checkAndSwap(email)){
                    logger.info(String.format("Maintenance on reporter %s has been complete", email));
                }
            } catch (SocialNewsDataAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
