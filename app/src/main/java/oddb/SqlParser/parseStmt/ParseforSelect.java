package oddb.SqlParser.parseStmt;

public class ParseforSelect {
    public static RawStmt ParseSelect(String sql) {
        SelectStmt selectstmt = new SelectStmt();

        if (sql.contains("WHERE")) {
            selectstmt.NodeTag = "SELECT";
            int frompos = sql.indexOf("FROM");
            String backhalf = sql.substring(frompos + 4);
            int wherepos = backhalf.indexOf("WHERE");
            String cln = backhalf.substring(0, wherepos - 1).trim();
            System.out.println(cln);
            int semicopos = backhalf.indexOf(";");
            String condition = backhalf.substring(wherepos + 5, semicopos).trim();
            System.out.println(condition);
            selectstmt.classname = cln;
            selectstmt.whereclause = condition;
            String forhalf = sql.substring(6, frompos);
            System.out.println(forhalf);
            String[] attres = forhalf.split(",");
            for (int i = 0; i < attres.length; i++)
                selectstmt.attrs.add(getattr(attres[i].trim()));
            return selectstmt;
        } else {
            selectstmt.NodeTag = "SELECT";
            int frompos = sql.indexOf("FROM");
            String backhalf = sql.substring(frompos + 4);
            int semicopos = backhalf.indexOf(";");
            String cln = backhalf.substring(0, semicopos).trim();
            selectstmt.classname = cln;
            System.out.println(frompos);
            String forhalf = sql.substring(6, frompos);
            System.out.println(forhalf);
            String[] attres = forhalf.split(",");
            for (int i = 0; i < attres.length; i++)
                selectstmt.attrs.add(getattr(attres[i].trim()));
            return selectstmt;

        }

//SELECT popsinger , bee->cool->yh.name , kb->kg.tg FROM hello WHERE yh>5 ;
    }

    public static void main(String[] args) {
        RawStmt rs = ParseSelect("SELECT sales , name , usprice from usproduct;");
        //String[] two = "yh.name".split("\\.");
        //System.out.println(two[0]);
        System.out.println(rs);
    }

    public static attrcontext getattr(String attr) {
        System.out.println("attr is " + attr);
        attrcontext attrentity = new attrcontext();
        int crosspos = attr.indexOf("->");
        if (crosspos == -1) {
            attrentity.isCross = false;
            attrentity.attrname = attr;
        } else {
            String[] crossclasses = attr.split("->");
            for (int i = 0; i < crossclasses.length - 1; i++)
                attrentity.crossclass.add(crossclasses[i].trim());
            String[] two = crossclasses[crossclasses.length - 1].split("\\.");
            System.out.println(crossclasses[crossclasses.length - 1]);
            System.out.println(two);
            attrentity.isCross = true;
            attrentity.attrname = two[1].trim();
            attrentity.crossclass.add(two[0].trim());
        }
        return attrentity;
    }

}