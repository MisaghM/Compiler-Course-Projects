package bytecode;

public class InvokeStatic extends Invoke {
    public InvokeStatic(String className, String methodName, String signature) {
        super(className + "/" + methodName, signature);
        invokeType = "static";
    }
}
