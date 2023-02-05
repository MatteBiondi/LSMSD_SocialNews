package it.unipi.lsmsd.socialnews.service.util;

import it.unipi.lsmsd.socialnews.dao.model.*;
import it.unipi.lsmsd.socialnews.dto.*;
import org.modelmapper.ModelMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class Util {

    private final static ModelMapper modelMapper = new ModelMapper();
    private static Properties properties;

    static {// Customize conversion Entity-DTO and vice versa

        // From Reporter entity to DTO
        modelMapper.createTypeMap(Reporter.class, ReporterDTO.class)
                .addMapping(Reporter::getReporterId, ReporterDTO::setId);

        // From Reader DTO to entity
        modelMapper.createTypeMap(ReaderDTO.class, Reader.class)
                .addMappings(mapper -> mapper.skip(Reader::setId));

        // From Reporter DTO to entity
        modelMapper.createTypeMap(ReporterDTO.class, Reporter.class)
               .addMappings(mapper -> mapper.skip(Reporter::setId))
               .addMappings(mapper -> mapper.skip(Reporter::setReporterId));

        // From Post DTO to entity
        modelMapper.createTypeMap(PostDTO.class, Post.class)
                .addMappings(mapper -> mapper.skip(Post::setId));

        // From Comment DTO to entity
        modelMapper.createTypeMap(CommentDTO.class, Comment.class)
                .addMappings(mapper -> mapper.skip(Comment::setId));

    }

    /**
     * Private constructor to prevent instantiation
     */
    private Util(){ }

    public static void configure(Properties properties){
        if(Util.properties == null)
            Util.properties = properties;
        else
            throw new RuntimeException("Configuration of services failed, it has already been set up");
    }

    public static String getProperty(String key){
        return getProperty(key, null);
    }

    public static String getProperty(String key, String defaultValue){
        if(properties != null && defaultValue != null){
            return properties.getProperty(key, defaultValue);
        }
        else if(properties != null && properties.contains(key)){
            return properties.getProperty(key);
        }
        else{
            throw new RuntimeException("Services configuration not yet available");
        }
    }

    public static Integer getIntProperty(String key, Integer defaultValue){
        return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
    }

    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        // Compute hash
        byte[] hashedPassword = MessageDigest
                .getInstance(getProperty("hashAlgorithm","SHA-256"))
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

    public static PostDTO buildPostDTO(Post source, String reporterId) {
        PostDTO postDTO = (PostDTO) buildDTO(source, PostDTO.class);
        postDTO.setReporterId(reporterId);
        return postDTO;
    }

    public static ReporterPageDTO buildReporterPageDTO(Reporter source) {
        ReporterDTO reporterDTO = (ReporterDTO) buildDTO(source, ReporterDTO.class);
        List<PostDTO> postListDTO = new ArrayList<>();
        source.getPosts().forEach(post -> postListDTO.add(buildPostDTO(post, reporterDTO.getId())));

        return new ReporterPageDTO(reporterDTO, postListDTO);
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

    public static Post buildPost(PostDTO source){
        return (Post) buildEntity(source, Post.class);
    }

    public static Comment buildComment(CommentDTO source){
        return (Comment) buildEntity(source, Comment.class);
    }
}
