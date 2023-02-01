package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Reporter;
import it.unipi.lsmsd.socialnews.dto.AdminDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

public interface AdminService {

    /**
     *  Register a new reporter in the application, storing the information into database
     *
     * @param newReporter reporter DTO object containing information of the new reporter
     * @return identifier assigned to the new reporter
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    String registerReporter(ReporterDTO newReporter) throws SocialNewsServiceException;

    /**
     * Authenticates an admin identified by email via secret password
     * @param email email of the admin
     * @param password cleartext secret password of the admin
     * @return if authentication succeed adminDTO object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    AdminDTO authenticate(String email, String password) throws SocialNewsServiceException;
}
