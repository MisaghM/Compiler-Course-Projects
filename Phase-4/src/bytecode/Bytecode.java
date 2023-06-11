package bytecode;

public class Bytecode {
    public static String indent(int level) {
        return "    ".repeat(Math.max(0, level));
    }
}
