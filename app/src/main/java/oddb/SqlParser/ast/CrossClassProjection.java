package oddb.SqlParser.ast;

import java.util.List;

public class CrossClassProjection extends Projection {
    public List<String> classList;
    public String propertyName;

    public CrossClassProjection(List<String> classList, String propertyName) {
        this.classList = classList;
        this.propertyName = propertyName;
    }

    @Override
    public ProjectionType getType() {
        return ProjectionType.CROSS_CLASS;
    }

    @Override
    public String toString() {
        return "CrossClassProjection{" +
                "name='" + name + '\'' +
                ", classList=" + classList +
                ", propertyName='" + propertyName + '\'' +
                '}';
    }
}
