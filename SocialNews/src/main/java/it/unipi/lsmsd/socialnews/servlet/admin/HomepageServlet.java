package it.unipi.lsmsd.socialnews.servlet.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "AdminHomepageServlet", value = "/admin")
public class HomepageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/admin/homepage.jsp";
        request.getRequestDispatcher(targetJSP).forward(request, response);
    }
}
