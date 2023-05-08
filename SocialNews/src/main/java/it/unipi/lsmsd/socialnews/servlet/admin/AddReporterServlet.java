package it.unipi.lsmsd.socialnews.servlet.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.dto.util.JSONConverter;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "NewReporterServlet", value = "/admin/addReporter")
public class AddReporterServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AddReporterServlet.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectNode success;

    public AddReporterServlet() {
        this.success = mapper.createObjectNode().put("result","success");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/admin/addReporter.jsp";
        request.getRequestDispatcher(targetJSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            String jsonReporter = request.getReader().readLine();
            ReporterDTO reporter = JSONConverter.ReporterDTOFromJSON(jsonReporter);
            String reporterId = ServiceLocator.getAdminService().registerReporter(reporter);
            logger.info("New reporter registered in the system: " + reporterId);
            response.getWriter().write(success.toString());
        }
        catch (Exception ex){
            ex.printStackTrace();
            response.setStatus(500);
            response.getWriter().write(mapper.createObjectNode()
                    .put("result","error")
                    .put("message",ex.getMessage())
                    .toString()
            );
        }
    }
}
