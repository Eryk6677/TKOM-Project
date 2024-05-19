package tkom.project.nodes;

public class Parameter implements Node {
    private final String identifier;
    private final TypeSpecifier type;

    private final boolean mutability;
    private final boolean optional;

    public Parameter(boolean mutability, boolean optional, String identifier, TypeSpecifier type) {
        this.mutability = mutability;
        this.optional = optional;
        this.identifier = identifier;
        this.type = type;
    }

    public TypeSpecifier getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isMutable() {
        return mutability;
    }

    public boolean isOptional() { return optional; }
}
