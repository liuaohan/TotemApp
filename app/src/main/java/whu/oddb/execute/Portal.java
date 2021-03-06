package whu.oddb.execute;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import whu.oddb.SqlParser.ast.CreateDeputyStatement;
import whu.oddb.SqlParser.ast.DropStatement;
import whu.oddb.SqlParser.ast.Statement;
import whu.oddb.SqlParser.generated.AndroidTotemSqlParser;
import whu.oddb.SqlParser.generated.ParseException;
import whu.oddb.SqlParser.parseStmt.CreateSelStmt;
import whu.oddb.SqlParser.parseStmt.CreateStmt;
import whu.oddb.SqlParser.parseStmt.DeleteStmt;
import whu.oddb.SqlParser.parseStmt.DropStmt;
import whu.oddb.SqlParser.parseStmt.InsertStmt;
import whu.oddb.SqlParser.parseStmt.SelectStmt;
import whu.oddb.SqlParser.parseStmt.UpdateStmt;
import whu.oddb.SqlParser.parseStmt.attrcontext;
import whu.oddb.memory.management.ManageInformation;
import whu.oddb.memory.management.Management;
import whu.oddb.memory.management.TupleManage;
import whu.oddb.memory.table.Class;
import whu.oddb.memory.table.*;
import whu.oddb.memory.table.TupleList;
import whu.oddb.memory.table.Tuplesp;
import whu.oddb.show.PrintResult;
import whu.oddb.show.ShowTable;


public class Portal {
    public Portal(Context context) {
        this.context = context;

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


    public void query(String sql) {
        Statement statement = parseSql(sql);
        if (statement == null) {
            alertDialog("解析失败");
        }

        switch (statement.getType()) {
            case CREATE_CLASS:
                createOriginClass(new CreateStmt(statement));
                alertDialog("创建成功");
                break;
                //new AlertDialog.Builder(context).setTitle("提示").setMessage("创建成功").setPositiveButton("确定", null).show();
            case CREATE_DEPUTY:
                createDeputyClass(new CreateSelStmt(statement));
                alertDialog("创建代理类成功");
                break;
            case DROP:
                Drop(new DropStmt(statement));
                alertDialog("删除类及其对象成功");
                break;
            case INSERT:
                insert(new InsertStmt(statement));
                alertDialog("插入成功");
                break;
            case DELETE:
                delete(new DeleteStmt(statement));
                alertDialog("成功删除");
                break;
            case SELECT:
                select(new SelectStmt(statement));
                break;
            case UPDATE:
                update(new UpdateStmt(statement));
                alertDialog("更改成功");
                break;
            default:
                System.out.println("未知语句");
                alertDialog("未知语句");
                break;
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
    public void createOriginClass(CreateStmt createStmt) {
        String classname = createStmt.classname;
        int attrsnum = createStmt.cols.size();
        ddl.createOriginClass(classTable, classname, attrsnum, attributeTable, createStmt.cols);
    }

    //创建代理类
    public void createDeputyClass(CreateSelStmt createSelStmt) {
        String classname = createSelStmt.classname;
        String deputyrule = createSelStmt.whereclause;
        int attrsnum = createSelStmt.deputyattrs.size() + createSelStmt.relattrs.size();
        int originClassId = getClassId(createSelStmt.originname);

        ddl.createDeputyClass(classTable, classname, attrsnum, attributeTable, deputyTable, deputyRuleTable, originClassId, deputyrule, createSelStmt.relattrs, createSelStmt.deputyattrs);

        int deputyClassId = getClassId(classname);
        for (int i = 0; i < createSelStmt.deputyattrs.size(); i++) {
            switchingRuleTable.setMaxId(switchingRuleTable.getMaxId() + 1);
            SwitchingRule switchingRule = new SwitchingRule(switchingRuleTable.getMaxId(), createSelStmt.deputyattrs.get(i).switchrule);
            switchingRuleTable.getSwitchingRuleTable().add(switchingRule);
        }

    }
    //删除类
    public void Drop(DropStmt dropStmt){
        List<Deputy> dti;
        List<DeputyRule> dtir = new ArrayList<>();
        dti = DropObject(dropStmt);
        for(Deputy item:dti){
            for(DeputyRule deputyRuleTableItem:deputyRuleTable.getDeputyRuleTable()){
                if(deputyRuleTableItem.getDeputyRuleId()==item.getDeputyRuleId())
                    dtir.add(deputyRuleTableItem);
            }
            deputyTable.getDeputyTable().remove(item);
        }
        for(DeputyRule deputyRuleTableItem1:dtir){
            deputyRuleTable.getDeputyRuleTable().remove(deputyRuleTableItem1);
        }

    }

    private List<Deputy> DropObject(DropStmt dp){
        String classname = dp.classname;
        int classid = 0;
        //找到classid顺便 清除类表和switch表
        for (Iterator it1 = classTable.getClassTable().iterator(); it1.hasNext();) {
            Class item =(Class) it1.next();
            if (item.getClassName().equals(classname) ){
                classid = item.getClassId();
                for(Iterator it2 = attributeTable.getAttributeTable().iterator(); it2.hasNext();){
                    Attribute attributeTableItem = (Attribute) it2.next();
                    if(attributeTableItem.getClassId() == classid){
                        for(Iterator it = switchingRuleTable.getSwitchingRuleTable().iterator(); it.hasNext();) {
                            Switching item2 =(Switching) it.next();
                            if (item2.getDeputyClassId() == attributeTableItem.isDeputy()){
                                it.remove();
                            }
                        }
                        it2.remove();
                    }
                }
                it1.remove();
            }
        }
        //清元组表同时清了bi
        OandB ob2 = new OandB();
        for(ObjectTableItem item1:objectTable.objectTable){
            if(item1.getClassId() == classid){
                OandB ob = DeletebyID(item1.getObjectId());
                for(ObjectTableItem obj:ob.objects){
                    ob2.objects.add(obj);
                }
                for(BiPointer bip:ob.biPointers){
                    ob2.biPointers.add(bip);
                }
            }
        }
        for(ObjectTableItem obj:ob2.objects){
            objectTable.objectTable.remove(obj);
        }
        for(BiPointer bip:ob2.biPointers) {
            biPointerTable.getBiPointerTable().remove(bip);
        }

        //清deputy
        List<Deputy> dti = new ArrayList<>();
        for(Deputy item3:deputyTable.getDeputyTable()){
            if(item3.getDeputyClassId() == classid){
                if(!dti.contains(item3))
                    dti.add(item3);
            }
            if(item3.getOriginClassId() == classid){
                //删除代理类
                DropStmt dp2 = dp;

                for(Class item5: classTable.getClassTable()) {
                    if (item5.getClassId() == item3.getDeputyClassId()) {
                        dp2.classname = item5.getClassName();
                        List<Deputy> dti2 = DropObject(dp2);
                        for(Deputy item8:dti2){
                            if(!dti.contains(item8))
                                dti.add(item8);
                        }
                    }
                }
                if(!dti.contains(item3))
                    dti.add(item3);
            }
        }
        return dti;

    }
    //insert
    public int insert(InsertStmt insertStmt) {
        String classname = insertStmt.classname;
        int classId = getClassId(classname);
        List<Attribute> attributes = getClassAtt(classId);
        int attrsSize = attributes.size();
        Object[] attrstuple = new Object[attrsSize];
        //将属性
        for (int i = 0; i < insertStmt.attrnames.size(); i++) {
            int pos = getpos(attributes, insertStmt.attrnames.get(i));
            attrstuple[pos] = insertStmt.attrvalues.get(i).value;
        }

        Tuple tuple = new Tuple(attrstuple);
        tuple.setTupleHeader(attrsSize);

        int[] blockattrs = InsertTuple(tuple);
        objectTable.maxTupleId++;
        int tupleId = objectTable.maxTupleId;
        objectTable.objectTable.add(new ObjectTableItem(classId, tupleId, blockattrs[0], blockattrs[1]));

        return tupleId;
    }

    //delete
    public void delete(DeleteStmt deleteStmt) {
        String classname = deleteStmt.classname;
        int classid = getClassId(classname);
        List<Attribute> attributes = getExistAttr(deleteStmt.whereclause, classid);

        OandB ob2 = new OandB();

        for (ObjectTableItem objectTableItem : objectTable.objectTable) {
            if (objectTableItem.getClassId() == classid) {
                Tuple tuple = GetTuple(objectTableItem.getBlockId(), objectTableItem.getOffset());
                HashMap<String, String> params = new HashMap<>();
                for (int i = 0; i < attributes.size(); i++) {
                    params.put(attributes.get(i).getAttrName(), tuple.getTuple()[attributes.get(i).getAttrId()-1].toString());
                }
                Check check = new Check();
                if (check.isWhereEnable(deleteStmt.where, params)) {
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

    public TupleList select(SelectStmt selectStmt) {
        TupleList tupleList = new TupleList();
        int classId = getClassId(selectStmt.classname);
        int oringinId;

        boolean flag = false;
        for (attrcontext attrcontext : selectStmt.attrs) {
            if (attrcontext.isCross) {
                flag = true;
            }
        }

        if (flag) {
            ArrayList<String> crossclass = selectStmt.attrs.get(0).crossclass;
            String[] attrname = new String[selectStmt.attrs.size()];
            String[] attrtype = new String[selectStmt.attrs.size()];
            int[] attrid = new int[selectStmt.attrs.size()];
            int[] attrorder = new int[selectStmt.attrs.size()];
            int lastclassid = getClassId(crossclass.get(crossclass.size() - 1));
            for (int i = 0; i < selectStmt.attrs.size(); i++) {
                attrorder[i] = getAttrPos(lastclassid, selectStmt.attrs.get(i).attrname);
                attrid[i] = i;
                attrname[i] = selectStmt.attrs.get(i).attrname;
                attrtype[i] = getAttrType(lastclassid, selectStmt.attrs.get(i).attrname);
            }
            List<Integer> firstlist = new ArrayList<>();
            Class classinfo = getclass(selectStmt.classname);
            List<Tuplesp> origintuples;
            boolean originflag = true;
            if (classinfo.getClassType() == 0) {
                oringinId = classId;
                origintuples = getOriginTuple(classId, selectStmt.whereclause);
            } else {
                origintuples = new ArrayList();
                Deputy deputy = getDeputy(classId);
                List<Tuplesp> tmpsp = getDeputyTuple(deputy.getOriginClassId(), classId);
                oringinId = deputy.getOriginClassId();

                List<Attribute> ls = getExistAttr(selectStmt.whereclause, classId);
                for (Tuplesp a : tmpsp) {
                    HashMap<String, String> params = new HashMap<>();
                    for (int i = 0; i < ls.size(); i++) {
                        params.put(ls.get(i).getAttrName(), a.tuple.getTuple()[ls.get(i).getAttrId()].toString());
                    }
                    Check boolcheck = new Check();
                    if (boolcheck.isEnable(selectStmt.whereclause, params)) {
                        origintuples.add(a);
                    }

                }
                List<Tuplesp> res = new ArrayList();
                for (Tuplesp tuplesp : origintuples) {
                    int newid = getBiDeputyObjectid(deputy.getOriginClassId(), classId, tuplesp.object_id);
                    if (newid != -1) {
                        int[] two = new int[2];
                        two = getobjectattr(newid);
                        Tuple origintuple = GetTuple(two[0], two[1]);
                        Tuplesp sp = new Tuplesp(origintuple, newid);
                        res.add(sp);
                    }
                }
                origintuples = res;

            }
            int startindex = 0;
            for (int i = 0; i < crossclass.size(); i++) {
                String tmpname = getclassname(oringinId);
                if (crossclass.get(i).equals(tmpname)) {
                    startindex = i;
                    break;
                }
            }
            List<Tuplesp> crosstuples = origintuples;
            for (int i = startindex; i < crossclass.size() - 1; i++) {
                int originid = getClassId(crossclass.get(i));
                int deputyid = getClassId(crossclass.get(i + 1));
                crosstuples = getCrossTuples(originid, deputyid, crosstuples);
            }
            System.out.println("cross!");
            for (int i = 0; i < crosstuples.size(); i++) {
                Tuple tupletmp = crosstuples.get(i).tuple;
                Object[] tuple_ = new Object[selectStmt.attrs.size()];
                for (int j = 0; j < selectStmt.attrs.size(); j++) {
                    tuple_[j] = tupletmp.getTuple()[attrorder[j]];
                    String tmp = (String) tuple_[i];
                    tmp = tmp.replaceAll("\"", "");
                    tuple_[i] = tmp;
                }
                Tuple resulttuple = new Tuple(tuple_);
                tupleList.addTuple(resulttuple);
            }
            PrintSelectResult(tupleList, attrname, attrid, attrtype);
            return tupleList;
        }

        if (selectStmt.whereclause != null) {
            List<Attribute> ls = getExistAttr(selectStmt.whereclause, classId);
            //List<AttributeTableItem> attls = new ArrayList();
            int[] posmark = new int[selectStmt.attrs.size()];
            String[] attrname = new String[selectStmt.attrs.size()];

            int[] attrid = new int[selectStmt.attrs.size()];
            String[] attrtype = new String[selectStmt.attrs.size()];
            for (int i = 0; i < selectStmt.attrs.size(); i++) {
                attrname[i] = selectStmt.attrs.get(i).attrname;
                attrid[i] = i;
                attrtype[i] = getAttrType(classId, selectStmt.attrs.get(i).attrname);
                if (selectStmt.attrs.get(i).isCross == false)
                    posmark[i] = getAttrPos(classId, selectStmt.attrs.get(i).attrname);
                else
                    posmark[i] = -1;
            }
            System.out.println("intercept");
            for (ObjectTableItem objectTableItem1 : objectTable.objectTable) {
                if (objectTableItem1.getClassId() == classId) {
                    Tuple tuple = GetTuple(objectTableItem1.getBlockId(), objectTableItem1.getOffset());
                    HashMap<String, String> params = new HashMap<>();
                    for (int i = 0; i < ls.size(); i++) {
                        params.put(ls.get(i).getAttrName(), tuple.getTuple()[ls.get(i).getAttrId()].toString());
                    }
                    Check boolcheck = new Check();
                    //找到符合select条件的行
                    if (boolcheck.isEnable(selectStmt.whereclause, params)) {
                        Object[] tuple_ = new Object[posmark.length];
                        for (int i = 0; i < posmark.length; i++) {
                            if (posmark[i] >= 0) {
                                tuple_[i] = tuple.getTuple()[posmark[i]];
                                //去掉引号
                                String tmp = (String) tuple_[i];
                                tmp = tmp.replaceAll("\"", "");
                                tuple_[i] = tmp;
                            } else {
                                tuple_[i] = getCrossValue(selectStmt.attrs.get(i), objectTableItem1.getObjectId());
                                attrtype[i] = getCrossType(selectStmt.attrs.get(i), objectTableItem1.getObjectId());
//去掉引号
                                String tmp = (String) tuple_[i];
                                tmp = tmp.replaceAll("\"", "");
                                tuple_[i] = tmp;

                            }
                        }
                        Tuple resulttuple = new Tuple(tuple_);
                        tupleList.addTuple(resulttuple);
                    }
                }
            }
            PrintSelectResult(tupleList, attrname, attrid, attrtype);
            return tupleList;
        } else {

            int[] posmark = new int[selectStmt.attrs.size()];
            String[] attrname = new String[selectStmt.attrs.size()];

            int[] attrid = new int[selectStmt.attrs.size()];
            String[] attrtype = new String[selectStmt.attrs.size()];
            for (int i = 0; i < selectStmt.attrs.size(); i++) {
                attrname[i] = selectStmt.attrs.get(i).attrname;
                attrid[i] = i;
                attrtype[i] = getAttrType(classId, selectStmt.attrs.get(i).attrname);
                if (selectStmt.attrs.get(i).isCross == false)
                    posmark[i] = getAttrPos(classId, selectStmt.attrs.get(i).attrname);
                else
                    posmark[i] = -1;
            }

            System.out.println("intercept");
            for (ObjectTableItem objectTableItem1 : objectTable.objectTable) {
                if (objectTableItem1.getClassId() == classId) {
                    Tuple tuple = GetTuple(objectTableItem1.getBlockId(), objectTableItem1.getOffset());
                    //找到符合select条件的行

                    Object[] tuple_ = new Object[posmark.length];
                    for (int i = 0; i < posmark.length; i++) {
                        if (posmark[i] >= 0) {
                            tuple_[i] = tuple.getTuple()[posmark[i]];
                            //去掉引号
                            String tmp = (String) tuple_[i];
                            tmp = tmp.replaceAll("\"", "");
                            tuple_[i] = tmp;
                        } else {
                            tuple_[i] = getCrossValue(selectStmt.attrs.get(i), objectTableItem1.getObjectId());
                            attrtype[i] = getCrossType(selectStmt.attrs.get(i), objectTableItem1.getObjectId());
//去掉引号
                            String tmp = (String) tuple_[i];
                            tmp = tmp.replaceAll("\"", "");
                            tuple_[i] = tmp;

                        }
                    }
                    Tuple resulttuple = new Tuple(tuple_);
                    tupleList.addTuple(resulttuple);
                }

            }

            PrintSelectResult(tupleList, attrname, attrid, attrtype);
            return tupleList;

        }

    }


    public void update(UpdateStmt updateStmt) {
        String classname = updateStmt.classname;

        int classid = 0;

        for (Class totemClass : classTable.getClassTable()) {
            if (totemClass.getClassName().equals(classname)) {
                classid = totemClass.getClassId();
                break;
            }
        }
        HashMap<String, Integer> attMap = getRuleAtt(updateStmt.whereclause, classid);

        OandB ob2 = new OandB();
        Iterator it1 = objectTable.objectTable.iterator();
        while (it1.hasNext()) {
            ObjectTableItem item3 = (ObjectTableItem) it1.next();
            if (item3.getClassId() == classid) {
                Tuple tuple = GetTuple(item3.getBlockId(), item3.getOffset());
                HashMap<String, String> params = new HashMap<>();
                for (String str : attMap.keySet()) {
                    params.put(str, tuple.getTuple()[attMap.get(str)-1].toString());
                }
                Check boolCheck = new Check();
                if (boolCheck.isWhereEnable(updateStmt.where, params)) {
                    UpdatebyID(ob2, classid, item3.getObjectId(), updateStmt);
                }
            }
        }
        for (ObjectTableItem obj : ob2.objects) {
            objectTable.objectTable.remove(obj);
        }
        for (ObjectTableItem obj : ob2.objects2) {
            objectTable.objectTable.add(obj);
        }
        for (BiPointer bip : ob2.biPointers) {
            biPointerTable.getBiPointerTable().remove(bip);
        }


    }

    private void UpdatebyID(OandB ob2, int classid, int tupleid, UpdateStmt us) {


        for (Iterator it1 = objectTable.objectTable.iterator(); it1.hasNext(); ) {
            ObjectTableItem item = (ObjectTableItem) it1.next();
            if (item.getObjectId() == tupleid && item.getClassId() == classid) {
                Tuple tuple = GetTuple(item.getBlockId(), item.getOffset());
                Tuple tupleOld = GetTuple(item.getBlockId(), item.getOffset());

                //更新元组值

                for (Attribute attributeTableItem1 : attributeTable.getAttributeTable()) {
                    for (int i = 0; i < us.attrs.size(); i++) {
                        if (us.attrs.get(i).equals(attributeTableItem1.getAttrName()) && attributeTableItem1.getClassId() == classid)
                            tuple.getTuple()[attributeTableItem1.getAttrId()-1] = us.attrvalue.get(i).value;
                    }
                }
                UpateTuple(tuple, item.getBlockId(), item.getOffset());
//                Tuple tuple1 = GetTuple(item.blockid,item.offset);
//                UpateTuple(tuple1,item.blockid,item.offset);


                for (Deputy deputy : deputyTable.getDeputyTable()) {

                    if (classid == deputy.getOriginClassId()) {

                        String deputyrule = null;

                        for (DeputyRule deputyRule : deputyRuleTable.getDeputyRuleTable()) {
                            if (deputyRule.getDeputyRuleId() == deputyRule.getDeputyRuleId())
                                deputyrule = deputyRule.getRule();
                        }

                        HashMap<String, Integer> attMap = getRuleAtt(deputyrule, classid);

                        HashMap<String, String> params = new HashMap<>();
                        HashMap<String, String> paramsOld = new HashMap<>();
                        for (String str : attMap.keySet()) {
                            params.put(str, tuple.getTuple()[attMap.get(str)-1].toString());
                        }
                        for (String str : attMap.keySet()) {
                            paramsOld.put(str, tupleOld.getTuple()[attMap.get(str)-1].toString());
                        }
                        Check boolCheck = new Check();
                        if (boolCheck.isEnable(deputyrule, params) && boolCheck.isEnable(deputyrule, paramsOld)) {
                            UpdateStmt us2 = new UpdateStmt();
                            for (BiPointer item1 : biPointerTable.getBiPointerTable()) {
                                if (item1.getOriginObjectId() == tupleid && item1.getOriginClassId() == classid) {
                                    for (Attribute item4 : attributeTable.getAttributeTable()) {
                                        if (item4.isDeputy() != 0) {
                                            String swirule = null;
                                            for (SwitchingRule switchingRule : switchingRuleTable.getSwitchingRuleTable()) {
                                                if (switchingRule.getSwitchRuleId() == item4.isDeputy())
                                                    swirule = switchingRule.getRule();
                                            }

                                            HashMap<String, Integer> swiattMap = getRuleAtt(swirule, classid);

                                            for (String name : swiattMap.keySet()) {
                                                System.out.println("不对劲" + tuple.getTuple()[swiattMap.get(name)-1].toString());
                                                swirule = swirule.replaceAll(name, tuple.getTuple()[swiattMap.get(name)-1].toString());
                                            }

                                            if (swirule.contains("(") || swirule.contains("{") || swirule.contains("[")) {
                                                int re = calculate(swirule);
                                                us2.values.add(String.valueOf(re));
                                            } else {
                                                us2.values.add(swirule);
                                            }

                                            us2.attrs.add(item4.getAttrName());
                                        } else {
                                            if (item4.getAttrType().equals("int"))
                                                us2.values.add("0");
                                            else if (item4.getAttrType().equals("char")) {
                                                us2.values.add(" ");
                                            }
                                            us2.attrs.add(item4.getAttrName());
                                        }
                                    }
                                    UpdatebyID(ob2, item1.getDeputyClassId(), item1.getDeputyObjectId(), us2);
                                }
                            }
                        } else if (!boolCheck.isEnable(deputyrule, params) && boolCheck.isEnable(deputyrule, paramsOld)) {

                            for (BiPointer item1 : biPointerTable.getBiPointerTable()) {
                                if (item1.getOriginObjectId() == tupleid) {
                                    OandB ob = new OandB(DeletebyID(item1.getDeputyObjectId()));
                                    for (ObjectTableItem obje : ob.objects) {
                                        ob2.objects.add(obje);
                                    }
                                    for (BiPointer bip : ob.biPointers) {
                                        ob2.biPointers.add(bip);
                                    }
                                }
                            }

                        } else if (boolCheck.isEnable(deputyrule, params) && !boolCheck.isEnable(deputyrule, paramsOld)) {
                            objectTable.maxTupleId++;
                            int Dtupid = objectTable.maxTupleId;


                            for (Class item4 : classTable.getClassTable()) {
                                if (item4.getClassId() == deputy.getDeputyClassId()) {
                                    Tuple t = new Tuple();
                                    t.setTupleHeader(item4.getAttrsNum());
                                    t.setTuple(new Object[t.getTupleHeader()]);

                                    for (Attribute attributeTableItem1 : attributeTable.getAttributeTable()) {
                                        if (attributeTableItem1.getClassId() == item4.getClassId() && attributeTableItem1.isDeputy() != 0) {
                                            String swirule = null;
                                            for (SwitchingRule switchingTableItem1 : switchingRuleTable.getSwitchingRuleTable()) {
                                                if (switchingTableItem1.getSwitchRuleId() == attributeTableItem1.isDeputy())
                                                    swirule = switchingTableItem1.getRule();
                                            }

                                            HashMap<String, Integer> swiattMap = getRuleAtt(swirule, classid);

                                            for (String name : swiattMap.keySet()) {
                                                swirule = swirule.replaceAll(name, tuple.getTuple()[swiattMap.get(name)-1].toString());
                                            }
                                            if (swirule.contains("(") || swirule.contains("{") || swirule.contains("[")) {
                                                int re = calculate(swirule);
                                                t.getTuple()[attributeTableItem1.getAttrId()] = re;
                                            } else {
                                                t.getTuple()[attributeTableItem1.getAttrId()] = swirule;
                                            }
                                        } else if (attributeTableItem1.getClassId() == item4.getClassId()) {
                                            if (attributeTableItem1.getAttrType().equals("int"))
                                                t.getTuple()[attributeTableItem1.getAttrId()] = "0";
                                            else if (attributeTableItem1.getAttrType().equals("char")) {
                                                t.getTuple()[attributeTableItem1.getAttrId()] = " ";
                                            }
                                        }
                                    }
                                    int[] result = InsertTuple(t);
                                    ob2.objects2.add(new ObjectTableItem(deputy.getDeputyClassId(), Dtupid, result[0], result[1]));

                                    //bi
                                    BiPointer biPointer = new BiPointer(classid, tupleid, deputy.getDeputyClassId(), Dtupid);
                                    biPointerTable.getBiPointerTable().add(biPointer);
                                    //biPointerTable.setBiPointerTable(biPointerTable.getBiPointerTable(new BiPointer(classid, tupleid, deputy.getDeputyClassId(), Dtupid));
                                }
                            }

                        }
                    }
                }

            }
        }


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

    /**
     * 判断字符c是否为合理的运算符
     *
     * @param c
     * @return
     */
    public static boolean isOpe(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    public static boolean isAllOpe(char c) {
        if (c == '+' || c == '-' || c == '*' || c == '/')
            return true;

        else return c == '(' || c == ')';
    }

    /**
     * 比较字符等级a是否大于b
     *
     * @param a
     * @param b
     * @return 大于返回true 小于等于返回false
     */
    public static boolean isGreater(char a, char b) {
        int a1 = getLevel(a);
        int b1 = getLevel(b);
        return a1 > b1;
    }

    /**
     * 得到一个字符的优先级
     *
     * @param a
     * @return
     */
    public static int getLevel(char a) {

        if (a == '+')
            return 0;
        else if (a == '-')
            return 1;
        else if (a == '*')
            return 3;
        else if (a == '/')
            return 4;
        else
            return -1;

    }

    //判断是不是数字
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public int calculate(String strExpression) {
        String s = simplify(strExpression);
        System.out.println("s : " + s);
        String numStr = "";//记录数字
        Stack<Character> opeStack = new Stack<>();//符号站
        int l = s.length();//字符串长度 l
        List<String> list = new ArrayList<>();

        for (int i = 0; i < l; i++) {
            char ch = s.charAt(i);

            if (isAllOpe(ch)) {
                if (numStr != "") {
                    list.add(numStr);
                    numStr = "";
                }


                if (ch == '(') {
                    opeStack.push(ch);
                } else if (isOpe(ch)) {
                    char top = opeStack.peek();
                    if (isGreater(ch, top))
                    // ch优先级大于top 压栈
                    {
                        opeStack.push(ch);
                    } else
                    //否则,将栈内元素出栈,直到遇见 '(' 然后将ch压栈
                    {
                        while (true)
                        //必须先判断一下 后出栈 否则会有空栈异常
                        {
                            char t = opeStack.peek();
                            if (t == '(')
                                break;
                            if (isGreater(ch, t))
                                break;

                            list.add(Character.toString(t));
                            t = opeStack.pop();
                        }
                        opeStack.push(ch);

                    }

                } else if (ch == ')') {
                    char t = opeStack.pop();
                    while (t != '(' && !opeStack.isEmpty()) {
                        list.add(Character.toString(t));
                        t = opeStack.pop();
                    }
                }

            } else//处理数字
            {
                numStr += ch;
            }
        }

        //计算后缀表达式
        System.out.println(list.toString());
        Stack<Integer> num = new Stack<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String t = list.get(i);
            if (isNumeric(t)) {//将t转换成int 方便计算
                num.push(Integer.parseInt(t));
            } else {
                //如果t为运算符则 只有一位
                char c = t.charAt(0);
                int b = num.pop();
                //如果有 算式是类似于 -8-8 这样的需要判断一下栈是否为空
                int a = num.pop();
                switch (c) {
                    case '+':
                        num.push(a + b);
                        break;
                    case '-':
                        num.push(a - b);
                        break;
                    case '*':
                        num.push(a * b);
                        break;
                    case '/':
                        num.push(a / b);
                        break;
                    default:
                        break;
                }
            }
        }
        //System.out.println(num.pop());
        return num.pop();
    }

    //根据类名获取类id
    public int getClassId(String classname) {
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
                return ls.get(i).getAttrId()-1;
        System.out.println("不存在相关属性位置");
        return -1;
    }

    //通过classid以及属性名称找到属性所在位置
    public int getAttrPos(int classid, String attrname) {
        for (int i = 0; i < attributeTable.getAttributeTable().size(); i++)
            if (attributeTable.getAttributeTable().get(i).getClassId() == classid && attributeTable.getAttributeTable().get(i).getAttrName().equals(attrname))
                return attributeTable.getAttributeTable().get(i).getAttrId()-1;
        System.out.println("未找到属性位置");

        return -2;
    }

    //获取双向源对象id
    public int getBiObjectid(int originid, int deputyid, int objectid) {
        for (BiPointer bi : biPointerTable.getBiPointerTable()) {
            if (bi.getOriginClassId() == originid && bi.getDeputyClassId() == deputyid && bi.getOriginObjectId() == objectid)
                return bi.getDeputyObjectId();
        }
        //System.out.println("未找到deputyobjectid");
        return -1;
    }

    //获取代理对象id
    public int getBiDeputyObjectid(int originid, int deputyid, int deputyobid) {
        for (BiPointer bi : biPointerTable.getBiPointerTable()) {
            if (bi.getOriginClassId() == originid && bi.getDeputyClassId() == deputyid && bi.getDeputyObjectId() == deputyobid)
                return bi.getOriginObjectId();
        }
        return -1;
    }

    //通过classid以及属性名称获取属性类型
    public String getAttrType(int classid, String attrname) {
        for (int i = 0; i < attributeTable.getAttributeTable().size(); i++)
            if (attributeTable.getAttributeTable().get(i).getClassId() == classid && attributeTable.getAttributeTable().get(i).getAttrName().equals(attrname))
                return attributeTable.getAttributeTable().get(i).getAttrType();
        // System.out.println("未找到属性类型");

        return "no";
    }

    //通过类id获取类名
    public String getclassname(int id) {
        for (Class totemclass : classTable.getClassTable())
            if (totemclass.getClassId() == id)
                return totemclass.getClassName();
        return "";
    }

    //通过类id获取deputy
    private Deputy getDeputy(int id) {
        for (Deputy deputy : deputyTable.getDeputyTable()) {
            if (deputy.getDeputyClassId() == id)
                return deputy;
        }
        return null;
    }

    //通过类名称获取类
    public Class getclass(String classname) {
        for (int i = 0; i < classTable.getClassTable().size(); i++)
            if (classname.equals(classTable.getClassTable().get(i).getClassName()))
                return classTable.getClassTable().get(i);
        System.out.println("不存在该类名");
        return null;

    }

    //通过类id获取属性个数
    public int getattrsnum(int classid) {

        for (int i = 0; i < classTable.getClassTable().size(); i++) {
            if (classid == classTable.getClassTable().get(i).getClassId())
                return classTable.getClassTable().get(i).getAttrsNum();
            break;
        }
        return -1;
    }

    //获取字符串中出现过的属性信息
    public List<Attribute> getExistAttr(String exp, int classid) {
        List<Attribute> attributes = getClassAtt(classid);
        List<Attribute> result = new ArrayList();
        for (int i = 0; i < attributes.size(); i++)
            if (exp.contains(attributes.get(i).getAttrName()))
                result.add(attributes.get(i));
        return result;
    }


    public String getCrossType(attrcontext attrc, int objectid) {
        int deputyobjectid = -1;
        int deputyclassid = -1;
        int originclassid = -1;
        for (int i = 0; i < attrc.crossclass.size() - 1; i++) {
            originclassid = getClassId(attrc.crossclass.get(i));
            deputyclassid = getClassId(attrc.crossclass.get(i + 1));
            deputyobjectid = getBiObjectid(originclassid, deputyclassid, objectid);
        }

        return getAttrType(deputyclassid, attrc.attrname);
    }

    public Object getCrossValue(attrcontext attrc, int objectid) {
        int deputyobjectid = -1;
        int deputyclassid = -1;
        int originclassid = -1;
        List<Integer> biids;
        for (int i = 0; i < attrc.crossclass.size() - 1; i++) {
            originclassid = getClassId(attrc.crossclass.get(i));
            deputyclassid = getClassId(attrc.crossclass.get(i + 1));
            deputyobjectid = getBiObjectid(originclassid, deputyclassid, objectid);
        }

        int attrid = getAttrPos(deputyclassid, attrc.attrname);
        Tuple tuple = null;
        for (ObjectTableItem ob : objectTable.objectTable) {
            if (ob.getObjectId() == deputyobjectid && ob.getClassId() == deputyclassid)
                tuple = GetTuple(ob.getBlockId(), ob.getOffset());
        }
        Object result = tuple.getTuple()[attrid-1];
        return result;
    }

    //获取到源类元组
    public List<Tuplesp> getOriginTuple(int classid, String whereclause) {
        List<Tuplesp> res = new ArrayList();
        List<Attribute> attributes = getExistAttr(whereclause, classid);
        if (whereclause == null) {
            int attrnum = getattrsnum(classid);
            for (ObjectTableItem objectTableItem : objectTable.objectTable) {
                if (objectTableItem.getClassId() == classid) {
                    Tuple tuple = GetTuple(objectTableItem.getBlockId(), objectTableItem.getOffset());

                    Object[] tuple_ = new Object[attrnum];
                    for (int i = 0; i < attrnum; i++) {
                        tuple_[i] = tuple.getTuple()[i];
                        //去掉引号
                        String tmp = (String) tuple_[i];
                        tmp = tmp.replaceAll("\"", "");
                        tuple_[i] = tmp;
                    }
                    Tuple resulttuple = new Tuple(tuple_);
                    Tuplesp sp = new Tuplesp(resulttuple, objectTableItem.getObjectId());
                    res.add(sp);

                }
            }
            return res;
        } else {
            int attrnum = getattrsnum(classid);
            for (ObjectTableItem objectTableItem : objectTable.objectTable) {
                if (objectTableItem.getClassId() == classid) {
                    Tuple tuple = GetTuple(objectTableItem.getBlockId(), objectTableItem.getOffset());
                    HashMap<String, String> params = new HashMap<>();
                    for (int i = 0; i < attributes.size(); i++) {
                        params.put(attributes.get(i).getAttrName(), tuple.getTuple()[attributes.get(i).getAttrId()-1].toString());
                    }
                    Check check = new Check();
                    if (check.isEnable(whereclause, params)) {
                        Object[] tuple_ = new Object[attrnum];
                        for (int i = 0; i < attrnum; i++) {
                            tuple_[i] = tuple.getTuple()[i];
                            //去掉引号
                            String tmp = (String) tuple_[i];
                            tmp = tmp.replaceAll("\"", "");
                            tuple_[i] = tmp;
                        }
                        Tuple resulttuple = new Tuple(tuple_);
                        Tuplesp sp = new Tuplesp(resulttuple, objectTableItem.getObjectId());
                        res.add(sp);
                    }
                }
            }
            return res;
        }
    }

    //获取跨类元组
    public List<Tuplesp> getCrossTuples(int originid, int deputyid, List<Tuplesp> ls) {
        int attrnum = getattrsnum(deputyid);
        List<Tuplesp> res = new ArrayList();
        for (Tuplesp a : ls) {
            int newid = getBiObjectid(originid, deputyid, a.object_id);
            int[] two = new int[2];
            two = getobjectattr(newid);
            Object[] tuple_ = new Object[attrnum];
            Tuple deputytuple = GetTuple(two[0], two[1]);

            for (Attribute attributeTable1 : attributeTable.getAttributeTable()) {
                //if(attributeTable1.classid == deputyclassid && attributeTable1.isdeputy != 0){
                if (attributeTable1.getClassId() == deputyid) {

                    String swirule = null;
                    for (SwitchingRule switchingTableItem1 : switchingRuleTable.getSwitchingRuleTable()) {
                        if (switchingTableItem1.getSwitchRuleId() == attributeTable1.isDeputy())
                            swirule = switchingTableItem1.getRule();
                    }

                    if (swirule == null) {
                        if (attributeTable1.isDeputy() == 0 && attributeTable1.getAttrType().equals("int"))
                            //ss.attrvalues.add("0");
                            tuple_[attributeTable1.getAttrId()] = deputytuple.getTuple()[attributeTable1.getAttrId()];
                        else if (attributeTable1.isDeputy() == 0 && attributeTable1.getAttrType().equals("char")) {
                            //ss.attrvalues.add(" ");
                            tuple_[attributeTable1.getAttrId()] = deputytuple.getTuple()[attributeTable1.getAttrId()];
                        }
                        //ss.attrnames.add(attributeTable1.attrname);
                        continue;
                    }

                    HashMap<String, Integer> swiattMap = getRuleAtt(swirule, originid);
                    //System.out.println("\n通过Map.keySet遍历key和value：");
                    for (String key : swiattMap.keySet()) {
                        if (swirule.contains(key))
                            swirule = swirule.replaceAll(key, a.tuple.getTuple()[swiattMap.get(key)-1].toString());
                    }
                    if (swirule.contains("(") || swirule.contains("{") || swirule.contains("[")) {
                        int re = calculate(swirule);
                        tuple_[attributeTable1.getAttrId()] = (String.valueOf(re));
                    } else {
                        //ss.attrvalues.add(swirule);
                        tuple_[attributeTable1.getAttrId()] = (swirule);
                    }
                    //ss.attrnames.add(attributeTable1.attrname);
                }
            }
            Tuple switchtuple = new Tuple(tuple_);
            Tuplesp sp = new Tuplesp(switchtuple, newid);
            res.add(sp);
        }

        return res;
    }

    //获取到代理类元组
    public List<Tuplesp> getDeputyTuple(int originid, int deputyid) {
        //System.out.println("Deputy!");
        List<Tuplesp> res = new ArrayList<Tuplesp>();
        for (BiPointer biPointer : biPointerTable.getBiPointerTable()) {
            if (biPointer.getOriginClassId() == originid && biPointer.getDeputyClassId() == deputyid) {
                int[] two = new int[2];
                two = getobjectattr(biPointer.getOriginObjectId());
                Tuple origintuple = GetTuple(two[0], two[1]);
                Object[] tuple_ = new Object[getattrsnum(deputyid)];
                two = getobjectattr(biPointer.getDeputyObjectId());
                Tuple deputytuple = GetTuple(two[0], two[1]);
                for (Attribute attribute : attributeTable.getAttributeTable()) {
                    //if(attributeTable1.classid == deputyclassid && attributeTable1.isdeputy != 0){
                    if (attribute.getClassId() == deputyid) {

                        String swirule = null;
                        for (SwitchingRule switchingRule : switchingRuleTable.getSwitchingRuleTable()) {
                            if (switchingRule.getSwitchRuleId() == attribute.isDeputy())
                                swirule = switchingRule.getRule();
                        }

                        if (swirule == null) {
                            if (attribute.isDeputy() == 0 && attribute.getAttrType().equals("int"))
                                //ss.attrvalues.add("0");
                                tuple_[attribute.getAttrId()] = deputytuple.getTuple()[attribute.getAttrId()];
                            else if (attribute.isDeputy() == 0 && attribute.getAttrType().equals("char")) {
                                //ss.attrvalues.add(" ");
                                tuple_[attribute.getAttrId()] = deputytuple.getTuple()[attribute.getAttrId()];
                            }
                            //ss.attrnames.add(attributeTable1.attrname);
                            continue;
                        }

                        HashMap<String, Integer> swiattMap = getRuleAtt(swirule, originid);
                        //System.out.println("\n通过Map.keySet遍历key和value：");
                        for (String key : swiattMap.keySet()) {
                            if (swirule.contains(key))
                                swirule = swirule.replaceAll(key, origintuple.getTuple()[swiattMap.get(key)].toString());
                        }
                        if (swirule.contains("(") || swirule.contains("{") || swirule.contains("[")) {
                            int re = calculate(swirule);
                            tuple_[attribute.getAttrId()] = (String.valueOf(re));
                        } else {
                            //ss.attrvalues.add(swirule);
                            tuple_[attribute.getAttrId()] = (swirule);
                        }
                        //ss.attrnames.add(attributeTable1.attrname);
                    }
                }
                Tuple switchtuple = new Tuple(tuple_);
                Tuplesp sp = new Tuplesp(switchtuple, biPointer.getDeputyObjectId());
                res.add(sp);
            }
        }
        return res;
    }

    //获取对象属性
    public int[] getobjectattr(int object_id) {
        int[] two = new int[2];
        for (ObjectTableItem objectTableItem1 : objectTable.objectTable) {
            if (objectTableItem1.getObjectId() == object_id) {
                two[0] = objectTableItem1.getBlockId();
                two[1] = objectTableItem1.getOffset();
            }
        }
        return two;
    }

    //获取对应规则和源类中，所有的属性名和属性位置
    public HashMap<String, Integer> getRuleAtt(String rule, int classid) {
        List<Attribute> attributes = getClassAtt(classid);
        HashMap<String, Integer> hs = new HashMap();
        for (int i = 0; i < attributes.size(); i++)
            if (rule.contains(attributes.get(i).getAttrName()))
                hs.put(attributes.get(i).getAttrName(), attributes.get(i).getAttrId());
        return hs;
    }


    public void updateDeputyObject(Object object) {

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

        public OandB(List<ObjectTableItem> objects, List<ObjectTableItem> objects2, List<BiPointer> biPointers) {
            this.objects = objects;
            this.objects2 = objects2;
            this.biPointers = biPointers;
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
                if(biPointerTable.getBiPointerTable() != null){
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
                }



                //删除自身
                DeleteTuple(item.getBlockId(), item.getOffset());
                if (!todelete2.contains(item))
                    todelete1.add(item);
            }
        }

        return ob;
    }


    private void PrintSelectResult(TupleList tpl, String[] attrname, int[] attrid, String[] type) {
        Intent intent;
        intent = new Intent(context, PrintResult.class);


        Bundle bundle = new Bundle();
        bundle.putSerializable("tupleList", tpl);
        bundle.putStringArray("attrname", attrname);
        bundle.putIntArray("attrid", attrid);
        bundle.putStringArray("type", type);
        intent.putExtras(bundle);
        context.startActivity(intent);


    }


    private void PrintTab(ObjectTable topt,SwitchingRuleTable switchingRT,DeputyTable deputyt,BiPointerTable biPointerT,ClassTable classTable, DeputyRuleTable deputyrulet, AttributeTable attributeTable) {
        Intent intent = new Intent(context, ShowTable.class);

        Bundle bundle0 = new Bundle();
        bundle0.putSerializable("ObjectTable",topt);
        bundle0.putSerializable("SwitchingRuleTable",switchingRT);
        bundle0.putSerializable("DeputyTable",deputyt);
        bundle0.putSerializable("BiPointerTable",biPointerT);
        bundle0.putSerializable("ClassTable",classTable);
        bundle0.putSerializable("DeputyRuleTable",deputyrulet);
        bundle0.putSerializable("AttributeTable",attributeTable);
        intent.putExtras(bundle0);
        context.startActivity(intent);
    }

    public void PrintTab(){
        PrintTab(objectTable,switchingRuleTable,deputyTable,biPointerTable,classTable,deputyRuleTable,attributeTable);
    }


}
