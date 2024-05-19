package tkom.project.exceptions;

public class DivisionByZeroException extends RuntimeException {
    public DivisionByZeroException() {
        super("ERROR: Division by zero is NOT ALLOWED");
    }
}
