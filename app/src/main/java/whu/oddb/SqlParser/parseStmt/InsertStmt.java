package whu.oddb.SqlParser.parseStmt;

import java.util.ArrayList;

import whu.oddb.SqlParser.ast.InsertStatement;
import whu.oddb.SqlParser.ast.SimpleExpression;
import whu.oddb.SqlParser.ast.Statement;

public class InsertStmt extends RawStmt {
    public String classname;
    public ArrayList<String> attrnames = new ArrayList<>();
    public ArrayList<SimpleExpression> attrvalues = new ArrayList<>();

    public InsertStmt() {
    }

    public InsertStmt(Statement statement) {
        InsertStatement is = (InsertStatement) statement;
        this.classname = is.className;
        this.attrnames.addAll(is.propertyList);
        this.attrvalues.addAll(is.valueList);
    }

    @Override
    public String toString() {
        return "InsertStmt{" +
                "classname='" + classname + '\'' +
                ", attrnames=" + attrnames.toString() +
                ", attrvalues=" + attrvalues.toString() +
                '}';
    }
}
