package it.unipi.lsmsd.socialnews.service.implement;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterPageDTO;
import it.unipi.lsmsd.socialnews.service.ReporterService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.ServiceWorkerPool;
import it.unipi.lsmsd.socialnews.service.util.Util;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    /**
     * Retrieves all the information about a reporter, identified by the identifier passed as argument, and the list
     * of his/her most recent post up to a configured number of posts
     *
     * @param reporterId reporter identifier
     * @return reporter page DTO containing information about reporter and the list of his\her most recent posts
     * @throws SocialNewsServiceException in case of failure of the operation or if the reporter is not in the system
     */
    @Override
    public ReporterPageDTO loadReporterPage(String reporterId) throws SocialNewsServiceException {
        try {
            List<Future<?>> futures = ServiceWorkerPool.getPool().submitTask(List.of(
                    () -> DAOLocator.getReporterDAO()
                            .reporterByReporterId(reporterId, Util.getIntProperty("listPostPageSize",10)),
                    () -> DAOLocator.getReporterDAO().getNumOfFollowers(reporterId)
            ));
            Reporter reporter = (Reporter) futures.get(0).get();
            Integer numFollowers = (Integer) futures.get(1).get();
            return Util.buildReporterPageDTO(reporter, numFollowers);
        } catch (IllegalArgumentException ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("Reporter not in the system");
        } catch (ExecutionException | InterruptedException ex) {
            throw new SocialNewsServiceException("Parallel execution error: " + ex.getMessage());
        }
    }

    /**
     * Retrieves information about posts published by the reporter specified as argument ordered by publication
     * timestamp starting from the offset passed as argument, up to a configured number of posts
     *
     * @param postOffset post DTO containing postId and reporterId of the last post in the previous page
     * @return list of postDTO objects containing all the information
     * @throws SocialNewsServiceException in case of failure of the operation or if the post is not in the system
     */
    @Override
    public List<PostDTO> nextReporterPagePosts(PostDTO postOffset) throws SocialNewsServiceException {
        try {
            Post offset = Util.buildPost(postOffset);
            List<PostDTO> postDTOList = new ArrayList<>();
            DAOLocator.getPostDAO()
                    .postsByReporterId(
                            postOffset.getReporterId(),
                            offset,
                            Util.getIntProperty("listPostPageSize",10))
                    .forEach(post -> postDTOList.add(Util.buildPostDTO(post, postOffset.getReporterId())));
            return postDTOList;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
        catch (IllegalArgumentException ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("Post not in the system");
        }
    }

    /**
     * Retrieves the top N most commented posts of a given reporter, considering the last N unit of times
     *
     * @param reporterId reporter identifier
     * @param lastN      compute statistic on last N unit of times
     * @param unitOfTime unit of time
     * @return list of postDTO objects containing the information about the top N posts of the reporter specified
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<PostDTO> latestHottestPost(String reporterId, Integer lastN, TemporalUnit unitOfTime) throws SocialNewsServiceException {
        try {
            List<PostDTO> postDTOList = new ArrayList<>();
            DAOLocator.getPostDAO().latestHottestPosts(
                    reporterId,
                    Util.getIntProperty("topNPosts",10),
                    Date.from(LocalDateTime.now().minus(lastN, unitOfTime)
                            .atZone(ZoneOffset.systemDefault()).toInstant()
                    )
            ).forEach(post -> postDTOList.add(Util.buildPostDTO(post, reporterId)));
            return postDTOList;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }
}
