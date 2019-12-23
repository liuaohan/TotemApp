package oddb.memory.table;

import java.io.Serializable;

public class Attribute implements Serializable {

    private int classId;
    private int attrId;
    private String attrName;         //属性名
    private String attrType;         //属性类型
    private byte isDeputy;        //是否为虚属性

    public Attribute(int classId, int attrId, String attrName, String attrType, byte isDeputy) {
        this.classId = classId;
        this.attrId = attrId;
        this.attrName = attrName;
        this.attrType = attrType;
        this.isDeputy = isDeputy;
    }


    public Attribute() {
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getAttrId() {
        return attrId;
    }

    public void setAttrId(int attrId) {
        this.attrId = attrId;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public byte isDeputy() {
        return isDeputy;
    }

    public void setDeputy(byte deputy) {
        isDeputy = deputy;
    }
}
