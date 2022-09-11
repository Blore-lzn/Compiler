package ir.values;

import ir.IRModule;
import ir.types.FunctionType;
import ir.types.Type;
import ir.values.instructions.MemInst;

import java.util.ArrayList;

/**
 * Value的工厂方法
 */
public class ValueFactory {
    private static final ValueFactory INSTANCE = new ValueFactory();
    private static final IRModule module = IRModule.getInstance();
    
    private ValueFactory() {
    
    }
    
    public static ValueFactory getInstance() {
        return INSTANCE;
    }
    
    public Function buildFunction(String name, Type ret, ArrayList<Type> parasTypes) {
        return new Function(name, new FunctionType(ret, parasTypes));
    }
    
    public Function buildLibFunction(String name, Type ret, ArrayList<Type> parasTypes) {
        return new Function(name, new FunctionType(ret, parasTypes), true);
    }
    
    public GlobalVariable buildGlobalVar(String name, Type type, ConstantArray init, boolean isConst) {
        var gv = new GlobalVariable(name, type, init, isConst);
        module.addGlobalVar(gv);
        return gv;
    }
    
    public GlobalVariable buildGlobalVar(String name, Type type, ConstantArray init) {
        return buildGlobalVar(name, type, init, false);
    }
    
    public MemInst.AllocaInst buildAllocaInst(Type type, BasicBlock bb) {
        var inst = new MemInst.AllocaInst(type);
        inst.insertAtBegin(bb); // 在基本块最前端插入
        return inst;
    }
    
    public MemInst.GEPInst buildGEP(Value ptr, ArrayList<Value> indices, BasicBlock bb) {
        var inst =  new MemInst.GEPInst(ptr, indices);
        inst.insertAtEnd(bb);
        return inst;
    }
    
    public MemInst.StoreInst buildStore(Value value, Value ptr, BasicBlock bb) {
        var inst = new MemInst.StoreInst(value, ptr);
        inst.insertAtEnd(bb);
        return inst;
    }
}
