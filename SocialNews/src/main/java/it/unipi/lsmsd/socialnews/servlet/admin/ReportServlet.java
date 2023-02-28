package it.unipi.lsmsd.socialnews.servlet.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.dto.ReportDTO;
import it.unipi.lsmsd.socialnews.dto.util.JSONConverter;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ReportServlet", value = "/admin/report")
public class ReportServlet extends HttpServlet {
    private final ObjectNode success;
    private final ObjectNode error;

    public ReportServlet() {
        ObjectMapper mapper = new ObjectMapper();
        success = mapper.createObjectNode().put("result","success");
        error = mapper.createObjectNode().put("result","error");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        try{
            String type = request.getParameter("type");
            String targetReporterId = request.getParameter("reporterId");
            if(type == null || type.equals("report")){

                int offset = Integer.parseInt(request.getParameter("offset"));
                List<ReportDTO> reports;
                if(offset == 0){
                    reports= ServiceLocator.getAdminService().firstPageReports(targetReporterId);
                }
                else {
                    reports = ServiceLocator.getAdminService().nextPageReports(targetReporterId, offset);
                }
                response.getWriter().write(JSONConverter.toJSONArray(reports));
            }
            else {
                String targetPostId = request.getParameter("postId");
                PostDTO post = ServiceLocator.getAdminService().retrievePost(targetReporterId, targetPostId);
                response.getWriter().write(JSONConverter.toJSON(post));
            }
        }
        catch (SocialNewsServiceException ex){
            ex.printStackTrace();
            response.setStatus(500);
            response.getWriter().write(error.put("message", ex.getMessage()).toString());

        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException{
        try {
            String reportId = request.getParameter("reportId");
            if(reportId == null || reportId.isEmpty())
                throw new RuntimeException("Missing report id");

            ServiceLocator.getAdminService().removeReport(reportId);
            response.setContentType("application/json");
            response.getWriter().write(success.toString());
        } catch (SocialNewsServiceException | RuntimeException ex) {
            ex.printStackTrace();
            response.setStatus(500);
            response.getWriter().write(error.put("message", ex.getMessage()).toString());
        }
    }
}
