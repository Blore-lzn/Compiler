package ir.values.instructions;

import adt.INode;
import ir.IRModule;
import ir.types.Type;
import ir.values.BasicBlock;
import ir.values.User;

public class Instruction extends User {
    private static int Handle = 0;
    private final int handle;
    private final INode<Instruction, BasicBlock> node = new INode<>(this);
    private final TAG tag;
    
    public Instruction(String name, Type type, TAG tag) {
        super(name, type);
        this.tag = tag;
        handle = Handle++;
        IRModule.getInstance().addInstr(this);
    }
    
    public int getHandle() {
        return handle;
    }
    
    public INode<Instruction, BasicBlock> getNode() {
        return node;
    }
    
    /* insert */
    public void insertAtBegin(BasicBlock bb) {
        this.node.insertAtBegin(bb.getList());
    }
    
    public void insertAtEnd(BasicBlock bb) {
        this.node.insertAtEnd(bb.getList());
    }
    
    public void insertAfter(Instruction inst) {
        this.node.insertAfter(inst.getNode());
    }
    
    public void insertBefore(Instruction inst) {
        this.node.insertBefore(inst.getNode());
    }
    
    /* inst type */
    public boolean isBinary() {
        return this.tag.ordinal() >= TAG.Add.ordinal() && this.tag.ordinal() <= TAG.Or.ordinal();
    }
    
    public boolean isRelBinary() {
        return this.tag.ordinal() >= TAG.Lt.ordinal() && this.tag.ordinal() <= TAG.Ne.ordinal();
    }
    
    public boolean isArithmeticBinary() {
        return this.tag.ordinal() >= TAG.Add.ordinal() && this.tag.ordinal() <= TAG.Div.ordinal();
    }
    
    public boolean isLogicalBinary() {
        return this.tag.ordinal() >= TAG.Lt.ordinal() && this.tag.ordinal() <= TAG.Or.ordinal();
    }
    
    protected enum TAG {
        //binary
        Add, Sub, Rsb, Mod, Mul, Div,
        Shr, Shl,
        Lt, Le, Ge, Gt, Eq, Ne, And, Or,
        //terminator
        Br, Call, Ret,
        //mem op
        Alloca, Load, Store, GEP,
        Zext,
        Phi,
        MemPhi,
        LoadDep,
        // vector op
        InsertEle,
        ExtractEle
    }
}
