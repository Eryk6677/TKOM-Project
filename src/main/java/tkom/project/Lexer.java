package tkom.project;

import tkom.project.exceptions.*;
import tkom.project.tokens.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.AbstractMap;

import java.util.Map;

import static java.lang.Math.addExact;
import static java.lang.Math.multiplyExact;

public class Lexer {
    Map<Character, TokenType> mapSCharOp = Map.ofEntries(
            new AbstractMap.SimpleEntry<>('+', TokenType.PLUS_OP),
            new AbstractMap.SimpleEntry<>('-', TokenType.MINUS_OP),
            new AbstractMap.SimpleEntry<>('*', TokenType.MULTI_OP),
            new AbstractMap.SimpleEntry<>('/', TokenType.DIV_OP),
            new AbstractMap.SimpleEntry<>('%', TokenType.MODULO_OP),
            new AbstractMap.SimpleEntry<>('(', TokenType.L_PAREN_OP),
            new AbstractMap.SimpleEntry<>(')', TokenType.R_PAREN_OP),
            new AbstractMap.SimpleEntry<>('{', TokenType.L_BRACKET_OP),
            new AbstractMap.SimpleEntry<>('}', TokenType.R_BRACKET_OP),
            new AbstractMap.SimpleEntry<>(';', TokenType.SEMICOLON),
            new AbstractMap.SimpleEntry<>(',', TokenType.COMMA),
            new AbstractMap.SimpleEntry<>('?', TokenType.OPTIONAL_OP)
    );

    Map<String , TokenType> mapKeyWords = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("def", TokenType.DEF_KW),
            new AbstractMap.SimpleEntry<>("if", TokenType.IF_KW),
            new AbstractMap.SimpleEntry<>("elif", TokenType.ELIF_KW),
            new AbstractMap.SimpleEntry<>("else", TokenType.ELSE_KW),
            new AbstractMap.SimpleEntry<>("while", TokenType.WHILE_KW),
            new AbstractMap.SimpleEntry<>("continue", TokenType.CONTINUE_KW),
            new AbstractMap.SimpleEntry<>("break", TokenType.BREAK_KW),
            new AbstractMap.SimpleEntry<>("return", TokenType.RETURN_KW),
            new AbstractMap.SimpleEntry<>("void", TokenType.VOID_KW),
            new AbstractMap.SimpleEntry<>("bool", TokenType.BOOL_KW),
            new AbstractMap.SimpleEntry<>("string", TokenType.STRING_KW),
            new AbstractMap.SimpleEntry<>("int", TokenType.INT_KW),
            new AbstractMap.SimpleEntry<>("float", TokenType.FLOAT_KW),
            new AbstractMap.SimpleEntry<>("mut", TokenType.MUTABLE_KW),
            new AbstractMap.SimpleEntry<>("exist", TokenType.EXIST_KW),
            new AbstractMap.SimpleEntry<>("match", TokenType.MATCH_KW),
            new AbstractMap.SimpleEntry<>("true", TokenType.BOOL_VAL),
            new AbstractMap.SimpleEntry<>("false", TokenType.BOOL_VAL)
    );

    private final Reader code;

    private int index = 0;
    private int lineNum = 1;
    private Character currentChar;

    public Lexer(FileReader code) throws IOException {
        this.code = code;
        getNextCharacter();
    }

    public Lexer(StringReader code) throws IOException {
        this.code = code;
        getNextCharacter();
    }

    public Token getNextToken() throws IOException {
        Token token;
        skipWhites();
        if (currentChar == null) return new Token(TokenType.ETX, getPos(lineNum, index));

        int startPos = index;
        if ((token = tryBuildSingleCharacterOperator(startPos)) != null) return token;
        if ((token = tryBuildMultiCharacterOperator(startPos))!= null) return token;
        if ((token = tryBuildIdentifierOrKeyWord(startPos)) != null) return token;
        if ((token = tryBuildNumber(startPos)) != null) return token;
        if ((token = tryBuildString(startPos)) != null) return token;
        if ((token = tryBuildComment(startPos)) != null) return token;

        getNextCharacter();
        return new Token(TokenType.UNKNOWN, getPos(lineNum, index));
    }

    private Token tryBuildSingleCharacterOperator(int startPos) throws IOException {
        TokenType type = mapSCharOp.get(currentChar);
        if (type != null) {
            getNextCharacter();
            return new Token(type, getPos(lineNum, startPos));
        }
        return null;
    }

    private Token tryBuildMultiCharacterOperator(int startPos) throws IOException {
        switch (currentChar) {
            case '=' -> {
                return tryBuildAssignmentOperator(currentChar, startPos);
            }
            case '!' -> {
                return tryBuildNegativeOperator(currentChar, startPos);
            }
            case '<', '>' -> {
                return tryBuildLessOrMoreOperator(currentChar, startPos);
            }
            case '&', '|' -> {
                return tryBuildLogicalOperator(currentChar, startPos);
            }
            default -> {
                return null;
            }
        }
    }

    private Token tryBuildAssignmentOperator(char firstChar, int startPos) throws IOException {
        if (firstChar != '=') return null;

        getNextCharacter();
        if (currentChar == null) return new Token(TokenType.ASSIGN_OP, getPos(lineNum, startPos));

        if (currentChar == '=') {
            getNextCharacter();
            return new Token(TokenType.EQUAL_OP, getPos(lineNum, startPos));
        } else if (currentChar == '>') {
            getNextCharacter();
            return new Token(TokenType.MATCH_CASE_OP, getPos(lineNum, startPos));
        }
        return new Token(TokenType.ASSIGN_OP, getPos(lineNum, startPos));
    }

    private Token tryBuildNegativeOperator(char firstChar, int startPos) throws IOException {
        if (firstChar != '!') return null;

        getNextCharacter();
        if (currentChar == null) return new Token(TokenType.NOT_OP, getPos(lineNum, startPos));

        if (currentChar == '=') {
            getNextCharacter();
            return new Token(TokenType.NOT_EQUAL_OP, getPos(lineNum, startPos));
        }
        return new Token(TokenType.NOT_OP, getPos(lineNum, startPos));
    }

    private Token tryBuildLessOrMoreOperator(char firstChar, int startPos) throws IOException {
        if (firstChar == '<') {
            getNextCharacter();
            if ((currentChar != null) && (currentChar == '=')) {
                getNextCharacter();
                return new Token(TokenType.LESS_EQUAL_OP, getPos(lineNum, startPos));
            }
            return new Token(TokenType.LESS_OP, getPos(lineNum, startPos));

        } else if (firstChar == '>') {
            getNextCharacter();
            if ((currentChar != null) && (currentChar == '=')) {
                getNextCharacter();
                return new Token(TokenType.MORE_EQUAL_OP, getPos(lineNum, startPos));
            }
            return new Token(TokenType.MORE_OP, getPos(lineNum, startPos));
        }
        return null;
    }

    public Token tryBuildLogicalOperator(char firstChar, int startPos) throws IOException {
        if (firstChar == '&') {
            getNextCharacter();
            if ((currentChar != null) && (currentChar == '&')) {
                getNextCharacter();
                return new Token(TokenType.AND_OP, getPos(lineNum, startPos));
            }
            throw new MissingMultiCharOpException(firstChar, getPos(lineNum, startPos));

        } else if (firstChar == '|') {
            getNextCharacter();
            if ((currentChar != null) && (currentChar == '|')) {
                getNextCharacter();
                return new Token(TokenType.OR_OP, getPos(lineNum, startPos));
            }
            throw new MissingMultiCharOpException(firstChar, getPos(lineNum, startPos));
        }
        return null;
    }

    private Token tryBuildIdentifierOrKeyWord(int startPos) throws IOException {
        if (Character.isLetter(currentChar) && Character.isLowerCase(currentChar)) {
            StringBuilder sb = new StringBuilder();

            sb.append(currentChar);

            getNextCharacter();
            if (currentChar == null) return new Token(TokenType.IDENTIFIER, getPos(lineNum, startPos), sb.toString());

            int length = 1;
            while ((currentChar != null) && (Character.isLetter(currentChar) || Character.isDigit(currentChar) || (currentChar == '_'))) {
                sb.append(currentChar);
                length++;
                checkLength(length, startPos);
                getNextCharacter();
            }

            Token keyword = tryBuildKeyword(sb.toString(), startPos);
            if (keyword != null) return keyword;
            else return new Token(TokenType.IDENTIFIER, getPos(lineNum, startPos), sb.toString());
        }
        return null;
    }

    private Token tryBuildKeyword(String result, int position) {
        TokenType type = mapKeyWords.get(result);

        if (type != null) {
            if (type == TokenType.BOOL_VAL) return new Token(type, getPos(lineNum, position), Boolean.parseBoolean(result));
            return new Token(type, getPos(lineNum, position));
        }
        return null;
    }

    private Token tryBuildNumber(int startPos) throws IOException {
        if (!Character.isDigit(currentChar)) return null;

        int intPart = 0;

        skipZeros();
        if (currentChar == null) return new Token(TokenType.INT_VAL, getPos(lineNum, startPos), intPart);

        if (currentChar != '.') {

            if (!Character.isDigit(currentChar)) return new Token(TokenType.INT_VAL, getPos(lineNum, startPos), intPart);

            intPart += currentChar - '0';

            getNextCharacter();
            if (currentChar == null) return new Token(TokenType.INT_VAL, getPos(lineNum, startPos), intPart);

            while (Character.isDigit(currentChar)) {
                try {
                    intPart = multiplyExact(intPart, 10);
                    intPart = addExact(intPart, currentChar - '0');
                } catch (ArithmeticException e) {
                    throw new IntOverflowException(getPos(lineNum, startPos));
                }

                getNextCharacter();
                if (currentChar == null) return new Token(TokenType.INT_VAL, getPos(lineNum, startPos), intPart);
            }
        }

        if (currentChar == '.') {
            long floatPart = 0;
            int decimalPlaces = 0;

            getNextCharacter();
            if (currentChar == null || (!Character.isDigit(currentChar))) throw new IncorrectFloatPartInFloatValue(getPos(lineNum, startPos));

            while ((currentChar != null) && Character.isDigit(currentChar)) {
                decimalPlaces++;
                try {
                    floatPart = multiplyExact(floatPart, 10);
                    floatPart = addExact(floatPart, (long) currentChar - '0');
                } catch (ArithmeticException e) {
                    throw new FloatPartOverflowException(getPos(lineNum, startPos));
                }
                getNextCharacter();
            }

            float result = intPart + ((float) (floatPart / (Math.pow(10, decimalPlaces))));
            checkFloatValue(result);

            return new Token(TokenType.FLOAT_VAL, getPos(lineNum, startPos), result);
        } else {
            return new Token(TokenType.INT_VAL, getPos(lineNum, startPos), intPart);
        }
    }

    private void checkFloatValue(float result) {
        // based on https://www.baeldung.com/java-overflow-underflow
        if (result == Float.POSITIVE_INFINITY) {
            throw new FloatOverOrUnderFlowException("POSITIVE_INFINITY");
        } else if (result == Float.NEGATIVE_INFINITY) {
            throw new FloatOverOrUnderFlowException("NEGATIVE_INFINITY");
        } else if (Float.compare(-0.0f, result) == 0) {
            throw new FloatOverOrUnderFlowException("NEGATIVE_ZERO");
        } else if (Float.compare(+0.0f, result) == 0) {
            throw new FloatOverOrUnderFlowException("POSITIVE_ZERO");
        }
    }

    private Token tryBuildString(int startPos) throws IOException {
        if (currentChar == '"') {
            StringBuilder sb = new StringBuilder();

            checkNextCharacter(startPos);

            int length = 0;
            while (currentChar != '"') {
                if (currentChar != '\\') {
                    sb.append(currentChar);
                } else {
                    checkNextCharacter(startPos);
                    sb.append(escapingChar(currentChar));
                }
                length++;
                checkLength(length, startPos);

                checkNextCharacter(startPos);
            }

            getNextCharacter();
            return new Token(TokenType.STRING_VAL, getPos(lineNum, startPos), sb.toString());
        } else return null;
    }

    private void checkNextCharacter(int startPos) throws IOException {
        getNextCharacter();
        if (currentChar == null) throw new NotClosedStringException(getPos(lineNum, startPos));
    }

    private char escapingChar(char givenChar) {
        switch (givenChar) {
            case 't' -> {
                return '\t';
            }
            case 'b' -> {
                return '\b';
            }
            case 'n' -> {
                return '\n';
            }
            case 'r' -> {
                return '\r';
            }
            case 'f' -> {
                return '\f';
            }
            case '\'' -> {
                return '\'';
            }
            case '"' -> {
                return '\"';
            }
            default -> {
                return '\\';
            }
        }
    }

    private Token tryBuildComment(int startPos) throws IOException {
        if (currentChar == '#') {
            StringBuilder sb = new StringBuilder();

            getNextCharacter();
            if (currentChar == null) return new Token(TokenType.COMMENT, getPos(lineNum, startPos), "");

            int length = 0;
            while(currentChar != '\n') {
                sb.append(currentChar);
                length++;
                checkLength(length, startPos);

                getNextCharacter();
                if (currentChar == null) return new Token(TokenType.COMMENT, getPos(lineNum, startPos), sb.toString());
            }
            getNextCharacter();
            return new Token(TokenType.COMMENT, getPos(lineNum++, startPos), sb.toString());
        }
        return null;
    }

    private void getNextCharacter() throws IOException {
        index++;
        int tmp = code.read();
        if (tmp != -1) currentChar = (char) tmp;
        else currentChar = null;
    }

    private void skipWhites() throws IOException {
        while ((currentChar != null) && Character.isWhitespace(currentChar)) {
            if (String.valueOf(currentChar).equals("\n")) {
                lineNum++;
                index = 0;
            }
            getNextCharacter();
        }
    }

    private void checkLength(int length, int startPos) {
        if (length == Integer.MAX_VALUE) throw new TextTooLongException(getPos(lineNum, startPos));
    }

    private void skipZeros() throws IOException {
        while ((currentChar != null) && (currentChar == '0')) getNextCharacter();
    }

    private AbstractMap.SimpleEntry<Integer, Integer> getPos(Integer line, Integer index) {
        return new AbstractMap.SimpleEntry<>(line, index);
    }
}
