package tkom.project.exceptions;

public class MissingElseBlockInExistStatementException extends RuntimeException {
    public MissingElseBlockInExistStatementException(String received) {
        super("ERROR: Expected ELSE block in EXIST statement, instead got: "+received);
    }
}
