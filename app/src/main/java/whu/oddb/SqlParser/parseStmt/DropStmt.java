package whu.oddb.SqlParser.parseStmt;

import whu.oddb.SqlParser.ast.DropStatement;
import whu.oddb.SqlParser.ast.Statement;

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
