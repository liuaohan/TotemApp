package whu.oddb.memory.table;

import java.io.Serializable;

public class Deputy implements Serializable {

    private int deputyClassId;           //代理类id

    private int originClassId;            //类id
    private int deputyRuleId;       //代理规则id

    public Deputy(int deputyClassId, int originClassId, int deputyRuleId) {
        this.deputyClassId = deputyClassId;
        this.originClassId = originClassId;
        this.deputyRuleId = deputyRuleId;
    }

    public Deputy() {
    }

    public int getDeputyClassId() {
        return deputyClassId;
    }

    public void setDeputyClassId(int deputyClassId) {
        this.deputyClassId = deputyClassId;
    }

    public int getOriginClassId() {
        return originClassId;
    }

    public void setOriginClassId(int originClassId) {
        this.originClassId = originClassId;
    }

    public int getDeputyRuleId() {
        return deputyRuleId;
    }

    public void setDeputyRuleId(int deputyRuleId) {
        this.deputyRuleId = deputyRuleId;
    }
}
