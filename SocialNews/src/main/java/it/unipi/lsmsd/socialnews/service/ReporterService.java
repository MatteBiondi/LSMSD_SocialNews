package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

public interface ReporterService {
    /**
     * Authenticates a reporter identified by email via secret password
     * @param email email of the reporter
     * @param password cleartext secret password of the reporter
     * @return if authentication succeed reporterDTO object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    ReporterDTO authenticate(String email, String password) throws SocialNewsServiceException;
}
