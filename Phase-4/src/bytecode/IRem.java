package bytecode;

public class IRem extends Bytecode {
    @Override
    public String toString() {
        return indent(1) + "irem";
    }
}
