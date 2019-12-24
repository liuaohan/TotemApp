package oddb.SqlParser.parseStmt;

import oddb.SqlParser.ast.Property;

public class RelAttr {
	
	public String attrname;
	public String attrtype;

	public RelAttr() {
	}

	public RelAttr(Property property) {
		this.attrname = property.name;
		this.attrtype = property.type.content;
	}

	@Override
	public String toString() {
		return "RelAttr{" +
				"attrname='" + attrname + '\'' +
				", attrtype='" + attrtype + '\'' +
				'}';
	}

}
