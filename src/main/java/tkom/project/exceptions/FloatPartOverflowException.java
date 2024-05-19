package tkom.project.exceptions;

import java.util.AbstractMap;

public class FloatPartOverflowException extends RuntimeException {
    public FloatPartOverflowException(AbstractMap.SimpleEntry<Integer, Integer> position) {
        super("ERROR: Building Float part Overflow at: " + position.getKey() + ":" + position.getValue());
    }
}
