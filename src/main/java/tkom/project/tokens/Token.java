package tkom.project.tokens;

import java.util.AbstractMap;

public class Token {
    private final TokenType type;
    private final AbstractMap.SimpleEntry<Integer, Integer> position;

    private Integer valueInt;
    private Float valueFloat;
    private Boolean valueBool;
    private String valueString;

    // Generic Token constructor for operators
    public Token(TokenType type, AbstractMap.SimpleEntry<Integer, Integer> position) {
        this.type = type;
        this.position = position;
    }
    // Token constructor for Integer values
    public Token(TokenType type, AbstractMap.SimpleEntry<Integer, Integer> position, Integer value) {
        this.type = type;
        this.position = position;
        this.valueInt = value;
    }
    // Token constructor for Float values
    public Token(TokenType type, AbstractMap.SimpleEntry<Integer, Integer> position, Float value) {
        this.type = type;
        this.position = position;
        this.valueFloat = value;
    }
    // Token constructor for Boolean values
    public Token(TokenType type, AbstractMap.SimpleEntry<Integer, Integer> position, Boolean value) {
        this.type = type;
        this.position = position;
        this.valueBool = value;
    }
    // Token constructor for Strings
    public Token(TokenType type, AbstractMap.SimpleEntry<Integer, Integer> position, String value) {
        this.type = type;
        this.position = position;
        this.valueString = value;
    }

    public TokenType getType() {
        return this.type;
    }


    public ReturnValue value() {
        switch (type) {
            case INT_VAL -> {
                return new ValueInteger(valueInt);
            }
            case FLOAT_VAL -> {
                return new ValueFloat(valueFloat);
            }
            case BOOL_VAL -> {
                return new ValueBool(valueBool);
            }
            case STRING_VAL, IDENTIFIER, COMMENT -> {
                return new ValueString(valueString);
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public String toString() {
        return "["+type+" -> "+position.getKey()+":"+position.getValue()+"]";
    }
}
