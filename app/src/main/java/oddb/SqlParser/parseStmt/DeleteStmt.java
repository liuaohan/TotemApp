package oddb.SqlParser.parseStmt;

import oddb.SqlParser.ast.DeleteStatement;
import oddb.SqlParser.ast.Statement;

public class DeleteStmt extends RawStmt {
	public String classname;
	public String whereclause;

	public DeleteStmt() {
	}

	public DeleteStmt(Statement statement) {
		DeleteStatement ds = (DeleteStatement) statement;
		this.classname = ds.className;
		this.whereclause = ds.whereClause.expression.rawString();
	}

	@Override
	public String toString() {
		return "DeleteStmt{" +
				"classname='" + classname + '\'' +
				", whereclause='" + whereclause + '\'' +
				'}';
	}

}
