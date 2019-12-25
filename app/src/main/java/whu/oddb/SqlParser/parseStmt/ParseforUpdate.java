package whu.oddb.SqlParser.parseStmt;

public class ParseforUpdate {

    public static RawStmt ParseUpdate(String sql){
        UpdateStmt updatestmt= new UpdateStmt();
        updatestmt.NodeTag="UPDATE";
        String cleansql = sql.trim();
        int setpos =cleansql.indexOf("SET");
        String classname = cleansql.substring(6,setpos-1).trim();
        updatestmt.classname=classname;
        int wherepos=cleansql.indexOf("WHERE");
        int endpos=cleansql.indexOf(";");
        String whereclause=cleansql.substring(wherepos+5,endpos).trim();
        updatestmt.whereclause=whereclause;
        String halfpart =cleansql.substring(setpos+3,wherepos-1);
        String[] sets=halfpart.split(",");
        for(int i=0;i<sets.length;i++){
            String[] two = sets[i].trim().split("=");
            updatestmt.attrs.add(two[0].trim());
            updatestmt.values.add(two[1].trim());
        }

        return updatestmt;
        //UPDATE company SET salary=1000,dhjkas WHERE name="gg";

    }

    public static void main(String[] args) {
        RawStmt rs = ParseUpdate("UPDATE company SET salary=1000,age=89 WHERE name=10;");
        System.out.println(rs.toString());
    }

}
