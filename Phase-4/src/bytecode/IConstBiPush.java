package bytecode;

public class IConstBiPush extends Bytecode {
    private final int value;

    public IConstBiPush(int value) {
        this.value = value;
    }

    public String toString() {
        return indent(1) + (value < 6 ? "iconst_" + value : "bipush " + value);
    }
}
