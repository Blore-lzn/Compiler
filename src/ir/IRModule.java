package ir;

import adt.IList;
import ir.values.Function;
import ir.values.GlobalVariable;
import ir.values.instructions.Instruction;

import java.util.ArrayList;
import java.util.HashMap;

public class IRModule {
    private static IRModule INSTANCE = new IRModule();
    private final IList<Function, IRModule> functions = new IList<>(this);
    private final ArrayList<GlobalVariable> globalVars = new ArrayList<>();
    private final HashMap<Integer, Instruction> instructions = new HashMap<>();
    
    private IRModule() {
    }
    
    public static IRModule getInstance() {
        return INSTANCE;
    }
    
}
