package ir.values;

import ir.types.IntegerType;
import ir.types.Type;

public class ConstantInteger extends Constant {
    private int value;
    public static ConstantInteger const0 = new ConstantInteger(0);
    
    public ConstantInteger(int value) {
        super(String.valueOf(value), IntegerType.i32);
        this.value = value;
    }
    
    public ConstantInteger(int value, Type type) {
        super(String.valueOf(value), type);
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return type + " " + this.value;
    }
}
