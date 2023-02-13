package it.unipi.lsmsd.socialnews.servlet.reader;

import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

@WebServlet(name = "HomepageServlet", value = "/reader/homepage", loadOnStartup = 0)
public class HomepageServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(HomepageServlet.class.getName());

    // Number of reporter for each page
    private int pageLength;

    @Override
    public void init(){
        InputStream input = null;
        Properties properties = new Properties();
        try {
            // Load servlet properties. See in "src/main/resources/servlet.properties"
            input = this.getClass().getClassLoader().getResourceAsStream("servlet.properties");
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Read interested values
        pageLength = Integer.parseInt(properties.getProperty("page_length", "25"));

        LOGGER.info("Init reader HomepageServlet");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/reader/homepage.jsp";

        String requestType = request.getParameter("search") == null ? "" : request.getParameter("search");

        if(requestType.equals("followedCard")) {
            targetJSP = "/pages/jsp/reader/reporterList.jsp";

            // Get followed reporter
            String id = (String) session.getAttribute("id");
            List<ReporterDTO> followedReporter;
            int pageNumber;

            try {
                if (request.getParameter("page") == null)
                    pageNumber = 1;
                else
                    pageNumber = Integer.parseInt(request.getParameter("page"));

                if (pageNumber > 1)
                    followedReporter = ServiceLocator.getReaderService()
                            .nextPageFollowing(id, pageLength * (pageNumber - 1));
                else
                    followedReporter = ServiceLocator.getReaderService().firstPageFollowing(id);

                request.setAttribute("reporterList", followedReporter);
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
}