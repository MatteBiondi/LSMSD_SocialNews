package it.unipi.lsmsd.socialnews.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterPageDTO;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import java.time.temporal.TemporalUnit;
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
     * Retrieves all the information about a reporter, identified by the identifier passed as argument, and the list
     * of his/her most recent post up to a configured number of posts. The method also returns if the reader is or is not
     * a follower of the reporter of interest
     *
     * @param reporterId reporter identifier
     * @param readerId reader identifier
     * @return ReporterPageDTO containing information about reporter, the list of his\her most recent posts and
     * a boolean value 'true' if the reader is a follower of the reporter, 'false' otherwise
     * @throws SocialNewsServiceException in case of failure of the operation or if the reporter is not in the system
     */
    ReporterPageDTO loadReporterPage(String reporterId,  String readerId) throws SocialNewsServiceException;

    /**
     * Retrieves information about posts published by the reporter specified as argument ordered by publication
     * timestamp starting from the offset passed as argument, up to a configured number of posts in reverse order
     *
     * @param postOffset post DTO containing postId and reporterId of the first post in the current page
     * @return list of postDTO objects containing all the information
     * @throws SocialNewsServiceException in case of failure of the operation or if the post is not in the system
     */
    List<PostDTO> prevReporterPagePosts(PostDTO postOffset) throws SocialNewsServiceException;

    /**
     * Retrieves information about posts published by the reporter specified as argument ordered by publication
     * timestamp starting from the offset passed as argument, up to a configured number of posts
     *
     * @param postOffset post DTO containing postId and reporterId of the last post in the current page
     * @return list of postDTO objects containing all the information
     * @throws SocialNewsServiceException in case of failure of the operation or if the post is not in the system
     */
    List<PostDTO> nextReporterPagePosts(PostDTO postOffset) throws SocialNewsServiceException;

    /**
     * Retrieves the top N most commented posts of a given reporter, considering the last N unit of times
     *
     * @param reporterId reporter identifier
     * @param lastN compute statistic on last N unit of times
     * @param unitOfTime unit of time
     * @return list of postDTO objects containing the information about the top N posts of the reporter specified
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<PostDTO> latestHottestPost(String reporterId, Integer lastN, TemporalUnit unitOfTime) throws SocialNewsServiceException;

    /**
     Computes the number of comments in each time window of the day, starting from a given instant
     *
     * @param windowSize size of temporal window, must be a divisor of 24
     * @param lastN compute statistic on last N unit of times
     * @param unitOfTime unit of time
     * @return JSON array containing the information computed by aggregation pipeline
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    ArrayNode hottestMomentsOfDay(Integer windowSize, Integer lastN, TemporalUnit unitOfTime) throws SocialNewsServiceException;
}
