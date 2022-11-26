package GroupQuery;

import other.Query;
import other.Table;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupQueriesComplex extends GroupQueries {
    public static double numerator;
    public static double denominator;

    public GroupQueriesComplex(String cluster, Set<String> columns, Map<String, Table> tables) {
        super(cluster, columns, tables);
    }

    @Override
    public double getCost(List<GroupQueries> groupsToRemove, List<GroupQueries> groups) {
        Set<String> columnsCopy = new HashSet<>(columns);
        Set<Query> queriesCopy = new HashSet<>(queries);
        if (groups != null) {
            groups.forEach(groupQueries -> {
                columnsCopy.addAll(groupQueries.getColumns());
                queriesCopy.addAll(groupQueries.getQueries());
            });
        }
        if (groupsToRemove != null) {
            groupsToRemove.forEach(groupQueries -> {
                columnsCopy.removeAll(groupQueries.getColumns());
                queriesCopy.removeAll(groupQueries.getQueries());
            });
        }

        double numQueries = queriesCopy.size();

        Table tableRoot = tables.get(cluster);
        double sumStorage = 0;
        Map<String, List<String>> groupClusterColumn = columnsCopy.stream().collect(Collectors.groupingBy(column -> tables.get(column.split(":")[0]).getCluster()));
        Table tableSource;
        for (Map.Entry<String, List<String>> entryGroupClusterColumn : groupClusterColumn.entrySet()) {
            double sumStorageCluster = 0;
            Map<String, List<String>> groupTableColumn = entryGroupClusterColumn.getValue().stream().collect(Collectors.groupingBy(column -> column.split(":")[0]));
            for (Map.Entry<String, List<String>> entryTableColumn : groupTableColumn.entrySet()) {
                double sizeColumns = 0;
                Table table = tables.get(entryTableColumn.getKey());
                for (String column : entryTableColumn.getValue()) {
                    sizeColumns += table.getSizeColumn(column);
                }
                sumStorageCluster += sizeColumns * table.getRows();
            }
            tableSource = tables.get(entryGroupClusterColumn.getKey());
            /*System.out.println(entryGroupClusterColumn.getKey());
            System.out.println(tableSource.getName());
            System.out.println(tableSource.getRows());*/
            sumStorageCluster = sumStorageCluster /* * tableSource.getRows() */ * ((double) tableRoot.getRows() / tableSource.getRows());
            sumStorage += sumStorageCluster;
        }

        double indexCost = 1 + (Math.log(tableRoot.getRows()) / Math.log(2));

        numerator = numQueries;
        denominator = (sumStorage + indexCost);
        return numQueries / (sumStorage + indexCost);
        /*double queriesCost = (double) queriesCopy.size();
        double columnsCost = (double) columnsCopy.size();
        if (queriesCost == 0) {
            return 0;
        }
        if (columnsCost == 0) {
            return queriesCost + 1;
        }
        return queriesCost / columnsCost;*/
    }
}
