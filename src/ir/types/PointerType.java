package ir.types;

public class PointerType implements Type {
    private final Type target;
    public static final PointerType i32ptrType = new PointerType(IntegerType.i32);
    
    public PointerType(Type target) {
        this.target = target;
    }
    
    public Type getTarget() {
        return target;
    }
    
    @Override
    public String toString() {
        return target.toString() + "* ";
    }
}
