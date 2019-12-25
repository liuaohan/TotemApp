package whu.oddb.SqlParser.parseStmt;

import java.util.ArrayList;

import whu.oddb.SqlParser.ast.CrossClassProjection;
import whu.oddb.SqlParser.ast.Projection;
import whu.oddb.SqlParser.ast.SimpleProjection;

public class attrcontext {

    public boolean isCross;
    public String attrname;
    public ArrayList<String> crossclass = new ArrayList<>();

    public attrcontext() {
    }

    public attrcontext(Projection projection) {
        if (projection instanceof SimpleProjection) {
            SimpleProjection sp = (SimpleProjection) projection;
            this.isCross = false;
            this.attrname = sp.columnName;
        } else if (projection instanceof CrossClassProjection) {
            CrossClassProjection ccp = (CrossClassProjection) projection;
            this.isCross = true;
            this.attrname = ccp.propertyName;
            this.crossclass.addAll(ccp.classList);
        } else {
            // 我没有找到原来的执行框架里，进行 BinaryProjection 的地方
            this.isCross = false;
        }
    }

    @Override
    public String toString() {
        return "attrcontext{" +
                ", isCross=" + isCross +
                ", attrname='" + attrname + '\'' +
                ", crossclass=" + crossclass.toString() +
                '}';
    }
}
