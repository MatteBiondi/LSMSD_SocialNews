package it.unipi.lsmsd.socialnews.service.implement;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Admin;
import it.unipi.lsmsd.socialnews.dto.AdminDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.service.AdminService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.Util;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class AdminServiceImpl implements AdminService {


    /**
     * Register a new reporter in the application, storing the information on database
     *
     * @param newReporter reporter DTO object containing information of the new reporter
     * @return identifier assigned to the new reporter
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public String registerReporter(ReporterDTO newReporter) throws SocialNewsServiceException {
        try {
            newReporter.setReporterId(UUID.randomUUID().toString()); // Ensure univocity of this field
            newReporter.setPassword(Util.hashPassword(newReporter.getPassword()));
            return DAOLocator.getReporterDAO().register(Util.buildReporter(newReporter));
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Configuration error: hash algorithm");
        }
    }

    /**
     * Authenticates an admin identified by email via secret password
     *
     * @param email    email of the admin
     * @param password cleartext secret password of the admin
     * @return if authentication succeed adminDTO object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public AdminDTO authenticate(String email, String password) throws SocialNewsServiceException {
        try {
            Admin admin = DAOLocator.getAdminDAO().authenticate(email, Util.hashPassword(password));
            System.out.println(admin);
            return Util.buildAdminDTO(admin);
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
