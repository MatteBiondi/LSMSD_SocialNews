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
import javax.servlet.http.HttpSession;
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

        HttpSession session = request.getSession(false);
        String reporterId = (String) session.getAttribute("id");
        String lastPostId = request.getParameter("lastId");
        long lastTimestamp = Long.parseLong(request.getParameter("lastTimestamp"));
        String direction = request.getParameter("direction");

        try {
            ReporterService reporterService = ServiceLocator.getReporterService();

            PostDTO lastPost = new PostDTO();
            lastPost.setId(lastPostId);
            lastPost.setTimestamp(new Date(lastTimestamp));
            lastPost.setReporterId(reporterId);

            System.out.println("direction: " + direction);

            if(direction.equals("next")) {

                System.out.println("next direction");

                List<PostDTO> nextPage = reporterService.nextReporterPagePosts(lastPost);
                String nextPageJSON = toJSONArray(nextPage);
                writer.write(nextPageJSON);
            }
            else {

                System.out.println("prev direction");
                System.out.println(lastPost);

                List<PostDTO> prevPage = reporterService.prevReporterPagePosts(lastPost);
                String prevPageJSON = toJSONArray(prevPage);
                System.out.println(prevPageJSON);
                writer.write(prevPageJSON);
            }
        } catch (SocialNewsServiceException ex) {
            String message = ex.getMessage();
            LOGGER.warning(String.format("Service error occurred: %s", message));
            writer.write(String.format("%s", message));
        } catch (Exception ex){
            String message = ex.getMessage();
            LOGGER.warning(String.format("Service error occurred: %s", message));
            writer.write(String.format("%s", message));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
            hashtagsList.replaceAll(s -> "#" + s);

            String links = request.getParameter("links");
            List<String> linksList = new ArrayList<>();
            if (links != null && !links.isEmpty()) {
                linksList = Arrays.asList(links.split(" "));
            }
            PostDTO newPost = new PostDTO(reporterID, text, linksList, hashtagsList);

            try {
                ServiceLocator.getPostService().publishPost(newPost);
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
            writer.write("success");
            try {
                ServiceLocator.getPostService().removePost(postID, reporterID);
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
