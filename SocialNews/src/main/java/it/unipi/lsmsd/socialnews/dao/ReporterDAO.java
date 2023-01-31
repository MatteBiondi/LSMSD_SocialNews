package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Post;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Reporter;
import java.util.List;

public interface ReporterDAO {
    String register(Reporter newReporter) throws SocialNewsDataAccessException;
    Reporter authenticate(String email, String password) throws SocialNewsDataAccessException;//TODO: include posts ?
    Reporter reporterByEmail(String email) throws SocialNewsDataAccessException;
    Reporter reporterByReporterId(String reporterId, Integer pageSize) throws SocialNewsDataAccessException;
    Reporter reporterByReporterId(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException;
    List<Reporter> reportersByFullName(String fullNamePattern, Integer pageSize) throws SocialNewsDataAccessException;
    List<Reporter> reportersByFullName(String fullNamePattern, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException;
    List<Reporter> allReporters(Integer pageSize) throws SocialNewsDataAccessException;
    List<Reporter> allReporters(Reporter offset, Integer pageSize) throws SocialNewsDataAccessException;
    Long removeReporter(String reporterId) throws SocialNewsDataAccessException;
}
