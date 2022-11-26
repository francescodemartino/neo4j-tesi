import GroupQuery.GroupQueries;
import other.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ResultBestGroup {
    private String cluster;
    private List<GroupQueries> groupQueries;
    private Set<String> columns = new HashSet<>();
    private Set<Query> queries = new HashSet<>();
    
    ResultBestGroup(String cluster, List<GroupQueries> groupQueries) {
        this.cluster = cluster;
        this.groupQueries = groupQueries;
        for (GroupQueries groupQuery : this.groupQueries) {
            columns.addAll(groupQuery.getColumns());
            queries.addAll(groupQuery.getQueries());
        }
    }

    public String getCluster() {
        return cluster;
    }

    public List<GroupQueries> getGroupQueries() {
        return groupQueries;
    }

    public Set<String> getColumns() {
        return columns;
    }

    public Set<Query> getQueries() {
        return queries;
    }

    public double getCost() {
        List<GroupQueries> groupQueriesCopy = new ArrayList<>(groupQueries);
        if (groupQueriesCopy.size() == 0) {
            return 0;
        }
        GroupQueries firstGroup = groupQueriesCopy.get(0);
        groupQueriesCopy.remove(0);
        return firstGroup.getCost(null, groupQueriesCopy);
    }
}
