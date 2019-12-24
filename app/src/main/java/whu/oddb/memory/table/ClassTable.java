package whu.oddb.memory.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClassTable implements Serializable {
    private List<Class> classTable;
    private int maxId;


    public ClassTable() {
        this.classTable = new ArrayList<>();
        this.maxId = 0;
    }

    public ClassTable(List<Class> classTable, int maxId) {
        this.classTable = classTable;
        this.maxId = maxId;
    }

    public List<Class> getClassTable() {
        return classTable;
    }

    public void setClassTable(List<Class> classTable) {
        this.classTable = classTable;
    }

    public int getMaxId() {
        return maxId;
    }

    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    public void add(Class totemClass){
        classTable.add(totemClass);
    }

    public void clear() {
        classTable.clear();
        maxId = 0;
    }
}

