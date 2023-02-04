package it.unipi.lsmsd.socialnews.dao.implement;

import it.unipi.lsmsd.socialnews.dao.ReportDAO;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Report;
import it.unipi.lsmsd.socialnews.dao.neo4j.ReportNeo4jDAO;

import java.util.List;

public class ReportDAOImpl implements ReportDAO {

    private final ReportNeo4jDAO reportNeo4jDAO;

    public ReportDAOImpl(){
        reportNeo4jDAO = new ReportNeo4jDAO();
    }

    /**
     * Inserts new report into the database
     *
     * @param report report object containing information of the new report
     * @throws SocialNewsDataAccessException in case of failure of the insert operation on database
     */
    @Override
    public void addReport(Report report) throws SocialNewsDataAccessException {
        reportNeo4jDAO.addReport(report);
    }

    /**
     * Retrieves information about the report identified by 'reportId' field
     *
     * @param reportId id of the report
     * @return report object containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public Report getReportById(Long reportId) throws SocialNewsDataAccessException {
        return reportNeo4jDAO.getReportById(reportId);
    }

    /**
     * Retrieves information about the reports associated with a reporter identified by 'reporterId' field
     *
     * @param reporterId id of the reporter
     * @param limit maximum number of reports to retrieve per request
     * @param offset report from which the query starts to retrieve information
     * @return list of reports objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Report> getReportsByReporterId(String reporterId, int limit, int offset) throws SocialNewsDataAccessException {
        return reportNeo4jDAO.getReportsByReporterId(reporterId, limit, offset);
    }

    /**
     * Deletes a report, identified by 'reportId' field, from the database
     *
     * @param reportId id of the report to remove
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    @Override
    public void deleteReport(Long reportId) throws SocialNewsDataAccessException {
        reportNeo4jDAO.deleteReport(reportId);
    }
}
