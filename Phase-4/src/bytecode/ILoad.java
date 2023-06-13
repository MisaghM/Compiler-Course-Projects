package bytecode;

public class ILoad extends Bytecode {
    private final int index;

    public ILoad(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return indent(1) + (index < 4 ? "iload_" + index : "iload " + index);
    }
}
