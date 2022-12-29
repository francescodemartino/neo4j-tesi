import other.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalculateOverallCost {
    private List<ResultBestGroup> toMove;
    private List<Integer> numQueriesList;
    private List<Long> spaceStorageList;
    private Map<String, Table> tables;
    private int numQueries = 0;
    private long spaceStorage = 0;

    public CalculateOverallCost(List<ResultBestGroup> toMove, Map<String, Table> tables) {
        this.toMove = toMove;
        this.tables = tables;
        numQueriesList = new ArrayList<>(toMove.size());
        spaceStorageList = new ArrayList<>(toMove.size());
    }

    public void exe() {
        int limitMoves = 100000;
        numQueries = 0;
        spaceStorage = 0;
        for (int i=0; i<limitMoves; i++) {
            if (toMove.size() == i) {
                break;
            }
            ResultBestGroup resultBestGroup = toMove.get(i);
            long rowsCluster = tables.get(resultBestGroup.getCluster()).getRows();
            Map<String, List<String>> groupClusterColumn = resultBestGroup.getColumns().stream().collect(Collectors.groupingBy(column -> tables.get(column.split(":")[0]).getCluster()));
            for (Map.Entry<String, List<String>> keyColumns : groupClusterColumn.entrySet()) {
                int sizeColumns = 0;
                for (String column : keyColumns.getValue()) {
                    sizeColumns = sizeColumns + (Table.columns.get(column) == null ? 255 : Table.columns.get(column));
                }
                long rowsTable = tables.get(keyColumns.getKey()).getRows();
                spaceStorage = spaceStorage + (sizeColumns * Math.max(rowsCluster, rowsTable));
            }
            numQueries = numQueries + resultBestGroup.getQueries().size();
            numQueriesList.add(numQueries);
            spaceStorageList.add(spaceStorage);
        }
    }

    public int getNumQueries() {
        return numQueries;
    }

    public long getSpaceStorage() {
        return spaceStorage;
    }

    public List<Integer> getNumQueriesList() {
        return numQueriesList;
    }

    public List<Long> getSpaceStorageList() {
        return spaceStorageList;
    }
}
