package it.unipi.lsmsd.socialnews.servlet.reporter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ReporterStatisticsServlet", value = "/reporter/statistics")
public class StatisticsServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/reporter/statistics.jsp";
        request.getRequestDispatcher(targetJSP).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json");
            String jsonRequestBody = request.getReader().readLine();
            JsonNode params = mapper.readTree(jsonRequestBody);
            System.out.println(params);

            HttpSession session = request.getSession(false);
            String reporterId = (String) session.getAttribute("id");


            if(params.get("statistic") == null) {
                Integer defaultWindowSize = 3;
                Integer defaultLastN = 10;
                TemporalUnit defaultUnitOfTime = ChronoUnit.YEARS;
                ArrayNode statisticMomentsList = ServiceLocator.getReporterService().hottestMomentsOfDay(defaultWindowSize, defaultLastN, defaultUnitOfTime);
                List<PostDTO> statisticPostsList = ServiceLocator.getReporterService().latestHottestPost(reporterId, defaultLastN, defaultUnitOfTime);

                ObjectMapper mapper = new ObjectMapper();

                Map<String, Object> map = new HashMap<>();
                map.put("hottestMomentsOfDay", statisticMomentsList);
                map.put("hottestPosts", mapper.valueToTree(statisticPostsList));
                String statisticJson = mapper.writeValueAsString(map);
                response.getWriter().write(statisticJson);
            }
            else {
                String statisticName = params.get("statistic").asText();
                Integer lastN = params.get("lastN").asInt();
                TemporalUnit unitOfTime = parseTemporalUnit(params.get("unitOfTime"));
                switch (statisticName) {
                    case "hottestMomentsOfDay" -> {
                        Integer windowSize = params.get("windowSize").asInt();
                        ArrayNode statisticMomentsList = ServiceLocator.getReporterService().hottestMomentsOfDay(windowSize, lastN, unitOfTime);

                        Map<String, Object> map = new HashMap<>();
                        map.put("hottestMomentsOfDay",statisticMomentsList);
                        String statisticJson = mapper.writeValueAsString(map);
                        response.getWriter().write(statisticJson);
                    }
                    case "hottestPosts" -> {
                        List<PostDTO> statisticPostsList = ServiceLocator.getReporterService().latestHottestPost(reporterId, lastN, unitOfTime);

                        Map<String, Object> map = new HashMap<>();
                        map.put("hottestPosts", mapper.valueToTree(statisticPostsList));
                        String statisticJson = mapper.writeValueAsString(map);
                        response.getWriter().write(statisticJson);
                    }
                }
            }
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

    private TemporalUnit parseTemporalUnit(JsonNode node) {
        String unitString = node.asText();
        return switch (unitString) {
            case "Hour" -> ChronoUnit.HOURS;
            case "Day" -> ChronoUnit.DAYS;
            case "Week" -> ChronoUnit.WEEKS;
            case "Month" -> ChronoUnit.MONTHS;
            case "Year" -> ChronoUnit.YEARS;
            default -> throw new IllegalArgumentException("Invalid unit string: " + unitString);
        };
    }
}
