package oddb.memory.table;

import java.io.Serializable;

public class Switching implements Serializable {
    private int originClassId;
    private int deputyClassId;
    private int originAttrId;
    private int deputyAttrId;
    private int switchingRuleId;

    public Switching(int originClassId, int deputyClassId, int originAttrId, int deputyAttrId, int switchingRuleId) {
        this.originClassId = originClassId;
        this.deputyClassId = deputyClassId;
        this.originAttrId = originAttrId;
        this.deputyAttrId = deputyAttrId;
        this.switchingRuleId = switchingRuleId;
    }

    public Switching() {
    }

    public int getOriginClassId() {
        return originClassId;
    }

    public void setOriginClassId(int originClassId) {
        this.originClassId = originClassId;
    }

    public int getDeputyClassId() {
        return deputyClassId;
    }

    public void setDeputyClassId(int deputyClassId) {
        this.deputyClassId = deputyClassId;
    }

    public int getOriginAttrId() {
        return originAttrId;
    }

    public void setOriginAttrId(int originAttrId) {
        this.originAttrId = originAttrId;
    }

    public int getDeputyAttrId() {
        return deputyAttrId;
    }

    public void setDeputyAttrId(int deputyAttrId) {
        this.deputyAttrId = deputyAttrId;
    }

    public int getSwitchingRuleId() {
        return switchingRuleId;
    }

    public void setSwitchingRuleId(int switchingRuleId) {
        this.switchingRuleId = switchingRuleId;
    }
}
