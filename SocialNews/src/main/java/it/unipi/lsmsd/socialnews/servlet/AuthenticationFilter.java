package it.unipi.lsmsd.socialnews.servlet;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * This class implements a Servlet Filter to check in advance if user is logged
 * in and if it is needed to be like so to access the requested URI.
 * In that case user will be redirected to login page if user is not logged in.
 */
@WebFilter(
        filterName = "AuthenticationFilter",
        urlPatterns = {"/*"},
        dispatcherTypes = DispatcherType.REQUEST,
        description = "Check if the user is logged or not. If not, redirect the user to login page"
)

public class AuthenticationFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());

    public void init(FilterConfig config) {
        LOGGER.info("AuthenticationFilter initialized");
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Each requested URI will be logged
        String uri = req.getRequestURI();

        // Get session. Don't create it if not exist
        HttpSession session = req.getSession(false);


        if(!uri.equals(req.getContextPath()+"/") && !uri.endsWith("login") && !uri.endsWith("signup") &&
                (session == null || session.getAttribute("email") == null) &&
                !(uri.contains("login.css") || uri.contains("signup.css") || uri.contains("index.css") || uri.contains("logo.svg")))
        {
            // This filter is applied to request user to perform login.
            // This filter is applied in the following cases:
            //  - User want open page different from login page (or its css and img resources) and
            //  - User want open page different from signup page (and its css and img resources) and
            //  - User is not logged in

            LOGGER.info("Unauthorized, access request");
            req.getSession().setAttribute("message", "Login is needed");
            req.getSession().setAttribute("messageType", "error-message");
            res.sendRedirect(req.getContextPath() + "/login");
        }else{
            // Pass the request along the filter chain
            chain.doFilter(request, response);
        }
    }
}