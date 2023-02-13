<%@ page import="it.unipi.lsmsd.socialnews.service.ServiceLocator" %>
<%@ page import="it.unipi.lsmsd.socialnews.service.PostService" %>
<%@ page import="it.unipi.lsmsd.socialnews.dto.PostDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.ArrayList" %>
<%
  String reporterID = request.getParameter("reporterID");
  String text = request.getParameter("text");
  String hashtags = request.getParameter("hashtags");
  List<String> hashtagsList = new ArrayList<>();
  if (hashtags != null && !hashtags.isEmpty()) {
    hashtagsList = Arrays.asList(hashtags.split(" "));
  }
  String links = request.getParameter("links");
  List<String> linksList = new ArrayList<>();
  if (links != null && !links.isEmpty()) {
    linksList = Arrays.asList(links.split(" "));
  }
  /*PostDTO newPost = new PostDTO(reporterID, text, linksList, hashtagsList);

  PostService postService = ServiceLocator.getPostService();
  String postID = postService.publishPost(newPost);*/
%>