package visitor.typeAnalyzer;

import ast.node.Program;
import ast.node.declaration.Declaration;
import ast.node.declaration.FuncDeclaration;
import ast.node.declaration.MainDeclaration;
import ast.node.expression.Expression;
import ast.node.expression.FunctionCall;
import ast.node.expression.Identifier;
import ast.node.expression.operators.BinaryOperator;
import ast.node.statement.*;
import ast.type.NoType;
import ast.type.Type;
import compileError.CompileError;
import compileError.Type.FunctionNotDeclared;
import compileError.Type.LeftSideNotLValue;
import compileError.Type.UnsupportedOperandType;
import compileError.Type.ConditionTypeNotBool;
import symbolTable.SymbolTable;
import symbolTable.itemException.ItemNotFoundException;
import symbolTable.symbolTableItems.ForLoopItem;
import symbolTable.symbolTableItems.FunctionItem;
import symbolTable.symbolTableItems.MainItem;
import visitor.Visitor;

import java.util.ArrayList;

public class TypeAnalyzer extends Visitor<Void> {
    public ArrayList<CompileError> typeErrors = new ArrayList<>();
    ExpressionTypeChecker expressionTypeChecker = new ExpressionTypeChecker(typeErrors);

    @Override
    public Void visit(Program program) {
        for(var functionDec : program.getFuncs()) {
            functionDec.accept(this);
        }

        program.getMain().accept(this);

        return null;
    }

    @Override
    public Void visit(FuncDeclaration funcDeclaration) {
        try {
            FunctionItem functionItem = (FunctionItem)  SymbolTable.root.get(FunctionItem.STARTKEY + funcDeclaration.getName().getName());
            SymbolTable.push((functionItem.getFunctionSymbolTable()));
        } catch (ItemNotFoundException e) {
            //unreachable
        }

        for(var stmt : funcDeclaration.getStatements()) {
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
    public Void visit(AssignStmt assignStmt) {
        Type tl = assignStmt.getLValue().accept(expressionTypeChecker);
        Type tr = assignStmt.getRValue().accept(expressionTypeChecker);



        if(!(assignStmt.getLValue() instanceof Identifier)) {
            typeErrors.add(new LeftSideNotLValue(assignStmt.getLine()));
        } else  if(tl.toString() != tr.toString()){
            if(tr instanceof NoType){
                // Do nothing
            }
            else{
                typeErrors.add(new UnsupportedOperandType(assignStmt.getLine(), BinaryOperator.assign.name()));
            }
        }

        return null;
    }

    @Override
    public Void visit(FunctionCall functionCall) {
        try {
                SymbolTable.root.get(FunctionItem.STARTKEY + functionCall.getUFuncName().getName());
        }
        catch (ItemNotFoundException e) {
            typeErrors.add(new FunctionNotDeclared(functionCall.getLine(), functionCall.getUFuncName().getName()));

            return null;
        }

        for (Expression arg: functionCall.getArgs())
            arg.accept(expressionTypeChecker);

        return null;
    }

    @Override
    public Void visit(ArrayDecStmt arrayDecStmt) {
        for (var value : arrayDecStmt.getInitialValues()) {
            var type = value.accept(expressionTypeChecker);
            if(type.toString() != arrayDecStmt.getType().toString() && type.toString() != "noType") {
                typeErrors.add(new UnsupportedOperandType(value.getLine(), BinaryOperator.assign.name()));
            }
        }

        return null;
    }

    @Override
    public Void visit(ImplicationStmt implicationStmt) {
        Type cond = implicationStmt.getCondition().accept(expressionTypeChecker);
        if ( cond.toString() != "boolean" && cond.toString() != "noType")
        {
            typeErrors.add(new ConditionTypeNotBool(implicationStmt.getLine()));
        }

        for(var stmt : implicationStmt.getStatements()) {
            stmt.accept(this);
        }

        return null;
    }

    @Override
    public Void visit(ForloopStmt forloopStmt) {
        forloopStmt.getIterator().accept(this);
        for (var stmt: forloopStmt.getStatements()) {
            stmt.accept(this);
        }

        return null;
    }

    @Override
    public Void visit(VarDecStmt varDecStmt) {
        var type = varDecStmt.getInitialExpression().accept(expressionTypeChecker);
        if(type.toString() != varDecStmt.getType().toString() && type.toString() != "noType") {
            typeErrors.add(new UnsupportedOperandType(varDecStmt.getLine(), BinaryOperator.eq.name()));
        }
        return null;
    }
}
