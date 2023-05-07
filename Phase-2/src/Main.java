import java.io.IOException;

import ast.node.Program;
import compileError.CompileError;
import main.grammar.LogicPLLexer;
import main.grammar.LogicPLParser;
import visitor.nameAnalyzer.NameAnalyzer;
import visitor.astPrinter.ASTPrinter;
import org.antlr.v4.runtime.*;

public class Main {
        public static void main(String[] args) throws java.io.IOException {

            CharStream reader = CharStreams.fromFileName(args[0]);
            LogicPLLexer lexer = new LogicPLLexer(reader);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LogicPLParser parser = new LogicPLParser(tokens);
            Program program = parser.program().p;

            ASTPrinter printer = new ASTPrinter();
            printer.visit(program);

            NameAnalyzer nameAnalyzer = new NameAnalyzer();
            nameAnalyzer.visit(program);
            if (nameAnalyzer.nameErrors.size() > 0){
                for(CompileError compileError: nameAnalyzer.nameErrors)
                    System.out.println(compileError.getMessage());
                return;
            }

            System.out.println("Compilation was Successful!!");
        }

}