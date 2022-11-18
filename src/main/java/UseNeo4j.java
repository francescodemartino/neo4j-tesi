import org.neo4j.driver.*;
import other.Configuration;
import other.Table;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UseNeo4j {
    public static void main(String[] args) throws IOException {
        Configuration startConfiguration = new Configuration();
        Map<String, List<GroupQueries>> mapCluster = new HashMap<>();
        Set<String> namesCluster = new HashSet<>();

        String algorithmClustering = "LDA";
        Driver driver = GraphDatabase.driver("bolt://localhost:11005", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session(SessionConfig.forDatabase("tesi"));
        // Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
        // Session session = driver.session();
        // Result result = session.run("MATCH (cl1:CLUSTER)-[:COMPOSES{ALGO:'" + algorithmClustering + "'}]->(t1:TABLE)-[:COMPOSE]->(c1:COLUMN)<-[:ENQUIRY{TYPE: 'SELECT'}]-(q:QUERY{TYPE:'SELECT'})-[:ENQUIRY{TYPE: 'SELECT'}]->(c2:COLUMN)<-[:COMPOSE]-(t2:TABLE)<-[:COMPOSES{ALGO:'" + algorithmClustering + "'}]-(cl2:CLUSTER) where cl1<>cl2 and NOT cl1.CODE = 'CLUSTER_No link' and NOT cl2.CODE = 'CLUSTER_No link' return q,t1,t2,cl1,cl2,c1,c2");
        Result result = session.run("MATCH (cl1:CLUSTER)-[:COMPOSES{ALGO:'" + algorithmClustering + "'}]->(t1:TABLE)-[:COMPOSE]->(c1:COLUMN)<-[e1:ENQUIRY]-(q:QUERY{TYPE:'SELECT'})-[e2:ENQUIRY]->(c2:COLUMN)<-[:COMPOSE]-(t2:TABLE)<-[:COMPOSES{ALGO:'" + algorithmClustering + "'}]-(cl2:CLUSTER) where  cl1<>cl2 and NOT cl1.CODE = 'CLUSTER_No link' and NOT cl2.CODE = 'CLUSTER_No link' and ('SELECT' IN e1.TYPE or 'WHERE' IN e1.TYPE) and ('SELECT' IN e2.TYPE or 'WHERE' IN e2.TYPE) and  (t1.OUT_OF_SCOPE is null or t1.OUT_OF_SCOPE <> '1') and (t2.OUT_OF_SCOPE is null or t2.OUT_OF_SCOPE <> '1') return q,t1,t2,cl1,cl2,c1,c2");
        // Result result = session.run("MATCH (cl1:CLUSTER)<-[:PROPOSED{ATTIVO:'1'}]-(t1:TABLE)-[:COMPOSE]->(c1:COLUMN)<-[e1:ENQUIRY]-(q:QUERY{TYPE:'SELECT'})-[e2:ENQUIRY]->(c2:COLUMN)<-[:COMPOSE]-(t2:TABLE)-[:PROPOSED{ATTIVO:'1'}]->(cl2:CLUSTER) where  cl1<>cl2 and NOT cl1.CODE = 'CLUSTER_No link' and NOT cl2.CODE = 'CLUSTER_No link' and ('SELECT' IN e1.TYPE or 'WHERE' IN e1.TYPE) and ('SELECT' IN e2.TYPE or 'WHERE' IN e2.TYPE) and  (t1.OUT_OF_SCOPE is null or t1.OUT_OF_SCOPE <> '1') and (t2.OUT_OF_SCOPE is null or t2.OUT_OF_SCOPE <> '1') return q,t1,t2,cl1,cl2,c1,c2");
        for (Record record : result.list()) {
            long idQuery = record.get("q").asNode().id();
            String sql = record.get("q").asNode().get("SQLTEXT").asString();
            String columnLeft = record.get("c1").asNode().get("NOME_CAMPO").asString();
            String tableLeft = record.get("t1").asNode().get("TABLE_NAME").asString();
            String clusterLeft = record.get("cl1").asNode().get("CODE").asString();
            String columnRight = record.get("c2").asNode().get("NOME_CAMPO").asString();
            String tableRight = record.get("t2").asNode().get("TABLE_NAME").asString();
            String clusterRight = record.get("cl2").asNode().get("CODE").asString();

            startConfiguration.addToQuery(idQuery, clusterLeft, tableLeft, columnLeft, sql);
            startConfiguration.addToQuery(idQuery, clusterRight, tableRight, columnRight, sql);
            startConfiguration.addTable(record.get("t1").asNode(), clusterLeft.substring(8));
            startConfiguration.addTable(record.get("t2").asNode(), clusterRight.substring(8));

            namesCluster.add(clusterLeft.substring(8));
            namesCluster.add(clusterRight.substring(8));

            if (record.get("c1").asNode().containsKey("LUNGHEZZA")) {
                int lengthDataTypeLeft = record.get("c1").asNode().get("LUNGHEZZA").asInt();
                Table.columns.put(tableLeft + ":" + columnLeft, lengthDataTypeLeft);
            }
            if (record.get("c2").asNode().containsKey("LUNGHEZZA")) {
                int lengthDataTypeRight = record.get("c2").asNode().get("LUNGHEZZA").asInt();
                Table.columns.put(tableRight + ":" + columnRight, lengthDataTypeRight);
            }

        }

        namesCluster.removeAll(startConfiguration.getTables().keySet());
        for (String nameCluster : namesCluster) {
            Result resultTableCluster = session.run("match (t:TABLE) where t.TABLE_NAME = '" + nameCluster + "' return t");
            startConfiguration.addTable(resultTableCluster.list().get(0).get("t").asNode(), nameCluster);
        }

        startConfiguration.configure();

        List<GroupQueries> groupQueriesList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            AtomicBoolean hasNotGroup = new AtomicBoolean(true);
            startConfiguration.getQueries().forEach((idQuery, query) -> {
                query.getColumnsToMove().forEach((cluster, columns) -> {
                    for (GroupQueries groupQueries : groupQueriesList) {
                        if (groupQueries.getCluster().equals(cluster) && groupQueries.getColumns().equals(columns)) {
                            hasNotGroup.set(false);
                            groupQueries.addQuery(query);
                        }
                    }
                    if (hasNotGroup.get()) {
                        GroupQueries groupQueries = new GroupQueries(cluster, columns, startConfiguration.getTables());
                        groupQueries.addQuery(query);
                        groupQueriesList.add(groupQueries);
                    }
                    hasNotGroup.set(true);
                });
            });
        }

        startConfiguration.getQueries().forEach((idQuery, query) -> {
            query.getColumnsToMove().forEach((cluster, columns) -> {
                for (GroupQueries groupQueries : groupQueriesList) {
                    if (groupQueries.getCluster().equals(cluster) && groupQueries.getColumns().containsAll(columns)) {
                        groupQueries.addQuery(query);
                    }
                }
            });
        });

        /* Map<String, GroupQueries> groupQueriesMap = new HashMap<>();startConfiguration.getQueries().forEach((idQuery, query) -> {
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

        List<GroupQueries> groupQueriesList = new ArrayList<>(groupQueriesMap.values()); */
        // in realtà sarebbe da ordinare in base alla funzione obiettivo sul singolo gruppo e bisognerebbe dividere tutti i gruppi in sottoinsiemi in base al cluster di destinazione
        // inoltre per ogni sottoinsieme sarebbero da creare altri sottoinsiemi e verificare la loro funzione obiettivo. Una volta fatto per tutti i sottoinsiemi di ogni gruppo dei cluster di destinazione, è necessario
        // verificare quello con il punteggo più alto e quindi rimuoverlo, considerando quel gruppo eseguito. Una volta fatto bisogna togliere tutti i campi coinvolti nella operazione e tutte le query coinvolte, perchè a quel punto saranno considerate risolte
        // successivamente bisognerà continuare a ruota.
        // Una funzione obiettivo potrebbe essere calcolata considerando il numero di campi rimossi e il numero di query coinvolte
        // oppure il numero di query coinvolte / il numero di campi coinvolti
        // groupQueriesList.sort((first, second) -> second.sizeQueries() - first.sizeQueries());

        List<String> listCluster = groupQueriesList.stream().map(GroupQueries::getCluster).distinct().collect(Collectors.toList());
        for (String cluster : listCluster) {
            mapCluster.put(cluster, groupQueriesList.stream().filter(groupQueries -> groupQueries.getCluster().equals(cluster)).collect(Collectors.toList()));
        }

        double bestCost;
        List<ResultBestGroup> toMove = new ArrayList<>();
        List<ResultBestGroup> resultsCluster = new ArrayList<>();

        do {
            resultsCluster.clear();

            for (String cluster : listCluster) {
                resultsCluster.add(CalculateBestGroup.getBestGroup(cluster, mapCluster.get(cluster)));
            }
            System.out.println("---> " + resultsCluster.size());
            resultsCluster.sort((first, second) -> Double.compare(second.getCost(), first.getCost()));

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
            for (String cluster : listCluster) {
                for (GroupQueries groupQueries : mapCluster.get(cluster)) {
                    groupQueries.removeQueries(bestGroupToAdd.getQueries());
                }
            }
            System.out.println("bestCost: " + bestCost);
        } while (bestCost >= Math.pow(10, -11));

        System.out.println("toMove size: " + toMove.size());

        CalculateOverallCost calculateOverallCost = new CalculateOverallCost(toMove, startConfiguration.getTables());
        calculateOverallCost.exe(10);

        System.out.println("---------------------------------------------------------------");
        System.out.println("Numero query: " + calculateOverallCost.getNumQueries());
        System.out.println("Storage in byte: " + calculateOverallCost.getSpaceStorage());
        System.out.println("---------------------------------------------------------------");

        SaveExcel saveExcel = new SaveExcel(toMove, session);
        saveExcel.save();

        driver.close();
    }
}
