package tkom.project.exceptions;

import tkom.project.nodes.TypeSpecifier;

public class IncorrectTypeInOperationException extends RuntimeException {
    public IncorrectTypeInOperationException(String operation, TypeSpecifier givenType) {
        super("ERROR: In "+operation.toUpperCase()+" operation, cannot evaluate for type: "+givenType);
    }
}
