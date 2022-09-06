import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Choose {
    private String nameCluster;
    private Move move;
    private Set<String> columns = new HashSet<>();
    private Set<String> columnsToMove = new HashSet<>();
    private Set<Choose> dependencies = new HashSet<>();
    private Map<String, Table> tables = new HashMap<>();

    public Choose(Move move, String nameCluster, Map<String, Table> tables) {
        this.move = move;
        this.nameCluster = nameCluster;
    }

    public void addColumn(String column) {
        columns.add(column);
    }

    public void addColumnToMove(String column) {
        columnsToMove.add(column);
    }

    public void addColumnsToMove(Set<String> columns) {
        columnsToMove.addAll(columns);
    }

    public void addDependency(Choose choose) {
        dependencies.add(choose);
    }

    public Set<Choose> getDependencies() {
        return dependencies;
    }

    public Set<String> getColumns() {
        return columns;
    }

    public Set<String> getColumnsToMove() {
        return columnsToMove;
    }

    public String getNameCluster() {
        return nameCluster;
    }

    public Move getMove() {
        return move;
    }
}
