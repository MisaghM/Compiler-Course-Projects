package bytecode;

public class IDiv extends Bytecode {
    @Override
    public String toString() {
        return indent(1) + "idiv";
    }
}
