package oddb.SqlParser.ast;

public enum OperatorType {
    UNKNOWN(""),
    ADD("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    EQUALS("="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_OR_EQUALS("<="),
    GREATER_THAN_OR_EQUALS(">=");

    public String content;
    OperatorType(String content){
        this.content = content;
    }

    public static OperatorType typeOf(String input) {
        switch (input) {
            case "+":
                return ADD;
            case "-":
                return MINUS;
            case "*":
                return MULTIPLY;
            case "/":
                return DIVIDE;
            case "=":
                return EQUALS;
            case "<":
                return LESS_THAN;
            case ">":
                return GREATER_THAN;
            case "<=":
                return LESS_THAN_OR_EQUALS;
            case ">=":
                return GREATER_THAN_OR_EQUALS;
            default:
                return UNKNOWN;
        }
    }
}
