package oddb.memory.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeputyRuleTable implements Serializable {
    private int maxId = 0;
    private List<DeputyRule> deputyRuleTable=new ArrayList<>();


    public int getMaxId() {
        return maxId;
    }

    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    public List<DeputyRule> getDeputyRuleTable() {
        return deputyRuleTable;
    }

    public void setDeputyRuleTable(List<DeputyRule> deputyRuleTable) {
        this.deputyRuleTable = deputyRuleTable;
    }

    public void clear(){
        deputyRuleTable.clear();
        maxId = 0;
    }
}
