package tkom.project;

import tkom.project.exceptions.*;
import tkom.project.nodes.*;
import tkom.project.nodes.expressions.Expression;
import tkom.project.nodes.expressions.OperatorExpression;
import tkom.project.nodes.statements.*;
import tkom.project.scope.Context;
import tkom.project.scope.Reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Interpreter {
    private final List<VariableDefinition> globalVariables;
    private final List<FunctionDefinition> functions;

    private Context currentFunctionContext;
    private final ArrayList<Context> previousFunctionContexts;

    private final Reader debug;

    public Interpreter(Program program) {
        this.previousFunctionContexts = new ArrayList<>();
        this.globalVariables = program.getVariables();
        this.functions = program.getFunctions();

        this.debug = null;
    }

    public Interpreter(Program program, Reader reader) {
        this.previousFunctionContexts = new ArrayList<>();
        this.globalVariables = program.getVariables();
        this.functions = program.getFunctions();

        this.debug = reader;
    }

    public String execute() {
        validate();
        FunctionDefinition main = findFunction("main");

        // Check if main structure is correct
        if (main.getType() != TypeSpecifier.VOID) throw new IncorrectReturnTypeException(main.getName(), main.getType(), TypeSpecifier.VOID);
        if (!main.getParameters().isEmpty()) throw new IncorrectParameterAmountException(main.getName(), main.getParameters().size(), 0);

        // Execute main function body
        List<Statement> statements = main.getStatements();
        currentFunctionContext = new Context();

        for (Statement statement : statements) {
            JumpStatement executionResult = executeStatement(statement);
            if (executionResult != null && executionResult.getType() != JumpType.RETURN) throw new InvalidJumpStatement("main");
        }

        return ("Program executed CORRECTLY!");
    }

    private JumpStatement executeStatement(Statement statement) {
        if (statement.getClass() == VariableDefinition.class) executeVariableDefinition((VariableDefinition) statement);
        if (statement.getClass() == FuncCallStatement.class || statement.getClass() == OperatorExpression.class) evaluateExpression((Expression) statement);
        if (statement.getClass() == IfElseStatement.class) return executeIfElseStatement((IfElseStatement) statement);
        if (statement.getClass() == WhileStatement.class) return executeWhileStatement((WhileStatement) statement);
        if (statement.getClass() == ExistStatement.class) return executeExistStatement((ExistStatement) statement);
        if (statement.getClass() == PatternMatchingStatement.class) return executePatternMatchingStatement((PatternMatchingStatement) statement);
        if (statement.getClass() == JumpStatement.class) return (JumpStatement) statement;
        return null;
    }

    // VARIABLE DEFINITION
    private void executeVariableDefinition(VariableDefinition varDef) {
        Expression resolved = evaluateExpression(varDef.getValue());

        // evaluate assigned value
        if ((resolved != null) && (resolved.getType() != varDef.getType())) {
            throw new MismatchedTypesException(varDef.getIdentifier(), varDef.getType(), resolved.getType());
        }
        // check if not already defined (or restricted)
        String identifier = varDef.getIdentifier();
        if ((findGlobalVar(identifier) != null) ||
                (currentFunctionContext.getVariable(identifier) != null) ||
                (Objects.equals(identifier, "var"))) {
            throw new VariableAlreadyDefinedException(varDef.getIdentifier());
        }

        currentFunctionContext.addVariable(
                new VariableDefinition(
                        varDef.getMutable(),
                        varDef.getOptional(),
                        varDef.getIdentifier(),
                        varDef.getType(),
                        resolved
                )
        );
    }

    // EVALUATING EXPRESSIONS
    private Expression evaluateExpression(Expression toResolve) {
        if (toResolve == null) return null;

        // Operator Expression
        if (toResolve.getClass() == OperatorExpression.class) {
            return tryEvaluateOperatorExpression(toResolve);
        }

        // Function Call
        if (toResolve.getClass() == FuncCallStatement.class) {
            return executeFuncCall((FuncCallStatement) toResolve);
        }

        // Variable Reference
        else if ((toResolve.getClass() == Expression.class) && (toResolve.getIdentifier() != null)) {
            return tryEvaluateVariableReference(toResolve);
        }
        // Literal
        return tryEvaluateLiteral(toResolve);
    }

    private Expression tryEvaluateOperatorExpression(Expression toResolve) {
        // get the operator of expression
        Operator op = ((OperatorExpression) toResolve).getOperator();

        // evaluate the right side of operation
        Expression rightResolved = evaluateExpression(((OperatorExpression) toResolve).getRightExpr());

        // if it's assignment operation no need to evaluate the left side, we just assign
        if (op == Operator.ASSIGN) {
            if (toResolve.getNotFlag() != null) throw new NegateAssignOperationException();

            Expression reAssignment = currentFunctionContext.setVariableValue(((OperatorExpression) toResolve).getLeftExpr().getIdentifier(), rightResolved);

            if (reAssignment == null) {
                return setGlobalVariableValue(((OperatorExpression) toResolve).getLeftExpr().getIdentifier(), rightResolved);
            } else return reAssignment;
        }

        // otherwise evaluate the left side of operation and evaluate the operation
        Expression leftResolved = evaluateExpression(((OperatorExpression) toResolve).getLeftExpr());

        switch (op) {
            case ADD -> {
                return tryAddExpression(leftResolved, rightResolved, toResolve.getNotFlag());
            }
            case SUB, MULTI, DIV, MODULO -> {
                return tryNumericExpression(op, leftResolved, rightResolved, toResolve.getNotFlag());
            }
            case LESS, LESS_EQ, MORE, MORE_EQ -> {
                return tryNumComparisonExpression(op, leftResolved, rightResolved, toResolve.getNotFlag());
            }
            case EQUAL, NOT_EQUAL -> {
                return tryComparisonExpression(op, leftResolved, rightResolved, toResolve.getNotFlag());
            }
            case OR, AND -> {
                return tryLogicalExpression(op, leftResolved, rightResolved, toResolve.getNotFlag());
            }
            default -> throw new ExpressionResolvingException();
        }
    }

    private Expression tryEvaluateVariableReference(Expression toResolve) {
        VariableDefinition variable;

        variable = currentFunctionContext.getVariable(toResolve.getIdentifier());
        if (variable == null) variable = findGlobalVar(toResolve.getIdentifier());
        if (variable == null) throw new VariableNotDeclaredException(toResolve.getIdentifier());

        Expression value = variable.getValue();

        if (value == null) {
            if (Boolean.TRUE.equals(variable.getOptional())) return null;
            else throw new NonOptionalVariableMissingValueException(variable.getIdentifier());
        }

        switch (value.getType()) {
            case INT -> {
                return retrieveIntValue(toResolve, value);
            }
            case FLOAT -> {
                return retrieveFloatValue(toResolve, value);
            }
            case STRING -> {
                return new Expression(null, value.getType(), (String) value.value().getValue());
            }
            case BOOL -> {
                return  retrieveBooleanValue(toResolve, value);
            }
            default -> {
                return null;
            }
        }
    }

    private Expression retrieveIntValue(Expression toResolve, Expression value) {
        if (toResolve.getNotFlag() == null) return new Expression(null, value.getType(), (Integer) value.value().getValue());
        else {
            if (toResolve.getNotFlag() != NotValue.ARITHMETIC) throw new ArithmeticNotException(toResolve.getType(), toResolve.getIdentifier());
            return new Expression(null, value.getType(), -(Integer) value.value().getValue());
        }
    }

    private Expression retrieveFloatValue(Expression toResolve, Expression value) {
        if (toResolve.getNotFlag() == null) return new Expression(null, value.getType(), (Float) value.value().getValue());
        else {
            if (toResolve.getNotFlag() != NotValue.ARITHMETIC) throw new ArithmeticNotException(toResolve.getType(), toResolve.getIdentifier());
            return new Expression(null, value.getType(), -(Float) value.value().getValue());
        }
    }

    private Expression retrieveBooleanValue(Expression toResolve, Expression value) {
        if (toResolve.getNotFlag() == null) return new Expression(null, value.getType(), (Boolean) value.value().getValue());
        else {
            if (toResolve.getNotFlag() != NotValue.LOGICAL) throw new LogicalNotException(toResolve.getType(), toResolve.getIdentifier());
            return new Expression(null, value.getType(), !(Boolean) value.value().getValue());
        }
    }

    private Expression tryEvaluateLiteral(Expression toResolve) {
        switch (toResolve.getType()) {
            case INT -> {
                if (toResolve.getNotFlag() == null) return new Expression(null, TypeSpecifier.INT, ((Integer) toResolve.value().getValue()));
                else return new Expression(null, TypeSpecifier.INT, -((Integer) toResolve.value().getValue()));
            }
            case FLOAT -> {
                if (toResolve.getNotFlag() == null) return new Expression(null, TypeSpecifier.FLOAT, ((Float) toResolve.value().getValue()));
                else return new Expression(null, TypeSpecifier.FLOAT, -((Float) toResolve.value().getValue()));
            }
            case STRING -> {
                return toResolve;
            }
            case BOOL -> {
                if (toResolve.getNotFlag() == null) return new Expression(null, TypeSpecifier.BOOL, ((Boolean) toResolve.value().getValue()));
                else return new Expression(null, TypeSpecifier.BOOL, !((Boolean) toResolve.value().getValue()));
            }
            default -> throw new TypeException(new TypeSpecifier[]{TypeSpecifier.INT, TypeSpecifier.FLOAT, TypeSpecifier.STRING, TypeSpecifier.BOOL}, toResolve.getType(), "expression");
        }
    }

    private Expression tryAddExpression(Expression left, Expression right, NotValue notFlag) {
        if (left.getType() != right.getType()) throw new MismatchedSidesOfOperationException("additive", left.getType(), right.getType());
        if (notFlag == NotValue.LOGICAL) throw new LogicalNotException(left.getType(), "additive-expression");
        else {
            switch (left.getType()) {
                case INT -> {
                    Integer result = (Integer) left.value().getValue() + (Integer) right.value().getValue();
                    return checkArithmeticNot(result, left.getType(), notFlag);
                }
                case FLOAT -> {
                    Float result = (Float) left.value().getValue() + (Float) right.value().getValue();
                    return checkArithmeticNot(result, left.getType(), notFlag);
                }
                case STRING -> {
                    if (notFlag != null) throw new ArithmeticNotException(TypeSpecifier.STRING, "additive-expression");
                    else return new Expression(null, left.getType(), (String) left.value().getValue() + right.value().getValue());
                }
                default -> throw new IncorrectTypeInOperationException("addition", left.getType());
            }
        }
    }

    private Expression tryNumericExpression(Operator op, Expression left, Expression right, NotValue notFlag) {
        String location = "sub/multi/div/mod";

        if (left.getType() != right.getType()) throw new MismatchedSidesOfOperationException(location, left.getType(), right.getType());
        if (notFlag == NotValue.LOGICAL) throw new LogicalNotException(left.getType(), location);
        else {
            switch (left.getType()) {
                case INT -> {
                    return integerNumericExpression(op, left, right, notFlag);
                }
                case FLOAT -> {
                    return floatNumericExpression(op, left, right, notFlag);
                }
                default -> throw new IncorrectTypeInOperationException(location, left.getType());
            }
        }
    }

    private Expression integerNumericExpression(Operator op, Expression left, Expression right, NotValue notFlag) {
        switch (op) {
            case SUB -> {
                Integer result = (Integer) left.value().getValue() - (Integer) right.value().getValue();
                return checkArithmeticNot(result, left.getType(), notFlag);
            }
            case MULTI -> {
                Integer result = (Integer) left.value().getValue() * (Integer) right.value().getValue();
                return checkArithmeticNot(result, left.getType(), notFlag);
            }
            case DIV -> {
                if ((Integer) right.value().getValue() == 0) throw new DivisionByZeroException();
                return checkArithmeticNot(((Integer) left.value().getValue() / (Integer) right.value().getValue()), TypeSpecifier.FLOAT, notFlag);
            }
            case MODULO -> {
                if ((Integer) right.value().getValue() == 0) throw new DivisionByZeroException();
                Integer result = (Integer) left.value().getValue() % (Integer) right.value().getValue();
                return checkArithmeticNot(result, left.getType(), notFlag);
            }
            default -> {
                return null;
            }
        }
    }

    private Expression floatNumericExpression(Operator op, Expression left, Expression right, NotValue notFlag) {
        switch (op) {
            case SUB -> {
                Float result = (Float) left.value().getValue() - (Float) right.value().getValue();
                return checkArithmeticNot(result, left.getType(), notFlag);
            }
            case MULTI -> {
                Float result = (Float) left.value().getValue() * (Float) right.value().getValue();
                return checkArithmeticNot(result, left.getType(), notFlag);
            }
            case DIV -> {
                if ((Float) right.value().getValue() == 0) throw new DivisionByZeroException();
                Float result = (Float) left.value().getValue() / (Float) right.value().getValue();
                return checkArithmeticNot(result, TypeSpecifier.FLOAT, notFlag);
            }
            case MODULO -> {
                if ((Float) right.value().getValue() == 0) throw new DivisionByZeroException();
                return checkArithmeticNot((Float) left.value().getValue() % (Float) right.value().getValue(), TypeSpecifier.INT, notFlag);
            }
            default -> {
                return null;
            }
        }
    }

    private Expression tryNumComparisonExpression(Operator op, Expression left, Expression right, NotValue notFlag) {
        String location = "comparison";

        if (left.getType() != right.getType()) throw new MismatchedSidesOfOperationException(location, left.getType(), right.getType());
        if (notFlag == NotValue.ARITHMETIC) throw new ArithmeticNotException(TypeSpecifier.BOOL, location);
        else {
            switch (left.getType()) {
                case INT -> {
                    switch (op) {
                        case LESS -> {
                            return checkLogicalNot((Integer) left.value().getValue() < (Integer) right.value().getValue(), notFlag);
                        }
                        case LESS_EQ -> {
                            return checkLogicalNot((Integer) left.value().getValue() <= (Integer) right.value().getValue(), notFlag);
                        }
                        case MORE -> {
                            return checkLogicalNot((Integer) left.value().getValue() > (Integer) right.value().getValue(), notFlag);
                        }
                        case MORE_EQ -> {
                            return checkLogicalNot((Integer) left.value().getValue() >= (Integer) right.value().getValue(), notFlag);
                        }
                        default -> {
                            return null;
                        }
                    }
                }
                case FLOAT -> {
                    switch (op) {
                        case LESS -> {
                            return checkLogicalNot((Float) left.value().getValue() < (Float) right.value().getValue(), notFlag);
                        }
                        case LESS_EQ -> {
                            return checkLogicalNot((Float) left.value().getValue() <= (Float) right.value().getValue(), notFlag);
                        }
                        case MORE -> {
                            return checkLogicalNot((Float) left.value().getValue() > (Float) right.value().getValue(), notFlag);
                        }
                        case MORE_EQ -> {
                            return checkLogicalNot((Float) left.value().getValue() >= (Float) right.value().getValue(), notFlag);
                        }
                        default -> {
                            return null;
                        }
                    }
                }
                default -> throw new IncorrectTypeInOperationException(location, left.getType());
            }
        }
    }

    private Expression tryComparisonExpression(Operator op, Expression left, Expression right, NotValue notFlag) {
        String location = "comparison";

        if (left.getType() != right.getType()) throw new MismatchedSidesOfOperationException(location, left.getType(), right.getType());
        if (notFlag == NotValue.ARITHMETIC) throw new ArithmeticNotException(TypeSpecifier.BOOL, location);
        else {
            if (left.getType() == TypeSpecifier.INT || left.getType() == TypeSpecifier.FLOAT || left.getType() == TypeSpecifier.STRING) {
                switch (op) {
                    case EQUAL -> {
                        return checkLogicalNot(Objects.equals(left.value().getValue(), right.value().getValue()), notFlag);
                    }
                    case NOT_EQUAL -> {
                        return checkLogicalNot(!Objects.equals(left.value().getValue(), right.value().getValue()), notFlag);
                    }
                    default -> {
                        return null;
                    }
                }
            } else throw new IncorrectTypeInOperationException(location, left.getType());
        }
    }

    private Expression tryLogicalExpression(Operator op, Expression left, Expression right, NotValue notFlag) {
        String location = "logical";

        if (left.getType() != right.getType()) throw new MismatchedSidesOfOperationException(location, left.getType(), right.getType());
        if (notFlag == NotValue.ARITHMETIC) throw new ArithmeticNotException(TypeSpecifier.BOOL, location);
        else {
            if (left.getType() == TypeSpecifier.BOOL) {
                switch (op) {
                    case AND -> {
                        return checkLogicalNot((Boolean) left.value().getValue() && (Boolean) right.value().getValue(), notFlag);
                    }
                    case OR -> {
                        return checkLogicalNot((Boolean) left.value().getValue() || (Boolean) right.value().getValue(), notFlag);
                    }
                    default -> {
                        return null;
                    }
                }
            } else throw new IncorrectTypeInOperationException(location, left.getType());
        }
    }

    private Expression checkArithmeticNot(Integer result, TypeSpecifier type, NotValue notFlag) {
        if (notFlag == NotValue.ARITHMETIC) return new Expression(null, type, -result);
        return new Expression(null, type, result);
    }

    private Expression checkArithmeticNot(Float result, TypeSpecifier type, NotValue notFlag) {
        if (notFlag == NotValue.ARITHMETIC) return new Expression(null, type, -result);
        return new Expression(null, type, result);
    }

    private Expression checkLogicalNot(Boolean result, NotValue notFlag) {
        if (notFlag == NotValue.LOGICAL) return new Expression(null, TypeSpecifier.BOOL, !result);
        return new Expression(null, TypeSpecifier.BOOL, result);
    }

    // EXECUTING FUNCTION CALLS
    private Expression executeFuncCall(FuncCallStatement toExecute) {
        String identifier = toExecute.getIdentifier();
        List<Expression> arguments = toExecute.getArguments();

        Expression predefined;
        if ((predefined = tryExecutePredefinedFunction(identifier, arguments)) != null) return predefined;

        FunctionDefinition currentFun = findFunction(identifier);

        // prepare parameters
        ArrayList<VariableDefinition> preparedParams = new ArrayList<>();

        List<Parameter> parameters = currentFun.getParameters();
        checkParameters(parameters, currentFun.getName());
        if (parameters.size() != arguments.size()) throw new IncorrectParameterAmountException(currentFun.getName(), arguments.size(), parameters.size());

        for (int i = 0; i < parameters.size(); i++) {
            // get currently resolved parameter
            Parameter currentParam = parameters.get(i);

            // resolve the given expression
            Expression resolvedExp = evaluateExpression(arguments.get(i));
            TypeSpecifier resolvedType;

            if (resolvedExp != null) {
                resolvedType = resolvedExp.getType();
                if (currentParam.getType() != resolvedType)
                    throw new MismatchedTypesException(currentParam.getIdentifier(), currentParam.getType(), resolvedExp.getType());
                if (findGlobalVar(currentParam.getIdentifier()) != null)
                    throw new VariableAlreadyDefinedException(currentParam.getIdentifier());
            } else {
                resolvedType = TypeSpecifier.UNKNOWN;
            }

            VariableDefinition addedParam = new VariableDefinition(
                    currentParam.isMutable(),
                    currentParam.isOptional(),
                    currentParam.getIdentifier(),
                    resolvedType,
                    resolvedExp
            );
            preparedParams.add(addedParam);
        }

        // stash previous context
        previousFunctionContexts.add(currentFunctionContext);

        // start execution of new function
        currentFunctionContext = new Context();
        for (VariableDefinition param : preparedParams) {
            currentFunctionContext.addVariable(param);
        }

        // execute the function statements
        return executeFunctionStatements(currentFun, toExecute.getNotFlag());
    }

    private void checkParameters(List<Parameter> parameters, String funName) {
        for (int i = 0; i < parameters.size()-1; i++) {
            for (int j = i+1; j < parameters.size()-1; j++) {
                if (Objects.equals(parameters.get(i).getIdentifier(), parameters.get(j).getIdentifier())) {
                    throw new ParameterAlreadyDeclaredException(parameters.get(i).getIdentifier(), funName);
                }
            }
        }
    }

    private Expression executeFunctionStatements(FunctionDefinition currentFun, NotValue notFlag) {
        for (Statement statement : currentFun.getStatements()) {
            JumpStatement executionResult = executeStatement(statement);

            if (executionResult != null) {
                if (executionResult.getType() != JumpType.RETURN) throw new InvalidJumpStatement(currentFun.getName());
                else {
                    Expression toReturn = evaluateReturnValue(executionResult.getRetExpr(), currentFun, notFlag);

                    currentFunctionContext = revertPreviousContext();
                    return toReturn;
                }
            }
        }
        currentFunctionContext = revertPreviousContext();
        return null;
    }

    private Expression evaluateReturnValue(Expression toReturn, FunctionDefinition currentFun, NotValue notFlag) {
        if (toReturn != null) {
            toReturn.setNotFlag(notFlag);
            toReturn = evaluateExpression(toReturn);
            if (toReturn.getType() != currentFun.getType())
                throw new IncorrectReturnTypeException(currentFun.getName(), toReturn.getType(), currentFun.getType());
        }
        return toReturn;
    }

    private Expression tryExecutePredefinedFunction(String identifier, List<Expression> arguments) {
        switch (identifier) {
            case "to_int" -> {
                return executeToInt(identifier, arguments);
            }
            case "to_float" -> {
                return executeToFloat(identifier, arguments);
            }
            case "to_string" -> {
                return executeToString(identifier, arguments);
            }
            case "to_bool" -> {
                return executeToBool(identifier, arguments);
            }
            case "print" -> {
                if (arguments.size() != 1) throw new IncorrectParameterAmountException(identifier, arguments.size(), 1);

                Expression toPrint = evaluateExpression(arguments.get(0));
                if (toPrint.getType() != TypeSpecifier.STRING) throw new MismatchedTypesException(identifier, TypeSpecifier.STRING, toPrint.getType());

                System.out.println(toPrint.value().getValue());
                if (debug != null) debug.write((String) toPrint.value().getValue());

                return new Expression(null, TypeSpecifier.STRING, (String) toPrint.value().getValue());
            }
            case "input" -> {
                if (!arguments.isEmpty()) throw new IncorrectParameterAmountException(identifier, arguments.size(), 0);
                Scanner scanner = new Scanner(System.in);

                return new Expression(null, TypeSpecifier.STRING, scanner.nextLine());
            }
            default -> {
                return null;
            }
        }
    }

    private Expression executeToInt(String identifier, List<Expression> arguments) {
        if (arguments.size() != 1) throw new IncorrectParameterAmountException(identifier, arguments.size(), 1);

        Expression evaluatedValue = evaluateExpression(arguments.get(0));
        switch (evaluatedValue.getType()) {
            case INT -> {
                return evaluatedValue;
            }
            case FLOAT -> {
                Float value = (Float) evaluatedValue.value().getValue();
                return new Expression(null, TypeSpecifier.INT, Math.round(value));
            }
            case BOOL -> {
                Boolean value = (Boolean) evaluatedValue.value().getValue();
                if (Boolean.TRUE.equals(value)) return new Expression(null, TypeSpecifier.INT, 1);
                else return new Expression(null, TypeSpecifier.INT, 0);
            }
            case STRING -> {
                String toParse = (String) evaluatedValue.value().getValue();
                int parsedInt;

                try {
                    parsedInt = Integer.parseInt(toParse);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException();
                }

                return new Expression(null, TypeSpecifier.INT, parsedInt);
            }
            default -> throw new TypeException(new TypeSpecifier[]{TypeSpecifier.INT, TypeSpecifier.FLOAT, TypeSpecifier.BOOL, TypeSpecifier.STRING}, evaluatedValue.getType(), identifier);
        }
    }

    private Expression executeToFloat(String identifier, List<Expression> arguments) {
        if (arguments.size() != 1) throw new IncorrectParameterAmountException(identifier, arguments.size(), 1);

        Expression evaluatedValue = evaluateExpression(arguments.get(0));
        switch (evaluatedValue.getType()) {
            case INT -> {
                Integer value = (Integer) evaluatedValue.value().getValue();
                return new Expression(null, TypeSpecifier.FLOAT, value.floatValue());
            }
            case FLOAT -> {
                return evaluatedValue;
            }
            default -> throw new TypeException(new TypeSpecifier[]{TypeSpecifier.INT, TypeSpecifier.FLOAT}, evaluatedValue.getType(), identifier);
        }
    }

    private Expression executeToString(String identifier, List<Expression> arguments) {
        if (arguments.size() != 1) throw new IncorrectParameterAmountException(identifier, arguments.size(), 1);

        Expression evaluatedValue = evaluateExpression(arguments.get(0));
        switch (evaluatedValue.getType()) {
            case INT -> {
                Integer value = (Integer) evaluatedValue.value().getValue();
                return new Expression(null, TypeSpecifier.STRING, value.toString());
            }
            case FLOAT -> {
                Float value = (Float) evaluatedValue.value().getValue();
                return new Expression(null, TypeSpecifier.STRING, value.toString());
            }
            case BOOL -> {
                Boolean value = (Boolean) evaluatedValue.value().getValue();
                return new Expression(null, TypeSpecifier.STRING, value.toString());
            }
            case STRING -> {
                return evaluatedValue;
            }
            default -> throw new TypeException(new TypeSpecifier[]{TypeSpecifier.INT, TypeSpecifier.FLOAT, TypeSpecifier.BOOL, TypeSpecifier.STRING}, evaluatedValue.getType(), identifier);
        }
    }

    private Expression executeToBool(String identifier, List<Expression> arguments) {
        if (arguments.size() != 1) throw new IncorrectParameterAmountException(identifier, arguments.size(), 1);

        Expression evaluatedValue = evaluateExpression(arguments.get(0));
        switch (evaluatedValue.getType()) {
            case INT -> {
                Integer value = (Integer) evaluatedValue.value().getValue();
                if (value == 0) return new Expression(null, TypeSpecifier.BOOL, false);
                else return new Expression(null, TypeSpecifier.BOOL, true);
            }
            case BOOL -> {
                return evaluatedValue;
            }
            case STRING -> {
                String value = (String) evaluatedValue.value().getValue();
                return new Expression(null, TypeSpecifier.BOOL, Boolean.valueOf(value));
            }
            default -> throw new TypeException(new TypeSpecifier[]{TypeSpecifier.INT, TypeSpecifier.BOOL, TypeSpecifier.STRING}, evaluatedValue.getType(), identifier);
        }
    }

    private JumpStatement executeIfElseStatement(IfElseStatement ifElseStatement) {
        currentFunctionContext = createInnerContext();

        // IF
        IfBlock ifBlock = ifElseStatement.getIfStatements();
        if (Boolean.TRUE.equals(testCondition(ifBlock.getCondition()))) return executeInstructionBlock(ifBlock.getStatements());

        // ELSE-IF
        for (IfBlock elifBlock : ifElseStatement.getElifStatements()) {
            if (Boolean.TRUE.equals(testCondition(elifBlock.getCondition()))) return executeInstructionBlock(elifBlock.getStatements());
        }

        // ELSE
        IfBlock elseBlock = ifElseStatement.getElseStatements();
        return executeInstructionBlock(elseBlock.getStatements());
    }

    private JumpStatement executeInstructionBlock(List<Statement> instructions) {
        for (Statement statement : instructions) {
            JumpStatement executionResult = executeStatement(statement);
            if (executionResult != null) {
                currentFunctionContext = revertFromInnerContext();
                return executionResult;
            }
        }
        currentFunctionContext = revertFromInnerContext();
        return null;
    }

    private JumpStatement executeWhileStatement(WhileStatement whileStatement) {
        currentFunctionContext = createInnerContext();

        while (Boolean.TRUE.equals(testCondition(whileStatement.getCondition()))) {
            for (Statement statement : whileStatement.getStatements()) {
                JumpStatement executionResult = executeStatement(statement);
                if (executionResult != null) {
                     if (executionResult.getType() == JumpType.RETURN) {
                        currentFunctionContext = revertFromInnerContext();
                        return executionResult;
                    } else if (executionResult.getType() == JumpType.BREAK) {
                        currentFunctionContext = revertFromInnerContext();
                        return null;
                    } else break;
                }
            }
        }
        currentFunctionContext = revertFromInnerContext();
        return null;
    }

    private Boolean testCondition(Expression condition) {
        Expression testedCondition = evaluateExpression(condition);

        if (testedCondition == null) throw new ExpressionResolvingException();
        if (testedCondition.getType() != TypeSpecifier.BOOL) throw new MismatchedTypesException("condition", TypeSpecifier.BOOL, testedCondition.getType());

        return (Boolean) testedCondition.value().getValue();
    }

    private JumpStatement executeExistStatement(ExistStatement statement) {
        currentFunctionContext = createInnerContext();

        if (evaluateExpression(statement.getExistValue()) != null) return executeInstructionBlock(statement.getExistStatements());
        else return executeInstructionBlock(statement.getElseStatements());
    }

    private JumpStatement executePatternMatchingStatement(PatternMatchingStatement statement) {
        currentFunctionContext = createInnerContext();

        Expression toMatch = evaluateExpression(statement.getToMatch());
        if (toMatch == null) throw new ExpressionResolvingException();

        currentFunctionContext.addVariable(new VariableDefinition(false, false, "var", toMatch.getType(), toMatch));

        for (MatchCaseStatement caseStatement : statement.getCases()) {
            if (Boolean.TRUE.equals(testCondition(caseStatement.getCondition()))) {
                currentFunctionContext = createInnerContext();
                JumpStatement returnable = executeInstructionBlock(caseStatement.getStatements());
                if (returnable != null) {
                    currentFunctionContext = revertFromInnerContext();
                    return returnable;
                }
            }
        }

        currentFunctionContext = revertFromInnerContext();
        return null;
    }

    private Context createInnerContext() {
        previousFunctionContexts.add(currentFunctionContext);
        return new Context(currentFunctionContext.getAllScopeVariables());
    }

    private Context revertFromInnerContext() {
        Context previousContext = previousFunctionContexts.get(previousFunctionContexts.size()-1);
        previousContext.updateVariables(currentFunctionContext.getAllScopeVariables());
        previousFunctionContexts.remove(previousFunctionContexts.size()-1);

        return previousContext;
    }

    private Context revertPreviousContext() {
        Context previousContext = previousFunctionContexts.get(previousFunctionContexts.size()-1);
        previousFunctionContexts.remove(previousFunctionContexts.size()-1);

        return previousContext;
    }

    private FunctionDefinition findFunction(String name) {
        for (FunctionDefinition tmp : functions) {
            if (Objects.equals(tmp.getName(), name)) return tmp;
        }
        throw new FunctionNotDeclaredException(name);
    }

    private VariableDefinition findGlobalVar(String identifier) {
        for (VariableDefinition tmp : globalVariables) {
            if (Objects.equals(tmp.getIdentifier(), identifier)) return tmp;
        }
        return null;
    }

    private Expression setGlobalVariableValue(String identifier, Expression newValue) {
        for (VariableDefinition globalVariable : globalVariables) {
            if (Objects.equals(globalVariable.getIdentifier(), identifier)) {
                globalVariable.setValue(newValue);
                return newValue;
            }
        }
        throw new VariableNotDeclaredException(identifier);
    }

    private void validate() {
        validateFunctions(functions);
        validateGlobalVariables(globalVariables);
    }

    private void validateGlobalVariables(List<VariableDefinition> variables) {
        for (int i = 0; i < variables.size(); i++) {
            for (int j = i+1; j < variables.size(); j++) {
                if (Objects.equals(variables.get(i).getIdentifier(), variables.get(j).getIdentifier())) {
                    throw new VariableAlreadyDefinedException(variables.get(i).getIdentifier());
                }
            }
        }
    }

    private void validateFunctions(List<FunctionDefinition> functions) {
        for (int i = 0; i < functions.size(); i++) {
            if (Boolean.TRUE.equals(restrictedName(functions.get(i).getName()))) {
                throw new FunctionAlreadyDefinedException(functions.get(i).getName());
            }
            for (int j = i+1; j < functions.size(); j++) {
                if (Objects.equals(functions.get(i).getName(), functions.get(j).getName())) {
                    throw new FunctionAlreadyDefinedException(functions.get(i).getName());
                }
            }
        }
    }

    private Boolean restrictedName(String name) {
        return (Objects.equals(name, "print")) ||
                (Objects.equals(name, "input")) ||
                (Objects.equals(name, "to_int")) ||
                (Objects.equals(name, "to_float")) ||
                (Objects.equals(name, "to_string")) ||
                (Objects.equals(name, "to_bool"));
    }
}
