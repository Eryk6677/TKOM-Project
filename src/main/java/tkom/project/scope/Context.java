package tkom.project.scope;

import tkom.project.nodes.VariableDefinition;
import tkom.project.nodes.expressions.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Context {

    private final List<VariableDefinition> scopeVariables;

    public Context() {
        scopeVariables = new ArrayList<>();
    }

    public Context(List<VariableDefinition> variables) {
        scopeVariables = variables;
    }

    public void addVariable(VariableDefinition variable) {
        scopeVariables.add(variable);
    }

    public List<VariableDefinition> getAllScopeVariables() {
        return scopeVariables;
    }

    public VariableDefinition getVariable(String identifier) {
        for (VariableDefinition scopeVariable : scopeVariables) {
            if (Objects.equals(scopeVariable.getIdentifier(), identifier)) return scopeVariable;
        }
        return null;
    }

    public Expression setVariableValue(String identifier, Expression newValue) {
        for (VariableDefinition scopeVariable : scopeVariables) {
            if (Objects.equals(scopeVariable.getIdentifier(), identifier)) {
                scopeVariable.setValue(newValue);
                return newValue;
            }
        }
        return null;
    }

    public void updateVariables(List<VariableDefinition> toUpdate) {
        for (VariableDefinition varToUpdate : toUpdate) {
            setVariableValue(varToUpdate.getIdentifier(), varToUpdate.getValue());
        }
    }
}
