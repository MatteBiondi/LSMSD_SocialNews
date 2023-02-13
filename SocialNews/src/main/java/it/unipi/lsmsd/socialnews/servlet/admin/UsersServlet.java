package it.unipi.lsmsd.socialnews.servlet.admin;

import it.unipi.lsmsd.socialnews.dto.*;
import it.unipi.lsmsd.socialnews.dto.util.JSONConverter;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "UsersServlet", value = "/admin/users")
public class UsersServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UsersServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String type = request.getParameter("type");
            String nextOffset = request.getParameter("nextOffset");
            String prevOffset = request.getParameter("prevOffset");
            String search = request.getParameter("search");

            String users;
            String nextPage = null;
            String prevPage = null;

            if(type.equals("readers")){
                List<ReaderDTO> readers;
                ReaderDTO readerOffset= new ReaderDTO();
                ReaderDTO readerFilter = new ReaderDTO();

                // Set filter
                if(search != null && !search.isEmpty()){
                   readerFilter.setEmail(search);
                }

                // Load readers
                if(nextOffset != null && !nextOffset.isEmpty()){
                    readerOffset.setId(nextOffset.split("&")[0]);
                    readerOffset.setFullName(nextOffset.split("&")[1]);
                    readers = ServiceLocator.getAdminService().nextPageReaders(readerFilter, readerOffset);
                }
                else if(prevOffset != null && !prevOffset.isEmpty()){
                    readerOffset.setId(prevOffset.split("&")[0]);
                    readerOffset.setFullName(prevOffset.split("&")[1]);
                    readers = ServiceLocator.getAdminService().prevPageReaders(readerFilter, readerOffset);
                }
                else {
                    readers = ServiceLocator.getAdminService().firstPageReaders(readerFilter);
                }

                if(readers.size() == 15){//TODO
                    nextPage = String.format("%s&%s", readers.get(readers.size()-1).getId(),
                            readers.get(readers.size()-1).getFullName());
                    readers.add(new ReaderDTO()); // Empty object, needed to inform about possible successive pages
                }
                if(readers.size() > 0) {
                    prevPage = String.format("%s&%s", readers.get(0).getId(), readers.get(0).getFullName());
                }

                users = JSONConverter.toJSONArray(readers);
            }
            else if(type.equals("reporters")) {
                List<ReporterDTO> reporters;
                ReporterDTO reporterOffset= new ReporterDTO();
                ReporterDTO reporterFilter = new ReporterDTO();

                // Set filter
                if(search != null && !search.isEmpty()){
                    reporterFilter.setEmail(search);
                }

                // Load reporters
                if(nextOffset != null && !nextOffset.isEmpty()){
                    reporterOffset.setId(nextOffset.split("&")[0]);
                    reporterOffset.setFullName(nextOffset.split("&")[1]);
                    reporters = ServiceLocator.getAdminService().nextPageReporters(reporterFilter, reporterOffset);
                }
                else if(prevOffset != null && !prevOffset.isEmpty()){
                    reporterOffset.setId(prevOffset.split("&")[0]);
                    reporterOffset.setFullName(prevOffset.split("&")[1]);
                    reporters = ServiceLocator.getAdminService().prevPageReporters(reporterFilter, reporterOffset);
                }
                else {
                    reporters = ServiceLocator.getAdminService().firstPageReporters(reporterFilter);
                }

                if(reporters.size() == 15){//TODO
                    nextPage = String.format("%s&%s", reporters.get(reporters.size()-1).getId(),
                            reporters.get(reporters.size()-1).getFullName());
                    reporters.add(new ReporterDTO()); // Empty object, needed to inform about possible successive pages
                }
                if(reporters.size() > 0) {
                    prevPage = String.format("%s&%s", reporters.get(0).getId(), reporters.get(0).getFullName());
                }

                users = JSONConverter.toJSONArray(reporters);
            }
            else {
                throw new RuntimeException();
            }
            /*response.setContentType("text/html");
            String targetJSP = "/pages/jsp/admin/users.jsp";

            request.setAttribute("users", users);
            request.setAttribute("nextOffset", nextPage);

            request.getRequestDispatcher(targetJSP).forward(request, response);*/
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                    "{\"users\":%s,\"prev\":\"%s\",\"next\":\"%s\"}",
                    users, prevPage, nextPage));
        } catch (SocialNewsServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        String userId = request.getParameter("userId");
        logger.info(type + " " + userId);
        try {
            if (type.equals("readers")){
                // ServiceLocator.getAdminService().removeReader(userId);
                logger.info("readers");
            }
            else if (type.equals("reporters")){
                // ServiceLocator.getAdminService().removeReporter(userId);
                logger.info("reporters");
            }
            else {
                throw new RuntimeException();
            }
            response.setContentType("application/json");
            response.getWriter().write("{\"result\":\"success\"}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
