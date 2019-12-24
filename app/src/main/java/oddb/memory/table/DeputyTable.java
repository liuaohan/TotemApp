package oddb.memory.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeputyTable implements Serializable {
    private List<Deputy> deputyTable;

    private int size;


    public DeputyTable() {
        this.deputyTable = new ArrayList<>();
        this.size = 0;
    }

    public DeputyTable(List<Deputy> deputyTable, int size) {
        this.deputyTable = deputyTable;
        this.size = size;
    }

    public List<Deputy> getDeputyTable() {
        return deputyTable;
    }

    public void setDeputyTable(List<Deputy> deputyTable) {
        this.deputyTable = deputyTable;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void add(Deputy deputy){
        deputyTable.add(deputy);
    }

    public void clear() {
        this.deputyTable.clear();
        this.size = 0;
    }
}

