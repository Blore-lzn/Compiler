package ir.types;

import java.util.ArrayList;

public class FunctionType implements Type{
    private final ArrayList<Type> parameters;
    private Type retType;
    public FunctionType() {
        this.parameters = new ArrayList<>();
    }
    
    public FunctionType(Type retType) {
        this.parameters = new ArrayList<>();
        this.retType = retType;
    }
    
    public FunctionType(Type retType, ArrayList<Type> parametersTypes) {
        this.parameters = new ArrayList<>(parametersTypes);
        this.retType = retType;
    }
    
    public ArrayList<Type> getParameters() {
        return parameters;
    }
    
    public Type getRetType() {
        return retType;
    }
    
    @Override
    public String toString() {
        return retType.toString();
    }
}
