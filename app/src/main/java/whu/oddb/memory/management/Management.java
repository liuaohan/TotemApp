package whu.oddb.memory.management;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import whu.oddb.memory.table.Attribute;
import whu.oddb.memory.table.AttributeTable;
import whu.oddb.memory.table.BiPointer;
import whu.oddb.memory.table.BiPointerTable;
import whu.oddb.memory.table.ClassTable;
import whu.oddb.memory.table.Deputy;
import whu.oddb.memory.table.DeputyRule;
import whu.oddb.memory.table.DeputyRuleTable;
import whu.oddb.memory.table.DeputyTable;
import whu.oddb.memory.table.Switching;
import whu.oddb.memory.table.SwitchingRule;
import whu.oddb.memory.table.SwitchingRuleTable;
import whu.oddb.memory.table.SwitchingTable;
import whu.oddb.memory.util.Byte2S;

public class Management {

    public ManageInformation info;

    public Management() {
        info = new ManageInformation();
        initBuffers();
        loadBlockSpace();
    }

    //初始化缓冲区使用位图
    private void initBuffers() {
        for (int i = 0; i < info.bufferMaxLength; i++) {
            info.canUse[i] = true;
        }
    }

    //从磁盘加载块空间信息
    private void loadBlockSpace() {
        File file = new File("/data/data/oddb.SqlParser/memory/blockSpace");
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

    //加载Class表
    public ClassTable loadClassTable() {
        ClassTable res = new ClassTable();
        whu.oddb.memory.table.Class temp;
        File classtab = new File("/data/data/oddb.SqlParser/systemTable/class");
        if (classtab.exists()) {
            try {
                FileInputStream input = new FileInputStream(classtab);
                byte[] x = new byte[4];
                if (input.read(x, 0, 4) != -1) {
                    res.setMaxId(Byte2S.bytes2Int(x, 0, 4));
                }
                byte[] buff = new byte[12 + info.attrMaxLength];
                while (input.read(buff, 0, 12 + info.attrMaxLength) != -1) {
                    temp = new whu.oddb.memory.table.Class();
                    temp.setClassName(Byte2S.byte2str(buff, 0, info.attrMaxLength));
                    temp.setClassId(Byte2S.bytes2Int(buff, info.attrMaxLength, 4));
                    temp.setAttrsNum(Byte2S.bytes2Int(buff, info.attrMaxLength + 4, 4));
                    temp.setClassType(Byte2S.bytes2Int(buff, info.attrMaxLength + 8, 4));
                    res.getClassTable().add(temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    //存Class表
    public boolean saveClassTable(ClassTable tab) {
        File classtab = new File("/data/data/oddb.SqlParser/systemTable/class");
        if (!classtab.exists()) {
            File path = classtab.getParentFile();
            System.out.println(path.getAbsolutePath());
            if (!path.exists()) {
                if (path.mkdirs())
                    System.out.println("创建路径/data/data/oddb.SqlParser/systemTable/成功！");
            }
            try {
                if (classtab.createNewFile()) {
                    System.out.println("创建路径/data/data/oddb.SqlParser/systemTable/class！");
                }
                System.out.println("创建文件成功！");
            } catch (IOException e) {
                System.out.println("创建文件失败！");
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(classtab));
            byte[] size = Byte2S.int2Bytes(tab.getMaxId(), 4);
            output.write(size, 0, size.length);
            System.out.println(tab.getMaxId());
            for (int i = 0; i < tab.getClassTable().size(); i++) {
                byte[] s1 = Byte2S.str2Bytes(tab.getClassTable().get(i).getClassName(), info.attrMaxLength);
                output.write(s1, 0, s1.length);
                byte[] i1 = Byte2S.int2Bytes(tab.getClassTable().get(i).getClassId(), 4);
                output.write(i1, 0, i1.length);
                byte[] i2 = Byte2S.int2Bytes(tab.getClassTable().get(i).getAttrsNum(), 4);
                output.write(i2, 0, i2.length);
                byte[] i3 = Byte2S.int2Bytes(tab.getClassTable().get(i).getClassType(), 4);
                output.write(i3, 0, i3.length);
            }
            output.flush();
            output.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到！");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("文件未正常写入！");
            e.printStackTrace();
        }
        return false;
    }


    //加载Attribute表
    public AttributeTable loadAttributeTable() {
        AttributeTable res = new AttributeTable();
        Attribute temp;
        File classtab = new File("/data/data/oddb.SqlParser/systemTable/attribute");
        if (classtab.exists()) {
            try {
                FileInputStream input = new FileInputStream(classtab);
                byte[] x = new byte[4];
                if (input.read(x, 0, 4) != -1) {
                    res.setMaxId(Byte2S.bytes2Int(x, 0, 4));
                }
                byte[] buff = new byte[12 + info.attrMaxLength * 2];
                while (input.read(buff, 0, 12 + info.attrMaxLength * 2) != -1) {
                    temp = new Attribute();
                    temp.setClassId(Byte2S.bytes2Int(buff, 0, 4));
                    temp.setAttrId(Byte2S.bytes2Int(buff, 4, 4));
                    temp.setAttrName(Byte2S.byte2str(buff, 8, info.attrMaxLength));
                    temp.setAttrType(Byte2S.byte2str(buff, info.attrMaxLength + 8, info.attrMaxLength));
                    temp.setDeputy((byte) Byte2S.bytes2Int(buff, info.attrMaxLength * 2 + 8, 1));

                    res.getAttributeTable().add(temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    //存Attribute表
    public boolean saveAttributeTable(AttributeTable tab) {
        File classtab = new File("/data/data/oddb.SqlParser/systemTable/attribute");
        if (!classtab.exists()) {
            File path = classtab.getParentFile();
            System.out.println(path.getAbsolutePath());
            if (!path.exists()) {
                if (path.mkdirs())
                    System.out.println("创建路径/data/data/oddb.SqlParser/transaction/成功！");
            }
            try {
                if (classtab.createNewFile())
                    System.out.println("创建路径/data/data/oddb.SqlParser/transaction/attributetable成功！");
                System.out.println("创建文件成功！");
            } catch (IOException e) {
                System.out.println("创建文件失败！");
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(classtab));
            byte[] maxi = Byte2S.int2Bytes(tab.getMaxId(), 4);
            output.write(maxi, 0, maxi.length);
            System.out.println(tab.getAttributeTable().size());
            for (int i = 0; i < tab.getAttributeTable().size(); i++) {
                byte[] i1 = Byte2S.int2Bytes(tab.getAttributeTable().get(i).getClassId(), 4);
                output.write(i1, 0, i1.length);
                byte[] i2 = Byte2S.int2Bytes(tab.getAttributeTable().get(i).getAttrId(), 4);
                output.write(i2, 0, i2.length);
                byte[] s1 = Byte2S.str2Bytes(tab.getAttributeTable().get(i).getAttrName(), info.attrMaxLength);
                output.write(s1, 0, s1.length);
                byte[] s2 = Byte2S.str2Bytes(tab.getAttributeTable().get(i).getAttrType(), info.attrMaxLength);
                output.write(s2, 0, s2.length);

                byte b1 = tab.getAttributeTable().get(i).isDeputy();
                output.write(b1);
            }
            output.flush();
            output.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到！");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("文件未正常写入！");
            e.printStackTrace();
        }
        return false;
    }


    //加载Detupy表
    public DeputyTable loadDeputyTable() {
        DeputyTable res = new DeputyTable();
        Deputy temp;


        File deputytab = new File("/data/data/oddb.SqlParser/systemTable/deputy");
        if (deputytab.exists()) {
            try {
                FileInputStream input = new FileInputStream(deputytab);
                byte[] buff = new byte[12];
                while (input.read(buff, 0, 12) != -1) {
                    temp = new Deputy();
                    temp.setOriginClassId(Byte2S.bytes2Int(buff, 0, 4));
                    temp.setDeputyClassId(Byte2S.bytes2Int(buff, 4, 4));
                    temp.setDeputyRuleId(Byte2S.bytes2Int(buff, 8, 4));
                    res.getDeputyTable().add(temp);
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    //存Deputy表
    public boolean saveDeputyTable(DeputyTable tab) {
        File deputytab = new File("/data/data/oddb.SqlParser/systemTable/deputy");
        if (!deputytab.exists()) {
            File path = deputytab.getParentFile();
            if (!path.exists()) {
                if (path.mkdirs())
                    System.out.println("创建路径/data/data/oddb.SqlParser/systemTable/成功！");
            }
            try {
                if (deputytab.createNewFile())
                    System.out.println("创建文件/data/data/oddb.SqlParser/systemTable/deputy！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(deputytab));
            for (int i = 0; i < tab.getDeputyTable().size(); i++) {
                byte[] i1 = Byte2S.int2Bytes(tab.getDeputyTable().get(i).getOriginClassId(), 4);
                output.write(i1, 0, i1.length);
                byte[] i2 = Byte2S.int2Bytes(tab.getDeputyTable().get(i).getDeputyClassId(), 4);
                output.write(i2, 0, i2.length);
                byte[] i3 = Byte2S.int2Bytes(tab.getDeputyTable().get(i).getDeputyRuleId(), 4);
                output.write(i3, 0, i3.length);
            }
            output.flush();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    //加载DetupyRule表
    public DeputyRuleTable loadDeputyRuleTable() {
        DeputyRuleTable res = new DeputyRuleTable();
        DeputyRule temp;
        File deputytab = new File("/data/data/oddb.SqlParser/systemTable/deputyrule");
        if (deputytab.exists()) {
            try {
                FileInputStream input = new FileInputStream(deputytab);
                byte[] x = new byte[4];
                if (input.read(x, 0, 4) != -1) {
                    res.setMaxId(Byte2S.bytes2Int(x, 0, 4));
                }
                byte[] buff = new byte[4 + info.attrMaxLength];
                while (input.read(buff, 0, 4 + info.attrMaxLength) != -1) {
                    temp = new DeputyRule();
                    temp.setDeputyRuleId(Byte2S.bytes2Int(buff, 0, 4));
                    temp.setRule(Byte2S.byte2str(buff, 4, info.attrMaxLength));
                    res.getDeputyRuleTable().add(temp);
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    //存DeputyRule表
    public boolean saveDeputyRuleTable(DeputyRuleTable tab) {
        File deputytab = new File("/data/data/oddb.SqlParser/systemTable/deputyrule");
        if (!deputytab.exists()) {
            File path = deputytab.getParentFile();
            if (!path.exists()) {
                if (path.mkdirs())
                    System.out.println("创建路径/data/data/oddb.SqlParser/systemTable/成功！");
            }
            try {
                if (deputytab.createNewFile())
                    System.out.println("创建文件/data/data/oddb.SqlParser/systemTable/deputyrule成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(deputytab));
            byte[] maxi = Byte2S.int2Bytes(tab.getMaxId(), 4);
            output.write(maxi, 0, maxi.length);
            for (int i = 0; i < tab.getDeputyRuleTable().size(); i++) {
                byte[] i1 = Byte2S.int2Bytes(tab.getDeputyRuleTable().get(i).getDeputyRuleId(), 4);
                output.write(i1, 0, i1.length);
                byte[] s1 = Byte2S.str2Bytes(tab.getDeputyRuleTable().get(i).getRule(), info.attrMaxLength);
                output.write(s1, 0, s1.length);
            }
            output.flush();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    //加载Switching表
    public SwitchingTable loadSwitchingTable() {
        SwitchingTable res = new SwitchingTable();
        Switching temp;
        File switab = new File("/data/data/oddb.SqlParser/systemTable/switching");
        if (switab.exists()) {
            try {
                FileInputStream input = new FileInputStream(switab);
                byte[] x = new byte[4];
                if (input.read(x, 0, 4) != -1) {
                    res.setMaxId(Byte2S.bytes2Int(x, 0, 4));
                }
                byte buff[] = new byte[4 * 4];
                while (input.read(buff, 0, 4 * 4) != -1) {
                    temp = new Switching();
                    temp.setOriginClassId(Byte2S.bytes2Int(buff, 0, 4));
                    temp.setDeputyClassId(Byte2S.bytes2Int(buff, 4, 8));
                    temp.setOriginAttrId(Byte2S.bytes2Int(buff, 8, 12));
                    temp.setDeputyAttrId(Byte2S.bytes2Int(buff, 12, 16));
                    res.getSwitchingTable().add(temp);
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    //存Switching表
    public boolean saveSwitchingTable(SwitchingTable tab) {
        File switab = new File("/data/data/oddb.SqlParser/systemTable/switching");
        if (!switab.exists()) {
            File path = switab.getParentFile();
            if (!path.exists()) {
                if (path.mkdirs())
                    System.out.println("创建路径/data/data/oddb.SqlParser/systemTable/成功！");
            }
            try {
                if (switab.createNewFile())
                    System.out.println("创建文件/data/data/oddb.SqlParser/systemTable/switchingtable成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(switab));
            byte[] maxi = Byte2S.int2Bytes(tab.getMaxId(), 4);
            output.write(maxi, 0, maxi.length);
            for (int i = 0; i < tab.getSwitchingTable().size(); i++) {
                byte[] i1 = Byte2S.int2Bytes(tab.getSwitchingTable().get(i).getOriginClassId(), 4);
                output.write(i1, 0, i1.length);
                byte[] i2 = Byte2S.int2Bytes(tab.getSwitchingTable().get(i).getDeputyClassId(), 4);
                output.write(i2, 0, i2.length);
                byte[] i3 = Byte2S.int2Bytes(tab.getSwitchingTable().get(i).getOriginAttrId(), 4);
                output.write(i3, 0, i3.length);
                byte[] i4 = Byte2S.int2Bytes(tab.getSwitchingTable().get(i).getDeputyAttrId(), 4);
                output.write(i4, 0, i4.length);
            }
            output.flush();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    //加载SwitchingRule表
    public SwitchingRuleTable loadSwitchingRuleTable() {
        SwitchingRuleTable res = new SwitchingRuleTable();
        SwitchingRule temp;
        File swirtab = new File("/data/data/oddb.SqlParser/systemTable/switchingRule");
        if (swirtab.exists()) {
            try {
                FileInputStream input = new FileInputStream(swirtab);
                byte[] x = new byte[4];
                if (input.read(x, 0, 4) != -1) {
                    res.setMaxId(Byte2S.bytes2Int(x, 0, 4));
                }
                byte[] buff = new byte[4 + info.attrMaxLength];
                while (input.read(buff, 0, 4 + info.attrMaxLength) != -1) {
                    temp = new SwitchingRule();
                    temp.setSwitchRuleId(Byte2S.bytes2Int(buff, 0, 4));
                    temp.setRule(Byte2S.byte2str(buff, 4, info.attrMaxLength));
                    res.getSwitchingRuleTable().add(temp);
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }


    //存Switching表
    public boolean saveSwitchingRuleTable(SwitchingRuleTable tab) {
        File swirtab = new File("/data/data/oddb.SqlParser/systemTable/switchingRule");
        if (!swirtab.exists()) {
            File path = swirtab.getParentFile();
            if (!path.exists()) {
                if (path.mkdirs())
                    System.out.println("创建路径/data/data/oddb.SqlParser/systemTable/成功！");
            }
            try {
                if (swirtab.createNewFile())
                    System.out.println("创建文件/data/data/oddb.SqlParser/systemTable/switchingRule成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(swirtab));
            byte[] maxi = Byte2S.int2Bytes(tab.getMaxId(), 4);
            output.write(maxi, 0, maxi.length);
            for (int i = 0; i < tab.getSwitchingRuleTable().size(); i++) {
                byte[] i1 = Byte2S.int2Bytes(tab.getSwitchingRuleTable().get(i).getSwitchRuleId(), 4);
                output.write(i1, 0, i1.length);
                byte[] s1 = Byte2S.str2Bytes(tab.getSwitchingRuleTable().get(i).getRule(), info.attrMaxLength);
                output.write(s1, 0, s1.length);
            }
            output.flush();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    //加载BiPointer表
    public BiPointerTable loadBiPointerTable() {
        BiPointerTable res = new BiPointerTable();
        BiPointer temp;
        File bitab = new File("/data/data/oddb.SqlParser/systemTable/biPointer");
        if (bitab.exists()) {
            try {
                FileInputStream input = new FileInputStream(bitab);
                byte[] buff = new byte[16];
                while (input.read(buff, 0, 16) != -1) {
                    temp = new BiPointer();
                    temp.setOriginClassId(Byte2S.bytes2Int(buff, 0, 4));
                    temp.setOriginObjectId(Byte2S.bytes2Int(buff, 4, 4));
                    temp.setDeputyClassId(Byte2S.bytes2Int(buff, 8, 4));
                    temp.setDeputyObjectId(Byte2S.bytes2Int(buff, 12, 4));
                    res.getBiPointerTable().add(temp);
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    //存BiPointer表
    public boolean saveBiPointerTable(BiPointerTable tab) {
        File bitab = new File("/data/data/oddb.SqlParser/systemTable/biPointer");
        if (!bitab.exists()) {
            File path = bitab.getParentFile();
            if (!path.exists()) {
                if (path.mkdirs())
                    System.out.println("创建路径/data/data/oddb.SqlParser/systemTable/成功！");
            }
            try {
                if (bitab.createNewFile())
                    System.out.println("创建文件/data/data/oddb.SqlParser/systemTable/biPointer！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(bitab));
            for (int i = 0; i < tab.getBiPointerTable().size(); i++) {
                byte[] i1 = Byte2S.int2Bytes(tab.getBiPointerTable().get(i).getOriginClassId(), 4);
                output.write(i1, 0, i1.length);
                byte[] i2 = Byte2S.int2Bytes(tab.getBiPointerTable().get(i).getOriginObjectId(), 4);
                output.write(i2, 0, i2.length);
                byte[] i3 = Byte2S.int2Bytes(tab.getBiPointerTable().get(i).getDeputyClassId(), 4);
                output.write(i3, 0, i3.length);
                byte[] i4 = Byte2S.int2Bytes(tab.getBiPointerTable().get(i).getDeputyObjectId(), 4);
                output.write(i4, 0, i4.length);
            }
            output.flush();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    //加载块
    private BufferPointer load(int block) {
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
        File file = new File("/data/data/oddb.SqlParser/Memory/" + block);
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
        File file = new File("/data/data/oddb.SqlParser/Memory/" + blockpointer.blockNum);
        if (!file.exists()) {
            File path = file.getParentFile();
            if (!path.exists()) {
                if (path.mkdirs()) System.out.println("创建路径/data/data/oddb.SqlParser/Memory/成功！");
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
            byte[] buff = new byte[info.maxBlockNum];
            offset = blockpointer.bufferId;
            for (int i = 0; i < info.maxBlockNum; i++) {
                buff[i] = info.memBuff.get(offset * info.maxBlockNum + i);
            }
            output.write(buff, 0, info.maxBlockNum);
            output.flush();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
