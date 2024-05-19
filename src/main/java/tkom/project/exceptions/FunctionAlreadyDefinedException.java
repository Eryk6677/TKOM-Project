package tkom.project.exceptions;

public class FunctionAlreadyDefinedException extends RuntimeException {
    public FunctionAlreadyDefinedException(String identifier) {
        super("ERROR: Function "+identifier.toUpperCase()+" has ALREADY been declared!");
    }
}
