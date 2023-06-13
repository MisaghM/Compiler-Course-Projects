package bytecode;

public class Return extends Bytecode {
    @Override
    public String toString() {
        return indent(1) + "return";
    }
}
