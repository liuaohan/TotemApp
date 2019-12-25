package whu.oddb.SqlParser.ast;

public class SimpleProjection extends Projection {
    public String columnName;

    public SimpleProjection(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public ProjectionType getType() {
        return ProjectionType.SIMPLE;
    }

    @Override
    public String toString() {
        return "SimpleProjection{" +
                "name='" + name + '\'' +
                ", columnName='" + columnName + '\'' +
                '}';
    }
}
