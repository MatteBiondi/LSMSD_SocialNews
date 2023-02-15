package it.unipi.lsmsd.socialnews.servlet.reader;

import it.unipi.lsmsd.socialnews.dto.ReaderDTO;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

@WebServlet(name = "ProfileServlet", value = "/reader/profile")
public class ProfileServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProfileServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        String email = (String) session.getAttribute("email");

        try{
            // Retrieve any user information from database
            ReaderDTO reader = ServiceLocator.getReaderService().readerInfo(email);

            // Set any information as parameter for JSP page
            request.setAttribute("fullName", reader.getFullName());
            request.setAttribute("email", email);
            request.setAttribute("gender", reader.getGender());
            request.setAttribute("country", reader.getCountry());
            // todo: is it needed?
            request.setAttribute("image", request.getContextPath() + "/images/user-avatar.svg");

            // todo: change password/delete profile?
        } catch(SocialNewsServiceException ex){
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
        String targetJSP = "/pages/jsp/reader/profile.jsp";
        request.getRequestDispatcher(targetJSP).forward(request, response);
    }
}
