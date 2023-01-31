package it.unipi.lsmsd.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.logging.Logger;

@WebServlet(name = "ReaderHomepageServlet", value = "/readerHomepage", loadOnStartup = 0)
public class ReaderHomepageServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ReaderHomepageServlet.class.getName());
}
