package tkom.project.exceptions;

import tkom.project.nodes.TypeSpecifier;

public class ArithmeticNotException extends RuntimeException {
    public ArithmeticNotException(TypeSpecifier type, String identifier) {
        super("ERROR: The result of "+identifier.toUpperCase()+" that returns "+type+", Cannot be negated with ARITHMETIC NOT");
    }
}

