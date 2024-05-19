package tkom.project.exceptions;

import tkom.project.nodes.TypeSpecifier;

public class MismatchedSidesOfOperationException extends RuntimeException {
    public MismatchedSidesOfOperationException(String operation, TypeSpecifier left, TypeSpecifier right) {
        super("ERROR: In "+operation.toUpperCase()+" operation there are mismatched types [L/R]: "+left+"/"+right);
    }
}
