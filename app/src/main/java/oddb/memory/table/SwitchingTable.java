package oddb.memory.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SwitchingTable implements Serializable {
    private int maxId;
    private List<Switching> switchingTable;


    public SwitchingTable(int maxId, List<Switching> switchingTable) {
        this.maxId = maxId;
        this.switchingTable = switchingTable;
    }

    public SwitchingTable() {
        this.maxId = 0;
        switchingTable = new ArrayList<>();
    }

    public int getMaxId() {
        return maxId;
    }

    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    public List<Switching> getSwitchingTable() {
        return switchingTable;
    }

    public void setSwitchingTable(List<Switching> switchingTable) {
        this.switchingTable = switchingTable;
    }
}
