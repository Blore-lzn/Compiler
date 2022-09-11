package ir.values;

import adt.IList;
import adt.INode;
import ir.types.Type;
import ir.values.instructions.Instruction;

public class BasicBlock extends Value {
    private final INode<BasicBlock, Function> node = new INode<>(this);
    private final IList<Instruction, BasicBlock> list = new IList<>(this);
    
    public BasicBlock(String name, Type type) {
        super(name, type);
    }
    
    public INode<BasicBlock, Function> getNode() {
        return node;
    }
    
    public Function getParent() {
        return node.getParent().getVal();
    }
    
    public IList<Instruction, BasicBlock> getList() {
        return list;
    }
}
