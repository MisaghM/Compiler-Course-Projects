package symbolTable.symbolTableItems;

import ast.node.statement.ForloopStmt;
import ast.type.Type;
import symbolTable.SymbolTable;

import java.util.ArrayList;

public class ForLoopItem extends SymbolTableItem {

    protected Type identifierType;
    protected Type arrayType;
    protected SymbolTable forLoopSymbolTable;
    protected ForloopStmt forLoopStmt;
    public static final String STARTKEY = "Variable_";

    public ForLoopItem(String name, Type identifierType, Type arrayType) {
        this.name = name;
        this.identifierType = identifierType;
        this.arrayType = arrayType;
    }

    public ForLoopItem(ForloopStmt forloopStmt) {
        this.name = forloopStmt.getIterator().getName();
        this.identifierType = forloopStmt.getIterator().getType();
        this.arrayType = forloopStmt.getArrayName().getType();
        this.forLoopStmt = forloopStmt;
    }

    public void setForLoopSymbolTable(SymbolTable symbolTable) {
        this.forLoopSymbolTable = symbolTable;
    }

    public SymbolTable getForLoopSymbolTable() {
        return this.forLoopSymbolTable;
    }

    public void setForLoopStmt(ForloopStmt forLoopStmt) {
        this.forLoopStmt = forLoopStmt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        forLoopStmt.getIterator().setName(name);
    }

    public ForloopStmt getHandlerStmt() {
        return forLoopStmt;
    }

    @Override
    public String getKey() {
        return ForLoopItem.STARTKEY + this.name;
    }
}
