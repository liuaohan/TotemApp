package whu.oddb.memory.table;

import java.io.Serializable;

public class Class implements Serializable {

    private int classId;            //类id
    private String className;          //类名
    private int attrsNum;           //类属性个数
    private int classType;          //类类型


    public Class() {
        this.classId = 0;
        this.className = null;
        this.attrsNum = 0;
        this.classType = 0;
    }

    public Class(int classId, String className, int attrNum, int classType) {
        this.classId = classId;
        this.className = className;
        this.classType = classType;
    }

    public int getAttrsNum() {
        return attrsNum;
    }

    public void setAttrsNum(int attrsNum) {
        this.attrsNum = attrsNum;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public int getClassType() {
        return classType;
    }

    public void setClassType(int classType) {
        this.classType = classType;
    }
}
