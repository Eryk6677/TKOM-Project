package tkom.project.exceptions;

public class FloatOverOrUnderFlowException extends RuntimeException{
    public FloatOverOrUnderFlowException(String type) {
        super("ERROR: Float overflow resulting in: " + type);
    }
}
