package whu.oddb.memory.table;

import java.io.Serializable;

public class Tuple implements Serializable {
    private int tupleHeader;
    private Object[] tuple;

    public Tuple(Object[] values) {
        tuple = values.clone();
        tupleHeader = values.length;
    }

    public int getTupleHeader() {
        return tupleHeader;
    }

    public void setTupleHeader(int tupleHeader) {
        this.tupleHeader = tupleHeader;
    }

    public Object[] getTuple() {
        return tuple;
    }

    public void setTuple(Object[] tuple) {
        this.tuple = tuple;
    }

    public Tuple() {
    }
}
