package tkom.project.nodes;

import java.util.List;

public class Program {
    private final List<FunctionDefinition> functions;
    private final List<VariableDefinition> variables;

    public Program(List<FunctionDefinition> functions, List<VariableDefinition> variables) {
        this.functions = functions;
        this.variables = variables;
    }

    public List<VariableDefinition> getVariables() {
        return variables;
    }

    public List<FunctionDefinition> getFunctions() {
        return functions;
    }
}

