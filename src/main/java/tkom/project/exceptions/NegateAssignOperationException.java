package tkom.project.exceptions;

public class NegateAssignOperationException extends RuntimeException {
    public NegateAssignOperationException() {
        super("ERROR: Assign Operation cannot be Negated");
    }
}
