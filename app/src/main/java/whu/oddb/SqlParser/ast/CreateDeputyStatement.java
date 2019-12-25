package whu.oddb.SqlParser.ast;

import java.util.List;

public class CreateDeputyStatement implements Statement {
    public String className;
    public List<Property> propertyList;
    public SelectStatement selectFrom;

    public CreateDeputyStatement(String className, List<Property> propertyList, SelectStatement selectFrom) {
        this.className = className;
        this.propertyList = propertyList;
        this.selectFrom = selectFrom;
    }

    @Override
    public StatementType getType() {
        return StatementType.CREATE_DEPUTY;
    }

    @Override
    public String toString() {
        return "CreateDeputyStatement{" +
                "className='" + className + '\'' +
                ", propertyList=" + propertyList +
                ", selectFrom=" + selectFrom +
                '}';
    }
}
