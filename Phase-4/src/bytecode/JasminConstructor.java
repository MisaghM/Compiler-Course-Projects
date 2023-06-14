package bytecode;

import java.util.List;

public class JasminConstructor extends JasminMethod {
    public JasminConstructor(String superClass) {
        super("<init>",
                "V",
                List.of(),
                List.of(new ALoad(0),
                        new InvokeNonVirtual(superClass, "<init>", "()V"),
                        new Return()));
        isStatic = false;
    }
}
