package tkom.project.tokens;

public class ValueBool implements ReturnValue {
    private final Boolean value;
    public ValueBool(Boolean value) {
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }
}
