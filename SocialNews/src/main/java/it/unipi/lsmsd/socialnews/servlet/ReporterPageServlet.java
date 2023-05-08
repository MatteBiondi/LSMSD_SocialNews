package it.unipi.lsmsd.socialnews.servlet;

import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterPageDTO;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(name = "ReporterPageServlet", value = "/reporterPage")
public class ReporterPageServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ReporterPageServlet.class.getName());


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/reporter/homepage.jsp";

        HttpSession session = request.getSession(false);

        String readerId = (String) session.getAttribute("id");

        // Retrieve reporter Id
        String reporterId = request.getParameter("id");

        // Retrieve reporter info
        try{
            if(reporterId == null)
                throw new ServletException("Missing reporter id error");

            ReporterPageDTO reporterPage = ServiceLocator.getReporterService().loadReporterPage(reporterId, readerId);
            List<PostDTO> postsList = reporterPage.getPosts();
            reporterPage.setPosts(null); //avoid passing duplicate values

            request.setAttribute("reporterPage",reporterPage);
            request.setAttribute("postsList",postsList);
            request.getRequestDispatcher(targetJSP).forward(request, response);
        } catch (SocialNewsServiceException ex) {
            String message = ex.getMessage();
            LOGGER.warning(String.format("Service error occurred: %s", message));
            PrintWriter writer = response.getWriter();
            writer.write(String.format("%s", message));
        } catch (Exception e) {
            String message = e.getMessage();
            LOGGER.warning(String.format("Unexpected error occurred: %s", message));
            PrintWriter writer = response.getWriter();
            writer.write(String.format("%s", message));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        // Take requested operation
        String operation = request.getParameter("operation");
        String reporterId = request.getParameter("reporterId");

        HttpSession session = request.getSession(false);
        String readerId = (String) session.getAttribute("id");

        try{
            Integer result;
            String msg;
            if(operation.equals("follow")) {
                result = ServiceLocator.getReaderService().followReporter(readerId, reporterId);
                msg = result == 1 ?
                        "New following relationship registered in the system: %s -> %s" :
                        "Already existing relationship in the system: %s -> %s";
            }else{
                result = ServiceLocator.getReaderService().unfollowReporter(readerId, reporterId);
                msg = result == 1 ?
                        "Removed following relationship in the system: %s -> %s" :
                        "Not existing relationship in the system to be removed: %s -> %s";
            }
            LOGGER.info(String.format(msg, readerId, reporterId));
            response.getWriter().write("Operation success");
        } catch (SocialNewsServiceException ex) {
            String message = ex.getMessage();
            LOGGER.warning(String.format("Service error occurred: %s", message));
            PrintWriter writer = response.getWriter();
            writer.write(String.format("Operation failed: %s", message));
        } catch (Exception e) {
            String message = e.getMessage();
            LOGGER.warning(String.format("Unexpected error occurred: %s", message));
            PrintWriter writer = response.getWriter();
            writer.write(String.format("Operation failed: %s", message));
        }
    }
}
