package it.unipi.lsmsd.socialnews.service.implement;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Reporter;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.service.ReporterService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.Util;
import java.security.NoSuchAlgorithmException;

public class ReporterServiceImpl implements ReporterService {

    /**
     * Authenticates a reporter identified by email via secret password
     *
     * @param email    email of the reporter
     * @param password cleartext secret password of the reporter
     * @return if authentication succeed reporterDTO object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public ReporterDTO authenticate(String email, String password) throws SocialNewsServiceException {
        try {
            Reporter reporter = DAOLocator.getReporterDAO().authenticate(email, Util.hashPassword(password));
            return Util.buildReporterDTO(reporter);
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
        catch (NoSuchAlgorithmException ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("Configuration error: hash algorithm");
        }
    }
}
