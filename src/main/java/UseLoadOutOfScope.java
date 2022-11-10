import org.neo4j.driver.*;
import other.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UseLoadOutOfScope {
    public static void main(String[] args) throws IOException {
        Driver driver = GraphDatabase.driver("bolt://localhost:11008", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session(SessionConfig.forDatabase("tesi"));
        String outOfScopeRaw = Files.readString(Path.of("out_of_scope.txt"));
        String[] outOfScopeTables = outOfScopeRaw.trim().split("\n");
        for (String table: outOfScopeTables) {
            session.run("MATCH (t:TABLE{TABLE_NAME: '" + table.trim() + "'}) SET t.OUT_OF_SCOPE = '1'");
        }
        driver.close();
    }
}
