package tkom.project.exceptions;

public class InvalidExistStatementIdentifierException  extends RuntimeException {
    public InvalidExistStatementIdentifierException(String where) {
        super("ERROR: Value passed to EXIST statement is not an IDENTIFIER at: "+where);
    }
}
