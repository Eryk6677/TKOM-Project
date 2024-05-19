package tkom.project.tokens;

public enum TokenType {
    // keywords:
    DEF_KW,
    IF_KW,
    ELIF_KW,
    ELSE_KW,
    WHILE_KW,
    CONTINUE_KW,
    BREAK_KW,
    RETURN_KW,
    VOID_KW,
    BOOL_KW,
    STRING_KW,
    INT_KW,
    FLOAT_KW,
    MUTABLE_KW,
    EXIST_KW,
    MATCH_KW,

    // values:
    BOOL_VAL,
    STRING_VAL,
    INT_VAL,
    FLOAT_VAL,

    // operators:
    ASSIGN_OP,
    NOT_OP,
    AND_OP,
    OR_OP,
    EQUAL_OP,
    NOT_EQUAL_OP,
    LESS_EQUAL_OP,
    MORE_EQUAL_OP,
    LESS_OP,
    MORE_OP,
    PLUS_OP,
    MINUS_OP,
    MULTI_OP,
    DIV_OP,
    MODULO_OP,
    L_PAREN_OP,
    R_PAREN_OP,
    L_BRACKET_OP,
    R_BRACKET_OP,
    OPTIONAL_OP,
    MATCH_CASE_OP,

    // terminals:
    IDENTIFIER,
    SEMICOLON,
    COMMA,

    COMMENT,
    ETX,
    UNKNOWN
}
