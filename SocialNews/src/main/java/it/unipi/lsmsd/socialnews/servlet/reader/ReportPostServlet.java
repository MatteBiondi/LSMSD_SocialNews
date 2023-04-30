package it.unipi.lsmsd.socialnews.servlet.reader;

import it.unipi.lsmsd.socialnews.dto.ReportDTO;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

@WebServlet(name = "ReportPostServlet", value = "/reader/reportPost")
public class ReportPostServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ReportPostServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/reader/reportPost.jsp";

        request.getRequestDispatcher(targetJSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        // Take post id, reason and reporterId from request
        String postId = request.getParameter("postId");
        String reason = request.getParameter("reason");
        String reporterId = request.getParameter("reporterId");

        HttpSession session = request.getSession(false);
        String readerId = (String) session.getAttribute("id");

        if (postId != null && reason != null && !reason.trim().isEmpty()) {
            // Valid parameters, report post
            try{
                ReportDTO newReport = new ReportDTO(readerId, postId, reason);
                String reportId = ServiceLocator.getReaderService().publishReport(newReport, reporterId);
                LOGGER.info("New report registered in the system: " + reportId);
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
}
