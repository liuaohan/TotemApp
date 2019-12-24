package whu.oddb.memory.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TupleList implements Serializable {
    public List<Tuple> tuplelist = new ArrayList<Tuple>();
    public int tuplenum = 0;

    public void addTuple(Tuple tuple){
        this.tuplelist.add(tuple);
        tuplenum++;
    }

}
