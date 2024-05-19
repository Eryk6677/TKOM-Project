package tkom.project.nodes.statements;

import tkom.project.nodes.JumpType;
import tkom.project.nodes.expressions.Expression;


public class JumpStatement extends Statement {
    JumpType type;
    Expression retExpr;

    public JumpStatement(JumpType type) {
        this.type = type;
        this.retExpr = null;
    }

    public JumpStatement(JumpType type, Expression retExpr) {
        this.type = type;
        this.retExpr = retExpr;
    }

    public JumpType getType() {
        return type;
    }

    public Expression getRetExpr() {
        return retExpr;
    }
}
