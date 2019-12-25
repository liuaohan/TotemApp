package whu.oddb.SqlParser.parseStmt;

import whu.oddb.SqlParser.ast.BinaryExpression;
import whu.oddb.SqlParser.ast.DeleteStatement;
import whu.oddb.SqlParser.ast.Statement;

public class DeleteStmt extends RawStmt {
	public String classname;
	public String whereclause;

	public BinaryExpression where;

	public DeleteStmt() {
	}

	public DeleteStmt(Statement statement) {
		DeleteStatement ds = (DeleteStatement) statement;
		this.classname = ds.className;
		this.whereclause = ds.whereClause.expression.rawString();
		this.where = ds.whereClause.expression;

	}

	@Override
	public String toString() {
		return "DeleteStmt{" +
				"classname='" + classname + '\'' +
				", whereclause='" + whereclause + '\'' +
				'}';
	}

}
