package it.unipi.lsmsd.socialnews.servlet;

import it.unipi.lsmsd.socialnews.dto.ReaderDTO;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This class allow the user to create its own profile and to see the signup form page.
 */
@WebServlet(name = "SignUpServlet", value = "/signup")
public class SignUpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get request are handled to let user see the signup page in case of not logged-in user.
        // Otherwise, user is redirected to the homepage

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            // User is not logged in
            response.setContentType("text/html");
            String resourceURL = "/pages/jsp/signup.jsp";
            RequestDispatcher rd = request.getRequestDispatcher(resourceURL);
            rd.forward(request, response);
        }
        else{
            // Already logged user
            response.setContentType("text/html");
            response.sendRedirect(request.getContextPath()+"/"+session.getAttribute("userType")+"Homepage");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Post request are handled to let user create its own profile in the system.

        // Get signup form data
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String surname = request.getParameter("surname");
        String password = request.getParameter("password");
        String country = request.getParameter("country");
        String gender = request.getParameter("gender");

        // Store the new user information into the database
        ReaderDTO new_user = new ReaderDTO();
        new_user.setPassword(password);
        new_user.setEmail(email);
        new_user.setGender(gender);
        new_user.setCountry(country);
        new_user.setFullName(name, surname);
        try {
            ServiceLocator.getReaderService().register(new_user);
            // Saving success. User is redirected to login page
            request.getSession().setAttribute("message", "User created");
            request.getSession().setAttribute("messageType", "success-message");
            response.setContentType("text/html");
            response.sendRedirect(request.getContextPath() + "/login");
        }
        catch (Exception e) {
            String resourceURL;
            // Saving failed
            response.setContentType("text/html");
            resourceURL = "/pages/jsp/signup.jsp";
            request.setAttribute("message", "User already exists");
            RequestDispatcher rd = request.getRequestDispatcher(resourceURL);
            rd.forward(request, response);
        }
    }
}