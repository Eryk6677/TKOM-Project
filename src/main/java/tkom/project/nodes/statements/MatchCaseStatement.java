package tkom.project.nodes.statements;

import tkom.project.nodes.expressions.Expression;

import java.util.List;

public class MatchCaseStatement extends Statement {
    private final Expression condition;

    private final List<Statement> statements;

    public MatchCaseStatement(Expression condition, List<Statement> statements) {
        this.condition = condition;
        this.statements = statements;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
