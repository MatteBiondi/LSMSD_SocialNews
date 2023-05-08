package it.unipi.lsmsd.socialnews.servlet;

import it.unipi.lsmsd.socialnews.dto.CommentDTO;
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
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static it.unipi.lsmsd.socialnews.dto.util.JSONConverter.toJSON;
import static it.unipi.lsmsd.socialnews.dto.util.JSONConverter.toJSONArray;

@WebServlet(name = "CommentHandlingServlet", value = "/commenthandling", loadOnStartup = 0)
public class CommentHandlingServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CommentHandlingServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter writer = response.getWriter();

        int page = Integer.parseInt(request.getParameter("page"));
        String postId = request.getParameter("postId");

        //da ricompattare
        if(page==1) {

            try {

                List<CommentDTO> commentPage = ServiceLocator.getPostService().firstPageComments(postId);
                String commentPageJSON = toJSONArray(commentPage);
                for (CommentDTO comment : commentPage){
                    comment.getReader().setFullName(new String(comment.getReader().getFullName().getBytes(),
                            StandardCharsets.UTF_8));
                }
                writer.write(commentPageJSON);

            } catch (SocialNewsServiceException ex) {
                String message = ex.getMessage();
                LOGGER.warning(String.format("Service error occurred: %s", message));
                writer.write(String.format("%s", message));
            } catch (Exception ex) {
                String message = ex.getMessage();
                LOGGER.warning(String.format("Unexpected service error occurred: %s", message));
                writer.write(String.format("%s", message));
            }
        }
        else {
            String commentsLastId = request.getParameter("commentsLastId");
            long commentsLastTimestamp = Long.parseLong(request.getParameter("commentsLastTimestamp"));

            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(commentsLastId);
            commentDTO.setTimestamp(new Date(commentsLastTimestamp));

            try {
                List<CommentDTO> nextCommentsPage = ServiceLocator.getPostService().nextPageComments(postId, commentDTO);
                for (CommentDTO comment : nextCommentsPage){
                    comment.getReader().setFullName(new String(comment.getReader().getFullName().getBytes(),
                            StandardCharsets.UTF_8));
                }
                String commentPageJSON = toJSONArray(nextCommentsPage);
                writer.write(commentPageJSON);

            } catch (SocialNewsServiceException ex) {
                String message = ex.getMessage();
                LOGGER.warning(String.format("Service error occurred: %s", message));
                writer.write(String.format("%s", message));
            } catch (Exception ex){
                String message = ex.getMessage();
                LOGGER.warning(String.format("Unexpected service error occurred: %s", message));
                writer.write(String.format("%s", message));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter writer = response.getWriter();

        String postId = request.getParameter("postId");
        String operation = request.getParameter("operation");
        HttpSession session = request.getSession(false);
        if(operation.equals("insert")) {
            response.setContentType("application/json");

            String text = request.getParameter("text");
            String readerId = (String) session.getAttribute("id");
            String readerFullName = (String) session.getAttribute("fullName");
            String reporterId = request.getParameter("reporterId");

            CommentDTO newComment = new CommentDTO(readerId, readerFullName, postId, reporterId, text);

            try {
                String newCommentId = ServiceLocator.getPostService().publishComment(newComment);
                newComment.setId(newCommentId);
                String newPostJson = toJSON(newComment);
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
            String commentId = request.getParameter("commentId");
            try {
                System.out.println(commentId);
                ServiceLocator.getPostService().removeComment(commentId, postId);
                writer.write("success");
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
