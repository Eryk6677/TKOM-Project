package tkom.project.exceptions;

import java.util.AbstractMap;

public class IncorrectFloatPartInFloatValue extends RuntimeException {
    public IncorrectFloatPartInFloatValue(AbstractMap.SimpleEntry<Integer, Integer> position) {
        super("ERROR: Incorrect FLOAT value after DOT at: " + position.getKey() + ":" + position.getValue());
    }
}
