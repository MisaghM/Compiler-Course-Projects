package visitor.typeAnalyzer;

import ast.node.Program;
import ast.node.declaration.Declaration;
import ast.node.declaration.FuncDeclaration;
import ast.node.declaration.MainDeclaration;
import ast.node.expression.*;
import ast.node.expression.operators.BinaryOperator;
import ast.node.expression.operators.UnaryOperator;
import ast.node.statement.ArrayDecStmt;
import ast.node.statement.AssignStmt;
import ast.node.statement.ForloopStmt;
import ast.node.statement.ImplicationStmt;
import ast.node.statement.PrintStmt;
import ast.node.statement.ReturnStmt;
import ast.node.statement.VarDecStmt;
import ast.type.NoType;
import ast.type.Type;
import ast.type.primitiveType.BooleanType;
import ast.type.primitiveType.IntType;
import compileError.CompileError;
import compileError.Type.FunctionNotDeclared;
import compileError.Type.LeftSideNotLValue;
import compileError.Type.UnsupportedOperandType;
import compileError.Type.ConditionTypeNotBool;
import symbolTable.SymbolTable;
import symbolTable.itemException.ItemNotFoundException;
import symbolTable.symbolTableItems.ForLoopItem;
import symbolTable.symbolTableItems.FunctionItem;
import symbolTable.symbolTableItems.ImplicationItem;
import symbolTable.symbolTableItems.MainItem;
import visitor.Visitor;

import java.util.ArrayList;

public class TypeAnalyzer extends Visitor<Void> {
    public ArrayList<CompileError> typeErrors = new ArrayList<>();
    ExpressionTypeChecker expressionTypeChecker = new ExpressionTypeChecker(typeErrors);

    @Override
    public Void visit(Program program) {
        for (var functionDec : program.getFuncs()) {
            functionDec.accept(this);
        }

        program.getMain().accept(this);
        return null;
    }

    @Override
    public Void visit(FuncDeclaration funcDeclaration) {
        try {
            FunctionItem functionItem = (FunctionItem) SymbolTable.root
                    .get(FunctionItem.STARTKEY + funcDeclaration.getName().getName());
            SymbolTable.push(functionItem.getFunctionSymbolTable());
        } catch (ItemNotFoundException e) {
            // unreachable
        }

        for (var stmt : funcDeclaration.getStatements()) {
            stmt.accept(this);
        }

        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(MainDeclaration mainDeclaration) {
        var mainItem = new MainItem(mainDeclaration);
        var mainSymbolTable = new SymbolTable(SymbolTable.top, "main");
        mainItem.setMainItemSymbolTable(mainSymbolTable);

        SymbolTable.push(mainItem.getMainItemSymbolTable());

        for (var stmt : mainDeclaration.getMainStatements()) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ForloopStmt forloopStmt) {
        try {
            ForLoopItem forLoopItem = (ForLoopItem) SymbolTable.top
                    .get(ForLoopItem.STARTKEY + forloopStmt.toString() + forloopStmt.getForloopId());
            SymbolTable.push(forLoopItem.getForLoopSymbolTable());
        } catch (ItemNotFoundException e) {
            // unreachable
        }

        forloopStmt.getArrayName().accept(expressionTypeChecker);
        for (var stmt : forloopStmt.getStatements()) {
            stmt.accept(this);
        }

        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(AssignStmt assignStmt) {
        Type tl = assignStmt.getLValue().accept(expressionTypeChecker);
        Type tr = assignStmt.getRValue().accept(expressionTypeChecker);

        if (!expressionTypeChecker.sameType(tl, tr) && !(tl instanceof NoType) && !(tr instanceof NoType)) {
            typeErrors.add(new UnsupportedOperandType(assignStmt.getLine(), BinaryOperator.assign.name()));
        }

        if (!expressionTypeChecker.isLvalue(assignStmt.getLValue())) {
            // is handled in grammar
            typeErrors.add(new LeftSideNotLValue(assignStmt.getLine()));
        }
        return null;
    }

    @Override
    public Void visit(FunctionCall functionCall) {
        try {
            SymbolTable.root.get(FunctionItem.STARTKEY + functionCall.getFuncName().getName());
        } catch (ItemNotFoundException e) {
            // unreachable
        }

        functionCall.accept(expressionTypeChecker);
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt) {
        var retExpr = returnStmt.getExpression();
        if (retExpr != null) {
            retExpr.accept(expressionTypeChecker);
        }
        return null;
    }

    @Override
    public Void visit(PrintStmt printStmt) {
        var printExpr = printStmt.getArg();
        if (printExpr != null) {
            printExpr.accept(expressionTypeChecker);
        }
        return null;
    }

    @Override
    public Void visit(ImplicationStmt implicationStmt) {
        try {
            ImplicationItem implicationItem = (ImplicationItem) SymbolTable.top
                    .get(ImplicationItem.STARTKEY + implicationStmt.toString() + implicationStmt.getImplicationId());
            SymbolTable.push(implicationItem.getImplicationSymbolTable());
        } catch (ItemNotFoundException e) {
            // unreachable
        }

        Type tl = implicationStmt.getCondition().accept(expressionTypeChecker);
        if (!(tl instanceof BooleanType) && !(tl instanceof NoType)) {
            typeErrors.add(new ConditionTypeNotBool(implicationStmt.getLine()));
        }

        for (var stmt : implicationStmt.getStatements()) {
            stmt.accept(this);
        }

        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(VarDecStmt vardecStmt) {
        if (vardecStmt.getInitialExpression() == null) {
            return null;
        }
        Type t1 = vardecStmt.getInitialExpression().accept(expressionTypeChecker);
        Type t2 = vardecStmt.getType();
        if (!expressionTypeChecker.sameType(t1, t2) && !(t1 instanceof NoType) && !(t2 instanceof NoType)) {
            typeErrors.add(new UnsupportedOperandType(vardecStmt.getLine(), BinaryOperator.assign.name()));
        }
        return null;
    }

    @Override
    public Void visit(ArrayDecStmt arrDecStmt) {
        if (arrDecStmt.getInitialValues().isEmpty()) {
            return null;
        }

        boolean hasError = false;
        for (var item : arrDecStmt.getInitialValues()) {
            Type t1 = item.accept(expressionTypeChecker);
            Type t2 = arrDecStmt.getType();
            if (!expressionTypeChecker.sameType(t1, t2) && !(t1 instanceof NoType) && !(t2 instanceof NoType)) {
                hasError = true;
            }
        }

        if (hasError) {
            typeErrors.add(new UnsupportedOperandType(arrDecStmt.getLine(), BinaryOperator.assign.name()));
        }
        return null;
    }
}
