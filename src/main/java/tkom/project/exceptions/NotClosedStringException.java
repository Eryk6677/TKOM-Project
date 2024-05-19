package tkom.project.exceptions;

import java.util.AbstractMap;

public class NotClosedStringException extends RuntimeException {
    public NotClosedStringException(AbstractMap.SimpleEntry<Integer, Integer> position) {
        super("ERROR: STRING at: " + position.getKey() + ":" + position.getValue() + " was not closed");
    }
}
