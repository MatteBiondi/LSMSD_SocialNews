package it.unipi.lsmsd.socialnews.servlet.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "NewReporterServlet", value = "/admin/addReporter")
public class AddReporterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/admin/addReporter.jsp";
        request.getRequestDispatcher(targetJSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
