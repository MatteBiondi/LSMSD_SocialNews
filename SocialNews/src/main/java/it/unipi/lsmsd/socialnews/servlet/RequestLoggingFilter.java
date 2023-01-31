package it.unipi.lsmsd.socialnews.servlet;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * This class allow to create a log sentence for any received request.
 * This sentence contains any request parameter (but password in case of login)
 */
@WebFilter(
        filterName = "RequestLoggingFilter",
        urlPatterns = {"/*"},
        dispatcherTypes = DispatcherType.REQUEST,
        description = "Log all the request received"
)
public class RequestLoggingFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(RequestLoggingFilter.class.getName());

    public void init(FilterConfig config){
        LOGGER.info("RequestLoggingFilter initialized");
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        LOGGER.info("Request received");
        HttpServletRequest req = (HttpServletRequest) request;
        // Get and write all the request parameters
        Enumeration<String> params = req.getParameterNames();
        while(params.hasMoreElements()){
            String name = params.nextElement();
            String value = request.getParameter(name);

            if(!name.equals("password"))
                // Password are not stored in log for privacy reason
                LOGGER.info(String.format("%s::Request Params::{%s=%s}",req.getRemoteAddr(), name, value));
        }

        // Pass the request along the filter chain
        chain.doFilter(request, response);
    }
}