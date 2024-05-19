package tkom.project.tokens;

public class ValueInteger implements ReturnValue {
    private final Integer value;
    public ValueInteger(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}