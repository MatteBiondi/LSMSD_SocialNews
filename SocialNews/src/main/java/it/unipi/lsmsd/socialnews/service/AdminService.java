package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.dto.AdminDTO;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

public interface AdminService {
    /**
     * Authenticates an admin identified by email via secret password
     * @param email email of the admin
     * @param password cleartext secret password of the admin
     * @return if authentication succeed adminDTO object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    AdminDTO authenticate(String email, String password) throws SocialNewsServiceException;
}
