package tkom.project.exceptions;

import tkom.project.nodes.TypeSpecifier;

public class LogicalNotException extends RuntimeException {
    public LogicalNotException(TypeSpecifier type, String where) {
        super("ERROR: The result of LITERAL PARSING at: "+where+"that returns "+type+", Cannot be negated with LOGICAL NOT");
    }
}
