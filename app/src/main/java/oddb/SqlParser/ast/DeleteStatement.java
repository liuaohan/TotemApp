package oddb.SqlParser.ast;

public class DeleteStatement implements Statement {
    public String className;
    public WhereClause whereClause;

    public DeleteStatement(String className, WhereClause whereClause) {
        this.className = className;
        this.whereClause = whereClause;
    }

    @Override
    public StatementType getType() {
        return StatementType.DELETE;
    }

    @Override
    public String toString() {
        return "DeleteStatement{" +
                "className='" + className + '\'' +
                ", whereClause=" + whereClause +
                '}';
    }
}
