package oddb.SqlParser.parseStmt;

import java.util.ArrayList;

import oddb.SqlParser.ast.Statement;
import oddb.SqlParser.ast.UpdateStatement;

public class UpdateStmt extends RawStmt {
    public String classname;
    public ArrayList<String> attrs = new ArrayList<>();
    public ArrayList<String> values = new ArrayList<>();
    public String whereclause;

    public UpdateStmt() {
    }

    public UpdateStmt(Statement statement) {
        UpdateStatement us = (UpdateStatement) statement;
        this.attrs.add(us.propertyName);
        this.attrs.add(us.propertyValue.toString());
        this.whereclause = us.whereClause.expression.rawString();
    }

    @Override
    public String toString() {
        return "UpdateStmt{" +
                "classname='" + classname + '\'' +
                ", attrs=" + attrs.toString() +
                ", values=" + values.toString() +
                ", whereclause='" + whereclause + '\'' +
                '}';
    }


}
