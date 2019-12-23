package oddb.memory.table;

import java.io.Serializable;

public class SwitchingRule implements Serializable {
    private int switchRuleId;
    private String rule;

    public SwitchingRule() {
    }

    public SwitchingRule(int switchRuleId, String rule) {
        this.switchRuleId = switchRuleId;
        this.rule = rule;
    }

    public int getSwitchRuleId() {
        return switchRuleId;
    }

    public void setSwitchRuleId(int switchRuleId) {
        this.switchRuleId = switchRuleId;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
