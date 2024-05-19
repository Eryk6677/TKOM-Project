package tkom.project.exceptions;

import tkom.project.nodes.TypeSpecifier;

public class IncorrectReturnTypeException extends RuntimeException{
    public IncorrectReturnTypeException(String funName, TypeSpecifier actual, TypeSpecifier expected) {
        super("ERROR: Function "+funName.toUpperCase()+" has incorrect return type[E/G]: "+expected+"/"+actual);
    }
}
