package tkom.project.exceptions;

import tkom.project.nodes.TypeSpecifier;

public class ValueException extends RuntimeException{

    public ValueException(TypeSpecifier type) {
        super("ERROR: Type "+type+" cannot be negative!");
    }
}
