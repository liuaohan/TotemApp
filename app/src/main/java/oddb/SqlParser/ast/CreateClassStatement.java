package oddb.SqlParser.ast;

import java.util.List;

public class CreateClassStatement implements Statement {
    public String className;
    public List<Property> propertyList;

    public CreateClassStatement(String className, List<Property> propertyList) {
        this.className = className;
        this.propertyList = propertyList;
    }

    @Override
    public StatementType getType() {
        return StatementType.CREATE_CLASS;
    }

    @Override
    public String toString() {
        return "CreateClassStatement{" +
                "className='" + className + '\'' +
                ", propertyList=" + propertyList +
                '}';
    }
}
