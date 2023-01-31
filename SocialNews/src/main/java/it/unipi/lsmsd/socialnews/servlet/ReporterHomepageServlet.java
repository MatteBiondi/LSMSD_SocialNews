package it.unipi.lsmsd.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.logging.Logger;

@WebServlet(name = "ReporterHomepageServlet", value = "/reporterHomepage", loadOnStartup = 0)
public class ReporterHomepageServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ReporterHomepageServlet.class.getName());
}