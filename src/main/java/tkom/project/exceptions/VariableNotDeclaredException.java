package tkom.project.exceptions;

public class VariableNotDeclaredException extends RuntimeException {
    public VariableNotDeclaredException(String identifier) {
        super("ERROR: Variable "+identifier.toUpperCase()+" has NOT been declared in this scope");
    }
}