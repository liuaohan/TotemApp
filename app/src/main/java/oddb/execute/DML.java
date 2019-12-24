package oddb.execute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oddb.memory.table.Tuple;
import oddb.memory.table.Attribute;
import oddb.memory.table.AttributeTable;
import oddb.memory.table.BiPointer;
import oddb.memory.table.BiPointerTable;
import oddb.memory.table.Class;
import oddb.memory.table.ClassTable;
import oddb.memory.table.ObjectTableItem;
import oddb.memory.table.ObjectTable;

public class DML {

    //增
    public void insert(Class totemclass, Map<String ,String> attrsMap){
        if(totemclass.getClassType() == 0){
            String classname = totemclass.getClassName();
            int classId = totemclass.getClassId();

        }else if (totemclass.getClassType() == 1){
            System.out.println("不能向代理类中新增");
        }else {
            System.out.println("未识别出类类型，请检查代码");
        }
        //暂时用来取代语句大小
        int size_num = 50;
        //暂时用来代替语句
        ArrayList<String> attrnames = new ArrayList();
        ArrayList<String> attrvalues = new ArrayList();
        List<Attribute> ls = new ArrayList<>();
        Object[] tuple_ = new Object[size_num];
        //将属性
        for(int i=0;i<size_num;i++) {
            int pos= getpos(ls,attrnames.get(1));

        }
        Tuple tuple = new Tuple(tuple_);
        tuple.setTupleHeader(size_num);


    }

    //删
    public void delete(ObjectTable objectTable, int id, BiPointerTable biPointerTable){



    }

    //查
    public void select(){

    }

    //改
    public void update(){

    }
    //获取属性所在的位置，用于把值放在insert tuple中的正确位置
    public int getpos(List<Attribute> ls, String attrname){
        for(int i=0;i<ls.size();i++)
            if(ls.get(i).getAttrName().equals(attrname))
                return ls.get(i).getAttrId();
        System.out.println("不存在相关属性位置");
        return -1;
    }


}
