package tkom.project.exceptions;

public class IncorrectParameterAmountException extends RuntimeException {
    public IncorrectParameterAmountException(String funName, int actual, int expected) {
        super("ERROR: Function "+funName.toUpperCase()+" has incorrect AMOUNT OF PARAMETERS [E/G]: "+expected+"/"+actual);
    }
}
