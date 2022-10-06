package other_2;

import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;

public class Move {
    private Record record;
    private long idQuery;
    private String columnLeft;
    private String tableLeft;
    private String clusterLeft;
    private String tableRight;
    private String clusterRight;
    private int countQuery = 1;

    public Move(Record record) {
        this.record = record;
        idQuery = record.get("q").asNode().id();
        columnLeft = record.get("c1").asNode().get("NOME_CAMPO").asString();
        tableLeft = record.get("t1").asNode().get("TABLE_NAME").asString();
        clusterLeft = record.get("cl1").asNode().get("CODE").asString();
        tableRight = record.get("t2").asNode().get("TABLE_NAME").asString();
        clusterRight = record.get("cl2").asNode().get("CODE").asString();
    }

    public int getCountQuery() {
        return countQuery;
    }

    public void incrementCountQuery() {
        countQuery++;
    }

    public Record getRecord() {
        return record;
    }

    public long getIdQuery() {
        return idQuery;
    }

    public String getColumnLeft() {
        return columnLeft;
    }

    public String getTableLeft() {
        return tableLeft;
    }

    public String getClusterLeft() {
        return clusterLeft;
    }

    public String getTableRight() {
        return tableRight;
    }

    public String getClusterRight() {
        return clusterRight;
    }

    @Override
    public String toString() {
        return "other_2.Move{" +
                "idQuery=" + idQuery +
                ", moveKey=" + getStringMove(record) + '\'' +
                ", countQuery=" + countQuery + '\'' +
                ", columnLeft='" + columnLeft + '\'' +
                ", tableLeft='" + tableLeft + '\'' +
                ", clusterLeft='" + clusterLeft + '\'' +
                ", tableRight='" + tableRight + '\'' +
                ", clusterRight='" + clusterRight +
                '}';
    }

    public static String getStringMove(Record record) {
        String columnLeft = record.get("c1").asNode().get("NOME_CAMPO").asString();
        String tableLeft = record.get("t1").asNode().get("TABLE_NAME").asString();
        String tableRight = record.get("t2").asNode().get("TABLE_NAME").asString();

        return tableLeft + ":" + columnLeft + "-" + tableRight;
    }
}
