package tkom.project.exceptions;

import java.util.AbstractMap;

public class MissingMultiCharOpException extends RuntimeException {
    public MissingMultiCharOpException(char missing, AbstractMap.SimpleEntry<Integer, Integer> position) {
        super("ERROR: "+missing + " missing in Operator at: " + position.getKey() + ":" + position.getValue());
    }
}
