package bytecode;

import java.util.List;

public class JasminMethod extends Bytecode {
    private final String name;
    private final String returnType;
    private final List<String> args;
    private final List<Bytecode> body;
    protected boolean isStatic = true;

    private Integer stackSize = 1;
    private Integer localSize = 1;

    public JasminMethod(String name, String returnType, List<String> args, List<Bytecode> body) {
        this.name = name;
        this.returnType = returnType;
        this.args = args;
        this.body = body;
    }

    public void setStackSize(Integer stackSize) {
        this.stackSize = stackSize;
    }

    public void setLocalSize(Integer localSize) {
        this.localSize = localSize;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        StringBuilder res = new StringBuilder();
        res.append('(');
        for (String arg : args) {
            res.append(arg);
        }
        res.append(')').append(returnType);
        return res.toString();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(".method public ");
        if (isStatic) {
            res.append("static ");
        }
        res.append(name).append('(');
        for (String arg : args) {
            res.append(arg);
        }
        res.append(')').append(returnType).append('\n');
        res.append(".limit stack ").append(stackSize).append('\n');
        res.append(".limit locals ").append(localSize).append('\n');
        for (Bytecode bytecode : body) {
            res.append(bytecode.toString()).append('\n');
        }
        res.append(".end method\n\n");
        return res.toString();
    }
}
