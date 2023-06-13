package bytecode;

public class IConst extends Bytecode {
    private final int value;

    public IConst(int value) {
        this.value = value;
    }

    private static String getBytecode(int value) {
        String res = "";
        int bits = 32 - Integer.numberOfLeadingZeros(value);
        if (value >= 0 && value <= 5) {
            res = "iconst_" + value;
        }
        else if (value == -1) {
            res = "iconst_m1";
        }
        else if (bits <= 8) {
            res = "bipush " + value;
        }
        else if (bits <= 16) {
            res = "sipush " + value;
        }
        else {
            res = "ldc " + value;
        }
        return res;
    }

    @Override
    public String toString() {
        return indent(1) + getBytecode(value);
    }
}
