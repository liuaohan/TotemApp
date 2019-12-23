package oddb.memory.table;

import java.io.Serializable;

public class DeputyRule implements Serializable {
    private int deputyRuleId;            //rule id
    private String rule;    //代理guizedui

    public DeputyRule(int deputyRuleId, String deputyRule) {
        this.deputyRuleId = deputyRuleId;
        this.rule = deputyRule;
    }

    public int getDeputyRuleId() {
        return deputyRuleId;
    }

    public void setDeputyRuleId(int deputyRuleId) {
        this.deputyRuleId = deputyRuleId;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public DeputyRule() {
        this.deputyRuleId = 0;
        this.rule = null;
    }


}
