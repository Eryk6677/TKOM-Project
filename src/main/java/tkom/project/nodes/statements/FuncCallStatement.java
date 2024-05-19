package tkom.project.nodes.statements;

import tkom.project.nodes.NotValue;
import tkom.project.nodes.expressions.Expression;
import java.util.List;

public class FuncCallStatement extends Expression {
    List<Expression> arguments;

    String funcIdentifier;

    NotValue notValue;

    public FuncCallStatement(NotValue notValue, String identifier, List<Expression> arguments) {
        this.notValue = notValue;
        this.funcIdentifier = identifier;
        this.arguments = arguments;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public String getIdentifier() {
        return funcIdentifier;
    }

    @Override
    public NotValue getNotFlag() {
        return notValue;
    }
}
