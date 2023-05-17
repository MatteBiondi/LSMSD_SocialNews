package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Report;

import java.util.List;


public interface ReportDAO {
    /**
     * Inserts new report into the database
     *
     * @param report report object containing information of the new report
     * @param reporterId id of the reporter, owner of the interested post
     * @return identifier of created report
     * @throws SocialNewsDataAccessException in case of failure of the insert operation on database
     */
    String addReport(Report report, String reporterId) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about the reports associated with a reporter identified by 'reporterId' field
     *
     * @param reporterId id of the reporter
     * @param limit maximum number of reports to retrieve per request
     * @param offset report from which the query starts to retrieve information
     * @return list of reports objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Report> getReportsByReporterId(String reporterId, int limit, int offset)
            throws SocialNewsDataAccessException ;

    /**
     * Deletes a report, identified by 'reportId' field, from the database
     *
     * @param reportId id of the report to remove
     * @return number of reports deleted from the database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    Integer deleteReport(String reportId) throws SocialNewsDataAccessException;
}
