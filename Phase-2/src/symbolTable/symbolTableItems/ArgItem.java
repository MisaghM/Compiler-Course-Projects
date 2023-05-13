package symbolTable.symbolTableItems;

import ast.node.declaration.ArgDeclaration;
import ast.type.Type;

public class ArgItem extends SymbolTableItem {

    protected Type type;
    protected ArgDeclaration argDeclaration;
    public static final String STARTKEY = "Variable_";

    public ArgItem(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public ArgItem(ArgDeclaration argDeclaration)
    {
        this.name = argDeclaration.getIdentifier().getName();
        this.type = argDeclaration.getType();
        this.argDeclaration = argDeclaration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        argDeclaration.getIdentifier().setName(name);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ArgDeclaration getVarDeclaration()
    {
        return argDeclaration;
    }

    public void setVarDeclaration(ArgDeclaration argDeclaration)
    {
        this.argDeclaration = argDeclaration;
    }

    @Override
    public String getKey() {
        return ArgItem.STARTKEY + this.name;
    }
}