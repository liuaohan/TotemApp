package whu.oddb.SqlParser.ast;

import java.util.List;

public class SelectStatement implements Statement {

    public List<Projection> projectionList;
    public String className;
    public WhereClause whereClause;

    public SelectStatement(List<Projection> projectionList, String className, WhereClause whereClause) {
        this.projectionList = projectionList;
        this.className = className;
        this.whereClause = whereClause;
    }

    @Override
    public StatementType getType() {
        return StatementType.SELECT;
    }

    @Override
    public String toString() {
        return "SelectStatement{" +
                "projectionList=" + projectionList +
                ", className='" + className + '\'' +
                ", whereClause=" + whereClause +
                '}';
    }
}
