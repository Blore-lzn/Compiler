package ir.types;

public class FloatingType implements Type {
    public static final FloatingType type = new FloatingType();
    
    private FloatingType() {
    }
    
    @Override
    public String toString() {
        return "float";
    }
}
