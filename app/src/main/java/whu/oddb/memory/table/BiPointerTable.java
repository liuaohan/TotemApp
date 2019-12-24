package whu.oddb.memory.table;

import java.io.Serializable;
import java.util.List;

public class BiPointerTable implements Serializable {
    private List<BiPointer> biPointerTable;
    //private int maxId;


    public BiPointerTable(List<BiPointer> biPointerTable) {
        this.biPointerTable = biPointerTable;
    }

    public BiPointerTable() {
    }

    public List<BiPointer> getBiPointerTable() {
        return biPointerTable;
    }

    public void setBiPointerTable(List<BiPointer> biPointerTable) {
        this.biPointerTable = biPointerTable;
    }
}
