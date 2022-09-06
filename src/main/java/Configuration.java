import org.neo4j.driver.types.Node;

import java.util.*;

public class Configuration {
    private Map<String, Table> tables = new HashMap<>();
    private Map<Long, Move> moves = new HashMap<>();
    private Set<String> movedColumns = new HashSet<>();

    public Map<Long, Move> getMoves() {
        return moves;
    }

    public void addToMove(long idQuery, String nameCluster, String nameTable, String nameColumn) {
        nameCluster = nameCluster.substring(8);
        if (moves.containsKey(idQuery)) {
            moves.get(idQuery).addToChoose(nameCluster, nameTable, nameColumn, tables);
        } else {
            Move move = new Move(idQuery);
            move.addToChoose(nameCluster, nameTable, nameColumn, tables);
            moves.put(idQuery, move);
        }
    }

    public void addTable(Node node) {
        if (!tables.containsKey(node.get("TABLE_NAME").asString())) {
            tables.put(node.get("TABLE_NAME").asString(), new Table(node.get("TABLE_NAME").asString(), node.get("KB").asLong(), node.get("ROWS").asLong(), node.get("ROW_SIZE").asInt()));
        }
    }

    public void configure() {
        setColumnsToMove();
        calculateDependencies();
    }

    private void setColumnsToMove() {
        for (Move move : moves.values()) {
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

    private void calculateDependencies() {
        for (Move move : moves.values()) {
            for (Move moveCheck : moves.values()) {
                for (Choose choose : move.getChooses().values()) {
                    for (Choose chooseCheck : moveCheck.getChooses().values()) {
                        if (choose != chooseCheck) {
                            if (chooseCheck.getColumnsToMove().containsAll(choose.getColumnsToMove())) {
                                chooseCheck.addDependency(choose);
                            }
                        }
                    }
                }
            }
        }
    }
}
