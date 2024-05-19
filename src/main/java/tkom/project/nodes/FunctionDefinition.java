package tkom.project.nodes;

import tkom.project.nodes.statements.Statement;

import java.util.List;

public class FunctionDefinition extends Statement {
    private final String name;
    private final TypeSpecifier type;
    private final List<Parameter> parameters;
    private List<Statement> statements;

    public FunctionDefinition(String identifier, TypeSpecifier type, List<Parameter> parameters, List<Statement> statements) {
        this.name = identifier;
        this.type = type;
        this.parameters = parameters;
        this.statements = statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public TypeSpecifier getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}