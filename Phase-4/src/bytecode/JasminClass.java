package bytecode;

import java.util.ArrayList;
import java.util.List;

public class JasminClass extends Bytecode {
    List<JasminMethod> methods = new ArrayList<>();
    String superClass = "java/lang/Object";
    String name;

    public JasminClass(String name, String superClass) {
        this.name = name;
        this.superClass = superClass;
        methods.add(new JasminConstructor(superClass));
    }

    public JasminClass(String name) {
        this.name = name;
        methods.add(new JasminConstructor(superClass));
    }

    public JasminMethod getMethod(String name) {
        for (JasminMethod method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void addMethod(Bytecode method) {
        methods.add((JasminMethod) method);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(".class public ").append(name).append('\n');
        res.append(".super ").append(superClass).append("\n\n");
        for (Bytecode method : methods) {
            res.append(method.toString());
        }
        return res.toString();
    }
}
