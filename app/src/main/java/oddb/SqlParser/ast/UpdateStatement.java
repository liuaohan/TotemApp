package oddb.SqlParser.ast;

public class UpdateStatement implements Statement {
    public String className;
    public String propertyName;
    public SimpleExpression propertyValue;
    public WhereClause whereClause;

    public UpdateStatement(String className, String propertyName, SimpleExpression propertyValue, WhereClause whereClause) {
        this.className = className;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.whereClause = whereClause;
    }

    @Override
    public StatementType getType() {
        return StatementType.UPDATE;
    }

    @Override
    public String toString() {
        return "UpdateStatement{" +
                "className='" + className + '\'' +
                ", propertyName='" + propertyName + '\'' +
                ", propertyValue=" + propertyValue +
                ", whereClause=" + whereClause +
                '}';
    }
}
