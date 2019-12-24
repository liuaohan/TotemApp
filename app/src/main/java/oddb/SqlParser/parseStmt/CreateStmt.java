package oddb.SqlParser.parseStmt;

import java.util.ArrayList;

import oddb.SqlParser.ast.CreateClassStatement;
import oddb.SqlParser.ast.Property;
import oddb.SqlParser.ast.Statement;

public class CreateStmt extends RawStmt{
	
	public String classname;
	public ArrayList<RelAttr> cols = new ArrayList();

	public CreateStmt() {
	}

	public CreateStmt(Statement statement) {

		CreateClassStatement ccs = (CreateClassStatement) statement;

		this.classname = ccs.className;
		for (Property property : ccs.propertyList) {
			this.cols.add(new RelAttr(property));
		}
	}

	@Override
	public String toString() {
		return "CreateStmt{" +
				"classname='" + classname + '\'' +
				", cols=" + cols.toString() +
				'}';
	}
}

