package tkom.project;

import org.junit.Assert;
import org.junit.Test;
import tkom.project.exceptions.*;
import tkom.project.tokens.Token;
import tkom.project.tokens.TokenType;

import java.io.IOException;
import java.io.StringReader;

public class LexerTest {

    @Test
    public void plusOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("+"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.PLUS_OP, testedToken.getType());
    }

    @Test
    public void minusOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("-"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.MINUS_OP, testedToken.getType());
    }

    @Test
    public void multiplicationOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("*"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.MULTI_OP, testedToken.getType());
    }

    @Test
    public void divisionOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("/"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.DIV_OP, testedToken.getType());
    }

    @Test
    public void moduloOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("%"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.MODULO_OP, testedToken.getType());
    }

    @Test
    public void leftParenOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("("));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.L_PAREN_OP, testedToken.getType());
    }

    @Test
    public void rightParenOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader(")"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.R_PAREN_OP, testedToken.getType());
    }

    @Test
    public void leftBracketOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("{"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.L_BRACKET_OP, testedToken.getType());
    }

    @Test
    public void rightBracketOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("}"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.R_BRACKET_OP, testedToken.getType());
    }

    @Test
    public void commaOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader(","));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.COMMA, testedToken.getType());
    }

    @Test
    public void semicolonOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader(";"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.SEMICOLON, testedToken.getType());
    }

    @Test
    public void optionalOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("?"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.OPTIONAL_OP, testedToken.getType());
    }

    @Test
    public void assignOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("="));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.ASSIGN_OP, testedToken.getType());
    }

    @Test
    public void equalityOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("=="));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.EQUAL_OP, testedToken.getType());
    }

    @Test
    public void notEqualOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("!="));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.NOT_EQUAL_OP, testedToken.getType());
    }

    @Test
    public void notOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("!"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.NOT_OP, testedToken.getType());
    }

    @Test
    public void lessOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("<"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.LESS_OP, testedToken.getType());
    }

    @Test
    public void lessEqualOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("<="));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.LESS_EQUAL_OP, testedToken.getType());
    }

    @Test
    public void moreOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader(">"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.MORE_OP, testedToken.getType());
    }

    @Test
    public void moreEqualOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader(">="));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.MORE_EQUAL_OP, testedToken.getType());
    }

    @Test
    public void andOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("&&"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.AND_OP, testedToken.getType());
    }

    @Test
    public void incorrectAndOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("&"));
        Assert.assertThrows(MissingMultiCharOpException.class, lexer::getNextToken);
    }

    @Test
    public void orOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("||"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.OR_OP, testedToken.getType());
    }

    @Test
    public void incorrectOrOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("|"));
        Assert.assertThrows(MissingMultiCharOpException.class, lexer::getNextToken);
    }

    @Test
    public void matchOperator() throws IOException {
        Lexer lexer = new Lexer(new StringReader("=>"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.MATCH_CASE_OP, testedToken.getType());
    }

    @Test
    public void defKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("def"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.DEF_KW, testedToken.getType());
    }

    @Test
    public void ifKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("if"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IF_KW, testedToken.getType());
    }

    @Test
    public void ifMistake1() throws IOException {
        Lexer lexer = new Lexer(new StringReader("iff"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("iff", testedToken.value().getValue());
    }

    @Test
    public void ifMistake2() throws IOException {
        Lexer lexer = new Lexer(new StringReader("iif"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("iif", testedToken.value().getValue());
    }

    @Test
    public void elifKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("elif"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.ELIF_KW, testedToken.getType());
    }

    @Test
    public void elseKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("else"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.ELSE_KW, testedToken.getType());
    }

    @Test
    public void whileKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("while"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.WHILE_KW, testedToken.getType());
    }

    @Test
    public void continueKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("continue"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.CONTINUE_KW, testedToken.getType());
    }

    @Test
    public void breakKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("break"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.BREAK_KW, testedToken.getType());
    }

    @Test
    public void returnKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("return"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.RETURN_KW, testedToken.getType());
    }

    @Test
    public void voidKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("void"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.VOID_KW, testedToken.getType());
    }

    @Test
    public void boolKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("bool"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.BOOL_KW, testedToken.getType());
    }

    @Test
    public void stringKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("string"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.STRING_KW, testedToken.getType());
    }

    @Test
    public void intKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("int"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.INT_KW, testedToken.getType());
    }

    @Test
    public void floatKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("float"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.FLOAT_KW, testedToken.getType());
    }

    @Test
    public void mutKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("mut"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.MUTABLE_KW, testedToken.getType());
    }

    @Test
    public void existKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("exist"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.EXIST_KW, testedToken.getType());
    }

    @Test
    public void trueKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("true"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.BOOL_VAL, testedToken.getType());
        Assert.assertEquals(true, testedToken.value().getValue());
    }

    @Test
    public void falseKeyword() throws IOException {
        Lexer lexer = new Lexer(new StringReader("false"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.BOOL_VAL, testedToken.getType());
        Assert.assertEquals(false, testedToken.value().getValue());
    }

    @Test
    public void emptyInput() throws IOException {
        Lexer lexer = new Lexer(new StringReader(""));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.ETX, testedToken.getType());
        Assert.assertNull(testedToken.value());
    }

    @Test
    public void singleLetterIdentifier() throws IOException {
        Lexer lexer = new Lexer(new StringReader("t"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("t", testedToken.value().getValue());
    }

    @Test
    public void multipleLetterIdentifier() throws IOException {
        Lexer lexer = new Lexer(new StringReader("test"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("test", testedToken.value().getValue());
    }

    @Test
    public void multipleCaseLetterIdentifier() throws IOException {
        Lexer lexer = new Lexer(new StringReader("tEsTts"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("tEsTts", testedToken.value().getValue());
    }

    @Test
    public void identifierWithUnderscore() throws IOException {
        Lexer lexer = new Lexer(new StringReader("test__"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("test__", testedToken.value().getValue());
    }

    @Test
    public void identifierWithUnderscoreAtBeginning() throws IOException {
        Lexer lexer = new Lexer(new StringReader("_test"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.UNKNOWN, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("test", testedToken.value().getValue());
    }

    @Test
    public void identifierWithUpperCaseAtBeginning() throws IOException {
        Lexer lexer = new Lexer(new StringReader("Ttest"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.UNKNOWN, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("test", testedToken.value().getValue());
    }

    @Test
    public void identifierWithDash() throws IOException {
        Lexer lexer = new Lexer(new StringReader("test1-test2"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("test1", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.MINUS_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("test2", testedToken.value().getValue());
    }

    @Test
    public void identifierWithDot() throws IOException {
        Lexer lexer = new Lexer(new StringReader("test1.test2"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("test1", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.UNKNOWN, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("test2", testedToken.value().getValue());
    }

    @Test
    public void lexerTestSingleCharOp() throws IOException {
        Lexer lexer = new Lexer(new StringReader("+-*%()\n"));

        Assert.assertEquals(TokenType.PLUS_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.MINUS_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.MULTI_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.MODULO_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.L_PAREN_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.R_PAREN_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void lexerTestMultiCharOp() throws IOException {
        Lexer lexer = new Lexer(new StringReader(">>=!==||\n"));

        Assert.assertEquals(TokenType.MORE_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.MORE_EQUAL_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.NOT_EQUAL_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.ASSIGN_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.OR_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void lexerTestRandomOperators() throws IOException {
        Lexer lexer = new Lexer(new StringReader("=>><<====!"));

        Assert.assertEquals(TokenType.MATCH_CASE_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.MORE_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.LESS_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.LESS_EQUAL_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.EQUAL_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.ASSIGN_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.NOT_OP, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void intZero() throws IOException {
        Lexer lexer = new Lexer(new StringReader("0"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.INT_VAL, testedToken.getType());
        Assert.assertEquals(0, testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void multipleZeros() throws IOException {
        Lexer lexer = new Lexer(new StringReader("000000"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.INT_VAL, testedToken.getType());
        Assert.assertEquals(0, testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void multipleZerosBeforeNum() throws IOException {
        Lexer lexer = new Lexer(new StringReader("0000007"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.INT_VAL, testedToken.getType());
        Assert.assertEquals(7, testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void integerTest() throws IOException {
        Lexer lexer = new Lexer(new StringReader("12345"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.INT_VAL, testedToken.getType());
        Assert.assertEquals(12345, testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void integerTestWithIdentifier1() throws IOException {
        Lexer lexer = new Lexer(new StringReader("12345abc"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.INT_VAL, testedToken.getType());
        Assert.assertEquals(12345, testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("abc", testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void integerTestWithIdentifier2() throws IOException {
        Lexer lexer = new Lexer(new StringReader("000abc"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.INT_VAL, testedToken.getType());
        Assert.assertEquals(0, testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("abc", testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void floatTest() throws IOException {
        Lexer lexer = new Lexer(new StringReader("123.456"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.FLOAT_VAL, testedToken.getType());
        Assert.assertEquals(123.456, (Float) testedToken.value().getValue(), 0.001);

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void floatTestIntZero() throws IOException {
        Lexer lexer = new Lexer(new StringReader("0.45678"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.FLOAT_VAL, testedToken.getType());
        Assert.assertEquals(0.45678, (Float) testedToken.value().getValue(), 0.00001);

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void floatTestIntMultipleZeros() throws IOException {
        Lexer lexer = new Lexer(new StringReader("0000.45678"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.FLOAT_VAL, testedToken.getType());
        Assert.assertEquals(0.45678, (Float) testedToken.value().getValue(), 0.00001);

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void floatTestZerosAndInt() throws IOException {
        Lexer lexer = new Lexer(new StringReader("00012.45678"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.FLOAT_VAL, testedToken.getType());
        Assert.assertEquals(12.45678, (Float) testedToken.value().getValue(), 0.00001);

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void floatNoFloatPart() throws IOException {
        Lexer lexer = new Lexer(new StringReader("123."));
        Assert.assertThrows(IncorrectFloatPartInFloatValue.class, lexer::getNextToken);
    }

    @Test
    public void floatDoubleDot() throws IOException {
        Lexer lexer = new Lexer(new StringReader("123..123"));
        Assert.assertThrows(IncorrectFloatPartInFloatValue.class, lexer::getNextToken);
    }

    @Test
    public void floatIncorrectFloatPart() throws IOException {
        Lexer lexer = new Lexer(new StringReader("123.abc"));
        Assert.assertThrows(IncorrectFloatPartInFloatValue.class, lexer::getNextToken);
    }

    @Test
    public void correctFloatWithIdentifier() throws IOException {
        Lexer lexer = new Lexer(new StringReader("123.0abc"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.FLOAT_VAL, testedToken.getType());
        Assert.assertEquals(123.0, (Float) testedToken.value().getValue(), 0);

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("abc", testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void lexerTestInteger() throws IOException {
        Lexer lexer = new Lexer(new StringReader("int value = 12;"));
        Token tmp;

        Assert.assertEquals(TokenType.INT_KW, lexer.getNextToken().getType());

        tmp = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, tmp.getType());
        Assert.assertEquals("value", tmp.value().getValue());

        Assert.assertEquals(TokenType.ASSIGN_OP, lexer.getNextToken().getType());

        tmp = lexer.getNextToken();
        Assert.assertEquals(TokenType.INT_VAL, tmp.getType());
        Assert.assertEquals(12, tmp.value().getValue());

        Assert.assertEquals(TokenType.SEMICOLON, lexer.getNextToken().getType());
    }

    @Test
    public void lexerTestBigInteger() throws IOException {
        Lexer lexer = new Lexer(new StringReader("int value = 222222222;"));
        Token tmp;

        Assert.assertEquals(TokenType.INT_KW, lexer.getNextToken().getType());

        tmp = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, tmp.getType());
        Assert.assertEquals("value", tmp.value().getValue());

        Assert.assertEquals(TokenType.ASSIGN_OP, lexer.getNextToken().getType());

        tmp = lexer.getNextToken();
        Assert.assertEquals(TokenType.INT_VAL, tmp.getType());
        Assert.assertEquals(222222222, tmp.value().getValue());

        Assert.assertEquals(TokenType.SEMICOLON, lexer.getNextToken().getType());
        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void lexerTestTooBigInteger() throws IOException {
        Lexer lexer = new Lexer(new StringReader("222222222222222"));
        Assert.assertThrows(IntOverflowException.class, lexer::getNextToken);
    }

    @Test
    public void lexerTestTooBigFloatPart() throws IOException {
        Lexer lexer = new Lexer(new StringReader("5.222222222222222222222222\n"));
        Assert.assertThrows(FloatPartOverflowException.class, lexer::getNextToken);
    }

    @Test
    public void lexerTestFloat() throws IOException {
        Lexer lexer = new Lexer(new StringReader("float value = 2222.2222;\n"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.FLOAT_KW, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("value", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.ASSIGN_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.FLOAT_VAL, testedToken.getType());
        Assert.assertEquals(2222.2222, (Float) testedToken.value().getValue(), 0.0001);

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.SEMICOLON, testedToken.getType());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void stringTest() throws IOException {
        Lexer lexer = new Lexer(new StringReader("\"test\""));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.STRING_VAL, testedToken.getType());
        Assert.assertEquals("test", testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void lexerTestString() throws IOException {
        Lexer lexer = new Lexer(new StringReader("string text = \"Some text\";\n"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.STRING_KW, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("text", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.ASSIGN_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.STRING_VAL, testedToken.getType());
        Assert.assertEquals("Some text", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.SEMICOLON, testedToken.getType());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void lexerTestStringWithEscape() throws IOException {
        Lexer lexer = new Lexer(new StringReader("text = \"Some \\\" text\""));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("text", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.ASSIGN_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.STRING_VAL, testedToken.getType());
        Assert.assertEquals("Some \" text", testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void lexerTestEmptyString() throws IOException {
        Lexer lexer = new Lexer(new StringReader("\"\""));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.STRING_VAL, testedToken.getType());
        Assert.assertEquals("", testedToken.value().getValue());
    }

    @Test
    public void lexerTestUnClosedString() throws IOException {
        Lexer lexer = new Lexer(new StringReader("\"test test"));
        Assert.assertThrows(NotClosedStringException.class, lexer::getNextToken);
    }

    @Test
    public void commentTest() throws IOException {
        Lexer lexer = new Lexer(new StringReader("#Comment"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.COMMENT, testedToken.getType());
        Assert.assertEquals("Comment", testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void emptyCommentTest() throws IOException {
        Lexer lexer = new Lexer(new StringReader("#"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.COMMENT, testedToken.getType());
        Assert.assertEquals("", testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void lexerTestComment() throws IOException {
        Lexer lexer = new Lexer(new StringReader("#this is a comment\n test\n"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.COMMENT, testedToken.getType());
        Assert.assertEquals("this is a comment", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("test", testedToken.value().getValue());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void matchCaseTestComparison() throws IOException {
        Lexer lexer = new Lexer(new StringReader("<50 => {print(\"String\")"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.LESS_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.INT_VAL, testedToken.getType());
        Assert.assertEquals(50, testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.MATCH_CASE_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.L_BRACKET_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("print", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.L_PAREN_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.STRING_VAL, testedToken.getType());
        Assert.assertEquals("String", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.R_PAREN_OP, testedToken.getType());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void matchCaseTestPredicate() throws IOException {
        Lexer lexer = new Lexer(new StringReader("is_odd => {}"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("is_odd", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.MATCH_CASE_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.L_BRACKET_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.R_BRACKET_OP, testedToken.getType());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }

    @Test
    public void OptionalParam() throws IOException {
        Lexer lexer = new Lexer(new StringReader("def greetings(string? name)"));

        Token testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.DEF_KW, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("greetings", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.L_PAREN_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.STRING_KW, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.OPTIONAL_OP, testedToken.getType());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.IDENTIFIER, testedToken.getType());
        Assert.assertEquals("name", testedToken.value().getValue());

        testedToken = lexer.getNextToken();
        Assert.assertEquals(TokenType.R_PAREN_OP, testedToken.getType());

        Assert.assertEquals(TokenType.ETX, lexer.getNextToken().getType());
    }
}
