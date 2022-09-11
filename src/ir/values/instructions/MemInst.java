package ir.values.instructions;

import ir.types.ArrayType;
import ir.types.PointerType;
import ir.types.Type;
import ir.types.VoidType;
import ir.values.GlobalVariable;
import ir.values.NullValue;
import ir.values.Value;

import java.util.ArrayList;

public class MemInst extends Instruction {
    public MemInst(String name, Type type, TAG tag) {
        super(name, type, tag);
    }
    
    public static class AllocaInst extends MemInst {
        private final Type allocaType;
        private boolean isInit = false;
        
        public AllocaInst(Type allocaType) {
            super("AllocInst", new PointerType(allocaType), TAG.Alloca);
            this.allocaType = allocaType;
        }
        
        public boolean isInit() {
            return isInit;
        }
        
        public void setInit(boolean init) {
            isInit = init;
        }
        
        @Override
        public String toString() {
            return getName() + " = alloca " + allocaType;
        }
    }
    
    public static class GEPInst extends MemInst {
        private Value target;
        
        public GEPInst(Value ptr, ArrayList<Value> indices) {
            super("GEPInst", getGEPType(ptr, indices), TAG.GEP);
            target = NullValue.value;
            if (ptr instanceof GEPInst) {
                target = ((GEPInst) ptr).target;
            } else if (ptr instanceof AllocaInst) {
                target = ptr;
            } else if (ptr instanceof GlobalVariable) {
                target = ptr;
            }
            addOperand(ptr);
            for (Value v : indices) {
                addOperand(v);
            }
        }
        
        private static Type getGEPType(Value ptr, ArrayList<Value> indices) {
            Type type = ((PointerType) ptr.getType()).getTarget();
            if (type instanceof ArrayType) {
                for (int i = 1; i < indices.size(); i++) {
                    type = ((ArrayType) type).getContained();
                }
            }
            return type;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name).append(" = getelementptr ")
                .append(((PointerType) operands.get(0).getType()).getTarget())
                .append(",")
                .append(operands.get(0).getType()).append(" ")
                .append(operands.get(0).getName())
                .append(" ");
            for (var i = 1; i < operands.size(); i++) {
                sb.append(", ").append(operands.get(i).getType()).append(" ")
                    .append(operands.get(i).getName());
            }
            return sb.toString();
        }
    }
    
    public static class StoreInst extends MemInst {
        public StoreInst(Value val, Value ptr) {
            super("StoreInst", VoidType.type, TAG.Store);
            addOperand(val);
            addOperand(ptr);
        }
        
        @Override
        public String toString() {
            var lhs = operands.get(0);
            var rhs = operands.get(1);
            return "store " + lhs.getType() + " " + lhs.getName() + ", " +
                rhs.getType() + " " + rhs.getName();
        }
    }
    
    public static class LoadInst extends MemInst {
        public LoadInst(Type type, Value ptr) {
            super("LoadInst", type, TAG.Load);
            addOperand(ptr);
        }
    
        @Override
        public String toString() {
            return getName() + " =  load " + getType() + "," +
                operands.get(0).getType() + " " + operands.get(0).getName();
        }
    }
}
