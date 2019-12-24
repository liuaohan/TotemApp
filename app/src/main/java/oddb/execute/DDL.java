package oddb.execute;

import java.util.List;

import oddb.SqlParser.parseStmt.RelAttr;
import oddb.memory.table.*;
import oddb.memory.table.Class;


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
    public void createDeputyClass(ClassTable classTable, String className, int attrsnum, DeputyTable deputyTable, DeputyRuleTable deputyRuleTable, int originClassId, String rule) {
        int classId = classTable.getMaxId() + 1;
        classTable.setMaxId(classId);
        Class totemClass = new Class(classId, className, attrsnum, 1);

        //deputyrule添加
        int deputyRuleId = deputyRuleTable.getMaxId() + 1;
        deputyRuleTable.setMaxId(deputyRuleId);
        DeputyRule deputyRule = new DeputyRule(deputyRuleId, rule);
        //添加至deputyruletable
        deputyRuleTable.add(deputyRule);

        //deputy索引添加
        Deputy deputy = new Deputy(originClassId,classId,deputyRuleId);
        deputyTable.add(deputy);
    }

    //类表添加
    public void classTableAdd(List<Class> classtable, Class totemClass) {

    }
}
