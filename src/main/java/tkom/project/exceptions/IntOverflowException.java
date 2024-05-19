package tkom.project.exceptions;

import java.util.AbstractMap;

public class IntOverflowException extends RuntimeException {
    public IntOverflowException(AbstractMap.SimpleEntry<Integer, Integer> position) {
        super("ERROR: Integer OVERFLOW at: " + position.getKey() + ":" + position.getValue());
    }
}
