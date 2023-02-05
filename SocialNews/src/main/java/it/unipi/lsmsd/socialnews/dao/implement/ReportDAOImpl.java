package it.unipi.lsmsd.socialnews.dao.implement;

import it.unipi.lsmsd.socialnews.dao.ReportDAO;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Report;
import it.unipi.lsmsd.socialnews.dao.neo4j.Neo4jReportDAO;

import java.util.List;

public class ReportDAOImpl implements ReportDAO {

    private final Neo4jReportDAO neo4JReportDAO;

    public ReportDAOImpl(){
        neo4JReportDAO = new Neo4jReportDAO();
    }

    /**
     * Inserts new report into the database
     *
     * @param report report object containing information of the new report
     * @return number of created report
     * @throws SocialNewsDataAccessException in case of failure of the insert operation on database
     */
    @Override
    public int addReport(Report report) throws SocialNewsDataAccessException {
        return neo4JReportDAO.addReport(report);
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
        return neo4JReportDAO.getReportById(reportId);
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
        return neo4JReportDAO.getReportsByReporterId(reporterId, limit, offset);
    }

    /**
     * Deletes a report, identified by 'reportId' field, from the database
     *
     * @param reportId id of the report to remove
     * @return number of reports deleted from the database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    @Override
    public int deleteReport(Long reportId) throws SocialNewsDataAccessException {
        return neo4JReportDAO.deleteReport(reportId);
    }
}
