package it.unipi.lsmsd.socialnews.servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * This class implements a Servlet Filter to check in advance whether previously logged-in users are trying to access
 * web resources for which they do not have authorisation.
 * In that case user will be redirected to homepage.
 * Pay attention:
 * -This filter bases its logic on the existence of another filter that checks the user login execution
 * -Some URI can be accessed by any type of user
 * -Any URI that only the admin can access include "admin"
 * -Any URI that only the reader can access include "reader"
 * -Any URI that only the reporter can access include "reporter"
 */

@WebFilter(
        filterName = "UnauthorisedAccessFilter",
        urlPatterns = {"/*"},
        description = "Check if the logged user is trying to access " +
            "pages for which they do not have authorisation. " +
            "If yes, redirect the user to the relative homepage"
)
public class UnauthorisedAccessFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(UnauthorisedAccessFilter.class.getName());

    // Pages that are shared among any type of logged users
    private static final String[] SHARED_SERVLET = {"commenthandling", "logout", "posthandling", "reporterPage"};

    // Accessible resource extension for page content
    private static final String[] OK_EXTENSIONS = {".css", ".js", ".jpeg", ".svg"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Get current session
        HttpSession session = req.getSession(true);

        // Check if the user is logged
        boolean logged = session.getAttribute("email") != null;
        if(!logged){
            // User is not logged. Consult Authentication Filter or Login Servlet
            chain.doFilter(request, response);
            return;
        }

        // Check the type of the user
        String userType = (String) session.getAttribute("userType");

        // Get last part of URI
        String uri = req.getRequestURI();
        String resReq = uri.substring(uri.lastIndexOf('/') + 1);
        LOGGER.info("[Access user]: userType=>"+userType+"; ServletReq=>"+uri);

        // Evaluate the resource extension
        boolean validExtension = false;
        for(String ext : OK_EXTENSIONS){
            if(resReq.contains(ext)) {
                validExtension = true;
                break;
            }
        }

        if(Arrays.asList(SHARED_SERVLET).contains(resReq))
            // User is accessing the pages that are shared among any type of logged users
            chain.doFilter(request, response);
        else if(uri.contains("/"+userType+"/"))
            // User is accessing the pages that regards only its type of user
            chain.doFilter(request, response);
        else if(validExtension)
            // User is accessing resources with an authorized extension for page content
            chain.doFilter(request, response);
        else{
            LOGGER.info("User request redirected to "+userType+" homepage");
            res.sendRedirect(req.getContextPath() + "/"+userType+"/homepage");
        }
    }
}