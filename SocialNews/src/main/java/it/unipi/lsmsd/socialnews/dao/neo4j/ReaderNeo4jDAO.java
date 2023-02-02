package it.unipi.lsmsd.socialnews.dao.neo4j;

import it.unipi.lsmsd.socialnews.dao.model.neo4j.Reader;
import it.unipi.lsmsd.socialnews.dao.model.neo4j.Reporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReaderNeo4jDAO{
    private final Neo4jConnection neo4jConnection;

    public ReaderNeo4jDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public void addReader(Reader reader){
        String query ="CREATE (r:Reader {reader_id: $readerId})";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", reader.getReaderId());

        neo4jConnection.getNeo4jSession().query(Reader.class, query, parameters);
    }

    public void followReporter(String readerId, String reporterId){
        String query ="MATCH (rd:Reader {reader_id: $readerId}) " +
                "MATCH (rp:Reporter {reporter_id: $reporterId}) " +
                "CREATE (rd) -[:FOLLOW]-> (rp)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);
        parameters.put("reporterId", reporterId);

        neo4jConnection.getNeo4jSession().query(Reader.class, query, parameters);
    }

    // READ OPERATIONS

    public Reader readerById(String readerId){
        //todo check depth of load
        return neo4jConnection.getNeo4jSession().load(Reader.class, readerId);
    }

    public List<Reporter> readFollowingByReaderId(String readerId, int limit, int offset){

        String query = "MATCH (:Reader {reader_id: $readerId})-[:FOLLOW]->(following:Reporter) " +
                "RETURN following " +
                "ORDER BY following.reporter_id " +
                "SKIP $offset " +
                "LIMIT $limit";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);
        parameters.put("offset", offset);
        parameters.put("limit", limit);

        return (List<Reporter>) neo4jConnection.getNeo4jSession().query(Reporter.class, query, parameters);
    }


    // DELETE OPERATIONS

    public void deleteReader(String readerId){
        String query = "MATCH (r:Reader {reader_id: $readerId}) "+
                "OPTIONAL MATCH (r) -[rep:REPORT]-> () "+
                "OPTIONAL MATCH (r) -[f:FOLLOW]-> () "+
                "DELETE rep " +
                "DELETE f " +
                "DELETE r";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);

        neo4jConnection.getNeo4jSession().query(Reader.class, query, parameters);
    }

    public void unfollowReporter(String readerId, String reporterId){
        String query = "MATCH (rd:Reader {reader_id: $readerId}) -[f:FOLLOW]-> (rp:Reporter {reporter_id: $reporterId}) "+
                "DELETE f";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);
        parameters.put("reporterId", reporterId);

        neo4jConnection.getNeo4jSession().query(Reader.class, query, parameters);
    }


    //STATISTICS OPERATIONS

    public List<Reporter> suggestReporters(String readerId, int limitListLen){

        String query = "MATCH (r:Reporter) " +
                "OPTIONAL MATCH (r)  <-[f:FOLLOW]- (rr:Reader) "+
                "WHERE rr.reader_id <> $readerId " +
                "RETURN r, count(f) as NumFollower " +
                "ORDER BY NumFollower DESC " +
                "LIMIT $limit";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);
        parameters.put("limit", limitListLen);

        return (List<Reporter>) neo4jConnection.getNeo4jSession().query(Reporter.class, query, parameters);
    }
}
