package ir.types;

import java.util.ArrayList;

public class ArrayType implements Type {
    private final ArrayList<Integer> dims;
    private final Type contained;  // 元素类型
    
    public ArrayType(Type contained, int numOfElement) {
        this.contained = contained;
        if (contained instanceof ArrayType) {
            this.dims = new ArrayList<>(((ArrayType) contained).dims);
            this.dims.add(0, numOfElement);
        } else {
            this.dims = new ArrayList<>();
            this.dims.add(numOfElement);
        }
    }
    
    public Type getContained() {
        return contained;
    }
    
    @Override
    public String toString() {
        return "[" + dims.get(0) + " x " + contained.toString() + "]";
    }
}
