package it.unipi.lsmsd.socialnews.servlet.reader;

import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.service.ServiceLocator;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(name = "SearchServlet", value = "/reader/search")
public class SearchServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(SearchServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String targetJSP = "/pages/jsp/reader/searchPage.jsp";
        String pageNumber = request.getParameter("page");

        if(pageNumber != null){
            String searchKey = request.getParameter("by");
            String searchValue = request.getParameter("value");
            String lastId = request.getParameter("lastId");
            String lastValue = request.getParameter("lastValue");
            String direction = request.getParameter("direction");

            try {
                if(searchKey.equals("Reporter Name")){
                    targetJSP = "/pages/jsp/reader/reporterList.jsp";
                    List<ReporterDTO> reporters;

                    // Retrieve reporters from database
                    if (lastId.equals("") && lastValue.equals(""))
                        reporters = ServiceLocator.getReaderService().firstPageReportersByFullName(searchValue);
                    else {
                        ReporterDTO lastReporter = new ReporterDTO();
                        lastReporter.setId(lastId);
                        lastReporter.setFullName(lastValue);
                        if(direction.equals("next"))
                            reporters = ServiceLocator.getReaderService().nextPageReportersByFullName(searchValue, lastReporter);
                        else
                            reporters = ServiceLocator.getReaderService().prevPageReportersByFullName(searchValue, lastReporter);

                    }
                    request.setAttribute("reporterList", reporters);

                } else if (searchKey.equals("Keyword")) {
                    targetJSP = "/pages/jsp/postList.jsp";
                    List<PostDTO> posts;
                    // Retrieve posts from database
                    if (lastId.equals("") && lastValue.equals(""))
                        posts = ServiceLocator.getPostService().firstPagePostsByHashtag(searchValue);
                    else {
                        PostDTO lastPost = new PostDTO();
                        lastPost.setId(lastId);
                        lastPost.setTimestamp(new Date(Long.parseLong(lastValue)));
                        if(direction.equals("next"))
                            posts = ServiceLocator.getPostService().nextPagePostsByHashtag(searchValue, lastPost);
                        else
                            posts = ServiceLocator.getPostService().prevPagePostsByHashtag(searchValue, lastPost);
                    }
                    request.setAttribute("postsList", posts);

                } else{
                    throw new ServletException("Unexpected search key");
                }
            } catch (SocialNewsServiceException ex) {
                String message = ex.getMessage();
                LOGGER.warning(String.format("Service error occurred: %s", message));
                PrintWriter writer = response.getWriter();
                writer.write(String.format("%s", message));
            } catch (Exception e) {
                String message = e.getMessage();
                LOGGER.warning(String.format("Unexpected error occurred: %s", message));
                PrintWriter writer = response.getWriter();
                writer.write(String.format("%s", message));
            }
        }

        request.getRequestDispatcher(targetJSP).forward(request, response);
    }
}