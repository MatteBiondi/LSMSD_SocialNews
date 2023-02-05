package it.unipi.lsmsd.socialnews.service.implement;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Admin;
import it.unipi.lsmsd.socialnews.dao.model.Reader;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import it.unipi.lsmsd.socialnews.dto.AdminDTO;
import it.unipi.lsmsd.socialnews.dto.ReaderDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.dto.StatisticPageDTO;
import it.unipi.lsmsd.socialnews.service.AdminService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.Statistic;
import it.unipi.lsmsd.socialnews.service.util.Util;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class AdminServiceImpl implements AdminService {

    /**
     * Registers a new reporter in the application, storing the information on database
     *
     * @param newReporter reporter DTO object containing information of the new reporter
     * @return identifier assigned to the new reporter
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public String registerReporter(ReporterDTO newReporter) throws SocialNewsServiceException {
        try {
            newReporter.setPassword(Util.hashPassword(newReporter.getPassword()));
            return DAOLocator.getReporterDAO().register(Util.buildReporter(newReporter));
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Configuration error: hash algorithm");
        }
    }

    /**
     * Authenticates an admin identified by email via secret password
     *
     * @param email    email of the admin
     * @param password cleartext secret password of the admin
     * @return if authentication succeed adminDTO object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public AdminDTO authenticate(String email, String password) throws SocialNewsServiceException {
        try {
            Admin admin = DAOLocator.getAdminDAO().authenticate(email, Util.hashPassword(password));
            System.out.println(admin);
            return Util.buildAdminDTO(admin);
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
        catch (NoSuchAlgorithmException ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("Configuration error: hash algorithm");
        }
    }

    /**
     * Retrieves information about readers ordered by name, up to a configured number of readers
     *
     * @return list of readerDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReaderDTO> firstPageReaders() throws SocialNewsServiceException {
        return nextPageReaders(null);
    }

    /**
     * Retrieves information about readers ordered by name starting from the offset passed as argument, up to a
     * configured number of readers
     *
     * @param readerOffset reader DTO containing id and fullName of the last reader in the previous page
     * @return list of readerDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReaderDTO> nextPageReaders(ReaderDTO readerOffset) throws SocialNewsServiceException {
        try {
            Reader offset = readerOffset == null ? null:Util.buildReader(readerOffset);
            List<ReaderDTO> firstPageReaderDTO = new ArrayList<>();
            DAOLocator.getReaderDAO()
                    .allReaders(offset, Util.getIntProperty("listUserPageSize",50))
                    .forEach(reader -> firstPageReaderDTO.add(Util.buildReaderDTO(reader)));
            return firstPageReaderDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    /**
     * Retrieves information about reporters ordered by name, up to a configured number of reporters
     *
     * @return list of reportersDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReporterDTO> firstPageReporters() throws SocialNewsServiceException {
        return nextPageReporters(null);
    }

    /**
     * Retrieves information about reporters ordered by name starting from the offset passed as argument, up to a
     * configured number of reporters
     *
     * @param reporterOffset reporter DTO containing reporterId and fullName of the last reporter in the previous page
     * @return list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReporterDTO> nextPageReporters(ReporterDTO reporterOffset) throws SocialNewsServiceException {
        try {
            Reporter offset = reporterOffset == null ? null:Util.buildReporter(reporterOffset);
            List<ReporterDTO> firstPageReporterDTO = new ArrayList<>();
            DAOLocator.getReporterDAO()
                    .allReporters(offset, Util.getIntProperty("listUserPageSize",50))
                    .forEach(reporter -> firstPageReporterDTO.add(Util.buildReporterDTO(reporter)));
            return firstPageReporterDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    /**
     * Remove a reader from the databases
     *
     * @param readerId id associated to the reader to remove
     * @throws SocialNewsServiceException in case of failure of the remove operation
     */
    @Override
    public void removeReader(String readerId) throws SocialNewsServiceException {
        throw new RuntimeException("Not yet implemented");//TODO
    }


    /**
     * Remove a reporter from the databases
     *
     * @param reporterId id associated to the reporter to remove
     * @throws SocialNewsServiceException in case of failure of the remove operation
     */
    @Override
    public void removeReporter(String reporterId) throws SocialNewsServiceException {
        throw new RuntimeException("Not yet implemented");//TODO
    }

    /**
     * Computes the statistics specified by arguments and pack them into a DTO containing the results
     *
     * @param statistics series of statistics that must be computed
     * @return statistic results grouped into a DTO objects
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public StatisticPageDTO computeStatistics(Statistic... statistics) throws SocialNewsServiceException {
        return null; //TODO
    }
}
