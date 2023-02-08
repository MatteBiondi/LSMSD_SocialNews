package it.unipi.lsmsd.socialnews.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(name = "ReaderHomepageServlet", value = "/readerHomepage", loadOnStartup = 0)
public class ReaderHomepageServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ReaderHomepageServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/readerHomepage.jsp";
        request.getRequestDispatcher(targetJSP).forward(request, response);
    }
}
