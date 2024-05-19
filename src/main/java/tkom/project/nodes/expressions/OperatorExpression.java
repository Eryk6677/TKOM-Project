package tkom.project.nodes.expressions;

import tkom.project.nodes.Operator;

public class OperatorExpression extends Expression {

    Expression leftExpr;
    Expression rightExpr;
    Operator operator;
    public OperatorExpression(Expression left, Expression right, Operator op) {
        super();
        this.leftExpr = left;
        this.rightExpr = right;
        this.operator = op;
    }

    public Expression getLeftExpr() {
        return leftExpr;
    }

    public Expression getRightExpr() {
        return rightExpr;
    }

    public Operator getOperator() {
        return operator;
    }
}
