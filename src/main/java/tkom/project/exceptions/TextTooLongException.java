package tkom.project.exceptions;

import java.util.AbstractMap;

public class TextTooLongException extends RuntimeException {
    public TextTooLongException(AbstractMap.SimpleEntry<Integer, Integer> position) {
        super("ERROR: Identifier/String/Comment TOO LONG at: " + position.getKey() + ":" + position.getValue());
    }
}
