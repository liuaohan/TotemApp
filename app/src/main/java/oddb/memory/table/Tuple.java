package oddb.memory.table;

import java.io.Serializable;

public class Tuple implements Serializable {
    private int tupleSize;
    private Object[] tuple;

    public Tuple(Object[] values) {
        tuple = values.clone();
        tupleSize = values.length;
    }

    public int getTupleHeader() {
        return tupleSize;
    }

    public void setTupleHeader(int tupleHeader) {
        this.tupleSize = tupleHeader;
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
