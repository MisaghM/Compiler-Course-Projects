package bytecode;

public class IAdd extends Bytecode {
    @Override
    public String toString() {
        return indent(1) + "iadd";
    }
}
