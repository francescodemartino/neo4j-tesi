import java.util.HashMap;
import java.util.Map;

public class Move {
    private long id;
    private Map<String, Choose> chooses = new HashMap<>();

    public Move(long id) {
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
}
