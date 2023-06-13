package bytecode;

public class IStore extends Bytecode {
    private final int index;

    public IStore(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return indent(1) + (index < 4 ? "istore_" + index : "istore " + index);
    }
}
