package tkom.project.exceptions;

import tkom.project.tokens.TokenType;

import java.util.Arrays;

public class UnexpectedTokenException extends RuntimeException {

    public UnexpectedTokenException(TokenType[] expected, String received) {
        super("ERROR: Unexpected TOKEN (E/G): "+ Arrays.toString(expected) +"/"+received);
    }
}