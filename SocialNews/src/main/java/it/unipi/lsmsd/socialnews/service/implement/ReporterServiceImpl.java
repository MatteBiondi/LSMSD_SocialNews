package it.unipi.lsmsd.socialnews.service.implement;

import com.fasterxml.jackson.databind.node.ArrayNode;
import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterPageDTO;
import it.unipi.lsmsd.socialnews.service.ReporterService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.Page;
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

    private List<PostDTO> reporterPagePosts(PostDTO postOffset, Page page) throws SocialNewsServiceException {
        try {
            Post offset = Util.buildPost(postOffset);
            List<PostDTO> postDTOList = new ArrayList<>();

            switch (page){
                case FIRST, NEXT -> DAOLocator.getPostDAO()
                        .postsByReporterIdNext(
                                postOffset.getReporterId(),
                                offset,
                                Util.getIntProperty("listPostPageSize",10))
                        .forEach(post -> postDTOList.add(Util.buildPostDTO(post, postOffset.getReporterId())));
                case PREV ->  DAOLocator.getPostDAO()
                        .postsByReporterIdPrev(
                                postOffset.getReporterId(),
                                offset,
                                Util.getIntProperty("listPostPageSize",10))
                        .forEach(post -> postDTOList.add(Util.buildPostDTO(post, postOffset.getReporterId())));

            }
            return postDTOList;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error: " + ex.getMessage());
        }
        catch (IllegalArgumentException ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("Post not in the system");
        }
    }

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

    @Override
    public ReporterPageDTO loadReporterPage(String reporterId) throws SocialNewsServiceException{
        return loadReporterPage(reporterId, null);
    }

    @Override
    public ReporterPageDTO loadReporterPage(String reporterId, String readerId) throws SocialNewsServiceException {
        try {
            List<Future<?>> futures = ServiceWorkerPool.getPool().submitTask(List.of(
                    () -> DAOLocator.getReporterDAO()
                            .reporterByReporterId(reporterId, Util.getIntProperty("listPostPageSize",10)),
                    () -> DAOLocator.getReporterDAO().getNumOfFollowers(reporterId, readerId)
            ));
            Reporter reporter = (Reporter) futures.get(0).get();

            ArrayNode followersArray = (ArrayNode) futures.get(1).get();
            Integer numFollowers = followersArray.get(0).asInt();
            Boolean isFollower = followersArray.get(1).asInt() == 1;

            return Util.buildReporterPageDTO(reporter, numFollowers, isFollower);
        } catch (IllegalArgumentException ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("Reporter not in the system");
        } catch (ExecutionException | InterruptedException ex) {
            throw new SocialNewsServiceException("Parallel execution error: " + ex.getMessage());
        }
    }

    @Override
    public List<PostDTO> prevReporterPagePosts(PostDTO postOffset) throws SocialNewsServiceException {
        return reporterPagePosts(postOffset, Page.PREV);
    }

    @Override
    public List<PostDTO> nextReporterPagePosts(PostDTO postOffset) throws SocialNewsServiceException {
        return reporterPagePosts(postOffset, Page.NEXT);
    }

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
            throw new SocialNewsServiceException("Database error: " + ex.getMessage());
        }
    }

    @Override
    public ArrayNode hottestMomentsOfDay(Integer windowSize, Integer lastN, TemporalUnit unitOfTime) throws SocialNewsServiceException{
        try {
            return DAOLocator.getCommentDAO().latestHottestMomentsOfDay(windowSize,
                    Date.from(LocalDateTime.now().minus(lastN, unitOfTime)
                            .atZone(ZoneOffset.systemDefault()).toInstant())
            );
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error: " + ex.getMessage());
        }
    }
}
