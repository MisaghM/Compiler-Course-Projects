package bytecode;

public class ALoad extends Bytecode {
    private final Integer index;

    public ALoad(Integer index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return indent(1) + (index < 4 ? "aload_" : "aload ") + index;
    }
}
