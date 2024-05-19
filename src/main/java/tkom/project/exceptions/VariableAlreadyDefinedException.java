package tkom.project.exceptions;

public class VariableAlreadyDefinedException extends RuntimeException {
    public VariableAlreadyDefinedException(String identifier) {
        super("ERROR: Variable "+identifier.toUpperCase()+" has ALREADY been declared!");
    }
}