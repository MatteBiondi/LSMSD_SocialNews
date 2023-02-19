package it.unipi.lsmsd.socialnews.servlet.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unipi.lsmsd.socialnews.dto.StatisticPageDTO;
import it.unipi.lsmsd.socialnews.dto.util.JSONConverter;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.Statistic;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

import static it.unipi.lsmsd.socialnews.service.util.Statistic.*;

@WebServlet(name = "DashboardServlet", value = "/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/admin/dashboard.jsp";
        request.getRequestDispatcher(targetJSP).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json");
            String jsonRequestBody = request.getReader().readLine();
            JsonNode params = mapper.readTree(jsonRequestBody);

            StatisticPageDTO statistics;

            if(params.get("statistic") == null){
                statistics =
                        ServiceLocator.getAdminService().computeStatistics(
                                Statistic.getStatistic(GENDER_STATISTIC),
                                Statistic.getStatistic(NATIONALITY_STATISTIC),
                                Statistic.getStatistic(MOST_ACTIVE_READERS),
                                Statistic.getStatistic(MOST_POPULAR_REPORTERS)
                        );
            }
            else{
                String statisticName = params.get("statistic").asText();
                statistics = switch (Statistic.fromString(statisticName)){
                    case GENDER_STATISTIC -> ServiceLocator
                            .getAdminService().computeStatistics(Statistic.getStatistic(GENDER_STATISTIC));
                    case NATIONALITY_STATISTIC -> ServiceLocator
                            .getAdminService().computeStatistics(Statistic.getStatistic(NATIONALITY_STATISTIC));
                    case MOST_ACTIVE_READERS -> ServiceLocator
                            .getAdminService().computeStatistics(Statistic.getStatistic(
                                    MOST_ACTIVE_READERS,
                                    params.get("lastN").asInt(),
                                    params.get("unitOfTime").asText()
                            ));
                    case MOST_POPULAR_REPORTERS -> ServiceLocator
                            .getAdminService().computeStatistics(Statistic.getStatistic(
                                    MOST_POPULAR_REPORTERS));
                };
            }
            response.getWriter().write(JSONConverter.toJSON(statistics));
        } catch (SocialNewsServiceException ex) {
            ex.printStackTrace();
            response.setStatus(500);
            response.getWriter().write(mapper.createObjectNode()
                    .put("result","error")
                    .put("message",ex.getMessage()).toString());
        }
        catch (Exception ex){
            ex.printStackTrace();
            response.setStatus(500);
            response.getWriter().write(mapper.createObjectNode()
                    .put("result","error")
                    .put("message","Server error").toString());
        }
    }
}
