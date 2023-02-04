package it.unipi.lsmsd.socialnews.dto.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unipi.lsmsd.socialnews.dto.*;

import java.util.List;
/**
 * JSONConverter is a utility class to handle DTO (de)serialization from/to JSON.
 * (De)Serialization relies on Jackson library, more specifically to the ObjectMapper class. The JSONConverter doesn't keep
 * a static instance of that class, although it is supposed to be thread safe, because it might represent a
 * bottleneck in case of many access to the instance by several threads
 */
@SuppressWarnings("unchecked")
public final class JSONConverter {

    /**
     * Deserialize a JSON string into a DTO object
     *
     * @param jsonObject JSON string to deserialize into a DTO object
     * @param clazz destination class
     * @return DTO object built from fields in the JSON string
     */
    private static BaseDTO fromJSON(String jsonObject, Class<?> clazz){
        try {
            return (BaseDTO) new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(jsonObject, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserialize a JSON array into a list of DTO objects
     *
     * @param jsonArray JSON array to deserialize into a list of DTO objects
     * @param clazz destination class of a single element
     * @return DTO object built from fields in the JSON string
     */
    private static List<?> fromJSONArray(String jsonArray, Class<?> clazz){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(jsonArray, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialize the current instance of DTO into a JSON string
     *
     * @return JSON string built from the DTO
     */
    public static String toJSON(BaseDTO dto){
        try {
            return new ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialize the current instance of list of DTOs into a JSON string
     *
     * @param DTOList list of DTOs to serialize into a JSON string
     * @return JSON string built from list of DTOs
     */
    public static String toJSONArray(List<BaseDTO> DTOList){
        try {
            return new ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .writeValueAsString(DTOList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static AdminDTO AdminDTOFromJSON(String jsonObject){
        return (AdminDTO) fromJSON(jsonObject, AdminDTO.class);
    }

    public static ReaderDTO ReaderDTOFromJSON(String jsonObject){
        return (ReaderDTO) fromJSON(jsonObject, ReaderDTO.class);
    }

    public static ReporterDTO ReporterDTOFromJSON(String jsonObject){
        return (ReporterDTO) fromJSON(jsonObject, ReaderDTO.class);
    }

    public static PostDTO PostDTOFromJSON(String jsonObject){
        return (PostDTO) fromJSON(jsonObject, PostDTO.class);
    }

    public static CommentDTO CommentDTOFromJSON(String jsonObject){
        return (CommentDTO) fromJSON(jsonObject, CommentDTO.class);
    }

    public static List<AdminDTO> AdminDTOListFromJSON(String jsonArray){
        return (List<AdminDTO>) fromJSONArray(jsonArray, AdminDTO.class);
    }

    public static List<ReaderDTO> ReaderDTOListFromJSON(String jsonArray){
        return (List<ReaderDTO>) fromJSONArray(jsonArray, ReaderDTO.class);
    }

    public static List<ReporterDTO> ReporterDTOListFromJSON(String jsonArray){
        return (List<ReporterDTO>) fromJSONArray(jsonArray, ReaderDTO.class);
    }

    public static List<PostDTO> PostDTOListFromJSON(String jsonArray){
        return (List<PostDTO>) fromJSONArray(jsonArray, PostDTO.class);
    }

    public static List<CommentDTO> CommentDTOListFromJSON(String jsonArray){
        return (List<CommentDTO>) fromJSONArray(jsonArray, CommentDTO.class);
    }

}
