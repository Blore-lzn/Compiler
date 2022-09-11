package ir.values;

import ir.types.Type;

import java.util.ArrayList;

public class ConstantArray extends Constant{
    private ArrayList<Constant> value;
    public ConstantArray(Type type, ArrayList<Constant> arr) {
        super("ConstantArray", type);
        value = new ArrayList<>(arr);
    }
    
    public ArrayList<Constant> getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getType().toString()).append("[");
        for (Constant constant : value) {
            sb.append(constant.toString()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}
