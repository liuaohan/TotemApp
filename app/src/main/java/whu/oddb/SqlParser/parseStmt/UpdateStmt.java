package whu.oddb.SqlParser.parseStmt;

import java.util.ArrayList;

import whu.oddb.SqlParser.ast.BinaryExpression;
import whu.oddb.SqlParser.ast.SimpleExpression;
import whu.oddb.SqlParser.ast.Statement;
import whu.oddb.SqlParser.ast.UpdateStatement;

public class UpdateStmt extends RawStmt {
    public String classname;
    public ArrayList<String> attrs = new ArrayList<>();
    public ArrayList<String> values = new ArrayList<>();
    public ArrayList<SimpleExpression> attrvalue = new ArrayList<>();

    public String whereclause;
    public BinaryExpression where;

    public UpdateStmt() {
    }

    public UpdateStmt(Statement statement) {
        UpdateStatement us = (UpdateStatement) statement;
        this.attrs.add(us.propertyName);
        this.attrs.add(us.propertyValue.toString());
        this.whereclause = us.whereClause.expression.rawString();
        this.classname = us.className;
        this.where = us.whereClause.expression;
        this.attrvalue.add(us.propertyValue);
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
