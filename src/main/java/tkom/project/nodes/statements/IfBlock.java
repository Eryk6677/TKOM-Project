package tkom.project.nodes.statements;

import tkom.project.nodes.expressions.Expression;

import java.util.List;

public class IfBlock {
    Expression condition;

    List<Statement> statements;

    public IfBlock(Expression cond, List<Statement> statements) {
        this.condition = cond;
        this.statements = statements;
    }

    public IfBlock(List<Statement> statements) {
        this.condition = null;
        this.statements = statements;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
