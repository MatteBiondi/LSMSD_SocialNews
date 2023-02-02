package it.unipi.lsmsd.socialnews.dao.neo4j;

import it.unipi.lsmsd.socialnews.dao.model.neo4j.Reporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporterNeo4jDAO{
    private final Neo4jConnection neo4jConnection;

    public ReporterNeo4jDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public void addReporter(Reporter reporter){
        String query = "CREATE (r:Reporter {reporter_id: $reporterId, name: $name, picture: $picture})";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", reporter.getReporterId());
        parameters.put("name", reporter.getName());
        parameters.put("picture", reporter.getPicture());

        neo4jConnection.getNeo4jSession().query(Reporter.class, query, parameters);
    }


    // READ OPERATIONS

    public Reporter getReporterById (String reporterId){
        return neo4jConnection.getNeo4jSession().load(Reporter.class, reporterId);
    }

    public int getNumOfFollowers(String reporterId){
        String query = "MATCH (r:Reporter {reporter_id: $reporterId}) <-[:FOLLOW]- (reader:Reader) " +
                "RETURN count(reader)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", reporterId);

        return neo4jConnection.getNeo4jSession().queryForObject(Integer.class, query, parameters);
    }



    //DELETE OPERATIONS

    public void deleteReporter(String reporterId){
        String query = "MATCH (r:Reporter {reporter_id: $reporterId}) " +
                "OPTIONAL MATCH (r) -[w:WRITE]-> (p:Post) " +
                "OPTIONAL MATCH (p) <-[rp]- () "+
                "OPTIONAL MATCH (r) <-[rr]- () "+
                "DELETE rp "+
                "DELETE rr "+
                "DELETE w " +
                "DELETE p "+
                "DELETE r ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", reporterId);

        neo4jConnection.getNeo4jSession().query(Reporter.class, query, parameters);
        //fixme: SERVE UNA TRANSAZIONE?
    }


    //STATISTICS OPERATIONS

    public List<Reporter> getMostPopularReporters(int limitTopRanking){
        String query = "MATCH (r:Reporter) " +
                "OPTIONAL MATCH (r) <-[f:FOLLOW]- () "+
                "RETURN r, count(f) as NumFollower " +
                "ORDER BY NumFollower DESC " +
                "LIMIT $limit";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("limit", limitTopRanking);

        return (List<Reporter>) neo4jConnection.getNeo4jSession().query(Reporter.class, query, parameters);
    }
}
