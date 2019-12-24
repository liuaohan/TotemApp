package whu.oddb.memory.management;

import java.io.Serializable;

import whu.oddb.memory.table.Tuple;
import whu.oddb.memory.util.Byte2S;

public class TupleManage implements Serializable {

    private ManageInformation info;

    private BlockManage blockManage;

    public TupleManage(ManageInformation info) {
        this.info = info;
        this.blockManage = new BlockManage(info);
    }

    //读元组
    public Tuple readTuple(int blockNum, int offset) {
        Tuple res = new Tuple();
        BufferPointer s = findBlock(blockNum);
        if (s == null) {
            //当块不在缓冲区中时，从磁盘加载块到缓冲区
            s = blockManage.load(blockNum);
        }
        //根据偏移获取元组在块内的指针link
        byte[] sta = new byte[4];
        for (int i = 0; i < 4; i++) {
            sta[i] = info.memBuff.get(s.bufferId * info.blockMaxLength + offset + i);
        }
        int start = Byte2S.bytes2Int(sta, 0, 4);
        //开始读元组，先读头文件即元组属性个数
        byte[] header = new byte[4];
        for (int i = 0; i < 4; i++) {
            header[i] = info.memBuff.get(s.bufferId * info.blockMaxLength + start + i);
        }
        res.setTupleHeader(Byte2S.bytes2Int(header, 0, 4));
        res.setTuple(new java.lang.Object[res.getTupleHeader()]);
        byte[] temp = new byte[res.getTupleHeader() * info.attrMaxLength];
        for (int i = 0; i < res.getTupleHeader() * info.attrMaxLength; i++) {
            temp[i] = info.memBuff.get(s.bufferId * info.blockMaxLength + start + 4 + i);
        }
        for (int i = 0; i < res.getTupleHeader(); i++) {
            String str = Byte2S.byte2str(temp, i * info.attrMaxLength, info.attrMaxLength);
            res.getTuple()[i] = str;
        }
        return res;
    }

    //写元组
    public int[] writeTuple(Tuple t) {
        int[] ret = new int[2];
        int tuplelength = 4 + info.attrMaxLength * t.getTupleHeader();
        BufferPointer p = null;
        int k = -1;
        if (info.blockSpace != null) {
            //如果块空间表不为空，表示已经有块被创建，否则没有块被创建，需要新建第一块
            for (int i = 0; i <= info.maxBlockNum; i++) {
                //寻找块空闲空间大小足够存入的块
                if (info.blockSpace[i] >= 8 + tuplelength) {
                    k = i;
                    break;
                }
            }
        }
        if (k != -1) {
            info.blockSpace[k] = info.blockSpace[k] - tuplelength - 4;
            if ((p = findBlock(k)) == null) {
                //块不在缓冲区，从磁盘加载块
                p = blockManage.load(k);
            }
            ;
            byte[] x = new byte[4];
            for (int i = 0; i < 4; i++) {
                x[i] = info.memBuff.get(p.bufferId * info.maxBlockNum + i);
            }
            int spacestart = Byte2S.bytes2Int(x, 0, 4);
            for (int i = 0; i < 4; i++) {
                x[i] = info.memBuff.get(p.bufferId * info.maxBlockNum + 4 + i);
            }
            int spaceend = Byte2S.bytes2Int(x, 0, 4);
            ret[0] = k;
            ret[1] = spacestart;
            spacestart = spacestart + 4;
            spaceend = spaceend - tuplelength;
            p.isDirty = true;
            if (info.cleanList.contains(p.bufferId))
                info.cleanList.remove(p.bufferId);
            byte[] hea = Byte2S.int2Bytes(t.getTupleHeader(), 4);
            for (int i = 0; i < 4; i++) {
                info.memBuff.put(p.bufferId * info.maxBlockNum + spaceend + i, hea[i]);
            }
            byte[] str;
            for (int i = 0; i < t.getTupleHeader(); i++) {
                str = Byte2S.str2Bytes(t.getTuple()[i].toString(), info.attrMaxLength);
                for (int j = 0; j < info.attrMaxLength; j++) {
                    info.memBuff.put(p.bufferId * info.maxBlockNum + spaceend + 4 + i * info.attrMaxLength + j, str[j]);
                }
            }
            byte[] sp = Byte2S.int2Bytes(spacestart, 4);
            for (int i = 0; i < 4; i++) {
                info.memBuff.put(p.bufferId * info.maxBlockNum + i, sp[i]);
            }
            sp = Byte2S.int2Bytes(spaceend, 4);
            for (int i = 0; i < 4; i++) {
                info.memBuff.put(p.bufferId * info.maxBlockNum + 4 + i, sp[i]);
                info.memBuff.put(p.bufferId * info.maxBlockNum + spacestart - 4 + i, sp[i]);
            }
            updateBufferPointerSequence(p);
            return ret;
        } else {
            p = blockManage.creatBlock();
            info.blockSpace[info.maxBlockNum] = info.blockSpace[info.maxBlockNum] - tuplelength - 4;
            ret[0] = info.maxBlockNum;
            ret[1] = 8;
            int spacetart = 12;
            int spaceend = info.maxBlockNum - tuplelength;
            p.isDirty = true;
            if (info.cleanList.contains(p.bufferId))
                info.cleanList.remove(p.bufferId);
            byte[] hea = Byte2S.int2Bytes(t.getTupleHeader(), 4);
            for (int i = 0; i < 4; i++) {
                info.memBuff.put(p.bufferId * info.maxBlockNum + spaceend + i, hea[i]);
            }
            byte[] str;
            for (int i = 0; i < t.getTupleHeader(); i++) {
                str = Byte2S.str2Bytes(t.getTuple()[i].toString(), info.attrMaxLength);
                for (int j = 0; j < info.attrMaxLength; j++) {
                    info.memBuff.put(p.bufferId * info.maxBlockNum + spaceend + 4 + i * info.attrMaxLength + j, str[j]);
                }
            }
            byte[] sp = Byte2S.int2Bytes(spacetart, 4);
            for (int i = 0; i < 4; i++) {
                info.memBuff.put(p.bufferId * info.maxBlockNum + i, sp[i]);
            }
            sp = Byte2S.int2Bytes(spaceend, 4);
            for (int i = 0; i < 4; i++) {
                info.memBuff.put(p.bufferId * info.maxBlockNum + 4 + i, sp[i]);
                info.memBuff.put(p.bufferId * info.maxBlockNum + spacetart - 4 + i, sp[i]);
            }
            updateBufferPointerSequence(p);
            return ret;
        }
    }

    private BufferPointer findBlock(int x) {
        BufferPointer res = null;
        for (int i = 0; i < info.buffPointerList.size(); i++) {
            res = info.buffPointerList.get(i);
            if (res.blockNum == x) {
                return res;
            }
        }
        return res;
    }

    //更新元组
    public void UpateTuple(Tuple tuple, int blockid, int offset) {
        BufferPointer p = null;
        if ((p = findBlock(blockid)) == null) {
            p = blockManage.load(blockid);
        }
        byte[] link = new byte[4];
        for (int i = 0; i < 4; i++) {
            link[i] = info.memBuff.get(p.bufferId * info.maxBlockNum + offset + i);
        }
        int sta = Byte2S.bytes2Int(link, 0, 4);
        byte[] header = Byte2S.int2Bytes(tuple.getTupleHeader(), 4);
        for (int i = 0; i < 4; i++) {
            info.memBuff.put(p.bufferId * info.maxBlockNum + sta + i, header[i]);
        }
        byte[] temp;
        for (int i = 0; i < tuple.getTupleHeader(); i++) {
            temp = Byte2S.str2Bytes(tuple.getTuple()[i].toString(), info.attrMaxLength);
            for (int j = 0; j < info.attrMaxLength; j++) {
                info.memBuff.put(p.bufferId * info.maxBlockNum + sta + 4 + i * info.attrMaxLength + j, temp[j]);
            }
        }
        updateBufferPointerSequence(p);
    }

    //更新缓冲区指针序列：将p置为缓冲区列表首位
    private void updateBufferPointerSequence(BufferPointer p) {
        BufferPointer q = new BufferPointer();
        q.blockNum = p.blockNum;
        q.bufferId = p.bufferId;
        q.isDirty = p.isDirty;
        info.buffPointerList.remove(p);
        info.buffPointerList.add(0, q);
    }
}
