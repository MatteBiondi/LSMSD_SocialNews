package it.unipi.lsmsd.socialnews.service.util;

import it.unipi.lsmsd.socialnews.dao.model.BaseEntity;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Admin;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Reader;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Reporter;
import it.unipi.lsmsd.socialnews.dto.AdminDTO;
import it.unipi.lsmsd.socialnews.dto.BaseDTO;
import it.unipi.lsmsd.socialnews.dto.ReaderDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import org.modelmapper.ModelMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Util {

    private final static ModelMapper modelMapper = new ModelMapper();

    /**
     * Private constructor to prevent instantiation
     */
    private Util(){ }

    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        // Compute hash
        byte[] hashedPassword = MessageDigest
                .getInstance("SHA-256")//TODO: properties
                .digest(password.getBytes(StandardCharsets.UTF_8));

        // Convert bytes to hexadecimal string
        StringBuilder hashedHexPassword = new StringBuilder();
        for(byte b: hashedPassword)
            hashedHexPassword.append(String.format("%1$02x",0xFF & b));

        return hashedHexPassword.toString();
    }

    private static BaseDTO buildDTO(BaseEntity source, Class<?> clazz){
        return (BaseDTO) modelMapper.map(source, clazz);
    }

    private static BaseEntity buildEntity(BaseDTO source, Class<?> clazz){
        return (BaseEntity) modelMapper.map(source, clazz);
    }

    public static AdminDTO buildAdminDTO(Admin source){
        return (AdminDTO) buildDTO(source, AdminDTO.class);
    }

    public static ReaderDTO buildReaderDTO(Reader source){
        return (ReaderDTO) buildDTO(source, ReaderDTO.class);
    }

    public static ReporterDTO buildReporterDTO(Reporter source) {
        return (ReporterDTO) buildDTO(source, ReporterDTO.class);
    }

    public static Admin buildAdmin(AdminDTO source){
        return (Admin) buildEntity(source, Admin.class);
    }

    public static Reader buildReader(ReaderDTO source){
        return (Reader) buildEntity(source, Reader.class);
    }

    public static Reporter buildReporter(ReporterDTO source){
        return (Reporter) buildEntity(source, Reporter.class);
    }
}
