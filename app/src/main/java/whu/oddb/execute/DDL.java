package whu.oddb.execute;

import java.util.List;

import whu.oddb.SqlParser.parseStmt.DeputyAttr;
import whu.oddb.SqlParser.parseStmt.RelAttr;
import whu.oddb.memory.table.Class;
import whu.oddb.memory.table.*;



public class DDL {
    //创建源类
    public void createOriginClass(ClassTable classTable, String className, int attrsnum, AttributeTable attributeTable, List<RelAttr> attrs ) {
        int classId = classTable.getMaxId() + 1;
        classTable.setMaxId(classId);
        Class totemClass = new Class(classId, className, attrsnum, 0);
        classTable.add(totemClass);

        for(int i = 0; i < attrs.size(); i++){
            attributeTable.addattr(classId, attrs.get(i).attrname,attrs.get(i).attrtype,(byte)0);
        }
    }

    //创建代理类
    public void createDeputyClass(ClassTable classTable, String className, int attrsnum, AttributeTable attributeTable, DeputyTable deputyTable, DeputyRuleTable deputyRuleTable, int originClassId, String rule,List<RelAttr> realAttrs, List<DeputyAttr> deputyAttrs) {
        int classId = classTable.getMaxId() + 1;
        classTable.setMaxId(classId);
        Class totemClass = new Class(classId, className, attrsnum, 1);
        classTable.add(totemClass);
        //deputyrule添加
        int deputyRuleId = deputyRuleTable.getMaxId() + 1;
        deputyRuleTable.setMaxId(deputyRuleId);
        DeputyRule deputyRule = new DeputyRule(deputyRuleId, rule);
        //添加至deputyruletable
        deputyRuleTable.add(deputyRule);

        //deputy索引添加
        Deputy deputy = new Deputy(classId,originClassId,deputyRuleId);
        deputyTable.add(deputy);

        for(int i = 0; i < realAttrs.size(); i++){
            attributeTable.addattr(classId, realAttrs.get(i).attrname,realAttrs.get(i).attrtype,(byte)0);
        }
        for(int i = 0; i < deputyAttrs.size(); i++){
            attributeTable.addattr(classId, deputyAttrs.get(i).deputyname,"deputy",(byte)1);

        }


    }

    //类表添加
    public void classTableAdd(List<Class> classtable, Class totemClass) {

    }
}
