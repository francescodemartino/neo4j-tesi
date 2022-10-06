package other_2;

import org.neo4j.driver.*;
import other.Configuration;

import java.util.*;

public class UseNeo4jColumns {
    private static Map<Long, Set<String>> mappingColumnQuery = new HashMap<>();
    private static Map<String, Move> columnsMap = new HashMap<>();
    private static List<Move> columns;

    public static void main(String[] args) {
        Configuration configuration = other.UseNeo4j.getConfiguration();

        // Louvain GMM KMeans LDA Girvan-Newman
        Driver driver = GraphDatabase.driver("bolt://localhost:11008", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session(SessionConfig.forDatabase("tesi"));
        // Result result = session.run("MATCH (cl1:CLUSTER)-[:COMPOSES{ALGO:'LDA'}]->(t1:TABLE)-[:COMPOSE]->(c1:COLUMN)<--(q:QUERY{TYPE:'SELECT'})-[:ENQUIRY{TYPE: 'SELECT'}]->(c2:COLUMN)<-[:COMPOSE]-(t2:TABLE)<-[:COMPOSES{ALGO:'LDA'}]-(cl2:CLUSTER) where cl1<>cl2 return q,t1,t2,cl1,cl2,c1,c2");
        Result result = session.run("MATCH (cl1:CLUSTER)-[:COMPOSES{ALGO:'LDA'}]->(t1:TABLE)-[:COMPOSE]->(c1:COLUMN)<--(q:QUERY{TYPE:'SELECT'})-[:ENQUIRY{TYPE: 'SELECT'}]->(c2:COLUMN)<-[:COMPOSE]-(t2:TABLE)<-[:COMPOSES{ALGO:'LDA'}]-(cl2:CLUSTER) where cl1<>cl2 return distinct q,t1,t2,cl1,cl2,c1");
        for (Record record : result.list()) {
            String moveKey = Move.getStringMove(record);
            Move move = columnsMap.get(moveKey);
            if (move == null) {
                columnsMap.put(moveKey, new Move(record));
            } else {
                move.incrementCountQuery();
            }
            long idQuery = record.get("q").asNode().id();
            if (mappingColumnQuery.containsKey(idQuery)) {
                mappingColumnQuery.get(idQuery).add(moveKey);
            } else {
                Set<String> columns = new HashSet<>();
                columns.add(moveKey);
                mappingColumnQuery.put(idQuery, columns);
            }
        }

        columns = new ArrayList<>(columnsMap.values());
        columns.sort((first, second) -> second.getCountQuery() - first.getCountQuery());

        columns.forEach(System.out::println);

        ExecutorOperations executorOperations = new ExecutorOperations(configuration, columns, mappingColumnQuery);
        executorOperations.run();

        driver.close();
    }
}
