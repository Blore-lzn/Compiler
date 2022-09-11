package frontend.myparser.lr1parser;

import adt.Pair;
import frontend.myparser.BaseParser;
import frontend.myparser.Lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Stack;

public class LR1Parser extends BaseParser {
    private ArrayList<CCSet> allCCSets = new ArrayList<>();
    private final HashMap<Pair<CCSet, String>, Pair<String, Object>> actionMap = new HashMap<>();
    private final HashMap<Pair<CCSet, String>, CCSet> gotoMap = new HashMap<>();
    
    private static final String SHIFT = "shift";
    private static final String REDUCE = "reduce";
    private static final String ACCEPTED = "accepted";
    
    public LR1Parser(Lexer lexer) {
        super(lexer);
    }
    
    public boolean parse() {
        Stack<Object> st = new Stack<>();
        st.push("$");
        st.push(allCCSets.get(0));
        CCSet state;
        String word = lexer.nextToken();
        while (true) {
            state = (CCSet) st.peek();
            Pair<String, Object> action = actionMap.get(new Pair<>(state, word));
            if (action == null) {
                return false;
            }
            if (action.getFirst().equals(REDUCE)) {
                Production p = ((Production) action.getSecond());
                for (int i = 0; i < p.rights.size(); i++) {
                    st.pop();
                    st.pop();
                }
                state = (CCSet) st.peek();
                st.push(p.left);
                st.push(gotoMap.get(new Pair<>(state, p.left)));
            } else if (action.getFirst().equals(SHIFT)) {
                st.push(word);
                st.push(action.getSecond());
                word = lexer.nextToken();
            } else if (action.getFirst().equals(ACCEPTED)) {
                break;
            }
        }
        
        return true;
    }
    
    public CCSet closure(CCSet s) {
        boolean changed = true;
        while (changed) {
            changed = false;
            HashSet<LR1Item> newItems = new HashSet<>(s.items);
            for (LR1Item item : s.items) {
                if (item.dotBeforeNT(grammar)) {
                    String nt = item.p.rights.get(item.pos);
                    ArrayList<String> succ = new ArrayList<>();
                    for (int i = item.pos + 1; i < item.p.rights.size(); i++) {
                        succ.add(item.p.rights.get(i));
                    }
                    succ.add(item.next);
                    HashSet<String> succFirstSet = getFirstSetOfStrings(succ);
                    for (Production p : grammar.nt2prods.get(nt)) {
                        for (String b : succFirstSet) {
                            LR1Item it = new LR1Item(p, 0, b);
                            if (!newItems.contains(it)) {
                                changed = true;
                                newItems.add(it);
                            }
                        }
                    }
                }
            }
            s.items = newItems;
        }
        
        return s;
    }
    
    public CCSet gotoFunc(CCSet s, String x) {
        CCSet moved = new CCSet();
        for (LR1Item i : s.items) {
            if (i.dotBeforeSym(x)) {
                moved.items.add(new LR1Item(i.p, i.pos + 1, i.next));
            }
        }
        return closure(moved);
    }
    
    public void makeCCSets() {
        CCSet cc0 = new CCSet();
        for (Production p : grammar.nt2prods.get(S)) {
            cc0.items.add(new LR1Item(p, 0, EOF));
        }
        cc0 = closure(cc0);
        allCCSets.add(cc0);
        boolean changed = true;
        HashSet<CCSet> vis = new HashSet<>();
        while (changed) {
            changed = false;
            ArrayList<CCSet> newAllCCSets = new ArrayList<>(allCCSets);
            for (CCSet cc : allCCSets) {
                if (!vis.contains(cc)) {
                    vis.add(cc);
                    for (String x : cc.getAllSymsAfterDot()) {
                        CCSet temp = gotoFunc(cc, x);
                        if (!newAllCCSets.contains(temp)) {
                            changed = true;
                            newAllCCSets.add(temp);
                        }
                        cc.addEdge(x, temp);
                    }
                }
            }
            allCCSets = newAllCCSets;
        }
        // 编号（可省略）
        for (int i = 0; i < allCCSets.size(); i++) {
            allCCSets.get(i).setHandler(i);
        }
    }
    
    public void makeTable() {
        for (CCSet cc : allCCSets) {
            for (LR1Item item : cc.items) {
                if (item.hasSymAfterDot() && isT(item.getSymAfterDot()) &&
                    cc.edges.containsKey(item.getSymAfterDot())) {
                    actionMap.put(new Pair<>(cc, item.getSymAfterDot()),
                        new Pair<>(SHIFT, cc.edges.get(item.getSymAfterDot())));
                } else if (!item.hasSymAfterDot()) {
                    actionMap.put(new Pair<>(cc, item.next),
                        new Pair<>(REDUCE, item.p));
                }
                // 原文伪代码为else if
                if (item.isFinal()) {
                    actionMap.put(new Pair<>(cc, EOF),
                        new Pair<>(ACCEPTED, null));
                }
            }
            
            for (String nt : grammar.NT) {
                if (cc.edges.containsKey(nt)) {
                    gotoMap.put(new Pair<>(cc, nt), cc.edges.get(nt));
                }
            }
        }
    }
    
    /**
     * LR(1)项
     */
    public static class LR1Item {
        public final Production p;
        public final int pos;  // ·在p.rights[pos]前
        public final String next;
        
        public LR1Item(Production p, int pos, String next) {
            this.p = p;
            this.pos = pos;
            this.next = next;
        }
        
        public boolean isFinal() {
            return p.left.equals(S) && pos == p.rights.size() && next.equals(EOF);
        }
        
        public String getSymAfterDot() {
            if (pos >= p.rights.size()) {
                return null;
            } else {
                return p.rights.get(pos);
            }
        }
        
        public boolean hasSymAfterDot() {
            return pos < p.rights.size();
        }
        
        public boolean dotBeforeNT(Grammar grammar) {
            return grammar.NT.contains(getSymAfterDot());
        }
        
        public boolean dotBeforeSym(String sym) {
            return Objects.equals(sym, getSymAfterDot());
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LR1Item lr1Item = (LR1Item) o;
            return pos == lr1Item.pos && Objects.equals(p, lr1Item.p) &&
                Objects.equals(next, lr1Item.next);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(p, pos, next);
        }
        
        @Override
        public String toString() {
            ArrayList<String> rights = new ArrayList<>(p.rights);
            rights.add(pos, "·");
            return "[" + p.left + " -> " + rights + ", " + next + "]";
        }
    }
    
    /**
     * LR(1)项集的规范族(canonical collection of sets of LR(1)items)
     */
    public static class CCSet {
        public HashSet<LR1Item> items = new HashSet<>();
        public HashMap<String, CCSet> edges = new HashMap<>();
        public int handler = -1;
        
        public void setHandler(int handler) {
            this.handler = handler;
        }
        
        public void addEdge(String s, CCSet ccSet) {
            edges.put(s, ccSet);
        }
        
        public HashSet<String> getAllSymsAfterDot() {
            HashSet<String> res = new HashSet<>();
            for (LR1Item item : items) {
                if (item.getSymAfterDot() != null) {
                    res.add(item.getSymAfterDot());
                }
            }
            return res;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CCSet ccSet = (CCSet) o;
            return Objects.equals(items, ccSet.items);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(items);
        }
        
        @Override
        public String toString() {
            return "CC" + handler;
        }
    }
    
    /**
     * 测试
     */
    public static void main(String[] args) {
        Lexer lexer = new Lexer("(())");
        
        LR1Parser parser = new LR1Parser(lexer);
        try {
            parser.initGrammar("src/frontend/ParenGrammar.g");
        } catch (IOException e) {
            e.printStackTrace();
        }
        parser.generateFirstSet();
        parser.makeCCSets();
        parser.makeTable();
        System.out.println(parser.parse());
        System.out.println("@ver");
    }
}
