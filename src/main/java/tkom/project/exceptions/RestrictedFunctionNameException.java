package tkom.project.exceptions;

public class RestrictedFunctionNameException extends RuntimeException {
    public RestrictedFunctionNameException(String identifier) {
        super("ERROR: Cannot declare Function "+identifier.toUpperCase()+", as the name is Restricted!");
    }
}
