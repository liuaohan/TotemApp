package whu.oddb.SqlParser.ast;

public class WhereClause {

    public BinaryExpression expression;

    public WhereClause(BinaryExpression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "WhereClause{" +
                "expression=" + expression +
                '}';
    }
}
