package it.unipi.lsmsd.socialnews.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This class is used to let user logout the system
 */
@WebServlet(name = "LogoutServlet", value = "/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Invalidate the session if exists
        HttpSession session = request.getSession(false);
        session.invalidate();
        request.logout();
        // Return success confirmation
        request.getSession().setAttribute("message", "Logout success");
        request.getSession().setAttribute("messageType", "success-message");
        response.sendRedirect(request.getContextPath() + "/login");
    }
}