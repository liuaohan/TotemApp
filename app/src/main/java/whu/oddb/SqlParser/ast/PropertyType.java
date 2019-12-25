package whu.oddb.SqlParser.ast;

public enum PropertyType {
    UNKNOWN(""),
    INT("int"),
    CHAR("char");

    public String content;

    PropertyType(String content) {
        this.content = content;
    }

    public static PropertyType typeOf(String input) {
        switch (input.toLowerCase()) {
            case "int":
                return INT;
            case "char":
                return CHAR;
            default:
                return UNKNOWN;
        }
    }
}
