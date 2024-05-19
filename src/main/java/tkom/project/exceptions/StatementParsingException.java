package tkom.project.exceptions;

public class StatementParsingException extends RuntimeException {
    public StatementParsingException(String what, String identifier) {
        super("ERROR: Could not build "+what+" statement at: "+identifier);
    }
}
