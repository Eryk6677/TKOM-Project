package tkom.project.exceptions;

import tkom.project.nodes.TypeSpecifier;

import java.util.Arrays;

public class TypeException extends RuntimeException {
    public TypeException(TypeSpecifier[] expected, TypeSpecifier received, String what) {
        super("ERROR: Mismatched TYPES (E/G): "+ Arrays.toString(expected)+"/"+received+" in: "+what.toUpperCase());
    }
}