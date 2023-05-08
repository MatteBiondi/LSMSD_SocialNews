package it.unipi.lsmsd.socialnews.servlet;

import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.service.ReporterService;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Logger;

import static it.unipi.lsmsd.socialnews.dto.util.JSONConverter.toJSON;
import static it.unipi.lsmsd.socialnews.dto.util.JSONConverter.toJSONArray;

@WebServlet(name = "PostHandlingServlet", value = "/posthandling", loadOnStartup = 0)
public class PostHandlingServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PostHandlingServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter writer = response.getWriter();

        String reporterId = request.getParameter("reporterId");
        String lastPostId = request.getParameter("lastId");
        long lastTimestamp = Long.parseLong(request.getParameter("lastTimestamp"));
        String direction = request.getParameter("direction");

        try {
            ReporterService reporterService = ServiceLocator.getReporterService();

            PostDTO lastPost = new PostDTO();
            lastPost.setId(lastPostId);
            lastPost.setTimestamp(new Date(lastTimestamp));
            lastPost.setReporterId(reporterId);

            if(direction.equals("next")) {
                List<PostDTO> nextPage = reporterService.nextReporterPagePosts(lastPost);
                String nextPageJSON = toJSONArray(nextPage);
                writer.write(nextPageJSON);
            }
            else {
                List<PostDTO> prevPage = reporterService.prevReporterPagePosts(lastPost);
                String prevPageJSON = toJSONArray(prevPage);
                writer.write(prevPageJSON);
            }
        } catch (Exception ex) {
            String message = ex.getMessage();
            LOGGER.warning(String.format("Service error occurred: %s", message));
            writer.write(String.format("%s", message));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter writer = response.getWriter();

        String reporterID = request.getParameter("reporterID");
        String operation = request.getParameter("operation");
        if(operation.equals("insert")) {
            response.setContentType("application/json");

            String text = request.getParameter("text");
            String hashtags = request.getParameter("hashtags");

            List<String> hashtagsList = new ArrayList<>();
            if (hashtags != null && !hashtags.isEmpty()) {
                hashtagsList = Arrays.asList(hashtags.split(" "));
            }
            String links = request.getParameter("links");
            List<String> linksList = new ArrayList<>();
            if (links != null && !links.isEmpty()) {
                linksList = Arrays.asList(links.split(" "));
            }
            PostDTO newPost = new PostDTO(reporterID, text, linksList, hashtagsList);

            try {
                String newPostId = ServiceLocator.getPostService().publishPost(newPost);
                newPost.setId(newPostId);
                String newPostJson = toJSON(newPost);
                writer.write(newPostJson);
            } catch (SocialNewsServiceException ex) {
                String message = ex.getMessage();
                LOGGER.warning(String.format("Service error occurred: %s", message));
                writer.write(String.format("%s", message));
            } catch (Exception e) {
                String message = e.getMessage();
                LOGGER.warning(String.format("Unexpected error occurred: %s", message));
                writer.write(String.format("%s", message));
            }
        }

        //"remove" operation
        else {
            response.setContentType("text/plain");

            String postID = request.getParameter("postID");
            try {
                ServiceLocator.getPostService().removePost(postID, reporterID);
                writer.write("{\"result\": \"ok\"}");
            } catch (SocialNewsServiceException ex) {
                String message = ex.getMessage();
                LOGGER.warning(String.format("Service error occurred: %s", message));
                writer.write(String.format("%s", message));
            } catch (Exception e) {
                String message = e.getMessage();
                LOGGER.warning(String.format("Unexpected error occurred: %s", message));
                writer.write(String.format("%s", message));
            }
        }
    }
}
