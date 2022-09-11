package frontend.antlrgen;

import ir.types.ArrayType;
import ir.types.FloatingType;
import ir.types.IntegerType;
import ir.types.PointerType;
import ir.types.Type;
import ir.types.VoidType;
import ir.values.BasicBlock;
import ir.values.Constant;
import ir.values.ConstantArray;
import ir.values.ConstantInteger;
import ir.values.Value;
import ir.values.ValueFactory;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MyVisitor extends SysY2022BaseVisitor<Void> {
    private final Scope scope = new Scope();
    private final ValueFactory factory = ValueFactory.getInstance();
    
    private BasicBlock curBlock = null;
    private Value curValue = null;
    
    private ArrayList<Value> curArray = null;
//    private ArrayList<Integer> curArrayDims = null;
    
    private boolean isGlobalInit = false;
    
    private static class Scope {
        private final ArrayList<HashMap<String, Value>> tables = new ArrayList<>();
        
        public Scope() {
            tables.add(new HashMap<>());
        }
        
        public HashMap<String, Value> top() {
            return tables.get(tables.size() - 1);
        }
        
        public Value find(String key) {
            for (int i = tables.size() - 1; i >= 0; i--) {
                if (tables.get(i).containsKey(key)) {
                    return tables.get(i).get(key);
                }
            }
            return null;
        }
        
        public void put(String key, Value v) {
            if (top().containsKey(key)) {
                throw new RuntimeException();
            } else {
                top().put(key, v);
            }
        }
        
        public boolean preEnter = false;
        
        public void addLayer() {
            if (preEnter) {
                preEnter = false;
                return;
            }
            tables.add(new HashMap<>());
            
        }
        
        public void popLayer() {
            tables.remove(tables.size() - 1);
        }
        
        public boolean isGlobal() {
            return this.tables.size() == 1;
        }
    }
    
    /* compUnit:
        (decl | funcDef)+
    ;*/
    @Override
    public Void visitCompUnit(SysY2022Parser.CompUnitContext ctx) {
        scope.put("getint", factory.buildLibFunction("getint", IntegerType.i32, new ArrayList<>()));
        scope.put("getch", factory.buildLibFunction("getch", IntegerType.i32, new ArrayList<>()));
        scope.put("getfloat",
            factory.buildLibFunction("getfloat", FloatingType.type, new ArrayList<>()));
        scope.put("getarray", factory.buildLibFunction("getarray", IntegerType.i32,
            new ArrayList<>(Collections.singleton(PointerType.i32ptrType))));
        scope.put("getfarray", factory.buildLibFunction("getfarray", IntegerType.i32,
            new ArrayList<>(Collections.singleton(PointerType.i32ptrType))));
        scope.put("putint", factory.buildLibFunction("putint", VoidType.type,
            new ArrayList<>(Collections.singleton(IntegerType.i32))));
        scope.put("putch", factory.buildLibFunction("putch", VoidType.type,
            new ArrayList<>(Collections.singleton(IntegerType.i32))));
        scope.put("putfloat", factory.buildLibFunction("putfloat", VoidType.type,
            new ArrayList<>(Collections.singleton(FloatingType.type))));
        scope.put("putarray",
            factory.buildLibFunction("putarray", VoidType.type, new ArrayList<>() {{
                add(IntegerType.i32);
                add(PointerType.i32ptrType);
            }}));
        scope.put("putfarray",
            factory.buildLibFunction("putfarray", VoidType.type, new ArrayList<>() {{
                add(IntegerType.i32);
                add(PointerType.i32ptrType);
            }}));
        scope.put("putf", factory.buildLibFunction("putf", VoidType.type, new ArrayList<>() {{
            add(PointerType.i32ptrType);
            add(IntegerType.i32);
        }}));
        scope.put("starttime",
            factory.buildLibFunction("_sysy_starttime", VoidType.type, new ArrayList<>() {{
                add(IntegerType.i32);
            }}));
        scope.put("stoptime",
            factory.buildLibFunction("_sysy_stoptime", VoidType.type, new ArrayList<>() {{
                add(IntegerType.i32);
            }}));
        scope.put("memset", factory.buildLibFunction("memset", VoidType.type, new ArrayList<>() {{
            add(PointerType.i32ptrType);
            add(IntegerType.i32);
            add(IntegerType.i32);
        }}));
        return super.visitCompUnit(ctx);
    }
    
    /* decl:
    constDecl
        | varDecl
    ;*/
    @Override
    public Void visitDecl(SysY2022Parser.DeclContext ctx) {
        return super.visitDecl(ctx);
    }
    
    @Override
    public Void visitConstDecl(SysY2022Parser.ConstDeclContext ctx) {
        ctx.constDef().forEach(this::visit);
        return null;
    }
    
    /* constDef:
    Ident (LSBRACKET constExp RSBRACKET)* ASSIGN constInitVal
        ;*/
    @Override
    public Void visitConstDef(SysY2022Parser.ConstDefContext ctx) {
        var ident = ctx.Ident().getText();
        assert !scope.top().containsKey(ident);
        if (ctx.constExp().isEmpty()) { //not array
            if (ctx.constInitVal() != null) {
                visit(ctx.constInitVal());
                scope.put(ident, curValue);
            }
        } else {
            var dims = new ArrayList<Integer>();
            ctx.constExp().forEach(context -> {
                visit(context);
                dims.add(((ConstantInteger) curValue).getValue());
            });
            
            // 递归构造数组
            Type arrType = IntegerType.i32;
            for (var i = dims.size(); i > 0; i--) {
                arrType = new ArrayType(arrType, dims.get(i - 1));
            }
            
            if (scope.isGlobal()) {
                if (ctx.constInitVal() != null) {
                    isGlobalInit = true;
                    visit(ctx.constInitVal());//dim.size() = n
                    isGlobalInit = false;
                    ArrayList<Constant> constArrayValue = new ArrayList<>();
                    curArray.forEach(i -> constArrayValue.add(((Constant) i)));
                    var arrInit = new ConstantArray(arrType, constArrayValue);
                    var gv =
                        factory.buildGlobalVar(ctx.Ident().getText(), arrType, arrInit, true);
                    scope.put(ctx.Ident().getText(), gv);
                } else {
                    var gv = factory.buildGlobalVar(ctx.Ident().getText(), arrType, null);
                    scope.put(ctx.Ident().getText(), gv);
                }
            } else {
                var allocaArray = factory
                    .buildAllocaInst(arrType, curBlock); //alloca will be move to first bb in cur function
                scope.put(ctx.Ident().getText(), allocaArray);
                if (ctx.constInitVal() != null) {
                    allocaArray.setInit(true);
                    visit(ctx.constInitVal());
                    var arr = curArray;
                    // GEP只计算指针，不访问内存，因此每次需要访问内存的时候需要解出需要的内存的指针并且build新的GEP指令
                    var ptr = factory.buildGEP(allocaArray, new ArrayList<>() {{
                        add(ConstantInteger.const0);
                        add(ConstantInteger.const0);
                    }}, curBlock);
                    for (int i = 1; i < ctx.constInitVal().dimInfo.size(); i++) {
                        ptr = factory.buildGEP(ptr, new ArrayList<>() {{
                            add(ConstantInteger.const0);
                            add(ConstantInteger.const0);
                        }}, curBlock);
                    }
                    for (int i = 0; i < arr.size(); i++) {
                        if (i == 0) {
                            factory.buildStore(arr.get(i), ptr, curBlock);
                        } else {
                            int finalI = i;
                            //build  GEP and store value
                            var p = factory.buildGEP(ptr, new ArrayList<>() {{
                                add(new ConstantInteger(finalI, IntegerType.i32));
                            }}, curBlock);
                            factory.buildStore(arr.get(i), p, curBlock);
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /*constInitVal:
    constExp
        | (LBRACE (constInitVal (COMMA constInitVal)*)? RBRACE)
    ;*/
    @Override
    public Void visitConstInitVal(SysY2022Parser.ConstInitValContext ctx) {
        if ((ctx.constExp() != null) && ctx.dimInfo == null) {
            visit(ctx.constExp());//非数组形式变量的初始化
        } else {
            var curDimLength = ctx.dimInfo.get(0);
            var sizeOfEachEle = 1;//每个元素（i32或者是数组）的长度
            var arrOfCurDim = new ArrayList<Value>();//
            //calculate Size of Ele in cur dim
            for (int i = 1; i < ctx.dimInfo.size(); i++) {
                sizeOfEachEle *= ctx.dimInfo.get(i);
            }
            //recursively init each dim
            for (SysY2022Parser.ConstInitValContext constInitValContext : ctx.constInitVal()) {
                if (constInitValContext.constExp() == null) {
                    var pos = arrOfCurDim.size();
                    for (int i = 0; i < (sizeOfEachEle - (pos % sizeOfEachEle)) % sizeOfEachEle; i++) {
                        arrOfCurDim.add(ConstantInteger.const0);
                    }
                    constInitValContext.dimInfo = new ArrayList<>(ctx.dimInfo.subList(1, ctx.dimInfo.size()));
                    visit(constInitValContext);
                    arrOfCurDim.addAll(curArray);
                } else {
                    visit(constInitValContext);
                    arrOfCurDim.add(curValue);
                }
            }
            for (int i = arrOfCurDim.size(); i < curDimLength * sizeOfEachEle; i++) {
                arrOfCurDim.add(ConstantInteger.const0);
            }//长度不足一个ele*dimsize 的补0
            curArray = arrOfCurDim;
        }
        
        return null;
    }
    
    @Override
    public Void visitVarDecl(SysY2022Parser.VarDeclContext ctx) {
        return null;
    }
    
    @Override
    public Void visitVarDef(SysY2022Parser.VarDefContext ctx) {
        return null;
    }
    
    @Override
    public Void visitInitVal(SysY2022Parser.InitValContext ctx) {
        return null;
    }
    
    @Override
    public Void visitFuncDef(SysY2022Parser.FuncDefContext ctx) {
        return null;
    }
    
    @Override
    public Void visitFuncFParams(SysY2022Parser.FuncFParamsContext ctx) {
        return null;
    }
    
    @Override
    public Void visitFuncFParam(SysY2022Parser.FuncFParamContext ctx) {
        return null;
    }
    
    @Override
    public Void visitBlock(SysY2022Parser.BlockContext ctx) {
        return null;
    }
    
    @Override
    public Void visitBlockItem(SysY2022Parser.BlockItemContext ctx) {
        return null;
    }
    
    @Override
    public Void visitStmt(SysY2022Parser.StmtContext ctx) {
        return null;
    }
    
    @Override
    public Void visitExp(SysY2022Parser.ExpContext ctx) {
        return null;
    }
    
    @Override
    public Void visitCond(SysY2022Parser.CondContext ctx) {
        return null;
    }
    
    @Override
    public Void visitLVal(SysY2022Parser.LValContext ctx) {
        return null;
    }
    
    @Override
    public Void visitPrimaryExp(SysY2022Parser.PrimaryExpContext ctx) {
        return null;
    }
    
    @Override
    public Void visitNumber(SysY2022Parser.NumberContext ctx) {
        return null;
    }
    
    @Override
    public Void visitUnaryExp(SysY2022Parser.UnaryExpContext ctx) {
        return null;
    }
    
    @Override
    public Void visitUnaryOp(SysY2022Parser.UnaryOpContext ctx) {
        return null;
    }
    
    @Override
    public Void visitFuncRParams(SysY2022Parser.FuncRParamsContext ctx) {
        return null;
    }
    
    @Override
    public Void visitMulExp(SysY2022Parser.MulExpContext ctx) {
        return null;
    }
    
    @Override
    public Void visitAddExp(SysY2022Parser.AddExpContext ctx) {
        return null;
    }
    
    @Override
    public Void visitRelExp(SysY2022Parser.RelExpContext ctx) {
        return null;
    }
    
    @Override
    public Void visitEqExp(SysY2022Parser.EqExpContext ctx) {
        return null;
    }
    
    @Override
    public Void visitLAndExp(SysY2022Parser.LAndExpContext ctx) {
        return null;
    }
    
    @Override
    public Void visitLOrExp(SysY2022Parser.LOrExpContext ctx) {
        return null;
    }
    
    @Override
    public Void visitConstExp(SysY2022Parser.ConstExpContext ctx) {
        return null;
    }
    
    @Override
    public Void visitChildren(RuleNode ruleNode) {
        return null;
    }
    
    @Override
    public Void visitTerminal(TerminalNode terminalNode) {
        return null;
    }
    
    @Override
    public Void visitErrorNode(ErrorNode errorNode) {
        return null;
    }
}
