package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterPageDTO;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import java.util.List;

public interface ReporterService {
    /**
     * Authenticates a reporter identified by email via secret password
     *
     * @param email email of the reporter
     * @param password cleartext secret password of the reporter
     * @return if authentication succeed reporterDTO object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    ReporterDTO authenticate(String email, String password) throws SocialNewsServiceException;

    /**
     * Retrieves all the information about a reporter, identified by the identifier passed as argument, and the list
     * of his/her most recent post up to a configured number of posts
     *
     * @param reporterId reporter identifier
     * @return reporter page DTO containing information about reporter and the list of his\her most recent posts
     * @throws SocialNewsServiceException in case of failure of the operation or if the reporter is not in the system
     */
    ReporterPageDTO loadReporterPage(String reporterId) throws SocialNewsServiceException;

    /**
     * Retrieves information about posts published by the reporter specified as argument ordered by publication
     * timestamp starting from the offset passed as argument, up to a configured number of posts
     *
     * @param postOffset post DTO containing postId and reporterId of the last post in the previous page
     * @return list of postDTO objects containing all the information
     * @throws SocialNewsServiceException in case of failure of the operation or if the post is not in the system
     */
    List<PostDTO> nextReporterPagePosts(PostDTO postOffset) throws SocialNewsServiceException;

    /**
     * Retrieves information about number of followers for a reporter passed as argument
     *
     * @param reporterId id of the reporter of interest
     * @return number of followers for the specified reporter
     * @throws SocialNewsServiceException in case of failure of the query operation
     */
    Integer howManyFollowers(String reporterId) throws SocialNewsServiceException;
}
