package oddb.SqlParser.ast;

import java.util.List;

public class InsertStatement implements Statement {
    public String className;
    public List<String> propertyList;
    public List<SimpleExpression> valueList;

    public InsertStatement(String className, List<String> propertyList, List<SimpleExpression> valueList) {
        this.className = className;
        this.propertyList = propertyList;
        this.valueList = valueList;
    }

    @Override
    public StatementType getType() {
        return StatementType.INSERT;
    }

    @Override
    public String toString() {
        return "InsertStatement{" +
                "className='" + className + '\'' +
                ", propertyList=" + propertyList +
                ", valueList=" + valueList +
                '}';
    }
}
