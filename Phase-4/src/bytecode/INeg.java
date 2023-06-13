package bytecode;

public class INeg extends Bytecode {
    @Override
    public String toString() {
        return indent(1) + "ineg";
    }
}
