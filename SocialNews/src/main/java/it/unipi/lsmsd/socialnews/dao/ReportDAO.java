package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Report;

import java.util.List;


public interface ReportDAO {
    /**
     * Inserts new report into the database
     *
     * @param report report object containing information of the new report
     * @return number of created report
     * @throws SocialNewsDataAccessException in case of failure of the insert operation on database
     */
    Integer addReport(Report report) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about the report identified by 'reportId' field
     *
     * @param reportId id of the report
     * @return report object containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    Report getReportById(Long reportId) throws SocialNewsDataAccessException;

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
    Integer deleteReport(Long reportId) throws SocialNewsDataAccessException;
}
