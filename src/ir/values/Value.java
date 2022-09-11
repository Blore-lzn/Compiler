package ir.values;

import ir.IRModule;
import ir.Use;
import ir.types.Type;

import java.util.ArrayList;

public abstract class Value {
    private final IRModule module = IRModule.getInstance();
    protected String name;
    protected Type type;
    private final ArrayList<Use> usesList = new ArrayList<>();
    
    public Value(String name, Type type) {
        this.name = name;
        this.type = type;
    }
    
    public IRModule getModule() {
        return module;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public ArrayList<Use> getUsesList() {
        return usesList;
    }
}
