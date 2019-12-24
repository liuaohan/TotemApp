package oddb.SqlParser.ast;

public class SimpleExpression {
    public Object value;
    public PropertyType type;

    public SimpleExpression(Object value, PropertyType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return "SimpleExpression{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }
}
