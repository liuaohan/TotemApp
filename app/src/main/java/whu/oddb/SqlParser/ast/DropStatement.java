package whu.oddb.SqlParser.ast;

public class DropStatement implements Statement {
    public String className;

    public DropStatement(String className) {
        this.className = className;
    }

    @Override
    public StatementType getType() {
        return StatementType.DROP;
    }

    @Override
    public String toString() {
        return "DropStatement{" +
                "className='" + className + '\'' +
                '}';
    }
}
