import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import other.Query;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SaveExcel {
    private List<ResultBestGroup> toMove;
    private Session session;
    private CalculateOverallCost calculateOverallCost;

    public SaveExcel(List<ResultBestGroup> toMove, Session session, CalculateOverallCost calculateOverallCost) {
        this.toMove = toMove;
        this.session = session;
        this.calculateOverallCost = calculateOverallCost;
    }

    public void save() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Risultati");

        String[] header = {"ID", "Numero query", "Tabelle e colonne da spostare", "Cluster target", "Storage", "Query complessive"};
        Row rowHeader = sheet.createRow(0);
        for (int i = 0; i < header.length; i++) {
            Cell cellHeader = rowHeader.createCell(i);
            cellHeader.setCellValue(header[i]);
            cellHeader.setCellStyle(createStyleHeader(workbook));
        }

        int rowCount = 1;
        for (ResultBestGroup bestGroup : toMove) {
            Object[] rowData = {rowCount, bestGroup.getQueries().size(), bestGroup.getColumns().toString().substring(1, bestGroup.getColumns().toString().length() - 1), bestGroup.getCluster(), calculateOverallCost.getSpaceStorageList().get(rowCount - 1), calculateOverallCost.getNumQueriesList().get(rowCount - 1)};
            Row row = sheet.createRow(rowCount);
            int columnCount = 0;
            for (Object field : rowData) {
                Cell cell = row.createCell(columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Long) {
                    cell.setCellValue((Long) field);
                }
                columnCount++;
            }
            FileWriter writeFile = new FileWriter("results/queries/" + rowCount + ".txt");
            writeFile.write(bestGroup.getQueries().stream().map(Query::getSql).distinct().collect(Collectors.joining("\n\n")));
            writeFile.close();
            System.out.println(bestGroup.getQueries());
            System.out.print(bestGroup.getColumns());
            System.out.println(" --> " + bestGroup.getCluster());
            rowCount++;
        }

        XSSFSheet sheetCluster = workbook.createSheet("Cluster");
        String[] headerCluster = {"Root", "Children"};
        Row rowHeaderCluster = sheetCluster.createRow(0);
        for (int i = 0; i < headerCluster.length; i++) {
            Cell cellHeader = rowHeaderCluster.createCell(i);
            cellHeader.setCellValue(headerCluster[i]);
            cellHeader.setCellStyle(createStyleHeader(workbook));
        }

        Map<String, Set<String>> mapClusterRootChildren = new HashMap<>();
        String algorithmClustering = "LDA";
        Result result = session.run("match (c:CLUSTER)-[:COMPOSES{ALGO: '" + algorithmClustering + "'}]->(t:TABLE) return c,t");
        // Result result = session.run("match (c:CLUSTER)<-[:PROPOSED{ATTIVO:'1'}]-(t:TABLE) return c,t");
        for (Record record : result.list()) {
            String rootCluster = record.get("c").asNode().get("ROOT").asString();
            String tableName = record.get("t").asNode().get("TABLE_NAME").asString();
            if (mapClusterRootChildren.containsKey(rootCluster)) {
                mapClusterRootChildren.get(rootCluster).add(tableName);
            } else {
                Set<String> children = new HashSet<>();
                children.add(tableName);
                mapClusterRootChildren.put(rootCluster, children);
            }
        }

        rowCount = 1;
        for (Map.Entry<String, Set<String>> clusterChildren : mapClusterRootChildren.entrySet()) {
            Object[] rowData = {clusterChildren.getKey(), clusterChildren.getValue().toString().substring(1, clusterChildren.getValue().toString().length() - 1)};
            Row row = sheetCluster.createRow(rowCount);
            int columnCount = 0;
            for (Object field : rowData) {
                Cell cell = row.createCell(columnCount);
                cell.setCellValue((String) field);
                columnCount++;
            }
            rowCount++;
        }

        FileOutputStream outputStream = new FileOutputStream("results/results.xlsx");
        workbook.write(outputStream);
    }

    private CellStyle createStyleHeader(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}
