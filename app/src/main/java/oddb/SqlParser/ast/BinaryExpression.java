package oddb.SqlParser.ast;

public class BinaryExpression {
    public SimpleExpression left;
    public SimpleExpression right;
    public OperatorType operator;

    public BinaryExpression(SimpleExpression left, SimpleExpression right, OperatorType operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "BinaryExpression{" +
                "left=" + left +
                ", right=" + right +
                ", operator=" + operator +
                '}';
    }

    // 兼容老版本
    public String rawString() {
        return left + " " + operator.content + " " + right;
    }
}
