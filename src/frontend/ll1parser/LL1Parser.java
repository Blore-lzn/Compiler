package frontend.ll1parser;

import adt.Pair;
import frontend.BaseParser;
import frontend.Lexer;
import gui.Table;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

public class LL1Parser extends BaseParser {
    protected HashMap<Pair<String, String>, Production> table = new HashMap<>();
    
    public LL1Parser(Lexer lexer) {
        super(lexer);
    }
    
    public void makeTable() {
        for (Production p : grammar.prods) {
            firstPlusSet.get(p).stream().filter(this::isT)
                .forEach(w -> table.put(new Pair<>(p.left, w), p));
            if (firstPlusSet.get(p).contains(EOF)) {
                table.put(new Pair<>(p.left, EOF), p);
            }
        }
    }
    
    public boolean parse() {
        Stack<String> st = new Stack<>();
        String word = lexer.nextToken();
        st.push(EOF);
        st.push(S);
        String focus = st.peek();
        while (true) {
            if (focus.equals("EOF") && word.equals("EOF")) {
                return true;
            } else if (isT(focus) || focus.equals("EOF")) {
                if (Objects.equals(focus, word)) {
                    st.pop();
                    word = lexer.nextToken();
                } else {
                    return false;
                }
            } else {
                Production p = table.get(new Pair<>(focus, word));
                if (p != null) {
                    st.pop();
                    for (int i = p.rights.size() - 1; i >= 0; i--) {
                        if (!p.rights.get(i).equals(EPS)) {
                            st.push(p.rights.get(i));
                        }
                    }
                } else {
                    return false;
                }
            }
            focus = st.peek();
        }
    }
    
    public void printTable() {
        String[] columnNames = new String[grammar.T.size() + 1];
        columnNames[0] = " ";
        for (int i = 0; i < grammar.T.size(); i++) {
            columnNames[i + 1] = grammar.T.get(i);
        }
        Object[][] data = new Object[grammar.NT.size()][grammar.T.size() + 1];
        for (int i = 0; i < grammar.NT.size(); i++) {
            String nt = grammar.NT.get(i);
            data[i][0] = nt;
            for (int j = 0; j < grammar.T.size(); j++) {
                String t = grammar.T.get(j);
                Production p = table.get(new Pair<>(nt, t));
                if (p != null) {
                    data[i][j + 1] = p.handler;
                } else {
                    data[i][j + 1] = "-";
                }
            }
        }
        Table table = new Table("LL(1)表");
        table.show("LL(1)表", data, columnNames);
    }
    
    /**
     * 测试
     */
    public static void main(String[] args) {
        Lexer lexer = new Lexer("1+2*(3+5)");
        
        LL1Parser parser = new LL1Parser(lexer);
        try {
            parser.initGrammar("src/frontend/ExprGrammar.g4");
        } catch (IOException e) {
            e.printStackTrace();
        }
        parser.generateFirstSet();
        parser.generateFollowSet();
        parser.generateFirstPlusSet();
        parser.makeTable();
        parser.printTable();
        System.out.println(parser.parse());
        System.out.println("@ver");
    }
}
