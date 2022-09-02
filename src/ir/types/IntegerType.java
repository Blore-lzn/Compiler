package ir.types;

public class IntegerType implements Type{
    private final int bit;
    
    private static final IntegerType i1 = new IntegerType(1);
    
    private static final IntegerType i32 = new IntegerType(32);
    
    private IntegerType(int bit) {
        this.bit = bit;
    }
    
    public boolean isI1() {
        return this.bit == 1;
    }
    
    public boolean isI32() {
        return this.bit == 32;
    }
    
    @Override
    public String toString() {
        return "i" + bit;
    }
}
