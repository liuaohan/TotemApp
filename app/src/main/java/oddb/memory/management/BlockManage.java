package oddb.memory.management;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import oddb.memory.util.Byte2S;

public class BlockManage implements Serializable {

    private ManageInformation info;

    public BlockManage(ManageInformation info) {
        this.info = info;
    }

    //加载块
    public BufferPointer load(int block) {
        if (info.buffPointerList.size() == info.bufferMaxLength) {
            if (info.cleanList.size() > 0.3 * info.bufferMaxLength) {
                if (save(info.buffPointerList.get(info.cleanList.get(0)))) {
                    info.canUse[info.buffPointerList.get(info.cleanList.get(0)).bufferId] = true;
                    info.buffPointerList.remove((int) info.cleanList.get(0));
                    info.cleanList.remove(0);
                }
            } else {
                for (int i = 0; i < info.bufferMaxLength * 0.4; i++) {
                    if (save(info.buffPointerList.get(info.cleanList.get(i)))) {
                        info.canUse[info.buffPointerList.get(info.cleanList.get(i)).bufferId] = true;
                    }
                }
                info.buffPointerList.clear();
            }
//            if(save(buffPointerList.get(bufferMaxLength-1))){
//                canUse[buffPointerList.get(bufferMaxLength-1).bufferId]=true;
//                buffPointerList.remove(bufferMaxLength-1);
//            }
        }
        File file = new File("/data/data/drz.oddb/Memory/" + block);
        BufferPointer Free = null;
        if (file.exists()) {
            Free = new BufferPointer();
            Free.blockNum = block;
            Free.isDirty = false;
            for (int i = 0; i < info.bufferMaxLength; i++) {
                if (info.canUse[i]) {
                    Free.bufferId = i;
                    info.canUse[i] = false;
                    info.cleanList.add(i);
                    break;
                }
            }
            int offset = Free.bufferId * info.blockMaxLength;
            try {
                FileInputStream input = new FileInputStream(file);
                byte[] temp = new byte[info.blockMaxLength];
                input.read(temp);
                for (int i = 0; i < info.blockMaxLength; i++) {
                    info.memBuff.put(offset + i, temp[i]);
                }
                info.buffPointerList.add(0, Free);
                input.close();
                return Free;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return Free;
    }

    //存块
    private boolean save(BufferPointer blockpointer) {
        if (!blockpointer.isDirty)
            return true;
        File file = new File("/data/data/drz.oddb/Memory/" + blockpointer.blockNum);
        if (!file.exists()) {
            File path = file.getParentFile();
            if (!path.exists()) {
                if (path.mkdirs()) System.out.println("创建路径/data/data/drz.oddb/Memory/成功！");
                System.out.println("创建文件夹成功！");
            }
            try {
                if (file.createNewFile()) {
                    System.out.println("创建文件成功！");
                }
            } catch (IOException e) {
                System.out.println("创建文件失败！");
                e.printStackTrace();
            }
        }
        int offset;
        try {
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buff = new byte[info.blockMaxLength];
            offset = blockpointer.bufferId;
            for (int i = 0; i < info.blockMaxLength; i++) {
                buff[i] = info.memBuff.get(offset * info.blockMaxLength + i);
            }
            output.write(buff, 0, info.blockMaxLength);
            output.flush();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //创建块
    public BufferPointer creatBlock() {
        BufferPointer newblockpointer = new BufferPointer();
        if (info.buffPointerList.size() == info.bufferMaxLength) {
            if (info.cleanList.size() > 0) {
                if (save(info.buffPointerList.get(info.cleanList.get(0)))) {
                    info.canUse[info.buffPointerList.get(info.cleanList.get(0)).bufferId] = true;
                    info.buffPointerList.remove(info.cleanList.get(0));
                    info.cleanList.remove(0);
                }
            } else {
                for (int i = 0; i < info.bufferMaxLength; i++) {
                    if (save(info.buffPointerList.get(info.cleanList.get(i)))) {
                        info.canUse[info.buffPointerList.get(info.cleanList.get(i)).bufferId] = true;
                    }
                }
                info.buffPointerList.clear();
            }
//            if(save(buffPointerList.get(bufferMaxLength-1))){
//                canUse[buffPointerList.get(bufferMaxLength-1).bufferId]=true;
//                buffPointerList.remove(bufferMaxLength-1);
//            }
        }
        for (int i = 0; i < info.bufferMaxLength; i++) {
            if (info.canUse[i]) {
                newblockpointer.bufferId = i;
                info.canUse[i] = false;
                break;
            }
        }
        info.maxBlockNum++;
        if (info.blockSpace != null) {
            int[] s = new int[info.maxBlockNum + 1];
            for (int i = 0; i < info.maxBlockNum; i++) {
                s[i] = info.blockSpace[i];
            }
            s[info.maxBlockNum] = info.blockMaxLength - 8;
            info.blockSpace = s;
        } else {
            info.blockSpace = new int[1];
            info.blockSpace[0] = info.blockMaxLength - 8;
        }
        newblockpointer.blockNum = info.maxBlockNum;
        newblockpointer.isDirty = true;
        if (info.cleanList.contains(newblockpointer.bufferId))
            info.cleanList.remove(newblockpointer.bufferId);
        byte[] header = new byte[8];
        byte[] start = Byte2S.int2Bytes(8, 4);
        byte[] end = Byte2S.int2Bytes(info.blockMaxLength, 4);
        for (int i = 0; i < 8; i++) {
            if (i < 4) {
                header[i] = start[i];
            } else {
                header[i] = end[i - 4];
            }
        }
        for (int i = 0; i < 8; i++) {
            info.memBuff.put(newblockpointer.bufferId * info.blockMaxLength + i, header[i]);
        }
        byte x = (byte) 32;
        for (int i = 4; i < info.blockMaxLength; i++) {
            info.memBuff.put(newblockpointer.bufferId * info.blockMaxLength + i, x);
        }
        info.buffPointerList.add(0, newblockpointer);
        return newblockpointer;
    }


    //从磁盘加载块空间信息
    private void loadBlockSpace() {
        File file = new File("/data/data/drz.oddb/Memory/blockspace");
        if (file.exists()) {
            try {
                FileInputStream input = new FileInputStream(file);
                byte[] temp = new byte[4];
                input.read(temp, 0, 4);
                info.maxBlockNum = Byte2S.bytes2Int(temp, 0, 4);
                info.blockSpace = new int[info.maxBlockNum + 1];
                for (int i = 0; i <= info.maxBlockNum; i++) {
                    input.read(temp, 0, 4);
                    info.blockSpace[i] = Byte2S.bytes2Int(temp, 0, 4);
                }
                input.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            info.blockSpace = null;
        }
    }

    //存入块空间信息到磁盘
    private boolean saveBlockSpace() {
        File file = new File("/data/data/drz.oddb/Memory/blockspace");
        if (!file.exists()) {
            File path = file.getParentFile();
            if (!path.exists()) {
                if (path.mkdirs()) System.out.println("创建路径/data/data/drz.oddb/Memory/成功！");
            }
            try {
                if (file.createNewFile()) System.out.println("创建成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
            byte[] temp = Byte2S.int2Bytes(info.maxBlockNum, 4);
            output.write(temp, 0, 4);
            for (int i = 0; i <= info.maxBlockNum; i++) {
                temp = Byte2S.int2Bytes(info.blockSpace[i], 4);
                output.write(temp, 0, 4);
            }
            output.flush();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
