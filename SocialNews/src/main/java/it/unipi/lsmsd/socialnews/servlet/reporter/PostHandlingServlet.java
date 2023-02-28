package it.unipi.lsmsd.socialnews.servlet.reporter;

import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static it.unipi.lsmsd.socialnews.dto.util.JSONConverter.toJSON;

@WebServlet(name = "PostHandlingServlet", value = "/reporter/posthandling", loadOnStartup = 0)
public class PostHandlingServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PostHandlingServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("application/json");

        String reporterID = request.getParameter("reporterID");
        String operation = request.getParameter("operation");
        if(operation.equals("insert")) {

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
            String postID = request.getParameter("postID");
            try {
                ServiceLocator.getPostService().removePost(postID, reporterID);
                writer.write("{result: ok}");
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
