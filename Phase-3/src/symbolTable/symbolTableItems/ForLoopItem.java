package symbolTable.symbolTableItems;

import ast.node.statement.ForloopStmt;
import symbolTable.SymbolTable;

public class ForLoopItem extends SymbolTableItem {
    protected SymbolTable forLoopSymbolTable;
    protected ForloopStmt forloopStmt;

    public static final String STARTKEY = "ForLoop_";
    private static int counter = 0;
    private int id;

    public ForLoopItem(ForloopStmt forloopStmt) {
        this.id = counter++;
        forloopStmt.setForloopId(id);
        this.name = forloopStmt.toString();
        this.forloopStmt = forloopStmt;
    }

    public SymbolTable getForLoopSymbolTable() {
        return this.forLoopSymbolTable;
    }

    public void setForLoopSymbolTable(SymbolTable symbolTable) {
        this.forLoopSymbolTable = symbolTable;
    }

    @Override
    public String getKey() {
        return ForLoopItem.STARTKEY + this.name + this.id;
    }
}
