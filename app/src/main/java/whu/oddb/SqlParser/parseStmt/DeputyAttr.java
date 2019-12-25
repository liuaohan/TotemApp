package whu.oddb.SqlParser.parseStmt;

import whu.oddb.SqlParser.ast.BinaryProjection;
import whu.oddb.SqlParser.ast.Projection;
import whu.oddb.SqlParser.ast.SimpleProjection;

public class DeputyAttr {
    public String deputyname;
    public String switchrule;
    public boolean isDeputy;

    public DeputyAttr() {
    }

    public DeputyAttr(Projection projection) {
        this.deputyname = projection.name;
        if (projection instanceof SimpleProjection) {
            this.switchrule = ((SimpleProjection) projection).columnName;
        } else if (projection instanceof BinaryProjection) {
            this.switchrule = ((BinaryProjection) projection).expression.rawString();
        }
        // 这里 projection 不能是 CrossClassProjection
    }

    @Override
    public String toString() {
        return "DeputyAttr{" +
                "deputyname='" + deputyname + '\'' +
                ", switchrule='" + switchrule + '\'' +
                ", isDeputy=" + isDeputy +
                '}';
    }
}
