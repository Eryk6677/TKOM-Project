package tkom.project.nodes.statements;

import tkom.project.nodes.expressions.Expression;

import java.util.List;

public class PatternMatchingStatement extends Statement {
    private final Expression toMatch;

    private final List<MatchCaseStatement> cases;

    public PatternMatchingStatement(Expression toMatch, List<MatchCaseStatement> cases) {
        this.toMatch = toMatch;
        this.cases = cases;
    }

    public Expression getToMatch() {
        return toMatch;
    }

    public List<MatchCaseStatement> getCases() {
        return cases;
    }
}
