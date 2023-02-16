package it.unipi.lsmsd.socialnews.servlet.admin;

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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = request.getParameter("data");
        if(data != null){
            response.setContentType("application/json");
            try {
                StatisticPageDTO statistics =
                        ServiceLocator.getAdminService().computeStatistics(
                                Statistic.getStatistic(GENDER_STATISTIC),
                                Statistic.getStatistic(NATIONALITY_STATISTIC),
                                Statistic.getStatistic(MOST_ACTIVE_READERS),
                                Statistic.getStatistic(HOTTEST_MOMENTS_OF_DAY)
                        );
                response.getWriter().write(JSONConverter.toJSON(statistics));
            } catch (SocialNewsServiceException e) {
                throw new RuntimeException(e);
            }

        }
        else {
            response.setContentType("text/html");
            String targetJSP = "/pages/jsp/admin/dashboard.jsp";
            request.getRequestDispatcher(targetJSP).forward(request, response);
        }
    }
}
