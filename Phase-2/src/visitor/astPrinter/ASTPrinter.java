package visitor.astPrinter;

import ast.node.declaration.FuncDeclaration;
import ast.node.declaration.MainDeclaration;
import ast.node.Program;
import ast.node.declaration.ArgDeclaration;
import ast.node.expression.*;
import ast.node.expression.values.*;
import ast.node.statement.*;
import visitor.Visitor;

public class ASTPrinter extends Visitor<Void> {
    public void messagePrinter(int line, String message){
        System.out.println("Line " + line + ": " + message);
    }

    @Override
    public Void visit(Program program) {
        messagePrinter(program.getLine(), program.toString());
        for (FuncDeclaration funcDeclaration : program.getFuncs())
            funcDeclaration.accept(this);
        program.getMain().accept(this);
        return null;
    }

    @Override
    public Void visit(MainDeclaration mainDeclaration) {
        messagePrinter(mainDeclaration.getLine(), mainDeclaration.toString());
        for (Statement statement: mainDeclaration.getMainStatements())
            statement.accept(this);
        return null;
    }

    @Override
    public Void visit(ArgDeclaration argDeclaration) {
        messagePrinter(argDeclaration.getLine(), argDeclaration.toString());
        if (argDeclaration.getIdentifier() != null)
            argDeclaration.getIdentifier().accept(this);
        return null;
    }

    @Override
    public Void visit(FuncDeclaration funcDeclaration) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(UnaryExpression unaryExpression) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(BinaryExpression binaryExpression) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(ArrayAccess arrayAccess) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(FunctionCall functionCall) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(QueryExpression queryExpression) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(IntValue value) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(FloatValue value) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(BooleanValue value) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(ArrayDecStmt arrayDecStmt) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(ForloopStmt forloopStmt) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(ImplicationStmt implicationStmt) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(PredicateStmt predicateStmt) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(VarDecStmt varDecStmt) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(PrintStmt printStmt) {
        // ToDo
        return null;
    }

    @Override
    public Void visit(AssignStmt assignStmt) {
        // ToDo
        return null;
    }

}

