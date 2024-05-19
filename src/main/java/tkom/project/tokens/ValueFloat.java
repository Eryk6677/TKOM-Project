package tkom.project.tokens;

public class ValueFloat implements ReturnValue {
    private final Float value;
    public ValueFloat(Float value) {
        this.value = value;
    }

    @Override
    public Float getValue() {
        return value;
    }
}
