package it.unipi.lsmsd.socialnews.servlet.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unipi.lsmsd.socialnews.dto.*;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet(name = "UsersServlet", value = "/admin/users/*")
public class UsersServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UsersServlet.class);
    private static Integer pageSize;
    private final ObjectMapper mapper;
    private final ObjectNode success;
    private final ObjectNode error;

    public static void setPageSize(Integer pageSize){
        UsersServlet.pageSize = pageSize;
    }

    public UsersServlet() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setDateFormat(df);
        success = mapper.createObjectNode().put("result","success");
        error = mapper.createObjectNode().put("result","error");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = request.getParameter("data");
        String type = request.getParameter("type");
        String nextOffset = request.getParameter("nextOffset");
        String prevOffset = request.getParameter("prevOffset");
        String search = request.getParameter("search");

        try {
            List<?> users;
            String nextPage = null;
            String prevPage = null;


            if(data == null){ // Load page structure
                String targetJSP = "/pages/jsp/admin/users.jsp";
                if(type == null || type .isEmpty())
                    throw new RuntimeException("Invalid query string");

                request.setAttribute("pageSize", pageSize);
                response.setContentType("text/html");
                request.getRequestDispatcher(targetJSP).forward(request, response);
            }
            else { // Load data
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

                    // Pagination offsets
                    if(readers.size() == pageSize){
                        nextPage = String.format("%s&%s", readers.get(readers.size()-1).getId(),
                                readers.get(readers.size()-1).getFullName());
                        readers.add(new ReaderDTO()); // Empty object, needed to inform about possible successive pages
                    }
                    if(readers.size() > 0) {
                        prevPage = String.format("%s&%s", readers.get(0).getId(), readers.get(0).getFullName());
                    }

                    users = readers;
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

                    // Pagination offsets
                    if(reporters.size() == pageSize){
                        nextPage = String.format("%s&%s", reporters.get(reporters.size()-1).getId(),
                                reporters.get(reporters.size()-1).getFullName());
                        reporters.add(new ReporterDTO()); // Empty object, needed to inform about possible successive pages
                    }
                    if(reporters.size() > 0) {
                        prevPage = String.format("%s&%s", reporters.get(0).getId(), reporters.get(0).getFullName());
                    }

                    users = reporters;
                }
                else {
                    throw new RuntimeException("Invalid query string");
                }

                // Build JSON response
                ArrayNode nodes = mapper.valueToTree(users);
                ObjectNode jsonResponse = mapper.createObjectNode()
                        .put("prev", prevPage)
                        .put("next", nextPage);
                jsonResponse.putArray("users").addAll(nodes);

                response.setContentType("application/json");
                response.getWriter().write(new String(jsonResponse.toString().getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.ISO_8859_1));
            }
        } catch (SocialNewsServiceException ex) {
            response.setContentType("application/json");
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String type = request.getParameter("type");
        String userId = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);

        try {
            if (type.equals("readers")){
                ServiceLocator.getAdminService().removeReader(userId);
                logger.info(userId);
            }
            else if (type.equals("reporters")){
                ServiceLocator.getAdminService().removeReporter(userId);
                logger.info(userId);
            }
            else {
                throw new RuntimeException("Invalid query string");
            }
            response.setContentType("application/json");
            response.getWriter().write(success.toString());
        } catch (SocialNewsServiceException | RuntimeException ex) {
            ex.printStackTrace();
            response.setStatus(500);
            response.getWriter().write(error.toString());
        }
    }
}
