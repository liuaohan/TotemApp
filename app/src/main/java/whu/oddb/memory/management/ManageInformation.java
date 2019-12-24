package whu.oddb.memory.management;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ManageInformation {

    final int attrMaxLength = 4 * 8; //属性最大字符串长度为4Byte
    final int bufferMaxLength = 1000;//缓冲区大小为1000个块
    final int blockMaxLength = 8 * 1024;//块大小为8KB

    List<BufferPointer> buffPointerList;//构建缓冲区指针表
    ByteBuffer memBuff;//分配maxBlockNum*bufflength大小的缓冲区
    boolean[] canUse;//缓冲区可用状态表，true为可用
    int maxBlockNum;//最大的块号
    int[] blockSpace;//块空闲空间信息
    List<Integer> cleanList;//构建干净页链表


    ManageInformation() {
        this.buffPointerList = new ArrayList<>();
        this.memBuff = ByteBuffer.allocate(blockMaxLength * bufferMaxLength);
        this.canUse = new boolean[bufferMaxLength];
        this.maxBlockNum = -1;
        this.blockSpace = new int[blockMaxLength];
        this.cleanList = new ArrayList<>();
    }


}
