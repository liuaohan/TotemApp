package oddb.SqlParser.parseStmt;

import java.util.ArrayList;

import oddb.SqlParser.ast.InsertStatement;
import oddb.SqlParser.ast.SimpleExpression;
import oddb.SqlParser.ast.Statement;

public class InsertStmt extends RawStmt {
    public String classname;
    public ArrayList<String> attrnames = new ArrayList<>();
    public ArrayList<String> attrvalues = new ArrayList<>();

    public InsertStmt() {
    }

    public InsertStmt(Statement statement) {
        InsertStatement is = (InsertStatement) statement;
        this.classname = is.className;
        this.attrnames.addAll(is.propertyList);
        for (SimpleExpression se : is.valueList) {
            this.attrvalues.add(se.toString());
        }
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
