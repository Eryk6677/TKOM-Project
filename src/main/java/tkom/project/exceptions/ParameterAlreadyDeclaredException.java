package tkom.project.exceptions;

public class ParameterAlreadyDeclaredException extends RuntimeException {
    public ParameterAlreadyDeclaredException(String paramName, String funName) {
        super("ERROR: Parameter "+paramName.toUpperCase()+" in function "+funName.toUpperCase()+" has ALREADY been declared!");
    }
}
