import java.io.FileWriter;
import java.io.IOException;

import ast.node.Program;
import compileError.CompileError;
import main.grammar.LogicPLLexer;
import main.grammar.LogicPLParser;
import visitor.nameAnalyzer.NameAnalyzer;
import visitor.astPrinter.ASTPrinter;
import visitor.typeAnalyzer.TypeAnalyzer;
import visitor.codeGenerator.CodeGenerator;
import org.antlr.v4.runtime.*;

public class Main {
    public static void main(String[] args) throws java.io.IOException {
        CharStream reader = CharStreams.fromFileName(args[0]);
        LogicPLLexer lexer = new LogicPLLexer(reader);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LogicPLParser parser = new LogicPLParser(tokens);
        Program program = parser.program().p;

        NameAnalyzer nameAnalyzer = new NameAnalyzer();
        nameAnalyzer.visit(program);

        TypeAnalyzer typeAnalyzer = new TypeAnalyzer();
        typeAnalyzer.visit(program);
        if (typeAnalyzer.typeErrors.size() > 0) {
            for (CompileError compileError : typeAnalyzer.typeErrors)
                System.out.println(compileError.getMessage());
            return;
        }

        CodeGenerator codeGen = new CodeGenerator(program);
        String result = codeGen.generate().toString();

        String output = (args.length > 1) ? args[1] : "out.j";
        if (writeToFile(output, result)) {
            System.out.println("Compilation was Successful!!");
        }
    }

    private static boolean writeToFile(String name, String text) {
        try {
            FileWriter file = new FileWriter(name, false);
            file.write(text);
            file.close();
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}