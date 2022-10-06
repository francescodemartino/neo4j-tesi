import org.neo4j.driver.*;
import other.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UseNeo4j {
    public static void main(String[] args) {
        Configuration startConfiguration = new Configuration();
        Map<String, GroupQueries> groupQueriesMap = new HashMap<>();

        // Louvain GMM KMeans LDA Girvan-Newman
        String algorithmClustering = "LDA";
        Driver driver = GraphDatabase.driver("bolt://localhost:11008", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session(SessionConfig.forDatabase("tesi"));
        Result result = session.run("MATCH (cl1:CLUSTER)-[:COMPOSES{ALGO:'" + algorithmClustering + "'}]->(t1:TABLE)-[:COMPOSE]->(c1:COLUMN)<--(q:QUERY{TYPE:'SELECT'})-[:ENQUIRY{TYPE: 'SELECT'}]->(c2:COLUMN)<-[:COMPOSE]-(t2:TABLE)<-[:COMPOSES{ALGO:'" + algorithmClustering + "'}]-(cl2:CLUSTER) where cl1<>cl2 and NOT cl1.CODE = 'CLUSTER_No link' and NOT cl2.CODE = 'CLUSTER_No link' return q,t1,t2,cl1,cl2,c1,c2");
        for (Record record : result.list()) {
            long idQuery = record.get("q").asNode().id();
            String columnLeft = record.get("c1").asNode().get("NOME_CAMPO").asString();
            String tableLeft = record.get("t1").asNode().get("TABLE_NAME").asString();
            String clusterLeft = record.get("cl1").asNode().get("CODE").asString();
            String columnRight = record.get("c2").asNode().get("NOME_CAMPO").asString();
            String tableRight = record.get("t2").asNode().get("TABLE_NAME").asString();
            String clusterRight = record.get("cl2").asNode().get("CODE").asString();

            startConfiguration.addToQuery(idQuery, clusterLeft, tableLeft, columnLeft);
            startConfiguration.addToQuery(idQuery, clusterRight, tableRight, columnRight);
            startConfiguration.addTable(record.get("t1").asNode());
            startConfiguration.addTable(record.get("t2").asNode());
        }

        startConfiguration.configure();

        startConfiguration.getQueries().forEach((idQuery, query) -> {
            query.getColumnsToMove().forEach((cluster, columns) -> {
                String hashKey = columns.hashCode() + ":" + cluster;
                if (groupQueriesMap.containsKey(hashKey)) {
                    groupQueriesMap.get(hashKey).addQuery(query);
                } else {
                    GroupQueries groupQueries = new GroupQueries(hashKey, cluster, columns);
                    groupQueries.addQuery(query);
                    groupQueriesMap.put(hashKey, groupQueries);
                }
                // System.out.println(columns);
                // System.out.println(columns.hashCode());
            });
        });

        Map<String, List<GroupQueries>> mapCluster = new HashMap<>();
        List<GroupQueries> groupQueriesList = new ArrayList<>(groupQueriesMap.values());
        // in realtà sarebbe da ordinare in base alla funzione obiettivo sul singolo gruppo e bisognerebbe dividere tutti i gruppi in sottoinsiemi in base al cluster di destinazione
        // inoltre per ogni sottoinsieme sarebbero da creare altri sottoinsiemi e verificare la loro funzione obiettivo. Una volta fatto per tutti i sottoinsiemi di ogni gruppo dei cluster di destinazione, è necessario
        // verificare quello con il punteggo più alto e quindi rimuoverlo, considerando quel gruppo eseguito. Una volta fatto bisogna togliere tutti i campi coinvolti nella operazione e tutte le query coinvolte, perchè a quel punto saranno considerate risolte
        // successivamente bisognerà continuare a ruota.
        // Una funzione obiettivo potrebbe essere calcolata considerando il numero di campi rimossi e il numero di query coinvolte
        // oppure il numero di query coinvolte / il numero di campi coinvolti
        groupQueriesList.sort((first, second) -> second.sizeQueries() - first.sizeQueries());

        List<String> listCluster = groupQueriesList.stream().map(GroupQueries::getCluster).distinct().collect(Collectors.toList());
        for (String cluster : listCluster) {
            mapCluster.put(cluster, groupQueriesList.stream().filter(groupQueries -> groupQueries.getCluster().equals(cluster)).collect(Collectors.toList()));
        }

        float bestCost;
        List<ResultBestGroup> toMove = new ArrayList<>();
        List<ResultBestGroup> resultsCluster = new ArrayList<>();

        do {
            resultsCluster.clear();

            for (String cluster : listCluster) {
                resultsCluster.add(CalculateBestGroup.getBestGroup(cluster, mapCluster.get(cluster)));
            }
            resultsCluster.sort((first, second) -> Float.compare(second.getCost(), first.getCost()));

            ResultBestGroup bestGroupToAdd = resultsCluster.get(0);
            toMove.add(bestGroupToAdd);
            bestCost = bestGroupToAdd.getCost();
            mapCluster.get(bestGroupToAdd.getCluster()).removeAll(bestGroupToAdd.getGroupQueries());
            /*for (String cluster : listCluster) {
                for (GroupQueries groupQueries : mapCluster.get(cluster)) {
                    groupQueries.removeColumns(bestGroupToAdd.getColumns());
                    groupQueries.removeQueries(bestGroupToAdd.getQueries());
                }
            }*/
            for (GroupQueries groupQueries : mapCluster.get(bestGroupToAdd.getCluster())) {
                groupQueries.removeColumns(bestGroupToAdd.getColumns());
                groupQueries.removeQueries(bestGroupToAdd.getQueries());
            }
            System.out.println("bestCost: " + bestCost);
        } while (bestCost >= 4);

        System.out.println("toMove size: " + toMove.size());
        for (ResultBestGroup bestGroup : toMove) {
            System.out.println(bestGroup.getQueries());
            System.out.print(bestGroup.getColumns());
            System.out.println(" --> " + bestGroup.getCluster());
        }

        driver.close();
    }
}
