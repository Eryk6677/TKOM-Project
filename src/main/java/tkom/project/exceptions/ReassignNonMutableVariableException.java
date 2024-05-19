package tkom.project.exceptions;

public class ReassignNonMutableVariableException extends RuntimeException {
    public ReassignNonMutableVariableException(String identifier) {
        super("ERROR: Variable "+identifier.toUpperCase()+" is NOT MUTABLE, you can not change its value");
    }
}
