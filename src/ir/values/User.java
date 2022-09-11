package ir.values;

import ir.types.Type;

import java.util.ArrayList;

public class User extends Value {
    protected ArrayList<Value> operands = new ArrayList<>();
    
    public User(String name, Type type) {
        super(name, type);
    }
    
    public void addOperand(Value operand) {
        operands.add(operand);
    }
}
