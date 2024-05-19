package tkom.project;

import tkom.project.exceptions.*;
import tkom.project.nodes.*;
import tkom.project.nodes.FunctionDefinition;
import tkom.project.nodes.statements.*;
import tkom.project.tokens.Token;
import tkom.project.tokens.TokenType;
import tkom.project.nodes.expressions.*;

import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    private final Lexer lexer;

    private final ArrayList<FunctionDefinition> functions = new ArrayList<>();
    private final ArrayList<VariableDefinition> variables = new ArrayList<>();

    private Token currentToken;


    public Parser(Lexer lexer) throws IOException {
        this.lexer = lexer;
        currentToken = this.lexer.getNextToken();
        skipComments();
    }

    private void pullNextToken() throws IOException {       // Allows to pull the next token from lexer
        if ((currentToken != null) && (currentToken.getType() != TokenType.ETX)) {
            currentToken = this.lexer.getNextToken();
            skipComments();
        }
    }

    private void skipComments() throws IOException {        // When a token is of type COMMENT it skips them
        while ((currentToken != null) && (currentToken.getType() == TokenType.COMMENT)) {
            currentToken = this.lexer.getNextToken();
        }
    }

    public Program parse() throws IOException {     // Main parser functionality, when run it parses all tokens passed from lexer
        while (currentToken != null) {

            VariableDefinition varDef;
            if ((varDef = tryParseVariableDefinition()) != null) {
                variables.add(varDef);
                pullNextToken();
            }

            FunctionDefinition funDef;
            if ((funDef = tryParseFunctionDefinition()) != null) {
                functions.add(funDef);
                pullNextToken();
            }

            if (currentToken.getType() == TokenType.ETX) break;     // Reached end of code
            if (varDef == null && funDef == null) {       // In case nothing could be parsed, but did not reach the end of code
                throw new ParserException("PARSER: Unable to parse Function/Variable Definition at: " + currentToken);
            }
        }
        return new Program(functions, variables);
    }

    // variable-definition
    private VariableDefinition tryParseVariableDefinition() throws IOException {      // DONE
        // Determine if variable is mutable
        boolean mutability = checkMutability();

        // Get the type of variable
        TypeSpecifier type = getVarParArgType();
        if (type == TypeSpecifier.UNKNOWN) {
            if (!mutability) return null;
            else throw new UnexpectedTokenException(new TokenType[]{TokenType.INT_KW, TokenType.FLOAT_KW, TokenType.STRING_KW, TokenType.BOOL_KW}, currentToken.toString());
        }

        // Check if variable is optional
        pullNextToken();
        boolean optional = checkOptionality();

        // Get the identifier of the variable
        expect(TokenType.IDENTIFIER, "Variable definition");
        String name = (String) currentToken.value().getValue();

        // Check the assigned value/just declaration
        pullNextToken();
        Expression expression = null;

        if (currentToken.getType() == TokenType.ASSIGN_OP) {
            // Parse the assigned value
            pullNextToken();
            expression = tryParseExpression();
            if (expression == null) throw new ParserException("PARSER: Could not parse Expression at: "+currentToken);
        }

        expect(TokenType.SEMICOLON, "Variable definition");
        return new VariableDefinition(mutability, optional, name, type, expression);
    }

    private boolean checkMutability() throws IOException {
        if (currentToken.getType() == TokenType.MUTABLE_KW) {
            pullNextToken();
            return true;
        } else return false;
    }

    private boolean checkOptionality() throws IOException {
        if (currentToken.getType() == TokenType.OPTIONAL_OP) {
            pullNextToken();
            return true;
        } else return false;
    }

    private TypeSpecifier getVarParArgType() {
        return switch (currentToken.getType()) {
            case INT_KW -> TypeSpecifier.INT;
            case FLOAT_KW -> TypeSpecifier.FLOAT;
            case STRING_KW -> TypeSpecifier.STRING;
            case BOOL_KW -> TypeSpecifier.BOOL;
            default -> TypeSpecifier.UNKNOWN;
        };
    }

    // function-definition
    private FunctionDefinition tryParseFunctionDefinition() throws IOException {      // DONE?
        // Function definition has to start with DEF keyword
        if (currentToken.getType() != TokenType.DEF_KW) return null;

        // Determine the returned value
        pullNextToken();
        TypeSpecifier type = getFunctionType();

        if (type == TypeSpecifier.UNKNOWN) throw new ParserException("PARSER: UNKNOWN function Type at: "+currentToken);

        // Determine the identifier of function
        pullNextToken();
        expect(TokenType.IDENTIFIER, "Function definition");
        String name = (String) currentToken.value().getValue();

        // Parse the parameters
        pullNextToken();
        expect(TokenType.L_PAREN_OP, "Function definition: "+name);

        pullNextToken();
        ArrayList<Parameter> parameters = tryParseParameters(name);

        // Parse the statements block
        ArrayList<Statement> statements = new ArrayList<>();
        FunctionDefinition currentFunction = new FunctionDefinition(name, type, parameters, statements);

        pullNextToken();
        expect(TokenType.L_BRACKET_OP, "Function definition"+name);

        pullNextToken();
        currentFunction.setStatements(tryParseStatements());

        return currentFunction;
    }

    private TypeSpecifier getFunctionType() {
        return switch (currentToken.getType()) {
            case INT_KW -> TypeSpecifier.INT;
            case FLOAT_KW -> TypeSpecifier.FLOAT;
            case STRING_KW -> TypeSpecifier.STRING;
            case BOOL_KW -> TypeSpecifier.BOOL;
            case VOID_KW -> TypeSpecifier.VOID;
            default -> TypeSpecifier.UNKNOWN;
        };
    }

    private ArrayList<Parameter> tryParseParameters(String funcName) throws IOException {
        ArrayList<Parameter> parameters = new ArrayList<>();

        Parameter parameter;
        while ((parameter = tryParseParameter(funcName)) != null) {
            parameters.add(parameter);

            if (currentToken.getType() != TokenType.COMMA) {
                expect(TokenType.R_PAREN_OP, "Parameters in: "+funcName);
            } else pullNextToken();
        }
        return parameters;
    }

    // parameters
    private Parameter tryParseParameter(String funcName) throws IOException {      // DONE
        // Determine if parameter is mutable
        boolean mutability = checkMutability();

        // Determine the type of parameter
        TypeSpecifier type = getVarParArgType();
        if (type == TypeSpecifier.UNKNOWN) {
            if (!mutability) return null;
            else throw new UnexpectedTokenException(new TokenType[]{TokenType.INT_KW, TokenType.FLOAT_KW, TokenType.STRING_KW, TokenType.BOOL_KW}, currentToken.toString());
        }

        // Check if parameter is optional
        pullNextToken();
        boolean optional = checkOptionality();

        // Determine the identifier of parameter
        expect(TokenType.IDENTIFIER, "Parameters in: "+funcName);
        String name = (String) currentToken.value().getValue();

        pullNextToken();
        return new Parameter(mutability, optional, name, type);
    }

    // primary-expression
    private Expression tryParsePrimaryExpression() throws IOException {      // DONE
        NotValue notFlag;
        if (currentToken.getType() == TokenType.MINUS_OP) {
            notFlag = NotValue.ARITHMETIC;
            pullNextToken();
        } else if (currentToken.getType() == TokenType.NOT_OP) {
            notFlag = NotValue.LOGICAL;
            pullNextToken();
        } else notFlag = null;

        Expression evaluatedValue;
        if ((evaluatedValue = tryParseExpressionInParentheses(notFlag)) != null) return evaluatedValue;
        if ((evaluatedValue = tryParseIdentifierOrFuncCall(notFlag)) != null) return evaluatedValue;
        if ((evaluatedValue = tryParseLiteral(notFlag)) != null) return evaluatedValue;
        return null;
    }

    private Expression tryParseExpressionInParentheses(NotValue notValue) throws IOException {
        if (currentToken.getType() == TokenType.L_PAREN_OP) {
            pullNextToken();
            Expression expression = tryParseExpression();

            if (expression != null) {
                expect(TokenType.R_PAREN_OP, "Primary expression: "+currentToken.toString());
                pullNextToken();

                return validatedNegation(expression, notValue);
            }
            throw new ParserException("PARSER: Could not parse PrimaryExpression at: "+currentToken);
        }
        return null;
    }

    private Expression validatedNegation(Expression expression, NotValue notValue) {
        if (notValue != null) {
            if (expression.getClass() == OperatorExpression.class) expression.setNotFlag(notValue);
            else {
                if (expression.getNotFlag() == null) expression.setNotFlag(notValue);
                else {
                    if (notValue == expression.getNotFlag()) expression.setNotFlag(null);
                    else throwIncorrectNegationException(notValue, expression.getType());
                }
            }
        }
        return expression;
    }

    private void throwIncorrectNegationException(NotValue notValue, TypeSpecifier type) {
        if (notValue == NotValue.ARITHMETIC) throw new ArithmeticNotException(type, "expression");
        else throw new LogicalNotException(type, "expression");
    }

    private Expression tryParseLiteral(NotValue notFlag) throws IOException {
        switch (currentToken.getType()) {
            case INT_VAL -> {
                if (notFlag == NotValue.LOGICAL) throw new LogicalNotException(TypeSpecifier.INT, currentToken.toString());
                Expression intResult = new Expression(notFlag, TypeSpecifier.INT, (Integer) currentToken.value().getValue());
                pullNextToken();
                return intResult;
            }
            case FLOAT_VAL -> {
                if (notFlag == NotValue.LOGICAL) throw new LogicalNotException(TypeSpecifier.FLOAT, currentToken.toString());
                Expression fltResult = new Expression(notFlag, TypeSpecifier.FLOAT, (Float) currentToken.value().getValue());
                pullNextToken();
                return fltResult;
            }
            case STRING_VAL -> {
                if (notFlag != null) throw new ValueException(TypeSpecifier.STRING);
                Expression strResult = new Expression(null, TypeSpecifier.STRING, (String) currentToken.value().getValue());
                pullNextToken();
                return strResult;
            }
            case BOOL_VAL -> {
                if (notFlag == NotValue.ARITHMETIC) throw new ArithmeticNotException(TypeSpecifier.BOOL, "_literal");
                Expression bolResult = new Expression(notFlag, TypeSpecifier.BOOL, (Boolean) currentToken.value().getValue());
                pullNextToken();
                return bolResult;
            }
            default -> {
                return null;
            }
        }
    }

    // expression

    private Expression tryParseExpression() throws IOException {      // DONE
        // Parse the left side of assignment
        Expression leftExpr = tryParseLogicalOrExpression();
        if (leftExpr == null) return null;

        // IF it is an identifier, it is possible to parse as ASSIGNMENT,
        // Otherwise it cannot be assigned, so just return build expression
        if (leftExpr.getIdentifier() != null) {
            Operator op;
            if (currentToken.getType() == TokenType.ASSIGN_OP) {
                op = Operator.ASSIGN;
            } else {
                return leftExpr;
            }

            pullNextToken();
            Expression rightExpr = tryParseExpression();
            if (rightExpr == null) return null;

            return new OperatorExpression(leftExpr, rightExpr, op);
        }
        return leftExpr;
    }

    public Expression testParseExpression() throws IOException {
        return tryParseExpression();
    }

    // logical-or-expression
    private Expression tryParseLogicalOrExpression() throws IOException {      // DONE
        Expression leftExpr = tryParseLogicalAndExpression();
        if (leftExpr == null) return null;

        Operator op;
        if (currentToken.getType() == TokenType.OR_OP) {
            op = Operator.OR;
        } else {
            return leftExpr;
        }

        pullNextToken();
        Expression rightExpr = tryParseLogicalOrExpression();
        if (rightExpr == null) return null;

        return new OperatorExpression(leftExpr, rightExpr, op);
    }

    // logical-and-expression
    private Expression tryParseLogicalAndExpression() throws IOException {      // DONE
        Expression leftExpr = tryParseEqualityExpression();
        if (leftExpr == null) return null;

        Operator op;
        if (currentToken.getType() == TokenType.AND_OP) {
            op = Operator.AND;
        } else {
            return leftExpr;
        }

        pullNextToken();
        Expression rightExpr = tryParseLogicalAndExpression();
        if (rightExpr == null) return null;

        return new OperatorExpression(leftExpr, rightExpr, op);
    }

    // equality-expression
    private Expression tryParseEqualityExpression() throws IOException {      // DONE
        Expression leftExpr = tryParseRelationalExpression();
        if (leftExpr == null) return null;

        Operator op;
        switch (currentToken.getType()) {
            case EQUAL_OP -> op = Operator.EQUAL;
            case NOT_EQUAL_OP -> op = Operator.NOT_EQUAL;
            default -> {
                return leftExpr;
            }
        }

        pullNextToken();
        Expression rightExpr = tryParseEqualityExpression();
        if (rightExpr == null) return null;

        return new OperatorExpression(leftExpr, rightExpr, op);
    }

    // relational-expression
    private Expression tryParseRelationalExpression() throws IOException {      // DONE
        Expression leftExpr = tryParseAdditiveExpression();
        if (leftExpr == null) return null;

        Operator op;
        switch (currentToken.getType()) {
            case LESS_OP -> op = Operator.LESS;
            case MORE_OP -> op = Operator.MORE;
            case LESS_EQUAL_OP -> op = Operator.LESS_EQ;
            case MORE_EQUAL_OP -> op = Operator.MORE_EQ;
            default -> {
                return leftExpr;
            }
        }

        pullNextToken();
        Expression rightExpr = tryParseRelationalExpression();
        if (rightExpr == null) return null;

        return new OperatorExpression(leftExpr, rightExpr, op);
    }

    // additive-expression
    private Expression tryParseAdditiveExpression() throws IOException {      // DONE
        Expression leftExpr = tryParseMultiplicativeExpression();
        if (leftExpr == null) return null;

        Operator op;
        switch (currentToken.getType()) {
            case PLUS_OP -> op = Operator.ADD;
            case MINUS_OP -> op = Operator.SUB;
            default -> {
                return leftExpr;
            }
        }

        pullNextToken();
        Expression rightExpr = tryParseAdditiveExpression();
        if (rightExpr == null) return null;

        return new OperatorExpression(leftExpr, rightExpr, op);
    }

    // multiplicative-expression
    private Expression tryParseMultiplicativeExpression() throws IOException {      // DONE
        Expression leftExpr = tryParsePrimaryExpression();
        if (leftExpr == null) return null;

        Operator op;
        switch (currentToken.getType()) {
            case MULTI_OP -> op = Operator.MULTI;
            case DIV_OP -> op = Operator.DIV;
            case MODULO_OP -> op = Operator.MODULO;
            default -> {
                return leftExpr;
            }
        }

        pullNextToken();
        Expression rightExpr = tryParseMultiplicativeExpression();
        if (rightExpr == null) return null;

        return new OperatorExpression(leftExpr, rightExpr, op);
    }

    // identifier-OR-func-call
    private Expression tryParseIdentifierOrFuncCall(NotValue notFlag) throws IOException {      // DONE
        // get the identifier
        if (currentToken.getType() != TokenType.IDENTIFIER) return null;
        String identifier = (String) currentToken.value().getValue();

        // check if it is just a variable or a func-call
        pullNextToken();
        // variable case
        if (currentToken.getType() != TokenType.L_PAREN_OP) {
            // return a variable call
            Expression ident = new Expression(notFlag, TypeSpecifier.UNKNOWN);
            ident.setIdentifier(identifier);
            return ident;
        }

        // func-call case
        pullNextToken();
        ArrayList<Expression> arguments = tryParseArguments();

        if (currentToken.getType() == TokenType.R_PAREN_OP) pullNextToken();

        FuncCallStatement result = new FuncCallStatement(notFlag, identifier, arguments);
        result.setNotFlag(notFlag);

        return result;
    }

    // arguments
    private ArrayList<Expression> tryParseArguments() throws IOException {      // DONE
        ArrayList<Expression> arguments = new ArrayList<>();

        Expression argument;
        while ((argument = tryParseExpression()) != null) {
            arguments.add(argument);

            if (currentToken.getType() != TokenType.COMMA) {
                expect(TokenType.R_PAREN_OP, "Arguments at: "+currentToken.toString());
            } else pullNextToken();
        }

        return arguments;
    }

    // statements
    private ArrayList<Statement> tryParseStatements() throws IOException {      // DONE
        ArrayList<Statement> statements = new ArrayList<>();

        Statement statement;
        while ((statement = tryParseStatement()) != null) {
            statements.add(statement);
            if (currentToken.getType() == TokenType.SEMICOLON) pullNextToken();
        }

        expect(TokenType.R_BRACKET_OP, currentToken.toString());

        if (statements.isEmpty()) throw new StatementParsingException("FUNCTION/INSTRUCTION BLOCK", currentToken.toString());
        return statements;
    }

    public Statement testParseStatements() throws IOException {
        return tryParseStatement();
    }

    private Statement tryParseStatement() throws IOException {       // DONE
        Statement statement;

        if ((statement = tryParseVariableDefinition()) != null) return statement;
        if ((statement = tryParseExpression()) != null) return statement;
        if ((statement = tryParseIfElseStatement()) != null) return statement;
        if ((statement = tryParseWhileStatement()) != null) return statement;
        if ((statement = tryParseJumpStatement()) != null) return statement;
        if ((statement = tryParseExistStatement()) != null) return statement;
        if ((statement = tryParsePatternStatement()) != null) return statement;

        return null;
    }

    private Expression tryParseCondition(String currentObj) throws IOException {
        expect(TokenType.L_PAREN_OP, currentToken.toString());
        pullNextToken();

        // Parse the actual condition in parentheses
        Expression condition;
        if ((condition = tryParseExpression())== null) throw new StatementParsingException(currentObj+"/CONDITION", currentToken.toString());

        expect(TokenType.R_PAREN_OP, currentToken.toString());
        pullNextToken();

        return condition;
    }

    private ArrayList<Statement> tryParseInstructionBlock(String currentObj) throws IOException {
        expect(TokenType.L_BRACKET_OP, currentToken.toString());

        // Parse the statements in block
        pullNextToken();
        ArrayList<Statement> statements = tryParseStatements();
        if (statements.isEmpty()) throw new StatementParsingException(currentObj+"/INSTRUCTION BLOCK", currentToken.toString());

        expect(TokenType.R_BRACKET_OP, currentToken.toString());
        pullNextToken();

        return statements;
    }

    // while-statement
    private Statement tryParseWhileStatement() throws IOException {      // DONE
        // While statement must start with keyword "while"
        if (currentToken.getType() != TokenType.WHILE_KW) return null;
        pullNextToken();

        // Parse the condition of while statement
        Expression condition = tryParseCondition("WHILE");
        // Parse the statements in instruction block of while statement
        ArrayList<Statement> statements = tryParseInstructionBlock("WHILE");

        return new WhileStatement(condition, statements);
    }

    // if-else-statement
    private Statement tryParseIfElseStatement() throws IOException {      // DONE
        // IF statement must start with keyword "if"
        if (currentToken.getType() != TokenType.IF_KW) return null;
        pullNextToken();

        // Parse the condition of IF statement
        Expression condition = tryParseCondition("IF");
        // Parse the statements of IF
        ArrayList<Statement> statements = tryParseInstructionBlock("IF");

        IfBlock ifPart = new IfBlock(condition, statements);

        // try to parse the ELIF blocks
        ArrayList<IfBlock> elifPart = new ArrayList<>();
        while (currentToken.getType() == TokenType.ELIF_KW) {
            pullNextToken();

            Expression elifCondition = tryParseCondition("ELIF");
            ArrayList<Statement> elifStatements = tryParseInstructionBlock("ELIF");

            elifPart.add(new IfBlock(elifCondition, elifStatements));
        }

        // ELSE part
        IfBlock elsePart = null;
        if (currentToken.getType() == TokenType.ELSE_KW) {
            pullNextToken();
            elsePart = new IfBlock(tryParseInstructionBlock("ELSE"));
        }

        return new IfElseStatement(ifPart, elifPart, elsePart);
    }

    // jump-statement
    private Statement tryParseJumpStatement() throws IOException {      // DONE
        if (currentToken.getType() == TokenType.CONTINUE_KW) {
            pullNextToken();
            return new JumpStatement(JumpType.CONTINUE);
        }

        if (currentToken.getType() == TokenType.BREAK_KW) {
            pullNextToken();
            return new JumpStatement(JumpType.BREAK);
        }

        if (currentToken.getType() == TokenType.RETURN_KW) {
            pullNextToken();
            Expression expr = tryParseExpression();
            if (expr == null) return new JumpStatement(JumpType.RETURN);

            return new JumpStatement(JumpType.RETURN, expr);
        }
        return null;
    }

    // exist-statement
    private Statement tryParseExistStatement() throws IOException {     // DONE
        // exist statement must start with EXIST keyword
        if (currentToken.getType() != TokenType.EXIST_KW) return null;

        // Parse the identifier of EXIST statement
        pullNextToken();
        expect(TokenType.L_PAREN_OP, currentToken.toString());

        pullNextToken();
        Expression identifier = tryParseExpression();
        if (identifier == null) throw new StatementParsingException("EXIST STATEMENT/VALUE", currentToken.toString());
        if (identifier instanceof FuncCallStatement || identifier.getIdentifier() == null) {
            throw new InvalidExistStatementIdentifierException(currentToken.toString());
        }

        expect(TokenType.R_PAREN_OP, currentToken.toString());

        // EXIST instruction block
        pullNextToken();
        ArrayList<Statement> statements = tryParseInstructionBlock("EXIST");

        // ELSE instruction block
        if (currentToken.getType() == TokenType.ELSE_KW) {
            pullNextToken();
            return new ExistStatement(identifier, statements, tryParseInstructionBlock("EXIST/ELSE"));
        } else throw new MissingElseBlockInExistStatementException(currentToken.toString());
    }

    // patter-matching-statement
    private Statement tryParsePatternStatement() throws IOException {     // DONE
        if (currentToken.getType() != TokenType.MATCH_KW) return null;

        // Parse the matched expression
        pullNextToken();
        Expression expression = tryParseCondition("MATCH CASE");

        // Parse the match cases block
        expect(TokenType.L_BRACKET_OP, currentToken.toString());
        pullNextToken();

        ArrayList<MatchCaseStatement> matchCases = tryParseMatchCases();
        if (matchCases.isEmpty()) throw new StatementParsingException("MATCH CASE/INSTRUCTION BLOCK", currentToken.toString());

        expect(TokenType.R_BRACKET_OP, currentToken.toString());
        pullNextToken();

        return new PatternMatchingStatement(expression, matchCases);
    }

    private ArrayList<MatchCaseStatement> tryParseMatchCases() throws IOException {
        ArrayList<MatchCaseStatement> matchCases= new ArrayList<>();

        while (currentToken.getType() != TokenType.R_BRACKET_OP) {
            Expression condition = tryParseMatchExpression();

            expect(TokenType.MATCH_CASE_OP, currentToken.toString());

            pullNextToken();
            ArrayList<Statement> statements = tryParseInstructionBlock("MATCH CASE");

            matchCases.add(new MatchCaseStatement(condition, statements));
        }
        return matchCases;
    }

    private Expression tryParseMatchExpression() throws IOException {  // DONE
        Expression leftExpr = tryParseMatchANDExpression();

        Operator op;
        if (currentToken.getType() == TokenType.OR_OP) op = Operator.OR;
        else return leftExpr;

        pullNextToken();
        Expression rightExpr = tryParseMatchExpression();

        return new OperatorExpression(leftExpr, rightExpr, op);
    }

    private Expression tryParseMatchANDExpression() throws IOException {
        Expression leftExpr = tryParseMatchCompPred();

        Operator op;
        if (currentToken.getType() == TokenType.AND_OP) op = Operator.AND;
        else return leftExpr;

        pullNextToken();
        Expression rightExpr = tryParseMatchANDExpression();
        return new OperatorExpression(leftExpr, rightExpr, op);
    }

    private Expression tryParseMatchCompPred() throws IOException {
        NotValue notFlag;
        if (currentToken.getType() == TokenType.NOT_OP) {
            notFlag = NotValue.LOGICAL;
            pullNextToken();
        } else notFlag = null;

        Expression evaluatedValue;
        if ((evaluatedValue = tryParseMatchExpressionInParentheses(notFlag)) != null) return evaluatedValue;
        if ((evaluatedValue = tryParsePredicate(notFlag)) != null) return evaluatedValue;
        if ((evaluatedValue = tryParseComparisonExpression()) != null) return evaluatedValue;
        return null;
    }

    private Expression tryParseMatchExpressionInParentheses(NotValue notFlag) throws IOException {
        if (currentToken.getType() == TokenType.L_PAREN_OP) {
            pullNextToken();
            Expression expression = tryParseMatchExpression();

            expect(TokenType.R_PAREN_OP, "Match case");

            pullNextToken();
            expression.setNotFlag(notFlag);
            return expression;
        }
        return null;
    }

    private FuncCallStatement tryParsePredicate(NotValue notFlag) throws IOException {
        if (currentToken.getType() == TokenType.IDENTIFIER) {

            ArrayList<Expression> arguments = new ArrayList<>();
            Expression varCall = new Expression(null, TypeSpecifier.UNKNOWN);
            varCall.setIdentifier("var");
            arguments.add(varCall);

            FuncCallStatement predicate = new FuncCallStatement(notFlag, (String) currentToken.value().getValue(), arguments);

            pullNextToken();
            return predicate;
        }
        return null;
    }

    private Expression tryParseComparisonExpression() throws IOException {
        Operator op;
        switch (currentToken.getType()) {
            case LESS_OP -> op = Operator.LESS;
            case MORE_OP -> op = Operator.MORE;
            case LESS_EQUAL_OP -> op = Operator.LESS_EQ;
            case MORE_EQUAL_OP -> op = Operator.MORE_EQ;
            case EQUAL_OP -> op = Operator.EQUAL;
            case NOT_OP -> op = Operator.NOT_EQUAL;
            default -> {
                return null;
            }
        }

        pullNextToken();
        Expression expression = tryParsePrimaryExpression();
        if (expression == null) throw new ParserException("PARSER: Could not parse Match case Expression at: "+currentToken);

        Expression tmp = new Expression(null, TypeSpecifier.UNKNOWN);
        tmp.setIdentifier("var");

        return new OperatorExpression(tmp, expression, op);
    }

    private void expect(TokenType expectedType, String where) {
        if (currentToken.getType() != expectedType) {
            throw new UnexpectedTokenException(new TokenType[]{expectedType}, where);
        }
    }
}
