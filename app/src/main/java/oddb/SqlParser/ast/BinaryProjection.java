package oddb.SqlParser.ast;

public class BinaryProjection extends Projection {

    public BinaryExpression expression;

    public BinaryProjection(BinaryExpression expression) {
        this.expression = expression;
    }

    @Override
    public ProjectionType getType() {
        return ProjectionType.BINARY;
    }

    @Override
    public String toString() {
        return "BinaryProjection{" +
                "name='" + name + '\'' +
                ", expression=" + expression +
                '}';
    }
}
