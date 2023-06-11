package visitor.typeAnalyzer;

import ast.node.declaration.ArgDeclaration;
import ast.node.expression.*;
import ast.node.expression.operators.BinaryOperator;
import ast.node.expression.operators.UnaryOperator;
import ast.node.expression.values.*;
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
import symbolTable.itemException.ItemAlreadyExistsException;
import symbolTable.itemException.ItemNotFoundException;
import symbolTable.symbolTableItems.ArrayItem;
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
        else if (expType instanceof FloatType) {
            return new FloatType();
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
                if (tl instanceof FloatType && tr instanceof FloatType) {
                    return new FloatType();
                }
                if ((tl instanceof NoType && (tr instanceof IntType || tr instanceof FloatType)) ||
                    (tr instanceof NoType && (tl instanceof IntType || tl instanceof FloatType))) {
                    return new NoType();
                }
            }
            case eq, neq, gt, lt, gte, lte -> {
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
                if ((tl instanceof NoType && tr instanceof BooleanType) ||
                    (tr instanceof NoType && tl instanceof BooleanType)) {
                    return new NoType();
                }
            }
        }

        typeErrors.add(new UnsupportedOperandType(binaryExpression.getLine(), operator.name()));
        return new NoType();
    }

    @Override
    public Type visit(Identifier identifier) {
        try {
            VariableItem var = (VariableItem) SymbolTable.top.get(VariableItem.STARTKEY + identifier.getName());
            return var.getType();
        } catch (ItemNotFoundException e) {
            typeErrors.add(new VarNotDeclared(identifier.getLine(), identifier.getName()));
            VariableItem vi = new VariableItem(identifier.getName(), new NoType());
            try {
                SymbolTable.top.put(vi);
            }
            catch (ItemAlreadyExistsException ee) {
                // unreachable?
            }
            return new NoType();
        }
    }

    @Override
    public Type visit(ArrayAccess arrayAccess) {
        try {
            VariableItem var = (VariableItem) SymbolTable.top.get(VariableItem.STARTKEY + arrayAccess.getName());
            if (arrayAccess.getIndex().accept(this) instanceof IntType) {
                return var.getType();
            }
            // typeErrors.add(new UnsupportedOperandType(arrayAccess.getLine(), "[]"));
            return new NoType();
        } catch (ItemNotFoundException e) {
            typeErrors.add(new VarNotDeclared(arrayAccess.getLine(), arrayAccess.getName()));
            VariableItem aa = new VariableItem(arrayAccess.getName(), new NoType());
            try {
                SymbolTable.top.put(aa);
            }
            catch (ItemAlreadyExistsException ee) {
                // unreachable
            }
            return new NoType();
        }
    }

    @Override
    public Type visit(FunctionCall functionCall) {
        try {
            FunctionItem func = (FunctionItem) SymbolTable.top.get(FunctionItem.STARTKEY + functionCall.getFuncName().getName());
            ArrayList<Expression> args = functionCall.getArgs();
            if (func.getHandlerDeclaration() == null) {
                return new NoType();
            }
            ArrayList<ArgDeclaration> argTypes = func.getHandlerDeclaration().getArgs();
            if (args.size() != argTypes.size()) {
            // typeErrors.add(new FunctionNotDeclared(functionCall.getLine(), functionCall.getFuncName().getName()));
            }
            for (int i = 0; i < args.size(); i++) {
                Type argType = args.get(i).accept(this);
                Type argType2 = argTypes.get(i).getType();
                if (!sameType(argType, argType2)) {
                    // typeErrors.add(new FunctionNotDeclared(functionCall.getLine(), functionCall.getFuncName().getName()));
                }
            }
            return func.getHandlerDeclaration().getType();
        } catch (ItemNotFoundException e) {
            typeErrors.add(new FunctionNotDeclared(functionCall.getLine(), functionCall.getFuncName().getName()));
            ArrayList<Type> arrl = new ArrayList<>();
            for (var item : functionCall.getArgs()) {
                arrl.add(item.getType());
            }
            FunctionItem vi = new FunctionItem(functionCall.getFuncName().getName(), arrl);
            try {
                SymbolTable.top.put(vi);
            }
            catch (ItemAlreadyExistsException ee) {
                // unreachable
            }
            return new NoType();
        }
    }

    @Override
    public Type visit(QueryExpression queryExpression) {
        var item = queryExpression.getVar();
        if (item != null) {
            item.accept(this);
            return new BooleanType();
        }
        else {
            return new NoType();
        }
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
