package tkom.project.nodes.expressions;

import tkom.project.nodes.NotValue;
import tkom.project.nodes.TypeSpecifier;
import tkom.project.nodes.statements.Statement;
import tkom.project.tokens.*;

public class Expression extends Statement {
    NotValue notFlag;
    TypeSpecifier type;

    String identifier = null;

    private Integer valueInt;
    private Float valueFloat;
    private Boolean valueBool;
    private String valueString;

    public Expression(NotValue notFlag, TypeSpecifier type, Integer value) {
        this.notFlag = notFlag;
        this.type = type;
        this.valueInt = value;
    }

    public Expression(NotValue notFlag, TypeSpecifier type, Float value) {
        this.notFlag = notFlag;
        this.type = type;
        this.valueFloat = value;
    }

    public Expression(NotValue notFlag, TypeSpecifier type, String value) {
        this.notFlag = notFlag;
        this.type = type;
        this.valueString = value;
    }

    public Expression(NotValue notFlag, TypeSpecifier type, Boolean value) {
        this.notFlag = notFlag;
        this.type = type;
        this.valueBool = value;
    }

    public Expression(NotValue notFlag, TypeSpecifier type) {
        this.notFlag = notFlag;
        this.type = type;
    }

    public Expression() {

    }

    public TypeSpecifier getType() {
        return type;
    }

    public NotValue getNotFlag() {
        return notFlag;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public ReturnValue value() {
        switch (type) {
            case INT -> {
                return new ValueInteger(valueInt);
            }
            case FLOAT -> {
                return new ValueFloat(valueFloat);
            }
            case BOOL -> {
                return new ValueBool(valueBool);
            }
            case STRING -> {
                return new ValueString(valueString);
            }
            default -> {
                return null;
            }
        }
    }

    public void setNotFlag(NotValue notFlag) {
        this.notFlag = notFlag;
    }
}
