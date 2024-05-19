package tkom.project;

import org.junit.Assert;
import org.junit.Test;
import tkom.project.exceptions.*;
import tkom.project.nodes.*;
import tkom.project.nodes.expressions.Expression;
import tkom.project.nodes.expressions.OperatorExpression;
import tkom.project.nodes.statements.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParserTest {
    private static Program setup(String code) throws IOException {
        // SETUP
        Lexer lexer = new Lexer(new StringReader(code));
        Parser parser = new Parser(lexer);
        return parser.parse();
        // SETUP
    }

    private Parser toParseSetup(String code) throws IOException {
        // SETUP
        Lexer lexer = new Lexer(new StringReader(code));
        return new Parser(lexer);
        // SETUP
    }

    private boolean compareExpression(Expression exp1, Expression exp2) {
        return Objects.equals(exp1.getIdentifier(), exp2.getIdentifier()) &&
                exp1.getType() == exp2.getType() &&
                exp1.getNotFlag() == exp2.getNotFlag() &&
                ((exp1.value() == null && exp1.value() == null) || (exp1.value().getValue() == exp2.value().getValue()));
    }



    // VARIABLE-DEFINITION

    @Test
    public void intVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("int intVariable;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("intVariable", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void intVariableDefinition() throws IOException {
        VariableDefinition variable = setup("int intVariable = 20;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("intVariable", variable.getIdentifier());
        Assert.assertEquals(20, variable.getValue().value().getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void negativeIntVariableDefinition() throws IOException {
        VariableDefinition variable = setup("int intVariable = -20;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("intVariable", variable.getIdentifier());
        Assert.assertEquals(20, variable.getValue().value().getValue());
        Assert.assertEquals(NotValue.ARITHMETIC, variable.getValue().getNotFlag());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void logicalNotIntVariableDefinition() throws IOException {
        Parser preparedParser = toParseSetup("int intVariable = !10;");
        Assert.assertThrows(LogicalNotException.class, preparedParser::parse);
    }

    @Test
    public void mutableVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("mut int test;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertTrue(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void mutableVariableDefinition() throws IOException {
        VariableDefinition variable = setup("mut int test = 123;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());
        Assert.assertEquals(123, variable.getValue().value().getValue());
        Assert.assertTrue(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void optionalVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("int? test;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertTrue(variable.getOptional());
    }

    @Test
    public void mutableOptionalVarDeclaration() throws IOException {
        VariableDefinition variable = setup("mut int? test;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertTrue(variable.getMutable());
        Assert.assertTrue(variable.getOptional());
    }

    @Test
    public void optionalVariableDefinition() throws IOException {
        VariableDefinition variable = setup("int? test = 123;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());
        Assert.assertEquals(123, variable.getValue().value().getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertTrue(variable.getOptional());
    }

    @Test
    public void mutableAndOptionalVariableDefinition() throws IOException {
        VariableDefinition variable = setup("mut int? test = 123;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());
        Assert.assertEquals(123, variable.getValue().value().getValue());
        Assert.assertTrue(variable.getMutable());
        Assert.assertTrue(variable.getOptional());
    }

    @Test
    public void reassignmentVariableDefinition() throws IOException {
        VariableDefinition variable = setup("int test = someValue;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());
        Assert.assertEquals("someValue", variable.getValue().getIdentifier());
    }

    @Test
    public void reassignmentTwiceVariableDefinition() throws IOException {
        VariableDefinition variable = setup("int test = someValue = anotherValue;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());

        Assert.assertTrue(variable.getValue() instanceof OperatorExpression);

        Assert.assertEquals(Operator.ASSIGN, ((OperatorExpression) variable.getValue()).getOperator());
        Assert.assertEquals("someValue", ((OperatorExpression) variable.getValue()).getLeftExpr().getIdentifier());
        Assert.assertEquals("anotherValue", ((OperatorExpression) variable.getValue()).getRightExpr().getIdentifier());
    }

    @Test
    public void funcCallVariableDefinition() throws IOException {
        VariableDefinition variable = setup("int test = getSomeValue();").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());
        Assert.assertTrue(variable.getValue() instanceof FuncCallStatement);
        Assert.assertEquals("getSomeValue", variable.getValue().getIdentifier());
    }

    @Test
    public void funcCallWithParamsVariableDefinition() throws IOException {
        VariableDefinition variable = setup("int test = getSomeValue(1, a);").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());
        Assert.assertTrue(variable.getValue() instanceof FuncCallStatement);
        Assert.assertEquals("getSomeValue", variable.getValue().getIdentifier());

        ArrayList<Expression> testedParams = new ArrayList<>();
        testedParams.add(new Expression(null, TypeSpecifier.INT, 1));
        Expression secondParam = new Expression(null, TypeSpecifier.UNKNOWN);
        secondParam.setIdentifier("a");
        testedParams.add(secondParam);

        Assert.assertTrue(compareExpression(testedParams.get(0), ((FuncCallStatement) variable.getValue()).getArguments().get(0)));
        Assert.assertTrue(compareExpression(testedParams.get(1), ((FuncCallStatement) variable.getValue()).getArguments().get(1)));
    }

    @Test
    public void missingSemicolonException() throws IOException {
        Parser preparedParser = toParseSetup("int test = getSomeValue(1, a)");
        Assert.assertThrows(UnexpectedTokenException.class, preparedParser::parse);
    }

    @Test
    public void missingVariableTypeException() throws IOException {
        Parser preparedParser = toParseSetup("test = getSomeValue(1, a);");
        Assert.assertThrows(ParserException.class, preparedParser::parse);
    }

    @Test
    public void missingVariableNameException() throws IOException {
        Parser preparedParser = toParseSetup("int = getSomeValue(1, a);");
        Assert.assertThrows(UnexpectedTokenException.class, preparedParser::parse);
    }

    @Test
    public void missingAssignmentOperatorVariableDefinitionException() throws IOException {
        Parser preparedParser = toParseSetup("int test getSomeValue(1, a);");
        Assert.assertThrows(UnexpectedTokenException.class, preparedParser::parse);
    }

    @Test
    public void missingValueAfterAssignmentOperator() throws IOException {
        Parser preparedParser = toParseSetup("int test =");
        Assert.assertThrows(ParserException.class, preparedParser::parse);
    }

    @Test
    public void multipleParenthesesVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("int test = 1+(2+(3+4));").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());

        Assert.assertTrue(variable.getValue() instanceof OperatorExpression);
        Assert.assertEquals(Operator.ADD, ((OperatorExpression) variable.getValue()).getOperator());

        Expression leftExpression = ((OperatorExpression) variable.getValue()).getLeftExpr();
        Assert.assertEquals(TypeSpecifier.INT, leftExpression.getType());
        Assert.assertEquals(1, leftExpression.value().getValue());

        OperatorExpression rightExpression = (OperatorExpression) ((OperatorExpression) variable.getValue()).getRightExpr();

        Expression leftInnerExpression = rightExpression.getLeftExpr();
        Assert.assertEquals(TypeSpecifier.INT, leftInnerExpression.getType());
        Assert.assertEquals(2, leftInnerExpression.value().getValue());

        OperatorExpression rightInnerExpression = (OperatorExpression) rightExpression.getRightExpr();

        Expression leftInner2Expression = rightInnerExpression.getLeftExpr();
        Assert.assertEquals(TypeSpecifier.INT, leftInner2Expression.getType());
        Assert.assertEquals(3, leftInner2Expression.value().getValue());
        Expression rightInner2Expression = rightInnerExpression.getRightExpr();
        Assert.assertEquals(TypeSpecifier.INT, rightInner2Expression.getType());
        Assert.assertEquals(4, rightInner2Expression.value().getValue());
    }

    @Test
    public void additionVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("int test = 5+6+7+8;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());

        Assert.assertTrue(variable.getValue() instanceof OperatorExpression);
        Assert.assertEquals(Operator.ADD, ((OperatorExpression) variable.getValue()).getOperator());

        Expression leftExpression = ((OperatorExpression) variable.getValue()).getLeftExpr();
        Assert.assertEquals(TypeSpecifier.INT, leftExpression.getType());
        Assert.assertEquals(5, leftExpression.value().getValue());

        OperatorExpression rightExpression = (OperatorExpression) ((OperatorExpression) variable.getValue()).getRightExpr();

        Expression leftInnerExpression = rightExpression.getLeftExpr();
        Assert.assertEquals(TypeSpecifier.INT, leftInnerExpression.getType());
        Assert.assertEquals(6, leftInnerExpression.value().getValue());

        OperatorExpression rightInnerExpression = (OperatorExpression) rightExpression.getRightExpr();

        Expression leftInner2Expression = rightInnerExpression.getLeftExpr();
        Assert.assertEquals(TypeSpecifier.INT, leftInner2Expression.getType());
        Assert.assertEquals(7, leftInner2Expression.value().getValue());
        Expression rightInner2Expression = rightInnerExpression.getRightExpr();
        Assert.assertEquals(TypeSpecifier.INT, rightInner2Expression.getType());
        Assert.assertEquals(8, rightInner2Expression.value().getValue());
    }

    @Test
    public void complexVariableDefinition() throws IOException {
        VariableDefinition variable = setup("int test = (7 + getSomeValue() * (-2));").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.INT, variable.getType());
        Assert.assertEquals("test", variable.getIdentifier());

        Assert.assertEquals(Operator.ADD, ((OperatorExpression) variable.getValue()).getOperator());
        Expression leftExpression = ((OperatorExpression) variable.getValue()).getLeftExpr();
        Assert.assertEquals(7, leftExpression.value().getValue());

        OperatorExpression rightExpression = (OperatorExpression) ((OperatorExpression) variable.getValue()).getRightExpr();

        Expression leftInnerExpression = rightExpression.getLeftExpr();
        Assert.assertTrue(leftInnerExpression instanceof FuncCallStatement);
        Assert.assertEquals("getSomeValue", leftInnerExpression.getIdentifier());

        Assert.assertEquals(Operator.MULTI, rightExpression.getOperator());

        Expression rightInnerExpression = rightExpression.getRightExpr();
        Assert.assertEquals(NotValue.ARITHMETIC, rightInnerExpression.getNotFlag());
        Assert.assertEquals(2, rightInnerExpression.value().getValue());
    }

    @Test
    public void floatVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("float fltVariable;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltVariable", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void mutableFloatVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("mut float fltVariable;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltVariable", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertTrue(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void optionalFloatVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("float? fltVariable;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltVariable", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertTrue(variable.getOptional());
    }

    @Test
    public void mutableOptionalFloatVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("mut float? fltVariable;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltVariable", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertTrue(variable.getMutable());
        Assert.assertTrue(variable.getOptional());
    }

    @Test
    public void floatVariableDefinition() throws IOException {
        VariableDefinition variable = setup("float fltVariable = 5.123;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltVariable", variable.getIdentifier());
        Assert.assertEquals(5.123f, variable.getValue().value().getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void mutableFloatVariableDefinition() throws IOException {
        VariableDefinition variable = setup("mut float fltVariable = 1.111;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltVariable", variable.getIdentifier());
        Assert.assertEquals(1.111f, (float)variable.getValue().value().getValue(), 0.001f);
        Assert.assertTrue(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void optionalFloatVariableDefinition() throws IOException {
        VariableDefinition variable = setup("float? fltVariable = 2.222;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltVariable", variable.getIdentifier());
        Assert.assertEquals(2.222f, (float)variable.getValue().value().getValue(), 0.001f);
        Assert.assertFalse(variable.getMutable());
        Assert.assertTrue(variable.getOptional());
    }

    @Test
    public void mutableOptionalFloatVariableDefinition() throws IOException {
        VariableDefinition variable = setup("mut float? fltVariable = 3.333;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltVariable", variable.getIdentifier());
        Assert.assertEquals(3.333f, (float)variable.getValue().value().getValue(), 0.001f);
        Assert.assertTrue(variable.getMutable());
        Assert.assertTrue(variable.getOptional());
    }

    @Test
    public void negativeFloatVariableDefinition() throws IOException {
        VariableDefinition variable = setup("float fltVariable = -06.789;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltVariable", variable.getIdentifier());
        Assert.assertEquals(6.789f, variable.getValue().value().getValue());
        Assert.assertEquals(NotValue.ARITHMETIC, variable.getValue().getNotFlag());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void logicalNotFloatVariableDefinition() throws IOException {
        Parser preparedParser = toParseSetup("float fltVariable = !11.11;");
        Assert.assertThrows(LogicalNotException.class, preparedParser::parse);
    }

    @Test
    public void reassignmentFloatVariableDefinition() throws IOException {
        VariableDefinition variable = setup("float fltValue = someOtherValue;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltValue", variable.getIdentifier());
        Assert.assertEquals("someOtherValue", variable.getValue().getIdentifier());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void parenthesesFloatVariableDefinition() throws IOException {
        VariableDefinition variable = setup("float fltValue = (((expression)));").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltValue", variable.getIdentifier());
        Assert.assertEquals("expression", variable.getValue().getIdentifier());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void funcCallFloatVariableDefinition() throws IOException {
        VariableDefinition variable = setup("float fltValue = getSomeValue();").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltValue", variable.getIdentifier());
        Assert.assertTrue(variable.getValue() instanceof FuncCallStatement);
        Assert.assertEquals("getSomeValue", variable.getValue().getIdentifier());
        Assert.assertEquals(0, ((FuncCallStatement) variable.getValue()).getArguments().size());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void stringVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("string strVariable;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.STRING, variable.getType());
        Assert.assertEquals("strVariable", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void mutableStringVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("mut string strVariable;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.STRING, variable.getType());
        Assert.assertEquals("strVariable", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertTrue(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void optionalStringVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("string? strVariable;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.STRING, variable.getType());
        Assert.assertEquals("strVariable", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertTrue(variable.getOptional());
    }

    @Test
    public void stringVariableDefinition() throws IOException {
        VariableDefinition variable = setup("string strVariable = \"Test\";").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.STRING, variable.getType());
        Assert.assertEquals("strVariable", variable.getIdentifier());
        Assert.assertEquals("Test", variable.getValue().value().getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void logicalNotBeforeStringException() throws IOException {
        Parser preparedParser = toParseSetup("string strVariable = !\"Test\";");
        Assert.assertThrows(ValueException.class, preparedParser::parse);
    }

    @Test
    public void arithmeticNotBeforeStringException() throws IOException {
        Parser preparedParser = toParseSetup("string strVariable = -\"Test\";");
        Assert.assertThrows(ValueException.class, preparedParser::parse);
    }

    @Test
    public void reassignmentStringVariableDefinition() throws IOException {
        VariableDefinition variable = setup("float fltValue = someExpression;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.FLOAT, variable.getType());
        Assert.assertEquals("fltValue", variable.getIdentifier());
        Assert.assertEquals("someExpression", variable.getValue().getIdentifier());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void boolVariableDeclaration() throws IOException {
        VariableDefinition variable = setup("bool boolVariable;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.BOOL, variable.getType());
        Assert.assertEquals("boolVariable", variable.getIdentifier());
        Assert.assertNull(variable.getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void bool1VariableDefinition() throws IOException {
        VariableDefinition variable = setup("bool boolVariable = true;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.BOOL, variable.getType());
        Assert.assertEquals("boolVariable", variable.getIdentifier());
        Assert.assertEquals(true, variable.getValue().value().getValue());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void bool2VariableDefinition() throws IOException {
        VariableDefinition variable = setup("bool boolVariable = !false;").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.BOOL, variable.getType());
        Assert.assertEquals("boolVariable", variable.getIdentifier());
        Assert.assertEquals(false, variable.getValue().value().getValue());
        Assert.assertEquals(NotValue.LOGICAL, variable.getValue().getNotFlag());
        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    @Test
    public void boolArithmeticNotException() throws IOException {
        Parser preparedParser = toParseSetup("bool boolVariable = -false;");
        Assert.assertThrows(ArithmeticNotException.class, preparedParser::parse);
    }

    @Test
    public void expressionBoolVariableDefinition() throws IOException {
        VariableDefinition variable = setup("bool boolVariable = (tak || nie);").getVariables().get(0);

        Assert.assertEquals(TypeSpecifier.BOOL, variable.getType());
        Assert.assertEquals("boolVariable", variable.getIdentifier());

        Assert.assertTrue(variable.getValue() instanceof OperatorExpression);

        Assert.assertEquals(Operator.OR, ((OperatorExpression) variable.getValue()).getOperator());
        Assert.assertEquals("tak", ((OperatorExpression) variable.getValue()).getLeftExpr().getIdentifier());
        Assert.assertEquals("nie", ((OperatorExpression) variable.getValue()).getRightExpr().getIdentifier());

        Assert.assertFalse(variable.getMutable());
        Assert.assertFalse(variable.getOptional());
    }

    // FUNCTION-DEFINITION
    @Test
    public void emptyFunctionBodyException() throws IOException {
        Parser preparedParser = toParseSetup("def void test() {}");
        Assert.assertThrows(StatementParsingException.class, preparedParser::parse);
    }

    @Test
    public void voidFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def void test() {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.VOID, function.getType());
        Assert.assertEquals(0, function.getParameters().size());
        Assert.assertEquals(1, function.getStatements().size());
    }

    @Test
    public void intFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def int test() {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.INT, function.getType());
        Assert.assertEquals(0, function.getParameters().size());
        Assert.assertEquals(1, function.getStatements().size());
    }

    @Test
    public void floatFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def float test() {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.FLOAT, function.getType());
        Assert.assertEquals(0, function.getParameters().size());
        Assert.assertEquals(1, function.getStatements().size());
    }

    @Test
    public void stringFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def string test() {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.STRING, function.getType());
        Assert.assertEquals(0, function.getParameters().size());
        Assert.assertEquals(1, function.getStatements().size());
    }

    @Test
    public void booleanFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def bool test() {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.BOOL, function.getType());
        Assert.assertEquals(0, function.getParameters().size());
        Assert.assertEquals(1, function.getStatements().size());
    }

    @Test
    public void oneIntParameterFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def void test(int param) {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.VOID, function.getType());
        Assert.assertEquals(1, function.getStatements().size());
        Assert.assertEquals(1, function.getParameters().size());

        Parameter parameter = function.getParameters().get(0);
        Assert.assertEquals(TypeSpecifier.INT, parameter.getType());
        Assert.assertEquals("param", parameter.getIdentifier());
        Assert.assertFalse(parameter.isMutable());
        Assert.assertFalse(parameter.isOptional());
    }

    @Test
    public void mutableIntParameterFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def void test(mut int param) {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.VOID, function.getType());
        Assert.assertEquals(1, function.getStatements().size());
        Assert.assertEquals(1, function.getParameters().size());

        Parameter parameter = function.getParameters().get(0);
        Assert.assertEquals(TypeSpecifier.INT, parameter.getType());
        Assert.assertEquals("param", parameter.getIdentifier());
        Assert.assertTrue(parameter.isMutable());
        Assert.assertFalse(parameter.isOptional());
    }

    @Test
    public void optionalIntParameterFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def void test(int? param) {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.VOID, function.getType());
        Assert.assertEquals(1, function.getStatements().size());
        Assert.assertEquals(1, function.getParameters().size());

        Parameter parameter = function.getParameters().get(0);
        Assert.assertEquals(TypeSpecifier.INT, parameter.getType());
        Assert.assertEquals("param", parameter.getIdentifier());
        Assert.assertFalse(parameter.isMutable());
        Assert.assertTrue(parameter.isOptional());
    }

    @Test
    public void mutableOptionalIntParameterFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def void test(mut int? param) {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.VOID, function.getType());
        Assert.assertEquals(1, function.getStatements().size());
        Assert.assertEquals(1, function.getParameters().size());

        Parameter parameter = function.getParameters().get(0);
        Assert.assertEquals(TypeSpecifier.INT, parameter.getType());
        Assert.assertEquals("param", parameter.getIdentifier());
        Assert.assertTrue(parameter.isMutable());
        Assert.assertTrue(parameter.isOptional());
    }

    @Test
    public void oneFloatParameterFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def void test(float param) {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.VOID, function.getType());
        Assert.assertEquals(1, function.getStatements().size());
        Assert.assertEquals(1, function.getParameters().size());

        Parameter parameter = function.getParameters().get(0);
        Assert.assertEquals(TypeSpecifier.FLOAT, parameter.getType());
        Assert.assertEquals("param", parameter.getIdentifier());
        Assert.assertFalse(parameter.isMutable());
        Assert.assertFalse(parameter.isOptional());
    }

    @Test
    public void mutableFloatParameterFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def void test(mut float param) {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.VOID, function.getType());
        Assert.assertEquals(1, function.getStatements().size());
        Assert.assertEquals(1, function.getParameters().size());

        Parameter parameter = function.getParameters().get(0);
        Assert.assertEquals(TypeSpecifier.FLOAT, parameter.getType());
        Assert.assertEquals("param", parameter.getIdentifier());
        Assert.assertTrue(parameter.isMutable());
        Assert.assertFalse(parameter.isOptional());
    }

    @Test
    public void optionalFloatParameterFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def void test(float? param) {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.VOID, function.getType());
        Assert.assertEquals(1, function.getStatements().size());
        Assert.assertEquals(1, function.getParameters().size());

        Parameter parameter = function.getParameters().get(0);
        Assert.assertEquals(TypeSpecifier.FLOAT, parameter.getType());
        Assert.assertEquals("param", parameter.getIdentifier());
        Assert.assertFalse(parameter.isMutable());
        Assert.assertTrue(parameter.isOptional());
    }

    @Test
    public void stringParameterFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def void test(string param) {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.VOID, function.getType());
        Assert.assertEquals(1, function.getStatements().size());
        Assert.assertEquals(1, function.getParameters().size());

        Parameter parameter = function.getParameters().get(0);
        Assert.assertEquals(TypeSpecifier.STRING, parameter.getType());
        Assert.assertEquals("param", parameter.getIdentifier());
        Assert.assertFalse(parameter.isMutable());
        Assert.assertFalse(parameter.isOptional());
    }

    @Test
    public void booleanParameterFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def void test(bool param) {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.VOID, function.getType());
        Assert.assertEquals(1, function.getStatements().size());
        Assert.assertEquals(1, function.getParameters().size());

        Parameter parameter = function.getParameters().get(0);
        Assert.assertEquals(TypeSpecifier.BOOL, parameter.getType());
        Assert.assertEquals("param", parameter.getIdentifier());
        Assert.assertFalse(parameter.isMutable());
        Assert.assertFalse(parameter.isOptional());
    }

    @Test
    public void multipleParametersFunctionDefinition() throws IOException {
        FunctionDefinition function = setup("def void test(bool param1, mut int param2, string? param3) {return;}").getFunctions().get(0);

        Assert.assertEquals("test", function.getName());
        Assert.assertEquals(TypeSpecifier.VOID, function.getType());
        Assert.assertEquals(1, function.getStatements().size());
        Assert.assertEquals(3, function.getParameters().size());

        Parameter parameter = function.getParameters().get(0);
        Assert.assertEquals(TypeSpecifier.BOOL, parameter.getType());
        Assert.assertEquals("param1", parameter.getIdentifier());
        Assert.assertFalse(parameter.isMutable());
        Assert.assertFalse(parameter.isOptional());

        Parameter parameter2 = function.getParameters().get(1);
        Assert.assertEquals(TypeSpecifier.INT, parameter2.getType());
        Assert.assertEquals("param2", parameter2.getIdentifier());
        Assert.assertTrue(parameter2.isMutable());
        Assert.assertFalse(parameter2.isOptional());

        Parameter parameter3 = function.getParameters().get(2);
        Assert.assertEquals(TypeSpecifier.STRING, parameter3.getType());
        Assert.assertEquals("param3", parameter3.getIdentifier());
        Assert.assertFalse(parameter3.isMutable());
        Assert.assertTrue(parameter3.isOptional());
    }

    @Test
    public void missingLeftParenthesisOnParams() throws IOException {
        Parser preparedParser = toParseSetup("def void test int param) {return;}");
        Assert.assertThrows(UnexpectedTokenException.class, preparedParser::parse);
    }

    @Test
    public void missingRightParenthesisOnParams() throws IOException {
        Parser preparedParser = toParseSetup("def void test (int param {return;}");
        Assert.assertThrows(UnexpectedTokenException.class, preparedParser::parse);
    }

    @Test
    public void missingLeftBracket() throws IOException {
        Parser preparedParser = toParseSetup("def void test (int param) return;}");
        Assert.assertThrows(UnexpectedTokenException.class, preparedParser::parse);
    }

    @Test
    public void missingRightBracket() throws IOException {
        Parser preparedParser = toParseSetup("def void test (int param) {return;");
        Assert.assertThrows(UnexpectedTokenException.class, preparedParser::parse);
    }

    @Test
    public void missingDefKeyWord() throws IOException {
        Parser preparedParser = toParseSetup("void test (int param) {return;}");
        Assert.assertThrows(ParserException.class, preparedParser::parse);
    }

    @Test
    public void missingTypeSpecifier() throws IOException {
        Parser preparedParser = toParseSetup("def test (int param) return;}");
        Assert.assertThrows(ParserException.class, preparedParser::parse);
    }

    @Test
    public void missingFunctionNameSpecifier() throws IOException {
        Parser preparedParser = toParseSetup("def void (int param) return;}");
        Assert.assertThrows(UnexpectedTokenException.class, preparedParser::parse);
    }

    // JUMP
    @Test
    public void returnEmptyJumpStatement() throws IOException {
        Parser parser= toParseSetup("return");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof JumpStatement);
        Assert.assertEquals(JumpType.RETURN, ((JumpStatement) statement).getType());
        Assert.assertNull(((JumpStatement) statement).getRetExpr());
    }

    @Test
    public void returnIntegerLiteralJumpStatement() throws IOException {
        Parser parser= toParseSetup("return 1");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof JumpStatement);
        Assert.assertEquals(JumpType.RETURN, ((JumpStatement) statement).getType());

        Expression returnValue = ((JumpStatement) statement).getRetExpr();
        Assert.assertEquals(TypeSpecifier.INT, returnValue.getType());
        Assert.assertEquals(1, returnValue.value().getValue());
    }

    @Test
    public void returnStringLiteralJumpStatement() throws IOException {
        Parser parser= toParseSetup("return \"TEST\"");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof JumpStatement);
        Assert.assertEquals(JumpType.RETURN, ((JumpStatement) statement).getType());

        Expression returnValue = ((JumpStatement) statement).getRetExpr();
        Assert.assertEquals(TypeSpecifier.STRING, returnValue.getType());
        Assert.assertEquals("TEST", returnValue.value().getValue());
    }

    @Test
    public void returnBooleanLiteralJumpStatement() throws IOException {
        Parser parser= toParseSetup("return true");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof JumpStatement);
        Assert.assertEquals(JumpType.RETURN, ((JumpStatement) statement).getType());

        Expression returnValue = ((JumpStatement) statement).getRetExpr();
        Assert.assertEquals(TypeSpecifier.BOOL, returnValue.getType());
        Assert.assertEquals(true, returnValue.value().getValue());
    }

    @Test
    public void returnExpressionJumpStatement() throws IOException {
        Parser parser= toParseSetup("return something%3");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof JumpStatement);
        Assert.assertEquals(JumpType.RETURN, ((JumpStatement) statement).getType());

        OperatorExpression returnValue = (OperatorExpression) ((JumpStatement) statement).getRetExpr();
        Assert.assertEquals(Operator.MODULO, returnValue.getOperator());
        Assert.assertEquals("something", returnValue.getLeftExpr().getIdentifier());
        Assert.assertEquals(3, returnValue.getRightExpr().value().getValue());
    }

    @Test
    public void returnFuncCallJumpStatement() throws IOException {
        Parser parser= toParseSetup("return call()");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof JumpStatement);
        Assert.assertEquals(JumpType.RETURN, ((JumpStatement) statement).getType());

        FuncCallStatement returnValue = (FuncCallStatement) ((JumpStatement) statement).getRetExpr();
        Assert.assertEquals("call", returnValue.getIdentifier());
        Assert.assertEquals(0, returnValue.getArguments().size());
    }

    @Test
    public void continueJumpStatement() throws IOException {
        Parser parser= toParseSetup("continue");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof JumpStatement);
        Assert.assertEquals(JumpType.CONTINUE, ((JumpStatement) statement).getType());
    }

    @Test
    public void breakJumpStatement() throws IOException {
        Parser parser= toParseSetup("break");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof JumpStatement);
        Assert.assertEquals(JumpType.BREAK, ((JumpStatement) statement).getType());
    }

    // WHILE
    @Test
    public void parseWhileStatement() throws IOException {
        Parser parser= toParseSetup("while(exp) {a = a+1; return;}");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof WhileStatement);

        Expression cond = ((WhileStatement) statement).getCondition();
        Assert.assertEquals("exp", cond.getIdentifier());

        List<Statement> statements = ((WhileStatement) statement).getStatements();
        Assert.assertEquals(2, statements.size());
    }

    @Test
    public void whileStatementNoCondition() throws IOException {
        Parser preparedParser = toParseSetup("while() {a = a+1;}");
        Assert.assertThrows(StatementParsingException.class, preparedParser::testParseStatements);
    }

    @Test
    public void whileMissingLeftParentheses() throws IOException {
        Parser preparedParser = toParseSetup("while exp) {a = a+1;}");
        Assert.assertThrows(UnexpectedTokenException.class, preparedParser::testParseStatements);
    }

    @Test
    public void whileMissingRightParentheses() throws IOException {
        Parser preparedParser = toParseSetup("while (exp {a = a+1;}");
        Assert.assertThrows(UnexpectedTokenException.class, preparedParser::testParseStatements);
    }

    @Test
    public void whileStatementNoStatements() throws IOException {
        Parser preparedParser = toParseSetup("while(exp) {}");
        Assert.assertThrows(StatementParsingException.class, preparedParser::testParseStatements);
    }

    // EXIST
    @Test
    public void existStatement() throws IOException {
        Parser parser= toParseSetup("exist(exp) {return true;} else {return false;}");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof ExistStatement);

        Expression testedVal = ((ExistStatement) statement).getExistValue();
        Assert.assertEquals("exp", testedVal.getIdentifier());

        List<Statement> existStatements = ((ExistStatement) statement).getExistStatements();
        Assert.assertEquals(1, existStatements.size());

        List<Statement> elseStatements = ((ExistStatement) statement).getElseStatements();
        Assert.assertEquals(1, elseStatements.size());
    }

    @Test
    public void existStatementMissingElseBlock() throws IOException {
        Parser preparedParser = toParseSetup("exist(exp) {return true;}");
        Assert.assertThrows(MissingElseBlockInExistStatementException.class, preparedParser::testParseStatements);
    }

    // IF
    @Test
    public void ifStatement() throws IOException {
        Parser parser= toParseSetup("if (exp) { return 1; }");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof IfElseStatement);

        IfBlock ifPart = ((IfElseStatement) statement).getIfStatements();
        Assert.assertEquals("exp", ifPart.getCondition().getIdentifier());
        Assert.assertEquals(1, ifPart.getStatements().size());
    }

    @Test
    public void ifStatementWithElse() throws IOException {
        Parser parser= toParseSetup("if (exp) { return 1; } else { return 2; }");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof IfElseStatement);

        IfBlock ifPart = ((IfElseStatement) statement).getIfStatements();
        Assert.assertEquals("exp", ifPart.getCondition().getIdentifier());
        Assert.assertEquals(1, ifPart.getStatements().size());

        IfBlock elsePart = ((IfElseStatement) statement).getElseStatements();
        Assert.assertNull(elsePart.getCondition());
        Assert.assertEquals(1, elsePart.getStatements().size());
    }

    @Test
    public void ifStatementWithElif() throws IOException {
        Parser parser= toParseSetup("if (exp) { return 1; } elif (no) { return 2; }");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof IfElseStatement);

        IfBlock ifPart = ((IfElseStatement) statement).getIfStatements();
        Assert.assertEquals("exp", ifPart.getCondition().getIdentifier());
        Assert.assertEquals(1, ifPart.getStatements().size());

        IfBlock elifPart = ((IfElseStatement) statement).getElifStatements().get(0);
        Assert.assertEquals("no", elifPart.getCondition().getIdentifier());
        Assert.assertEquals(1, elifPart.getStatements().size());

        IfBlock elsePart = ((IfElseStatement) statement).getElseStatements();
        Assert.assertNull(elsePart);
    }

    @Test
    public void ifStatementWith2ElifAndElse() throws IOException {
        Parser parser= toParseSetup("if (yes) { return 1; } elif (maybe) { return 2; } elif (no) { return 3; } else { return 0; }");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof IfElseStatement);

        IfBlock ifPart = ((IfElseStatement) statement).getIfStatements();
        Assert.assertEquals("yes", ifPart.getCondition().getIdentifier());
        Assert.assertEquals(1, ifPart.getStatements().size());

        IfBlock elifPart1 = ((IfElseStatement) statement).getElifStatements().get(0);
        Assert.assertEquals("maybe", elifPart1.getCondition().getIdentifier());
        Assert.assertEquals(1, elifPart1.getStatements().size());

        IfBlock elifPart2 = ((IfElseStatement) statement).getElifStatements().get(1);
        Assert.assertEquals("no", elifPart2.getCondition().getIdentifier());
        Assert.assertEquals(1, elifPart2.getStatements().size());

        IfBlock elsePart = ((IfElseStatement) statement).getElseStatements();
        Assert.assertNull(elsePart.getCondition());
        Assert.assertEquals(1, elsePart.getStatements().size());
    }

    //PATTERN
    @Test
    public void patternMatchStatement() throws IOException {
        Parser parser= toParseSetup("match(number) { >10 => { return 11; } }");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof PatternMatchingStatement);

        Assert.assertEquals("number", ((PatternMatchingStatement) statement).getToMatch().getIdentifier());

        Assert.assertEquals(1, ((PatternMatchingStatement) statement).getCases().size());

        MatchCaseStatement matchCase = ((PatternMatchingStatement) statement).getCases().get(0);
        Assert.assertEquals("var", ((OperatorExpression) matchCase.getCondition()).getLeftExpr().getIdentifier());
        Assert.assertEquals(10, ((OperatorExpression) matchCase.getCondition()).getRightExpr().value().getValue());
        Assert.assertEquals(Operator.MORE, ((OperatorExpression) matchCase.getCondition()).getOperator());

        Assert.assertEquals(1, matchCase.getStatements().size());
    }

    @Test
    public void patternMatch3Statement() throws IOException {
        Parser parser= toParseSetup("match(number) { predicate => { int test; } }");
        Statement statement = parser.testParseStatements();

        Assert.assertTrue(statement instanceof PatternMatchingStatement);

        Assert.assertEquals("number", ((PatternMatchingStatement) statement).getToMatch().getIdentifier());

        Assert.assertEquals(1, ((PatternMatchingStatement) statement).getCases().size());

        MatchCaseStatement matchCase = ((PatternMatchingStatement) statement).getCases().get(0);
        Assert.assertEquals("predicate", (matchCase.getCondition()).getIdentifier());

        Assert.assertEquals(1, matchCase.getStatements().size());
    }

    @Test
    public void additionExpParse() throws IOException {
        Parser parser= toParseSetup("1+2");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(1, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(2, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.ADD, expression.getOperator());
    }

    @Test
    public void subtractionExpParse() throws IOException {
        Parser parser= toParseSetup("3-4");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(3, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(4, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.SUB, expression.getOperator());
    }

    @Test
    public void multiplyExpParse() throws IOException {
        Parser parser= toParseSetup("5*6");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(5, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(6, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.MULTI, expression.getOperator());
    }

    @Test
    public void divisionExpParse() throws IOException {
        Parser parser= toParseSetup("7/8");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(7, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(8, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.DIV, expression.getOperator());
    }

    @Test
    public void moduloExpParse() throws IOException {
        Parser parser= toParseSetup("9%10");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(9, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(10, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.MODULO, expression.getOperator());
    }

    @Test
    public void lessEqExpParse() throws IOException {
        Parser parser= toParseSetup("1<=4");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(1, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(4, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.LESS_EQ, expression.getOperator());
    }

    @Test
    public void moreEqExpParse() throws IOException {
        Parser parser= toParseSetup("2>=6");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(2, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(6, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.MORE_EQ, expression.getOperator());
    }

    @Test
    public void lessExpParse() throws IOException {
        Parser parser= toParseSetup("3<5");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(3, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(5, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.LESS, expression.getOperator());
    }

    @Test
    public void moreExpParse() throws IOException {
        Parser parser= toParseSetup("7>2");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(7, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(2, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.MORE, expression.getOperator());
    }

    @Test
    public void equalsExpParse() throws IOException {
        Parser parser= toParseSetup("3==9");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(3, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(9, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.EQUAL, expression.getOperator());
    }

    @Test
    public void notEqualsExpParse() throws IOException {
        Parser parser= toParseSetup("1!=2");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(1, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(2, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.NOT_EQUAL, expression.getOperator());
    }

    @Test
    public void orExpParse() throws IOException {
        Parser parser= toParseSetup("1||2");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(1, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(2, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.OR, expression.getOperator());
    }

    @Test
    public void andExpParse() throws IOException {
        Parser parser= toParseSetup("1&&2");
        OperatorExpression expression = (OperatorExpression) parser.testParseExpression();

        Assert.assertEquals(1, expression.getLeftExpr().value().getValue());
        Assert.assertEquals(2, expression.getRightExpr().value().getValue());
        Assert.assertEquals(Operator.AND, expression.getOperator());
    }
}
