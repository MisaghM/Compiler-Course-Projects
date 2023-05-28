package visitor.typeAnalyzer;

import ast.node.declaration.ArgDeclaration;
import ast.node.expression.*;
import ast.node.expression.operators.BinaryOperator;
import ast.node.expression.operators.UnaryOperator;
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

    public ExpressionTypeChecker(ArrayList<CompileError> typeErrors) {
        this.typeErrors = typeErrors;
    }

    public boolean sameType(Type el1, Type el2) {
        return el1.getClass().equals(el2.getClass()) && !(el1 instanceof NoType || el2 instanceof NoType);
    }

    public boolean isLvalue(Expression expr) {
        return expr instanceof Variable;
    }


    @Override
    public Type visit(UnaryExpression unaryExpression) {

        Expression uExpr = unaryExpression.getOperand();
        Type expType = uExpr.accept(this);
        UnaryOperator operator = unaryExpression.getUnaryOperator();

        if (operator.equals(UnaryOperator.not)) {
            if (expType instanceof BooleanType) {
                return new BooleanType();
            }
        }
        else if (expType instanceof IntType) {
            return new IntType();
        }

        if (!(expType instanceof NoType)) {
            typeErrors.add(new UnsupportedOperandType(unaryExpression.getLine(), operator.name()));
        }
        return new NoType();
    }

    @Override
    public Type visit(BinaryExpression binaryExpression) {
        Type tl = binaryExpression.getLeft().accept(this);
        Type tr = binaryExpression.getRight().accept(this);
        BinaryOperator operator = binaryExpression.getBinaryOperator();

        if (tl instanceof NoType && tr instanceof NoType) {
            return new NoType();
        }

        switch (operator) {
            case add, sub, mult, div, mod -> {
                if (tl instanceof IntType && tr instanceof IntType) {
                    return new IntType();
                }
                if (tl instanceof NoType && tr instanceof IntType || tl instanceof IntType && tr instanceof NoType) {
                    return new NoType();
                }
            }
            case gt, lt, gte, lte -> { // TODO: The fuck vaghean
                if (sameType(tl, tr)) {
                    return new BooleanType();
                }
                if (tl instanceof NoType || tr instanceof NoType) {
                    return new NoType();
                }
            }
            case and, or -> {
                if (tl instanceof BooleanType && tr instanceof BooleanType) {
                    return new BooleanType();
                }
                if (tl instanceof NoType && tr instanceof BooleanType || tl instanceof BooleanType && tr instanceof NoType) {
                    return new NoType();
                }
            }
            case eq, neq -> {
                if (sameType(tl, tr)) {
                    return new BooleanType();
                }
                if (tl instanceof NoType || tr instanceof NoType)
                    return new NoType();
            }
        }

        typeErrors.add(new UnsupportedOperandType(binaryExpression.getLine(), operator.name()));
        return new NoType();
    }

    @Override
    public Type visit(Identifier identifier) {
        try {
            VariableItem var = (VariableItem) SymbolTable.root.get(VariableItem.STARTKEY + identifier.getName());
            return var.getType();
        } catch (ItemNotFoundException e) {
            typeErrors.add(new VarNotDeclared(identifier.getLine(), identifier.getName()));
            return new NoType();
        }
    }

    @Override
    public Type visit(FunctionCall functionCall) {
        try {
            FunctionItem func = (FunctionItem) SymbolTable.root.get(FunctionItem.STARTKEY + functionCall.getFuncName().getName());
            ArrayList<Expression> args = functionCall.getArgs();
            ArrayList<ArgDeclaration> argTypes = func.getHandlerDeclaration().getArgs();
            if (args.size() != argTypes.size()) {
//                typeErrors.add(new FunctionNotDeclared(functionCall.getLine(), functionCall.getFuncName().getName()));
            }
            for (int i = 0; i < args.size(); i++) {
                Type argType = args.get(i).accept(this);
                Type argType2 = argTypes.get(i).getType();
                if (!sameType(argType, argType2)) {
//                    typeErrors.add(new FunctionNotDeclared(functionCall.getLine(), functionCall.getFuncName().getName()));
                }
            }
            return func.getHandlerDeclaration().getType();
        } catch (ItemNotFoundException e) {
            typeErrors.add(new FunctionNotDeclared(functionCall.getLine(), functionCall.getFuncName().getName()));
            return new NoType();
        }
    }

    @Override
    public Type visit(IntValue value) {
        return new IntType();
    }

    @Override
    public Type visit(FloatType value) {
        return new FloatType();
    }

    @Override
    public Type visit(BooleanType value) {
        return new BooleanType();
    }
}
