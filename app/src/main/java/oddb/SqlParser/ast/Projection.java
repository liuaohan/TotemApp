package oddb.SqlParser.ast;

public abstract class Projection {

    public String name;

    public abstract ProjectionType getType();
}
