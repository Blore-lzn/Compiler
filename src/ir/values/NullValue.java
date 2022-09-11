package ir.values;

import ir.types.Type;
import ir.types.VoidType;

public class NullValue extends Value {
    public static NullValue value = new NullValue();
    
    private NullValue() {
        super("Null", VoidType.type);
    }
    
    @Override
    public String toString() {
        return "null";
    }
}
