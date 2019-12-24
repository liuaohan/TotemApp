package oddb.SqlParser.parseStmt;

import java.util.ArrayList;

import oddb.SqlParser.ast.CreateDeputyStatement;
import oddb.SqlParser.ast.Projection;
import oddb.SqlParser.ast.Property;
import oddb.SqlParser.ast.Statement;

public class CreateSelStmt extends CreateStmt {

    public String classname;
    public String originname;
    public ArrayList<RelAttr> relattrs = new ArrayList<>();
    public ArrayList<DeputyAttr> deputyattrs = new ArrayList<>();
    public String whereclause;

    public CreateSelStmt() {
    }

    public CreateSelStmt(Statement statement) {
        CreateDeputyStatement cds = (CreateDeputyStatement) statement;
        this.classname = cds.className;
        this.originname = cds.selectFrom.className;
        for (Property property : cds.propertyList) {
            this.relattrs.add(new RelAttr(property));
        }
        for (Projection projection : cds.selectFrom.projectionList) {
            this.deputyattrs.add(new DeputyAttr(projection));
        }
        this.whereclause = cds.selectFrom.whereClause.expression.rawString();
    }

    @Override
    public String toString() {
        return "CreateSelStmt{" +
                "classname='" + classname + '\'' +
                ", originname='" + originname + '\'' +
                ", relattrs=" + relattrs.toString() +
                ", deputyattrs=" + deputyattrs.toString() +
                ", whereclause='" + whereclause + '\'' +
                '}';
    }

}
