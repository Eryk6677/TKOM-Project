package tkom.project.nodes.statements;

import java.util.List;

public class IfElseStatement extends Statement {
    IfBlock ifStatements;
    List<IfBlock> elifStatements;
    IfBlock elseStatements;

    public IfElseStatement(IfBlock ifPart, List<IfBlock> elifPart, IfBlock elsePart) {
        this.ifStatements = ifPart;
        this.elifStatements = elifPart;
        this.elseStatements = elsePart;
    }

    public IfBlock getIfStatements() {
        return ifStatements;
    }

    public List<IfBlock> getElifStatements() {
        return elifStatements;
    }

    public IfBlock getElseStatements() {
        return elseStatements;
    }
}
