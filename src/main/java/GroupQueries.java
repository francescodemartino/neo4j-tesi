import other.Query;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupQueries {
    private String name;
    private String cluster;
    private Set<String> columns;
    private Set<Query> queries = new HashSet<>();

    public GroupQueries(String name, String cluster, Set<String> columns) {
        this.name = name;
        this.cluster = cluster;
        this.columns = columns;
    }

    public void addQuery(Query query) {
        queries.add(query);
    }

    public int sizeQueries() {
        return queries.size();
    }

    public String getName() {
        return name;
    }

    public String getCluster() {
        return cluster;
    }

    public Set<String> getColumns() {
        return columns;
    }

    public void removeColumns(Set<String> columns) {
        this.columns.removeAll(columns);
    }

    public void removeQueries(Set<Query> queries) {
        this.queries.removeAll(queries);
    }

    public Set<Query> getQueries() {
        return queries;
    }

    boolean isOnTop(List<GroupQueries> groupsToRemove, List<GroupQueries> groups) {
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
        return columnsCopy.size() == 0 && queriesCopy.size() != 0;
    }

    float getCost(List<GroupQueries> groupsToRemove, List<GroupQueries> groups) {
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

        float queriesCost = (float)queriesCopy.size();
        float columnsCost = (float)columnsCopy.size();
        if (queriesCost == 0) {
            return 0;
        }
        if (columnsCost == 0) {
            return queriesCost + 1;
        }
        return queriesCost / columnsCost;
    }
}
