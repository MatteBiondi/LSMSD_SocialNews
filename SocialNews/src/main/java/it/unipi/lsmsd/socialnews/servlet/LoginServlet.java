package it.unipi.lsmsd.socialnews.servlet;

import it.unipi.lsmsd.socialnews.dto.AdminDTO;
import it.unipi.lsmsd.socialnews.dto.ReaderDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * This class implement the login behind the user login and login page visualization
 */
@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get request are handled to let user see the login page in case of not logged-in user.
        // Otherwise, user is redirected to the homepage

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            // Not logged in user
            response.setContentType("text/html");
            String targetJSP = "pages/jsp/login.jsp";
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
            requestDispatcher.forward(request, response);
        }
        else{
            //Already logged user
            response.setContentType("text/html");
            String type = (String) session.getAttribute("userType");
            String page = switch (type) {
                case "reader" -> "/reader/homepage";
                case "reporter" -> "/reporter/homepage";
                default -> "/admin/homepage";
            };
            response.sendRedirect(request.getContextPath() + page);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Post request are handled to let user log-in the system.

        // Get user inserted credentials
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        // Type of access
        String accessType = request.getParameter("accessType");

        try {
            if(accessType.equals("reader")) {
                ReaderDTO readerDTO = ServiceLocator.getReaderService().authenticate(email, password);
                if(readerDTO!=null) {
                    // Credentials match
                    // Create a session
                    HttpSession session = request.getSession(true);
                    // In a session we save the user email
                    session.setAttribute("email", email);
                    session.setAttribute("userType", "reader");
                    session.setAttribute("id", readerDTO.getId());
                    session.setAttribute("fullName",readerDTO.getFullName());
                    // Set session to expire in 30 mins
                    session.setMaxInactiveInterval(30 * 60);

                    response.setContentType("text/html");
                    response.sendRedirect(request.getContextPath() + "/reader/homepage");
                    return;
                }
            }
            else if(accessType.equals("reporter")) {
                ReporterDTO reporterDTO = ServiceLocator.getReporterService().authenticate(email, password);
                if(reporterDTO!=null) {
                    // Credentials match
                    // Create a session
                    HttpSession session = request.getSession(true);
                    // In a session we save the user email
                    session.setAttribute("email", email);
                    session.setAttribute("userType", "reporter");
                    session.setAttribute("id", reporterDTO.getId());
                    // Set session to expire in 30 mins
                    session.setMaxInactiveInterval(30 * 60);

                    response.setContentType("text/html");
                    response.sendRedirect(request.getContextPath() + "/reporter/homepage");
                    return;
                }
            }
            else {
                AdminDTO adminDTO = ServiceLocator.getAdminService().authenticate(email, password);
                if(adminDTO!=null) {
                    // Credentials match
                    // Create a session
                    HttpSession session = request.getSession(true);
                    // In a session we save the user email
                    session.setAttribute("email", email);
                    session.setAttribute("userType", "admin");
                    session.setAttribute("id", adminDTO.getId());
                    // Set session to expire in 30 mins
                    session.setMaxInactiveInterval(30 * 60);

                    response.setContentType("text/html");
                    response.sendRedirect(request.getContextPath() + "/admin/homepage");
                    return;
                }
            }
        }
        catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).warning("Login error: " + e.getMessage());
        }

        // User not exist or incorrect credentials
        response.setContentType("text/html");
        String targetJSP ="/pages/jsp/login.jsp";
        request.setAttribute("message","Password or  email not valid");
        request.setAttribute("messageType","error-message");
        RequestDispatcher requestDispatcher=request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request,response);

    }
}