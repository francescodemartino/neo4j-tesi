import org.neo4j.driver.*;

public class UseNeo4j {
    public static void main(String[] args) {
        Configuration startConfiguration = new Configuration();

        // Louvain GMM KMeans LDA Girvan-Newman
        Driver driver = GraphDatabase.driver("bolt://localhost:11008", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session(SessionConfig.forDatabase("tesi"));
        Result result = session.run("MATCH (cl1:CLUSTER)-[:COMPOSES{ALGO:'LDA'}]->(t1:TABLE)-[:COMPOSE]->(c1:COLUMN)<--(q:QUERY{TYPE:'SELECT'})-[:ENQUIRY{TYPE: 'SELECT'}]->(c2:COLUMN)<-[:COMPOSE]-(t2:TABLE)<-[:COMPOSES{ALGO:'LDA'}]-(cl2:CLUSTER) where cl1<>cl2 return q,t1,t2,cl1,cl2,c1,c2");
        for (Record record : result.list()) {
            long idQuery = record.get("q").asNode().id();
            String columnLeft = record.get("c1").asNode().get("NOME_CAMPO").asString();
            String tableLeft = record.get("t1").asNode().get("TABLE_NAME").asString();
            String clusterLeft = record.get("cl1").asNode().get("CODE").asString();
            String columnRight = record.get("c2").asNode().get("NOME_CAMPO").asString();
            String tableRight = record.get("t2").asNode().get("TABLE_NAME").asString();
            String clusterRight = record.get("cl2").asNode().get("CODE").asString();

            startConfiguration.addToMove(idQuery, clusterLeft, tableLeft, columnLeft);
            startConfiguration.addToMove(idQuery, clusterRight, tableRight, columnRight);
            startConfiguration.addTable(record.get("t1").asNode());
            startConfiguration.addTable(record.get("t2").asNode());
        }

        System.out.println("Start configure");
        startConfiguration.configure();
        System.out.println(startConfiguration.getMoves().size());

        driver.close();
    }
}
