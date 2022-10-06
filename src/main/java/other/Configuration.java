package other;

import org.neo4j.driver.types.Node;

import java.util.*;

public class Configuration {
    private Map<String, Table> tables = new HashMap<>();
    private Map<Long, Query> queries = new HashMap<>();
    private Set<String> movedColumns = new HashSet<>();

    public Map<Long, Query> getQueries() {
        return queries;
    }

    public void addToQuery(long idQuery, String nameCluster, String nameTable, String nameColumn) {
        nameCluster = nameCluster.substring(8);
        if (queries.containsKey(idQuery)) {
            queries.get(idQuery).addToChoose(nameCluster, nameTable, nameColumn, tables);
        } else {
            Query move = new Query(idQuery);
            move.addToChoose(nameCluster, nameTable, nameColumn, tables);
            queries.put(idQuery, move);
        }
    }

    public void addTable(Node node) {
        if (!tables.containsKey(node.get("TABLE_NAME").asString())) {
            tables.put(node.get("TABLE_NAME").asString(), new Table(node.get("TABLE_NAME").asString(), node.get("KB").asLong(), node.get("ROWS").asLong(), node.get("ROW_SIZE").asInt()));
        }
    }

    public void configure() {
        setColumnsToMove();
        queries.forEach((idQuery, query) -> query.setColumnsToMove());
        /*queries.forEach((idQuery, query) -> {
            System.out.println(">>>>>>>>>>> " + idQuery);
            query.getColumnsToMove().forEach((name, columns) -> {
                System.out.println("--------> " + name);
                columns.forEach(System.out::println);
            });
        });*/
    }

    private void setColumnsToMove() {
        for (Query move : queries.values()) {
            Set<String> keys = new HashSet<>(move.getChooses().keySet());
            for(Map.Entry<String, Choose> entryChoose: move.getChooses().entrySet()) {
                keys.remove(entryChoose.getKey());
                keys.forEach(keySub -> {
                    for (String column : move.getChooses().get(keySub).getColumns()) {
                        if (!movedColumns.contains(entryChoose.getValue().getNameCluster() + ":" + column)) {
                            entryChoose.getValue().addColumnToMove(column);
                        }
                    }
                });
                keys.add(entryChoose.getKey());
            }
        }
    }
}
