package ir.values;

import adt.IList;
import adt.INode;
import ir.IRModule;
import ir.types.Type;

public class Function extends Value {
    private final IList<BasicBlock, Function> list = new IList<>(this);
    private final INode<Function, IRModule> node = new INode<>(this);
    private final boolean isLib;
    
    public Function(String name, Type type) {
        super(name, type);
        isLib = false;
    }
    
    public Function(String name, Type type, boolean isLib) {
        super(name, type);
        this.isLib = isLib;
    }
}
