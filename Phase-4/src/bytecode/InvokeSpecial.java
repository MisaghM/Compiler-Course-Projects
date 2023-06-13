package bytecode;

public class InvokeSpecial extends Invoke {
    public InvokeSpecial(String className, String methodName, String signature) {
        super(className + "/" + methodName, signature);
        invokeType = "special";
    }
}
