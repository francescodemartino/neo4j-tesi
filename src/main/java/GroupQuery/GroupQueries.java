package GroupQuery;

import other.Query;
import other.Table;
import scale.Scaling;

import java.util.*;

public abstract class GroupQueries {
    protected String cluster;
    protected Set<String> columns;
    protected Set<Query> queries = new HashSet<>();
    protected Map<String, Table> tables;
    private boolean isAdded = false;

    public GroupQueries(String cluster, Set<String> columns, Map<String, Table> tables) {
        this.cluster = cluster;
        this.columns = columns;
        this.tables = tables;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public void addQuery(Query query) {
        queries.add(query);
    }

    public int sizeQueries() {
        return queries.size();
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

    public boolean isOnTop(List<GroupQueries> groupsToRemove, List<GroupQueries> groups) {
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

    public abstract ResponseCost getCost(List<GroupQueries> groupsToRemove, List<GroupQueries> groups, Scaling scaling);
}
