package tkom.project.exceptions;

public class NonOptionalVariableMissingValueException extends RuntimeException {
    public NonOptionalVariableMissingValueException(String identifier) {
        super("ERROR: Variable "+identifier.toUpperCase()+" has no assigned value, but is NOT optional");
    }
}
