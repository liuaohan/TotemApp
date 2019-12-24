package whu.oddb.memory.table;

import java.io.Serializable;

public class BiPointer implements Serializable {
    public int originClassId;
    public int originObjectId;
    public int deputyClassId;
    public int deputyObjectId;


    public BiPointer() {
    }

    public BiPointer(int originClassId, int originObjectId, int deputyClassId, int deputyObjectId) {
        this.originClassId = originClassId;
        this.originObjectId = originObjectId;
        this.deputyClassId = deputyClassId;
        this.deputyObjectId = deputyObjectId;
    }

    public int getOriginClassId() {
        return originClassId;
    }

    public void setOriginClassId(int originClassId) {
        this.originClassId = originClassId;
    }

    public int getOriginObjectId() {
        return originObjectId;
    }

    public void setOriginObjectId(int originObjectId) {
        this.originObjectId = originObjectId;
    }

    public int getDeputyClassId() {
        return deputyClassId;
    }

    public void setDeputyClassId(int deputyClassId) {
        this.deputyClassId = deputyClassId;
    }

    public int getDeputyObjectId() {
        return deputyObjectId;
    }

    public void setDeputyObjectId(int deputyObjectId) {
        this.deputyObjectId = deputyObjectId;
    }
}
