package ir.types;

public class VoidType implements Type {
    public static final VoidType type = new VoidType();
    
    private VoidType() {
    }
    
    @Override
    public String toString() {
        return "void";
    }
}
