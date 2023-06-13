package bytecode;

public class InvokeVirtual extends Invoke {
    public InvokeVirtual(String className, String methodName, String methodSignature) {
        super(className + "/" + methodName, methodSignature);
        this.invokeType = "virtual";
    }
}
