package other_2;

import other.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExecutorOperations {
    final int MIN_COUNT_QUERY = 5;
    private Configuration configuration;
    private List<Move> columns;
    private Map<Long, Set<String>> mappingColumnQuery;

    public ExecutorOperations(Configuration configuration, List<Move> columns, Map<Long, Set<String>> mappingColumnQuery) {
        this.configuration = configuration;
        this.columns = columns;
        this.mappingColumnQuery = mappingColumnQuery;
    }

    public void run() {
        columns = columns.stream().filter(move -> move.getCountQuery() >= MIN_COUNT_QUERY).collect(Collectors.toList());

        /*
        Valutare query per query e vedere se è presente il sottoinsieme di operazioni, questo per ogni gruppo di mosse e si prende quello che da il punteggio più alto, il punteggio massimo per ogni gruppo è 1 e si valuta tra 0 e 1 in base a quante mosse mancano
        Valutare poi per ogni query se esistano gruppi di mosse che già soddisfano quella query e togliere il punteggio che si ottiene da quello ottenuto precedentemente
         */
    }
}
