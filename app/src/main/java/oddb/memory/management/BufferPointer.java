package oddb.memory.management;

public class BufferPointer {
    int blockNum;        //块号
    Boolean isDirty;        //标记改块是否为脏（true为脏）
    int bufferId;        //缓冲区索引号
}
