package oddb.memory.table;

import java.io.Serializable;

public class ObjectTableItem implements Serializable {


    private int classId = 0;    //类id
    private int objectId = 0;    //duixiangid
    private int blockId = 0;    //块id
    private int offset = 0;      //元组偏移量

    public ObjectTableItem(int classId, int objectId, int blockId, int offset) {
        this.classId = classId;
        this.objectId = objectId;
        this.blockId = blockId;
        this.offset = offset;
    }

    public ObjectTableItem() {
    }
}