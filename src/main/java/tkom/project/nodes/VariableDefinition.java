package tkom.project.nodes;

import tkom.project.exceptions.ReassignNonMutableVariableException;
import tkom.project.nodes.expressions.Expression;
import tkom.project.nodes.statements.Statement;

public class VariableDefinition extends Statement {
    private final String identifier;
    private final TypeSpecifier type;

    private Expression value;
    private final Boolean mutable;
    private final Boolean optional;

    public VariableDefinition(Boolean mutable, Boolean optional, String identifier, TypeSpecifier type, Expression expression) {
        this.mutable = mutable;
        this.optional = optional;
        this.identifier = identifier;
        this.type = type;
        this.value = expression;
    }

    public TypeSpecifier getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Expression getValue() {
        return value;
    }
    public void setValue(Expression newValue) {
        if (value == null) value = newValue;
        if (value != newValue){
            if (Boolean.TRUE.equals(mutable)) value = newValue;
            else throw new ReassignNonMutableVariableException(identifier);
        }
    }

    public Boolean getMutable() {
        return mutable;
    }

    public Boolean getOptional() {
        return optional;
    }
}
