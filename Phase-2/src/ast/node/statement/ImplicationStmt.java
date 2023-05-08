package ast.node.statement;

import ast.node.expression.Expression;
import ast.node.expression.Identifier;
import ast.node.expression.Variable;
import visitor.IVisitor;

import java.util.ArrayList;

//Line -> ARROW
public class ImplicationStmt extends Statement {
    private Expression condition;
    private ArrayList<Statement> resultStmts =  new ArrayList<>();

    public ImplicationStmt(Expression condition, ArrayList<Statement> resultStmts) {
        this.condition = condition;
        this.resultStmts = resultStmts;
    }

    public void setStatements(ArrayList<Statement> statements) {
        this.resultStmts = statements;
    }
    public ArrayList<Statement> getStatements() {
        return resultStmts;
    }
    public void addStatements(Statement statement) {
        resultStmts.add(statement);
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "ImplicationStmt";
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
