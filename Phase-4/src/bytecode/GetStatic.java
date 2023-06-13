package bytecode;

public class GetStatic extends Bytecode {
    public final String className;
    public final String fieldName;
    public final String fieldType;

    public GetStatic(String className, String fieldName, String fieldType) {
        this.className = className;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    @Override
    public String toString() {
        return indent(1) + "getstatic " + className + "/" + fieldName + " " + fieldType;
    }
}
