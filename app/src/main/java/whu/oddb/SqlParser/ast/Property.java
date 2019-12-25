package whu.oddb.SqlParser.ast;

public class Property {

    public String name;
    public PropertyType type;

    public Property(String name, PropertyType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
