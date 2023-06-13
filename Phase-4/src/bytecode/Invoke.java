package bytecode;

public class Invoke extends Bytecode {
    protected String invokeType = "static";
    private final String name;
    private final String signature;

    public Invoke(String name, String signature) {
        this.name = name;
        this.signature = signature;
    }

    @Override
    public String toString() {
        return indent(1) + "invoke" + invokeType + " " + name + signature;
    }
}
