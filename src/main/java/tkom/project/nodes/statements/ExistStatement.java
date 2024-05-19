package tkom.project.nodes.statements;

import tkom.project.nodes.expressions.Expression;

import java.util.List;

public class ExistStatement extends Statement {
    Expression identifier;

    List<Statement> existStatements;
    List<Statement> elseStatements;

    public ExistStatement(Expression identifier, List<Statement> statements1, List<Statement> statements2) {
        this.identifier = identifier;
        this.existStatements = statements1;
        this.elseStatements = statements2;
    }

    public Expression getExistValue() {
        return identifier;
    }

    public List<Statement> getExistStatements() {
        return existStatements;
    }

    public List<Statement> getElseStatements() {
        return elseStatements;
    }
}
