package tkom.project.exceptions;

public class InvalidJumpStatement extends RuntimeException {
    public InvalidJumpStatement(String location) {
        super("ERROR: Cannot use CONTINUE/BREAK outside WHILE-LOOP in: "+location.toUpperCase());
    }
}
