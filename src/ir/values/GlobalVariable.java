package ir.values;

import ir.types.Type;

public class GlobalVariable extends Value{
    private Constant init;
    private boolean isConst;
    
    public GlobalVariable(String name, Type type, Constant init) {
        super(name, type);
        this.init = init;
    }
    
    public GlobalVariable(String name, Type type, Constant init, boolean isConst) {
        super(name, type);
        this.init = init;
        this.isConst = isConst;
    }
}
