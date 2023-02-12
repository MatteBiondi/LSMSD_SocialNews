package it.unipi.lsmsd.socialnews.servlet;

import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(name = "ReaderStatisticsServlet", value = "/reader/statistics")
public class ReaderStatisticsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ReaderProfileServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/reader/statistics.jsp";
        String requestType = request.getParameter("search") == null ? "" : request.getParameter("search");

        if(requestType.equals("suggestedCard")) {
            targetJSP = "/pages/jsp/reader/reporterList.jsp";
            String id = (String) session.getAttribute("id");

            try {
                // Retrieve any user information from database
                List<ReporterDTO> suggested = ServiceLocator.getReaderService().readSuggestedReporters(id);
                request.setAttribute("reporterList", suggested);
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

        request.getRequestDispatcher(targetJSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
