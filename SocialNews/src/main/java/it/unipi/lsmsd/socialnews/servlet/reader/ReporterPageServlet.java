package it.unipi.lsmsd.socialnews.servlet.reader;

import it.unipi.lsmsd.socialnews.dto.ReporterPageDTO;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

@WebServlet(name = "ReporterPageServlet", value = "/reader/reporterPage")
public class ReporterPageServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ReporterPageServlet.class.getName());


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/reporter/homepage.jsp";
        // Retrieve reporter Id
        String reporterId = request.getParameter("id");
        // Retrieve reporter info
        try{
            if(reporterId == null)
                throw new ServletException("Missing reporter id error");

            ReporterPageDTO reporter = ServiceLocator.getReporterService().loadReporterPage(reporterId);
            request.setAttribute("reporter", reporter);
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

        request.getRequestDispatcher(targetJSP).forward(request, response);
    }
}
