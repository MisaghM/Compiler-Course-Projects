package bytecode;

public class IReturn extends Bytecode {
    @Override
    public String toString() {
        return indent(1) + "ireturn";
    }
}
