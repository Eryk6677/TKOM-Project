package tkom.project.exceptions;

import tkom.project.nodes.TypeSpecifier;

public class MismatchedTypesException extends RuntimeException {
    public MismatchedTypesException(String name, TypeSpecifier expected, TypeSpecifier actual) {
        super("ERROR: Variable "+name.toUpperCase()+" of type: "+expected+" cannot hava assigned value of type: "+actual);
    }
}
