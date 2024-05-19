package tkom.project;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tkom.project.exceptions.*;
import tkom.project.nodes.Program;
import tkom.project.scope.Reader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class InterpreterTest {
    String path = "src/test/java/tkom/project/interpreterTestFiles/";

    private Interpreter executionSetup(String code, Reader reader) {
        File file = new File(code);
        try (FileReader fr = new FileReader(file)) {
            Program program = new Parser(new Lexer(fr)).parse();
            if (program != null) {
                Interpreter interpreter;

                if (reader == null ) interpreter = new Interpreter(program);
                else interpreter = new Interpreter(program, reader);

                return interpreter;
            } else throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @ParameterizedTest(name = "{index} => {0}: {2}")
    @CsvSource(delimiter = '|', textBlock = """
        simple test             |   test1.tkom  | test
        from Int To Int         |   test2.tkom  | 1
        from Float To Int       |   test3.tkom  | 1 9
        from Bool To Int        |   test4.tkom  | 1 0
        from String To Int      |   test5.tkom  | 123
        from Int To Float       |   test7.tkom  | 1.0
        from Float To Float     |   test8.tkom  | 1.111111
        from Int to Bool        |   test11.tkom | false true true
        from Bool to Bool       |   test12.tkom | true false
        from String to Bool     |   test13.tkom | true true false false
        from Int to String      |   test15.tkom | 456
        from Float To String    |   test16.tkom | 456.789
        from Bool To String     |   test17.tkom | true
        from String To String   |   test18.tkom | test
        functionCall No params  |   test19.tkom | Name
        functionCall 1 param    |   test20.tkom | 15
        functionCall 2 params   |   test21.tkom | 31
        functionCall twice+rec  |   test22.tkom | 120
        functionCall returnInt  |   test23.tkom | 123
        functionCall returnFlt  |   test24.tkom | 123.123
        functionCall returnBool |   test25.tkom | true
        functionCall returnStr  |   test26.tkom | test
        ifStatement (if)        |   test38.tkom | Yes
        ifStatement (elif)      |   test39.tkom | What
        ifStatement (else)      |   test40.tkom | No
        ifStatement Complex     |   test41.tkom | 3628800
        whileStatement          |   test43.tkom | 10
        existStatement Value    |   test45.tkom | Test
        existStatement NoValue  |   test46.tkom | NoValue
        MatchCaseStatement1     |   test47.tkom | Test2
        MatchCaseStatement2     |   test48.tkom | Medium
        MatchCaseStatement3     |   test49.tkom | odd
        MatchCaseStatement4     |   test50.tkom | OP2
        ExpressionEvaluation1   |   test51.tkom | 9
        ExpressionEvaluation2   |   test52.tkom | 30
        ExpressionEvaluation3   |   test53.tkom | 9
        ExpressionEvaluation4   |   test54.tkom | 7.5
        ExpressionEvaluation5   |   test55.tkom | test
        ExpressionEvaluation6   |   test56.tkom | true
        ExpressionEvaluation7   |   test57.tkom | false
        While-Break             |   test67.tkom | 10
        While-Continue          |   test68.tkom | 51
    """)
    void executeTest(String description, String location, String result) {
        Reader reader = new Reader();
        Interpreter interpreter = executionSetup(path+location, reader);

        interpreter.execute();

        Assertions.assertEquals(result, reader.read());
    }

    @Test
    void fromStringToIntException() {
        Interpreter interpreter = executionSetup(path+"test6.tkom", null);
        Assert.assertThrows(NumberFormatException.class, interpreter::execute);
    }

    @Test
    void fromBoolToFloatException() {
        Interpreter interpreter = executionSetup(path+"test9.tkom", null);
        Assert.assertThrows(TypeException.class, interpreter::execute);
    }

    @Test
    void fromStringToFloatException() {
        Interpreter interpreter = executionSetup(path+"test10.tkom", null);
        Assert.assertThrows(TypeException.class, interpreter::execute);
    }

    @Test
    void fromFloatToBool() {
        Interpreter interpreter = executionSetup(path+"test14.tkom", null);
        Assert.assertThrows(TypeException.class, interpreter::execute);
    }

    @Test
    void ReturnStringOnVoid() {
        Interpreter interpreter = executionSetup(path+"test27.tkom", null);
        Assert.assertThrows(IncorrectReturnTypeException.class, interpreter::execute);
    }

    @Test
    void ReturnIntOnVoid() {
        Interpreter interpreter = executionSetup(path+"test28.tkom", null);
        Assert.assertThrows(IncorrectReturnTypeException.class, interpreter::execute);
    }

    @Test
    void ReturnFloatOnVoid() {
        Interpreter interpreter = executionSetup(path+"test29.tkom", null);
        Assert.assertThrows(IncorrectReturnTypeException.class, interpreter::execute);
    }

    @Test
    void ReturnBoolOnVoid() {
        Interpreter interpreter = executionSetup(path+"test30.tkom", null);
        Assert.assertThrows(IncorrectReturnTypeException.class, interpreter::execute);
    }

    @Test
    void IncorrectAmountOfParams() {
        Interpreter interpreter = executionSetup(path+"test31.tkom", null);
        Assert.assertThrows(IncorrectParameterAmountException.class, interpreter::execute);
    }

    @Test
    void WrongParameterType() {
        Interpreter interpreter = executionSetup(path+"test32.tkom", null);
        Assert.assertThrows(MismatchedTypesException.class, interpreter::execute);
    }

    @Test
    void FunctionDefinedTwice() {
        Interpreter interpreter = executionSetup(path+"test33.tkom", null);
        Assert.assertThrows(FunctionAlreadyDefinedException.class, interpreter::execute);
    }

    @Test
    void ReservedFunctionNameDefinition() {
        Interpreter interpreter = executionSetup(path + "test34.tkom", null);
        Assert.assertThrows(FunctionAlreadyDefinedException.class, interpreter::execute);
    }

    @Test
    void RedefinedGlobalVariable() {
        Interpreter interpreter = executionSetup(path + "test35.tkom", null);
        Assert.assertThrows(VariableAlreadyDefinedException.class, interpreter::execute);
    }

    @Test
    void VariableAlreadyDefined() {
        Interpreter interpreter = executionSetup(path + "test36.tkom", null);
        Assert.assertThrows(VariableAlreadyDefinedException.class, interpreter::execute);
    }

    @Test
    void RestrictedVariableName() {
        Interpreter interpreter = executionSetup(path + "test37.tkom", null);
        Assert.assertThrows(VariableAlreadyDefinedException.class, interpreter::execute);
    }

    @Test
    void IfStatementIncorrectCondition() {
        Interpreter interpreter = executionSetup(path + "test42.tkom", null);
        Assert.assertThrows(MismatchedTypesException.class, interpreter::execute);
    }

    @Test
    void WhileStatementIncorrectCondition() {
        Interpreter interpreter = executionSetup(path + "test44.tkom", null);
        Assert.assertThrows(MismatchedSidesOfOperationException.class, interpreter::execute);
    }

    @Test
    void ChangeValueOfNonMutableVariable() {
        Interpreter interpreter = executionSetup(path + "test58.tkom", null);
        Assert.assertThrows(ReassignNonMutableVariableException.class, interpreter::execute);
    }

    @Test
    void PassNonOptionalValueToExistStatement() {
        Interpreter interpreter = executionSetup(path + "test59.tkom", null);
        Assert.assertThrows(NonOptionalVariableMissingValueException.class, interpreter::execute);
    }

    @Test
    void ExpressionEvaluationException1() {
        Interpreter interpreter = executionSetup(path + "test60.tkom", null);
        Assert.assertThrows(MismatchedSidesOfOperationException.class, interpreter::execute);
    }

    @Test
    void ExpressionEvaluationException2() {
        Interpreter interpreter = executionSetup(path + "test61.tkom", null);
        Assert.assertThrows(MismatchedSidesOfOperationException.class, interpreter::execute);
    }

    @Test
    void ExpressionEvaluationException3() {
        Interpreter interpreter = executionSetup(path + "test62.tkom", null);
        Assert.assertThrows(MismatchedSidesOfOperationException.class, interpreter::execute);
    }

    @Test
    void CallNotDeclaredFunction() {
        Interpreter interpreter = executionSetup(path + "test63.tkom", null);
        Assert.assertThrows(FunctionNotDeclaredException.class, interpreter::execute);
    }

    @Test
    void NotDeclaredVariable() {
        Interpreter interpreter = executionSetup(path + "test64.tkom", null);
        Assert.assertThrows(VariableNotDeclaredException.class, interpreter::execute);
    }

    @Test
    void ContinueOutsideWhileLoop() {
        Interpreter interpreter = executionSetup(path + "test65.tkom", null);
        Assert.assertThrows(InvalidJumpStatement.class, interpreter::execute);
    }

    @Test
    void BreakOutsideWhileLoop() {
        Interpreter interpreter = executionSetup(path + "test66.tkom", null);
        Assert.assertThrows(InvalidJumpStatement.class, interpreter::execute);
    }
}
