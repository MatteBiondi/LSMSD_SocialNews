package it.unipi.lsmsd.socialnews.servlet.reporter;

import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterPageDTO;
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
import java.util.*;
import java.util.logging.Logger;

@WebServlet(name = "ReporterHomepageServlet", value = "/reporter/homepage", loadOnStartup = 0)
public class HomepageServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(HomepageServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        String id = (String) session.getAttribute("id");

        try {

            ReporterPageDTO reporterPageDTO = ServiceLocator.getReporterService().loadReporterPage(id);
            List<PostDTO> postsList = reporterPageDTO.getPosts();
            reporterPageDTO.setPosts(null); //avoid passing duplicate values
            reporterPageDTO.getReporter().setFullName(new String(reporterPageDTO.getReporter().getFullName().getBytes(), StandardCharsets.UTF_8));
            response.setContentType("text/html");
            request.setAttribute("reporterPage",reporterPageDTO);
            request.setAttribute("postsList",postsList);

            String targetJSP = "/pages/jsp/reporter/homepage.jsp";
            request.getRequestDispatcher(targetJSP).forward(request, response);

        } catch (SocialNewsServiceException ex) {
            String message = ex.getMessage();
            LOGGER.warning(String.format("Service error occurred: %s", message));
            PrintWriter writer = response.getWriter();
            writer.write(String.format("%s", message));
        } catch (Exception e){
            String message = e.getMessage();
            LOGGER.warning(String.format("Unexpected error occurred: %s", message));
            PrintWriter writer = response.getWriter();
            writer.write(String.format("%s", message));
        }
    }
}