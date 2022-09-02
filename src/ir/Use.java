package ir;

import ir.values.User;
import ir.values.Value;

// def - use
public class Use {
    private final Value value;
    private final User user;
    private final int operandNo;
    
    public Use(Value value, User user, int operandNo) {
        this.value = value;
        this.user = user;
        this.operandNo = operandNo;
    }
    
    public Value getValue() {
        return value;
    }
    
    public User getUser() {
        return user;
    }
    
    public int getOperandNo() {
        return operandNo;
    }
}
