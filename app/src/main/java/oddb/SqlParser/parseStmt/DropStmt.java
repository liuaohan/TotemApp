package oddb.SqlParser.parseStmt;

import oddb.SqlParser.ast.DropStatement;
import oddb.SqlParser.ast.Statement;

public class DropStmt extends RawStmt{
	public String classname;

	public DropStmt() {
	}

	public DropStmt(Statement statement) {
		DropStatement ds = (DropStatement) statement;
		this.classname = ds.className;
	}

	@Override
	public String toString() {
		return "DropStmt{" +
				"classname='" + classname + '\'' +
				'}';
	}

}
