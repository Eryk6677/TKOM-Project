package tkom.project;

import tkom.project.nodes.Program;

import java.io.*;

public class App {
    public static void main( String[] args ) throws IOException {
        Program program = null;

        if (args.length == 0) {
            String code = "int test = 5;";
            Lexer lexer = new Lexer(new StringReader(code));
            Parser parser = new Parser(lexer);

            program = parser.parse();
        } else {
            File file = new File(args[0]);
            try (FileReader fr = new FileReader(file)) {
                Lexer lexer = new Lexer(fr);
                Parser parser = new Parser(lexer);

                program = parser.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (program != null) {
            Interpreter interpreter = new Interpreter(program);
            String msg = interpreter.execute();

            System.out.println(msg);
        }
    }
}
