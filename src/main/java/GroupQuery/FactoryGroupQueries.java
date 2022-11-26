package GroupQuery;

import other.Table;

import java.util.Map;
import java.util.Set;

public class FactoryGroupQueries {
    public static GroupQueries getGroupQueries(String method, String cluster, Set<String> columns, Map<String, Table> tables) {
        switch (method) {
            case "complex":
                return new GroupQueriesComplex(cluster, columns, tables);
        }
        return new GroupQueriesSimpler(cluster, columns, tables);
    }
}
