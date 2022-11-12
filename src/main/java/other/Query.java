package other;

import java.util.*;

public class Query {
    private long id;
    private String sql;
    private Map<String, Choose> chooses = new HashMap<>();
    private Map<String, Set<String>> columnsToMove = new HashMap<>();

    public Query(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Map<String, Choose> getChooses() {
        return chooses;
    }

    public void addToChoose(String nameCluster, String nameTable, String nameColumn, Map<String, Table> tables) {
        if (chooses.containsKey(nameCluster)) {
            chooses.get(nameCluster).addColumn(nameTable + ":" + nameColumn);
        } else {
            Choose choose = new Choose(this, nameCluster, tables);
            choose.addColumn(nameTable + ":" + nameColumn);
            chooses.put(nameCluster, choose);
        }
    }

    public void setColumnsToMove() {
        chooses.forEach((s, choose) -> {
            columnsToMove.put(s, choose.getColumnsToMove());
            /*if (getSql().equals("SELECT DBA . COD_PERSONA INTO : H FROM T04DBA01 DBA WHERE DBA . COD_EMPRESA = : H AND DBA . COD_FISCO = : H AND EXISTS ( SELECT 1 FROM E77DEP1R DEP WHERE DEP . CIST = : H AND DEP . NCDG = DBA . COD_PERSONA AND DEP . CSDP > 0 ) WITH UR")) {
                System.out.println("++++++++++++++++++++++++++++++++++++++++");

                getChooses().forEach((cluster, columns) -> {
                    System.out.println("----> " + cluster);
                    System.out.println(">>>>> " + columns.getColumns());
                });

                System.out.println("----------------------------------------");

                getColumnsToMove().forEach((cluster, columns) -> {
                    System.out.println("----> " + cluster);
                    System.out.println(">>>>> " + columns);
                });
            }*/
        });
    }

    public Map<String, Set<String>> getColumnsToMove() {
        return columnsToMove;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
