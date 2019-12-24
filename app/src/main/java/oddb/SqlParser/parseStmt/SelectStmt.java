package oddb.SqlParser.parseStmt;

import java.util.ArrayList;

import oddb.SqlParser.ast.Projection;
import oddb.SqlParser.ast.SelectStatement;
import oddb.SqlParser.ast.Statement;

public class SelectStmt extends RawStmt {
    public String classname;
    public ArrayList<attrcontext> attrs = new ArrayList<>();
    public String whereclause;

    public SelectStmt() {
    }

    public SelectStmt(Statement statement) {
        SelectStatement select = (SelectStatement) statement;
        this.classname = select.className;
        for (Projection projection : select.projectionList) {
            attrs.add(new attrcontext(projection));
        }
        this.whereclause = select.whereClause.expression.rawString();
    }

    @Override
    public String toString() {
        return "SelectStmt{" +
                "NodeTag='" + NodeTag + '\'' +
                ", classname='" + classname + '\'' +
                ", attrs=" + attrs.toString() +
                ", whereclause='" + whereclause + '\'' +
                '}';
    }
}
