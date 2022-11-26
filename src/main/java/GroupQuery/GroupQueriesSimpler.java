package GroupQuery;

import other.Query;
import other.Table;

import java.util.*;
import java.util.stream.Collectors;

public class GroupQueriesSimpler extends GroupQueries {
    public GroupQueriesSimpler(String cluster, Set<String> columns, Map<String, Table> tables) {
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

        double columnsCost = 0;
        Map<String, List<String>> groupColumns = new HashMap<>();
        List<String[]> columnsCopyTableColumn = columnsCopy.stream().map(columns -> columns.split(":")).collect(Collectors.toList());
        for (String[] tableColumn : columnsCopyTableColumn) {
            if (groupColumns.containsKey(tableColumn[0])) {
                groupColumns.get(tableColumn[0]).add(tableColumn[1]);
            } else {
                groupColumns.put(tableColumn[0], new ArrayList<>(List.of(tableColumn[1])));
            }
        }
        for (Map.Entry<String, List<String>> element : groupColumns.entrySet()) {
            columnsCost += tables.get(element.getKey()).getRows() * element.getValue().size();
        }
        columnsCost = columnsCost * tables.get(cluster).getRows();

        double queriesCost = queriesCopy.size();
        if (queriesCost == 0) {
            return 0;
        }
        if (columnsCost == 0) {
            return queriesCost + 1;
        }
        return queriesCost / columnsCost;
    }
}
