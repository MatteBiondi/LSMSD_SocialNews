package it.unipi.lsmsd.socialnews.servlet.reporter;

import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterPageDTO;
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
import java.util.*;
import java.util.logging.Logger;

@WebServlet(name = "ReporterHomepageServlet", value = "/reporter/homepage", loadOnStartup = 0)
public class HomepageServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(HomepageServlet.class.getName());

    // Number of post for each page
    private int pageLength;

    // Number of items in the pages menu
    private int navPageLen;

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
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Read interested values
        pageLength = Integer.parseInt(properties.getProperty("page_length", "10"));
        navPageLen = Integer.parseInt(properties.getProperty("nav_page_length", "5"));
        LOGGER.info("Init ReaderHomepageServlet");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        String id = (String) session.getAttribute("id");

        // Get ID, follower readers, full name, date of birth, location, cell number, email and posts.
        String reporterID = null;
        Integer followerReaders = null;
        String fullName = null;
        Date dateOfBirth = null;
        String location = null;
        String cell = null;
        String email = null;
        String picture = null;
        List<PostDTO> postsList = null;

        try {

            ReporterPageDTO reporterPageDTO = ServiceLocator.getReporterService().loadReporterPage(id);
            followerReaders = reporterPageDTO.getNumOfFollower();
            ReporterDTO reporterDTO = reporterPageDTO.getReporter();

            reporterID = reporterDTO.getId();
            fullName = reporterDTO.getFullName();
            dateOfBirth = reporterDTO.getDateOfBirth();
            location = reporterDTO.getLocation();
            cell = reporterDTO.getCell();
            email = reporterDTO.getEmail();
            picture = reporterDTO.getPicture();
            postsList = reporterPageDTO.getPosts();

        } catch (SocialNewsServiceException ex) {
            String message = ex.getMessage();
            LOGGER.warning(String.format("Service error occurred: %s", message));
            PrintWriter writer = response.getWriter();
            writer.write(String.format("%s", message));
        } catch (Exception e){
            String message = e.getMessage();
            LOGGER.warning(String.format("Unexpected error occurred: %s", message));
            PrintWriter writer = response.getWriter();
            writer.write(String.format("%s", message));
        }

        response.setContentType("text/html");
        request.setAttribute("reporterID",reporterID);
        request.setAttribute("followers",followerReaders);
        request.setAttribute("fullName", fullName);
        request.setAttribute("dateOfBirth", dateOfBirth);
        request.setAttribute("location",location);
        request.setAttribute("cell", cell);
        request.setAttribute("email", email);
        request.setAttribute("picture", picture);
        request.setAttribute("postsList",postsList);

        String targetJSP = "/pages/jsp/reporter/homepage.jsp";
        request.getRequestDispatcher(targetJSP).forward(request, response);
    }
}