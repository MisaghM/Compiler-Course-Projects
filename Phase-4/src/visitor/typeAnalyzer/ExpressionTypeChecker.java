package visitor.typeAnalyzer;

import ast.node.expression.*;
import ast.node.expression.operators.BinaryOperator;
import ast.node.expression.operators.UnaryOperator;
import ast.node.expression.values.BooleanValue;
import ast.node.expression.values.FloatValue;
import ast.node.expression.values.IntValue;
import ast.type.NoType;
import ast.type.Type;
import ast.type.primitiveType.BooleanType;
import ast.type.primitiveType.FloatType;
import ast.type.primitiveType.IntType;
import compileError.CompileError;
import compileError.Type.FunctionNotDeclared;
import compileError.Type.UnsupportedOperandType;
import compileError.Type.VarNotDeclared;
import symbolTable.SymbolTable;
import symbolTable.itemException.ItemNotFoundException;
import symbolTable.symbolTableItems.FunctionItem;
import symbolTable.symbolTableItems.VariableItem;
import visitor.Visitor;

import java.util.ArrayList;

public class ExpressionTypeChecker extends Visitor<Type> {
    public ArrayList<CompileError> typeErrors;
    public ExpressionTypeChecker(ArrayList<CompileError> typeErrors){
        this.typeErrors = typeErrors;
    }

    public boolean sameType(Type el1, Type el2){
        //TODO check the two type are same or not

        if ((el1 instanceof IntType && el2 instanceof IntType) || (el1 instanceof NoType && el2 instanceof IntType) || (el1 instanceof IntType && el2 instanceof NoType))
            return true;
        if ((el1 instanceof BooleanType && el2 instanceof BooleanType) || (el1 instanceof NoType && el2 instanceof BooleanType) || (el1 instanceof BooleanType && el2 instanceof NoType))
            return true;
        if ((el1 instanceof FloatType && el2 instanceof FloatType) || (el1 instanceof NoType && el2 instanceof FloatType) || (el1 instanceof FloatType&& el2 instanceof NoType))
            return true;

        return false;
    }

    public boolean isLvalue(Expression expr){
        //TODO check the expr are lvalue or not

        if (expr instanceof Identifier)
            return true;

        if (expr instanceof ArrayAccess)
            return true;

        return false;
    }


    @Override
    public Type visit(UnaryExpression unaryExpression) {

        Expression uExpr = unaryExpression.getOperand();
        Type expType = uExpr.accept(this);
        UnaryOperator operator = unaryExpression.getUnaryOperator();

        //TODO check errors and return the type
        if(expType instanceof IntType) {
            return new IntType();
        }
        else if(expType instanceof BooleanType) {
            return new BooleanType();
        }
        else if(expType instanceof  FloatType) {
            return new FloatType();
        }
        else {
            typeErrors.add(new UnsupportedOperandType(unaryExpression.getLine(), operator.name()));
            return new NoType();
        }
    }

    @Override
    public Type visit(BinaryExpression binaryExpression) {
        Type tl = binaryExpression.getLeft().accept(this);
        Type tr = binaryExpression.getRight().accept(this);
        BinaryOperator operator =  binaryExpression.getBinaryOperator();

        if(operator.equals(BinaryOperator.eq) || operator.equals(BinaryOperator.or) || operator.equals(BinaryOperator.gt) || operator.equals(BinaryOperator.and) || operator.equals(BinaryOperator.gte) || operator.equals(BinaryOperator.lt) || operator.equals(BinaryOperator.lte) || operator.equals((BinaryOperator.neq))) {

            if(sameType(tl,tr))
                return new BooleanType();
            else {
                typeErrors.add(new UnsupportedOperandType(binaryExpression.getLine(), operator.name()));
                return new NoType();
            }
        }

        else { // + -  *

            if(sameType(tl,tr) && (tl instanceof IntType || tr instanceof IntType || (tl instanceof NoType && tr instanceof NoType)))
                return new IntType();
            else if(sameType(tl,tr) && (tl instanceof FloatType || tr instanceof FloatType || (tl instanceof NoType && tr instanceof NoType)))
                return new FloatType();
            else {
                typeErrors.add(new UnsupportedOperandType(binaryExpression.getLine(), operator.name()));
                return new NoType();
            }
        }
    }

    @Override
    public Type visit(Identifier identifier) {
        try {

            var varDec = (VariableItem) SymbolTable.top.get(VariableItem.STARTKEY + identifier.getName());
            return varDec.getType();
        }
        catch (ItemNotFoundException e){
            typeErrors.add(new VarNotDeclared(identifier.getLine(), identifier.getName()));
            return new NoType();
        }
    }

    @Override
    public Type visit(ArrayAccess arrayAccess) {
        try {

            var varDec = (VariableItem) SymbolTable.top.get(VariableItem.STARTKEY + arrayAccess.getName());
            return varDec.getType();
        }
        catch (ItemNotFoundException e){
            typeErrors.add(new VarNotDeclared(arrayAccess.getLine(), arrayAccess.getName()));
            return new NoType();
        }
    }

    @Override
    public Type visit(FunctionCall functionCall) {
        try {
            var functionItem = (FunctionItem) SymbolTable.root.get(FunctionItem.STARTKEY + functionCall.getUFuncName().getName());

            for(Expression arg: functionCall.getArgs())
                arg.accept(this);

            return functionItem.getHandlerDeclaration().getType();
        }
        catch (ItemNotFoundException e){
            typeErrors.add(new FunctionNotDeclared(functionCall.getLine(), functionCall.getUFuncName().getName()));
            return new NoType();
        }


    }

    @Override
    public Type visit(QueryExpression queryExpression) {
        return new BooleanType();
    }

    @Override
    public Type visit(IntValue value) {
        return new IntType();
    }

    @Override
    public Type visit(FloatValue value) {
        return new FloatType();
    }

    @Override
    public Type visit(BooleanValue value) {
        return new BooleanType();
    }
}
