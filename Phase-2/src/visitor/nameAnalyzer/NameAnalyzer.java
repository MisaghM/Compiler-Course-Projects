package visitor.nameAnalyzer;

import ast.node.Program;
import ast.node.declaration.*;
import ast.node.statement.ArrayDecStmt;
import ast.node.statement.VarDecStmt;
import ast.node.statement.ForloopStmt;
import ast.node.statement.ImplicationStmt;
import compileError.*;
import compileError.Name.*;
import symbolTable.SymbolTable;
import symbolTable.symbolTableItems.*;
import symbolTable.itemException.ItemAlreadyExistsException;
import visitor.Visitor;

import java.util.ArrayList;

public class NameAnalyzer extends Visitor<Void> {

    public ArrayList<CompileError> nameErrors = new ArrayList<>();

    @Override
    public Void visit(Program program) {
        SymbolTable.root = new SymbolTable();
        SymbolTable.push(SymbolTable.root);

        for (FuncDeclaration functionDeclaration : program.getFuncs()) {
            functionDeclaration.accept(this);
        }

        for (var stmt : program.getMain().getMainStatements()) {
            if (stmt instanceof VarDecStmt
                    || stmt instanceof ArrayDecStmt
                    || stmt instanceof ForloopStmt
                    || stmt instanceof ImplicationStmt) {
                stmt.accept(this);
            }
        }

        return null;
    }

    @Override
    public Void visit(FuncDeclaration funcDeclaration) {
        var functionItem = new FunctionItem(funcDeclaration);

        while (true) {
            try {
                SymbolTable.top.put(functionItem);
                break;
            } catch (ItemAlreadyExistsException e) {
                if (!functionItem.getName().endsWith("@"))
                    nameErrors.add(new FunctionRedefinition(funcDeclaration.getLine(), functionItem.getName()));
                functionItem.setName(functionItem.getName() + "@");
            }
        }

        var functionSymbolTable = new SymbolTable(SymbolTable.top, functionItem.getName());
        functionItem.setFunctionSymbolTable(functionSymbolTable);
        SymbolTable.push(functionSymbolTable);

        for (ArgDeclaration varDeclaration : funcDeclaration.getArgs()) {
            varDeclaration.accept(this);
        }

        for (var stmt : funcDeclaration.getStatements()) {
            if (stmt instanceof VarDecStmt
                    || stmt instanceof ArrayDecStmt
                    || stmt instanceof ForloopStmt
                    || stmt instanceof ImplicationStmt) {
                stmt.accept(this);
            }
        }

        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(VarDecStmt varDeclaration) {
        var variableItem = new VariableItem(varDeclaration);

        while (true) {
            try {
                SymbolTable.top.put(variableItem);
                break;
            } catch (ItemAlreadyExistsException e) {
                if (!variableItem.getName().endsWith("@"))
                    nameErrors.add(new VariableRedefinition(varDeclaration.getLine(), variableItem.getName()));
                variableItem.setName(variableItem.getName() + "@");
            }
        }

        return null;
    }

    @Override
    public Void visit(ArrayDecStmt arrDeclaration) {
        var arrayItem = new ArrayItem(arrDeclaration);

        while (true) {
            try {
                SymbolTable.top.put(arrayItem);
                break;
            } catch (ItemAlreadyExistsException e) {
                if (!arrayItem.getName().endsWith("@"))
                    nameErrors.add(new VariableRedefinition(arrDeclaration.getLine(), arrayItem.getName()));
                arrayItem.setName(arrayItem.getName() + "@");
            }
        }

        return null;
    }

    @Override
    public Void visit(ArgDeclaration argDeclaration) {
        var argItem = new ArgItem(argDeclaration);

        while (true) {
            try {
                SymbolTable.top.put(argItem);
                break;
            } catch (ItemAlreadyExistsException e) {
                if (!argItem.getName().endsWith("@"))
                    nameErrors.add(new VariableRedefinition(argDeclaration.getLine(), argItem.getName()));
                argItem.setName(argItem.getName() + "@");
            }
        }

        return null;
    }

    @Override
    public Void visit(ForloopStmt forloopStmt) {
        var forLoopItem = new ForLoopItem(forloopStmt);

        var forLoopSymbolTable = new SymbolTable(SymbolTable.top, forLoopItem.getName());
        forLoopItem.setForLoopSymbolTable(forLoopSymbolTable);
        SymbolTable.push(forLoopSymbolTable);

        try {
            SymbolTable.top.put(forLoopItem);
        } catch (ItemAlreadyExistsException e) {
            // Will not reach here because SymbolTable.top is empty.
        }

        for (var stmt : forloopStmt.getStatements()) {
            if (stmt instanceof VarDecStmt
                    || stmt instanceof ArrayDecStmt
                    || stmt instanceof ForloopStmt
                    || stmt instanceof ImplicationStmt) {
                stmt.accept(this);
            }
        }

        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(ImplicationStmt implStmt) {
        var implSymbolTable = new SymbolTable(SymbolTable.top, "ImplicationStmt");
        SymbolTable.push(implSymbolTable);

        for (var stmt : implStmt.getStatements()) {
            if (stmt instanceof VarDecStmt
                    || stmt instanceof ArrayDecStmt
                    || stmt instanceof ForloopStmt
                    || stmt instanceof ImplicationStmt) {
                stmt.accept(this);
            }
        }

        SymbolTable.pop();
        return null;
    }
}
