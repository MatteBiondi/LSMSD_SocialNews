package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.dto.*;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.Statistic;

import java.util.List;

public interface AdminService {

    /**
     *  Registers a new reporter in the application, storing the information into database
     *
     * @param newReporter reporter DTO object containing information of the new reporter
     * @return identifier assigned to the new reporter
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    String registerReporter(ReporterDTO newReporter) throws SocialNewsServiceException;

    /**
     * Authenticates an admin identified by email via secret password
     * @param email email of the admin
     * @param password cleartext secret password of the admin
     * @return if authentication succeed adminDTO object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    AdminDTO authenticate(String email, String password) throws SocialNewsServiceException;

    /**
     * Retrieves information about readers ordered by name, up to a configured number of readers
     *
     * @param readerFilter readerDTO object containing email pattern used to filter readers
     * @return list of readerDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReaderDTO> firstPageReaders(ReaderDTO readerFilter) throws SocialNewsServiceException;

    /**
     * Retrieves information about readers ordered by name starting from the offset passed as argument, up to a
     * configured number of readers in revers order
     *
     * @param readerFilter readerDTO object containing email pattern used to filter readers
     * @param readerOffset reader DTO containing id and fullName of the first reader in the current page
     * @return list of readerDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReaderDTO> prevPageReaders(ReaderDTO readerFilter, ReaderDTO readerOffset) throws SocialNewsServiceException;

    /**
     * Retrieves information about readers ordered by name starting from the offset passed as argument, up to a
     * configured number of readers
     *
     * @param readerFilter readerDTO object containing email pattern used to filter readers
     * @param readerOffset reader DTO containing id and fullName of the last reader in the current page
     * @return list of readerDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReaderDTO> nextPageReaders(ReaderDTO readerFilter, ReaderDTO readerOffset) throws SocialNewsServiceException;

    /**
     * Retrieves information about reporters ordered by name, up to a configured number of reporters
     *
     * @param reporterFilter reporterDTO object containing email pattern used to filter readers
     * @return list of reportersDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReporterDTO> firstPageReporters(ReporterDTO reporterFilter) throws SocialNewsServiceException;

    /**
     * Retrieves information about reporters ordered by name starting from the offset passed as argument, up to a
     * configured number of reporters in reverse order
     *
     * @param reporterFilter reporterDTO object containing email pattern used to filter readers
     * @param reporterOffset reporter DTO containing reporterId and fullName of the first reporter in the current page
     * @return list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReporterDTO> prevPageReporters(ReporterDTO reporterFilter, ReporterDTO reporterOffset) throws SocialNewsServiceException;

    /**
     * Retrieves information about reporters ordered by name starting from the offset passed as argument, up to a
     * configured number of reporters
     *
     * @param reporterFilter reporterDTO object containing email pattern used to filter readers
     * @param reporterOffset reporter DTO containing reporterId and fullName of the last reporter in the current page
     * @return list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReporterDTO> nextPageReporters(ReporterDTO reporterFilter, ReporterDTO reporterOffset) throws SocialNewsServiceException;

    /**
     * Retrieves information about report, ordered by id, associated to a reporter, up to a configured number of report
     *
     * @param reporterId id of the reporter for which retrieve associated reports
     * @return list of reportDTO objects containing all the information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReportDTO> firstPageReports(String reporterId) throws SocialNewsServiceException;


    /**
     * Retrieves information about reports (ordered by id starting), from the offset passed as argument, of a reporter,
     * up to a configured number of reports
     *
     * @param reporterId id of the reporter for which retrieve associated reports
     * @param reportOffset integer containing the number of the last report in the previous page with respect the total
     *                     number of results
     * @return list of reportDTO objects containing all the information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReportDTO> nextPageReports(String reporterId, Integer reportOffset) throws SocialNewsServiceException;

    /**
     * Retrieves a post identified by its id
     *
     * @param reporterId reporter
     * @param postId post identifier used to filter the posts
     * @return post object containing all the information
     * @throws SocialNewsServiceException in case of failure of the query operation on database or if the post is
     * not in the system
     */
    PostDTO retrievePost(String reporterId, String postId) throws SocialNewsServiceException;

    /**
     * Removes a reader from the system, deleting all the information stored into the database, including the
     * comments published by the reader
     *
     * @param toRemoveReaderId reader identifier
     * @throws SocialNewsServiceException in case of failure of the operation or if the reader is not present in the
     *                                    system
     */
    void removeReader(String toRemoveReaderId) throws SocialNewsServiceException;

    /**
     * Removes a reporter from the system, deleting all the information stored into the database, including the post
     * published by the reporter and the associated comments
     *
     * @param toRemoveReporterId reporter identifier
     * @throws SocialNewsServiceException in case of failure of the operation or if the reporter is not present in the
     *                                    system
     */
    void removeReporter(String toRemoveReporterId) throws SocialNewsServiceException;

    /**
     * Remove a report from the database
     *
     * @param reportId id associated to the report to remove
     * @throws SocialNewsServiceException in case of failure of the remove operation
     */
    void removeReport(String reportId) throws SocialNewsServiceException;

    /**
     * Computes the statistics specified by arguments and pack them into a DTO containing the results
     *
     * @param statistics series of statistics that must be computed
     * @return statistic results grouped into a DTO objects
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    StatisticPageDTO computeStatistics(Statistic... statistics) throws SocialNewsServiceException;
}
