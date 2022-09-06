public class Table {
    final private String name;
    final private long kb;
    final private long rows;
    final private int rowSize;
    final private float sizeColumn;

    public Table(String name, long kb, long rows, int rowSize) {
        this.name = name;
        this.kb = kb;
        this.rows = rows;
        this.rowSize = rowSize;
        sizeColumn = ((float)(kb/rows)/rowSize);

        /*System.out.println(name);
        System.out.println(kb);
        System.out.println(rows);
        System.out.println(rowSize);
        System.out.println(sizeColumn);
        System.out.println("-----------------------------");*/
    }

    public long getKb() {
        return kb;
    }

    public long getRows() {
        return rows;
    }

    public long getRowSize() {
        return rowSize;
    }

    // TODO in base al nome della colonna dare il peso preciso,
    //  si può fare perchè molte colonne nelle tabelle hanno questa info,
    //  quindi in fase di alimentazione bisogna alimentare la tabella con queste info
    //  Alla fine non serve che controlli anche la lunghezza, basta che differenzi tra
    //  i tipi principali, per esempio String, Number e Date e cose così,
    //  questo perchè alla fine su mongo vengono conservati così
    public float getSizeColumn(String nameColumn) {
        return sizeColumn;
    }
}
