package whu.oddb.memory.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AttributeTable  implements Serializable {//属性
    private List<Attribute> attributeTable=new ArrayList<>();
    private int maxId=0;

    public void clear(){
        attributeTable.clear();
        maxId = 0;
    }

    public List<Attribute> getAttributeTable() {
        return attributeTable;
    }

    public void setAttributeTable(List<Attribute> attributeTable) {
        this.attributeTable = attributeTable;
    }

    public int getMaxId() {
        return maxId;
    }

    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }
}

