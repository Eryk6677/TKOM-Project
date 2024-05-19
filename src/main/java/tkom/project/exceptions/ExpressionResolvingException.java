package tkom.project.exceptions;

public class ExpressionResolvingException extends RuntimeException {
    public ExpressionResolvingException() {
        super("ERROR: Unexpected error occurred during EXPRESSION resolving");
    }
}
