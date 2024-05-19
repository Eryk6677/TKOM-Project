package tkom.project.tokens;

public class ValueString implements ReturnValue{
    private final String value;
    public ValueString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
