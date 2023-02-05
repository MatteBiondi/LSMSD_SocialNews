package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.dto.AdminDTO;
import it.unipi.lsmsd.socialnews.dto.ReaderDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.dto.StatisticPageDTO;
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
     * @return list of readerDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReaderDTO> firstPageReaders() throws SocialNewsServiceException;


    /**
     * Retrieves information about readers ordered by name starting from the offset passed as argument, up to a
     * configured number of readers
     *
     * @param readerOffset reader DTO containing id and fullName of the last reader in the previous page
     * @return list of readerDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReaderDTO> nextPageReaders(ReaderDTO readerOffset) throws SocialNewsServiceException;

    /**
     * Retrieves information about reporters ordered by name, up to a configured number of reporters
     *
     * @return list of reportersDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReporterDTO> firstPageReporters() throws SocialNewsServiceException;


    /**
     * Retrieves information about reporters ordered by name starting from the offset passed as argument, up to a
     * configured number of reporters
     *
     * @param reporterOffset reporter DTO containing reporterId and fullName of the last reporter in the previous page
     * @return list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReporterDTO> nextPageReporters(ReporterDTO reporterOffset) throws SocialNewsServiceException;

    //TODO: remove readers+comments and reporters+posts+comments

    /**
     * Remove a reader from the databases
     *
     * @param readerId id associated to the reader to remove
     * @throws SocialNewsServiceException in case of failure of the remove operation
     */
    void removeReader(String readerId) throws SocialNewsServiceException;

    /**
     * Remove a reporter from the databases
     *
     * @param reporterId id associated to the reporter to remove
     * @throws SocialNewsServiceException in case of failure of the remove operation
     */
    void removeReporter(String reporterId) throws SocialNewsServiceException;

    /**
     * Computes the statistics specified by arguments and pack them into a DTO containing the results
     *
     * @param statistics series of statistics that must be computed
     * @return statistic results grouped into a DTO objects
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    StatisticPageDTO computeStatistics(Statistic... statistics) throws SocialNewsServiceException;
}
