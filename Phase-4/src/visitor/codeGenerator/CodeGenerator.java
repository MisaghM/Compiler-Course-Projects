package visitor.codeGenerator;

import java.util.HashMap;

import visitor.Visitor;
import ast.node.Program;
import ast.node.declaration.FuncDeclaration;
import ast.node.declaration.MainDeclaration;
import ast.node.expression.Expression;
import ast.node.expression.UnaryExpression;
import ast.node.expression.BinaryExpression;
import ast.node.expression.Variable;
import ast.node.expression.Identifier;
import ast.node.expression.values.*;
import ast.node.statement.AssignStmt;
import ast.node.statement.ReturnStmt;
import ast.node.statement.VarDecStmt;
import ast.type.Type;
import ast.type.primitiveType.IntType;
import ast.type.primitiveType.FloatType;
import ast.type.primitiveType.BooleanType;

public class CodeGenerator extends Visitor<String> {
    private final String indent = "    ";

    private final StringBuilder result = new StringBuilder();
    private final HashMap<String, Integer> slots = new HashMap<String, Integer>();

    private Integer slotOf(String var) {
        if (!slots.containsKey(var)) {
            slots.put(var, slots.size());
            return slots.size() - 1;
        }
        return slots.get(var);
    }

    private String startClass(String name) {
        StringBuilder res = new StringBuilder();
        res.append(".class public ").append(name).append('\n');
        res.append(".super java/lang/Object\n\n");
        res.append(".method public <init>()V\n");
        res.append(indent + "aload_0\n");
        res.append(indent + "invokenonvirtual java/lang/Object/<init>()V\n");
        res.append(indent + "return\n");
        res.append(".end method\n");
        return res.toString();
    }

    private String getTypeStr(Type type) {
        if (type instanceof IntType) {
            return "I";
        }
        if (type instanceof FloatType) {
            return "F";
        }
        if (type instanceof BooleanType) {
            return "Z";
        }
        return "V";
    }

    @Override
    public String visit(Program program) {
        result.append(startClass(program.toString())).append('\n');
        result.append(program.getMain().accept(this)).append('\n');
        for (var func : program.getFuncs()) {
            slots.clear();
            result.append(func.accept(this)).append('\n');
        }
        return result.toString();
    }

    @Override
    public String visit(MainDeclaration mainDeclaration) {
        StringBuilder res = new StringBuilder();
        res.append(".method public static main([Ljava/lang/String;)V\n");
        res.append(indent + ".limit stack 32\n");
        res.append(indent + ".limit locals 32\n");

        slotOf("__args");

        for (var stmt : mainDeclaration.getMainStatements()) {
            if (stmt instanceof VarDecStmt ||
                    stmt instanceof AssignStmt ||
                    stmt instanceof ReturnStmt) {
                res.append(indent).append(stmt.accept(this));
            }
        }

        res.append(indent + "return\n");
        res.append(".end method\n");
        return res.toString();
    }

    @Override
    public String visit(FuncDeclaration funcDeclaration) {
        StringBuilder res = new StringBuilder();
        res.append(".method public static ").append(funcDeclaration.getName().getName());

        res.append("(");
        for (var arg : funcDeclaration.getArgs()) {
            res.append(getTypeStr(arg.getType()));
            slotOf(arg.getIdentifier().getName());
        }
        res.append(")");
        res.append(getTypeStr(funcDeclaration.getType())).append('\n');

        res.append(indent + ".limit stack 32\n");
        res.append(indent + ".limit locals 32\n");

        for (var stmt : funcDeclaration.getStatements()) {
            if (stmt instanceof VarDecStmt ||
                    stmt instanceof AssignStmt ||
                    stmt instanceof ReturnStmt) {
                res.append(indent).append(stmt.accept(this));
            }
        }

        res.append(indent + "return\n");
        res.append(".end method\n");
        return res.toString();
    }

    @Override
    public String visit(VarDecStmt varDecStmt) {
        int slot = slotOf(varDecStmt.getIdentifier().getName());
        Expression init = varDecStmt.getInitialExpression();
        Type type = varDecStmt.getType();

        StringBuilder res = new StringBuilder();
        if (type instanceof IntType || type instanceof BooleanType) {
            if (init == null) {
                res.append("iconst_0\n");
            } else {
                res.append(init.accept(this));
            }
            res.append("istore ").append(slot);
        }

        res.append('\n');
        return res.toString();
    }

    @Override
    public String visit(AssignStmt assignStmt) {
        String code = assignStmt.getRValue().accept(this);
        int slot = slotOf(((Variable) assignStmt.getLValue()).getName());
        return code + "istore " + slot + '\n';
    }

    @Override
    public String visit(ReturnStmt returnStmt) {
        String code = returnStmt.getExpression().accept(this);
        return code;
    }

    @Override
    public String visit(BinaryExpression binaryExpression) {
        String leftCode = binaryExpression.getLeft().accept(this);
        String rightCode = binaryExpression.getRight().accept(this);
        String op = "";
        switch (binaryExpression.getBinaryOperator()) {
            case add:
                op = "iadd";
                break;
            case sub:
                op = "isub";
                break;
            case mult:
                op = "imul";
                break;
            case div:
                op = "idiv";
                break;
            case mod:
                op = "imod";
                break;
            default:
                break;
        }
        return leftCode + rightCode + op + '\n';
    }

    @Override
    public String visit(UnaryExpression unaryExpression) {
        String code = unaryExpression.getOperand().accept(this);
        String op = "";
        switch (unaryExpression.getUnaryOperator()) {
            case plus:
                break;
            case minus:
                op = "ineg";
                break;
            case not:
                op = "iconst_1\nixor";
                break;
        }
        return code + op + '\n';
    }

    @Override
    public String visit(Identifier identifier) {
        return "iload " + slotOf(identifier.getName()) + '\n';
    }

    @Override
    public String visit(IntValue intValue) {
        return "ldc " + intValue.getConstant() + '\n';
    }
}
