package oddb.execute;


import android.app.AlertDialog;
import android.content.Context;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import oddb.SqlParser.ast.DeleteStatement;
import oddb.SqlParser.ast.Statement;
import oddb.SqlParser.generated.*;
import oddb.SqlParser.parseStmt.CreateSelStmt;
import oddb.SqlParser.parseStmt.CreateStmt;
import oddb.SqlParser.parseStmt.DeleteStmt;
import oddb.SqlParser.parseStmt.InsertStmt;
import oddb.SqlParser.parseStmt.SelectStmt;
import oddb.SqlParser.parseStmt.UpdateStmt;
import oddb.execute.Check;
import oddb.execute.DML;
import oddb.memory.management.ManageInformation;
import oddb.memory.management.Management;
import oddb.memory.management.TupleManage;
import oddb.memory.table.Attribute;
import oddb.memory.table.AttributeTable;
import oddb.memory.table.BiPointer;
import oddb.memory.table.BiPointerTable;
import oddb.memory.table.Class;
import oddb.memory.table.ClassTable;
import oddb.memory.table.Deputy;
import oddb.memory.table.DeputyRule;
import oddb.memory.table.DeputyRuleTable;
import oddb.memory.table.DeputyTable;
import oddb.memory.table.ObjectTable;
import oddb.memory.table.ObjectTableItem;
import oddb.memory.table.Switching;
import oddb.memory.table.SwitchingRule;
import oddb.memory.table.SwitchingRuleTable;
import oddb.memory.table.SwitchingTable;
import oddb.execute.DDL;
import oddb.memory.table.Tuple;
import oddb.memory.table.TupleList;


public class Portal {
    public Portal() {
        //this.context = context;

    }

    Context context;
    //内存管理器创建
    DDL ddl = new DDL();
    DML dml = new DML();
    public Management management = new Management();
    public TupleManage tupleManage = new TupleManage(management.info);
    public ClassTable classTable = management.loadClassTable();
    public AttributeTable attributeTable = management.loadAttributeTable();
    public DeputyTable deputyTable = management.loadDeputyTable();
    public DeputyRuleTable deputyRuleTable = management.loadDeputyRuleTable();
    public SwitchingTable switchingTable = management.loadSwitchingTable();
    public SwitchingRuleTable switchingRuleTable = management.loadSwitchingRuleTable();
    public BiPointerTable biPointerTable = management.loadBiPointerTable();
    //?????
    public ObjectTable objectTable = new ObjectTable();



    private static Statement parseSql(String sql) {
        try {
            AndroidTotemSqlParser parser = new AndroidTotemSqlParser(new ByteArrayInputStream(sql.getBytes()));
            return parser.statement();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }




    public void query(String sql){
        Statement statement = parseSql(sql);
        if(statement == null){
            alertDialog("解析失败");
        }

        switch (statement.getType()){
            case CREATE_CLASS:
                createOriginClass(new CreateStmt(statement));
                break;
            case INSERT:
                insert(new InsertStmt(statement));
            default:
                System.out.println("未知语句");
                alertDialog("未知语句");
        }

    }

    private void alertDialog(String message) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }

    //创建原始类
    public void createOriginClass(CreateStmt createStmt){
        String classname = createStmt.classname;
        int attrsnum = createStmt.cols.size();
        ddl.createOriginClass(classTable,classname,attrsnum,attributeTable,createStmt.cols);
    }

    //创建代理类
    public void createDeputyClass(CreateSelStmt createSelStmt){
        String classname = createSelStmt.classname;
        String deputyrule = createSelStmt.whereclause;
        int attrsnum = createSelStmt.deputyattrs.size() + createSelStmt.relattrs.size();
        int originClassId = getClassId(createSelStmt.originname);

        ddl.createDeputyClass(classTable,classname,attrsnum,deputyTable,deputyRuleTable,originClassId,deputyrule);


    }

    //insert
    public int insert(InsertStmt insertStmt){
        String classname = insertStmt.classname;
        int classId = getClassId(classname);
        List<Attribute> attributes = getClassAtt(classId);
        int attrsSize = attributes.size();
        Object[] attrstuple = new Object[attrsSize];
        //将属性
        for(int i = 0; i < insertStmt.attrnames.size(); i++){
            int pos = getpos(attributes, insertStmt.attrnames.get(i));
            attrstuple[pos] = insertStmt.attrvalues.get(i);
        }

        Tuple tuple = new Tuple(attrstuple);
        tuple.setTupleHeader(attrsSize);

        int[] blockattrs = InsertTuple(tuple);
        objectTable.maxTupleId ++;
        int tupleId = objectTable.maxTupleId;
        objectTable.objectTable.add(new ObjectTableItem(classId, tupleId, blockattrs[0],blockattrs[1]));

        return tupleId;
    }

    //delete
    public void delete(DeleteStmt deleteStmt){
        String classname = deleteStmt.classname;
        int classid = getClassId(classname);
        List<Attribute> attributes = getExistAttr(deleteStmt.whereclause, classid);

        OandB ob2 = new OandB();

        for(ObjectTableItem objectTableItem : objectTable.objectTable){
            if(objectTableItem.getClassId() == classid){
                Tuple tuple = GetTuple(objectTableItem.getBlockId(),objectTableItem.getOffset());
                HashMap<String, String> params = new HashMap<>();
                for(int i = 0; i < attributes.size(); i++){
                    params.put(attributes.get(i).getAttrName(), tuple.getTuple()[attributes.get(i).getAttrId()].toString());
                }
                Check check = new Check();
                if (check.isEnable(deleteStmt.whereclause, params)) {
                    OandB ob = new OandB(DeletebyID(objectTableItem.getObjectId()));
                    for (ObjectTableItem obj : ob.objects) {
                        ob2.objects.add(obj);
                    }
                    for (BiPointer bip : ob.biPointers) {
                        ob2.biPointers.add(bip);
                    }
                }
            }
        }
        for (ObjectTableItem obj : ob2.objects) {
            objectTable.objectTable.remove(obj);
        }
        for (BiPointer bip : ob2.biPointers) {
            List<BiPointer> bipointertable = biPointerTable.getBiPointerTable();
            bipointertable.remove(bip);
            biPointerTable.setBiPointerTable(bipointertable);
        }

    }

//    public Tuple select(SelectStmt selectStmt){
//        TupleList tupleList = new TupleList();
//        int classId = getClassId(selectStmt.classname);
//        int oringinId ;
//
//        boolean flag = false;
//
//    }


    public void update(UpdateStmt updateStmt){
        String classname = updateStmt.classname;
        int classid = getClassId(classname);


    }
    /**
     * 化简表达式
     * 将表达式中的 {}[]替换为()
     * 负数的处理
     * 为了方便将中缀转换为后缀在字符串前后分别加上(,) eg:"1+1" 变为"(1+1)"
     *
     * @param str 输入的字符串
     * @return s 返回简化完的表达式
     */
    public static String simplify(String str) {
        //负数的处理
        // 处理负数，这里在-前面的位置加入一个0，如-4变为0-4，
        // 细节：注意-开头的地方前面一定不能是数字或者反括号，如9-0,(3-4)-5，这里地方是不能加0的
        // 它的后面可以是数字或者正括号，如-9=>0-9, -(3*3)=>0-(3*3)
        String s = str.replaceAll("(?<![0-9)}\\]])(?=-[0-9({\\[])", "0");
        //将表达式中的 {}[]替换为()
        s = s.replace('[', '(');
        s = s.replace('{', '(');
        s = s.replace(']', ')');
        s = s.replace('}', ')');
        //为了方便将中缀转换为后缀在字符串前后分别加上(,)
        s = "(" + s + ")";

        return s;
    }

    //根据类名获取类id
    public int getClassId(String classname){
        for (int i = 0; i < classTable.getClassTable().size(); i++)
            if (classname.equals(classTable.getClassTable().get(i).getClassName()))
                return classTable.getClassTable().get(i).getClassId();
        System.out.println("不存在该类名");
        return -1;
    }

    //获取一个类的所有属性列表
    public List<Attribute> getClassAtt(int classid) {
        List<Attribute> attributes = new ArrayList();
        for (int i = 0; i < attributeTable.getAttributeTable().size(); i++)
            if (attributeTable.getAttributeTable().get(i).getClassId() == classid)
                attributes.add(attributeTable.getAttributeTable().get(i));
        return attributes;
    }

    //获取属性所在的位置，用于把值放在insert tuple中的正确位置
    public int getpos(List<Attribute> ls, String attrname) {
        for (int i = 0; i < ls.size(); i++)
            if (ls.get(i).getAttrName().equals(attrname))
                return ls.get(i).getAttrId();
        System.out.println("不存在相关属性位置");
        return -1;
    }

    //获取字符串中出现过的属性信息
    public List<Attribute> getExistAttr(String exp, int classid) {
        List<Attribute> ls = getClassAtt(classid);
        List<Attribute> result = new ArrayList();
        for (int i = 0; i < ls.size(); i++)
            if (exp.contains(ls.get(i).getAttrName()))
                result.add(ls.get(i));
        return result;
    }

    public void updateDeputyObject(Object object){

    }

    private Tuple GetTuple(int id, int offset) {

        return tupleManage.readTuple(id, offset);
    }

    private int[] InsertTuple(Tuple tuple) {
        return tupleManage.writeTuple(tuple);
    }

    private void DeleteTuple(int id, int offset) {
        //tupleManage.deleteTuple();
    }

    private void UpateTuple(Tuple tuple, int blockid, int offset) {
        tupleManage.UpateTuple(tuple, blockid, offset);
    }

    private class OandB {
        public List<ObjectTableItem> objects = new ArrayList<>();
        public List<ObjectTableItem> objects2 = new ArrayList<>();
        public List<BiPointer> biPointers = new ArrayList<>();

        public OandB() {
        }

        public OandB(OandB oandB) {
            this.objects = oandB.objects;
            this.objects2 = oandB.objects2;
            this.biPointers = oandB.biPointers;
        }

        public OandB(List<ObjectTableItem> o, List<ObjectTableItem> o2, List<BiPointer> b) {
            this.objects = o;
            this.objects2 = o2;
            this.biPointers = b;
        }
    }
    private OandB DeletebyID(int id) {

        List<ObjectTableItem> todelete1 = new ArrayList<>();
        List<ObjectTableItem> todelete3 = new ArrayList<>();
        List<BiPointer> todelete2 = new ArrayList<>();
        OandB ob = new OandB(todelete1, todelete3, todelete2);
        for (Iterator it1 = objectTable.objectTable.iterator(); it1.hasNext(); ) {
            ObjectTableItem item = (ObjectTableItem) it1.next();
            if (item.getObjectId() == id) {
                //需要删除的tuple


                //删除代理类的元组
                int deobid = 0;

                for (Iterator it = biPointerTable.getBiPointerTable().iterator(); it.hasNext(); ) {
                    BiPointer biPointer = (BiPointer) it.next();
                    if (item.getObjectId() == biPointer.getDeputyObjectId()) {
                        //it.remove();
                        if (!todelete2.contains(biPointer))
                            todelete2.add(biPointer);
                    }
                    if (item.getObjectId() == biPointer.getOriginObjectId()) {
                        deobid = biPointer.getDeputyObjectId();
                        OandB ob2 = new OandB(DeletebyID(deobid));

                        for (ObjectTableItem obj : ob2.objects) {
                            if (!todelete1.contains(obj))
                                todelete1.add(obj);
                        }
                        for (BiPointer bip : ob2.biPointers) {
                            if (!todelete2.contains(bip))
                                todelete2.add(bip);
                        }

                        //biPointerT.biPointerTable.remove(item1);

                    }
                }


                //删除自身
                DeleteTuple(item.getBlockId(), item.getOffset());
                if (!todelete2.contains(item))
                    todelete1.add(item);
            }
        }

        return ob;
    }




}
