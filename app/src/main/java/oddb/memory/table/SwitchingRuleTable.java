package oddb.memory.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SwitchingRuleTable implements Serializable {
    private int maxId;
    private List<SwitchingRule> switchingRuleTable;

    public SwitchingRuleTable(int maxId, List<SwitchingRule> switchingRuleTable) {
        this.maxId = maxId;
        this.switchingRuleTable = switchingRuleTable;
    }

    public SwitchingRuleTable() {
        this.maxId = 0;
        this.switchingRuleTable = new ArrayList<>();
    }

    public int getMaxId() {
        return maxId;
    }

    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    public List<SwitchingRule> getSwitchingRuleTable() {
        return switchingRuleTable;
    }

    public void setSwitchingRuleTable(List<SwitchingRule> switchingRuleTable) {
        this.switchingRuleTable = switchingRuleTable;
    }
}
