package tkom.project.exceptions;

public class FunctionNotDeclaredException extends RuntimeException {
    public FunctionNotDeclaredException(String identifier) {
        super("ERROR: Function "+identifier.toUpperCase()+" has not been declared!");
    }
}
