package bytecode;

public class InvokeNonVirtual extends Invoke {
    public InvokeNonVirtual(String className, String methodName, String methodSignature) {
        super(className + "/" + methodName, methodSignature);
        this.invokeType = "nonvirtual";
    }
}
