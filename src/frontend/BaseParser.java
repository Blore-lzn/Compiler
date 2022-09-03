package frontend;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

/**
 * 构建FIRST集和FOLLOW集合
 */
public class BaseParser {
    protected final HashMap<String, HashSet<String>> firstSet = new HashMap<>();
    protected final HashMap<String, HashSet<String>> followSet = new HashMap<>();
    protected final HashMap<Production, HashSet<String>> firstPlusSet = new HashMap<>();
    protected final Grammar grammar = new Grammar();
    protected final Lexer lexer;
    
    protected static final String S = "Goal"; // 标识符
    protected static final String EOF = "EOF"; // 结束符
    protected static final String EPS = "eps"; // 空串
    
    public BaseParser(Lexer lexer) {
        this.lexer = lexer;
    }
    
    public void initGrammar(String grammarFilePath) throws IOException {
        FileInputStream file = new FileInputStream(grammarFilePath);
        InputStreamReader reader = new InputStreamReader(file);
        BufferedReader br = new BufferedReader(reader);
        String line;
        String left = null;
        while ((line = br.readLine()) != null) {
            if (line.equals("END_OF_GRAMMAR")) {
                break;
            } else if (line.endsWith(":")) {
                left = line.split(":")[0];
            } else if (line.endsWith(";")) {
                left = null;
            } else {
                String tmp = line.trim();
                if (tmp.charAt(0) == '|') {
                    tmp = tmp.substring(2);
                }
                String[] rights = tmp.split(" ");
                grammar.addProduction(new Production(left, rights));
                
            }
        }
        // 构造非终结符和终结符集合
        for (Production p : grammar.prods) {
            if (!isNT(p.left) && !isT(p.left)) {
                grammar.NT.add(p.left);
            }
        }
        
        for (Production p : grammar.prods) {
            for (String r : p.rights) {
                if (!isNT(r) && !isT(r)) {
                    grammar.T.add(r);
                }
            }
        }
        
        grammar.T.add(EOF);
    }
    
    public void generateFirstSet() {
        firstSet.clear();
        /* 终结符的FIRST集是其本身 */
        for (String s : grammar.T) {
            HashSet<String> set = new HashSet<>();
            set.add(s);
            firstSet.put(s, set);
        }
        for (String s : grammar.NT) {
            firstSet.put(s, new HashSet<>());
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            
            for (Production p : grammar.prods) {
                HashSet<String> rhs = new HashSet<>(firstSet.get(p.rights.get(0)));
                firstSet.get(p.rights.get(0)).stream().filter(s -> !s.equals(EPS))
                    .forEach(rhs::add);
                int i = 0;
                while (i < p.rights.size() - 1 && firstSet.get(p.rights.get(i)).contains(EPS)) {
                    firstSet.get(p.rights.get(i + 1)).stream().filter(s -> !s.equals(EPS))
                        .forEach(rhs::add);
                    i = i + 1;
                }
                if (i == p.rights.size() - 1 && firstSet.get(p.rights.get(i)).contains(EPS)) {
                    rhs.add(EPS);
                }
                rhs.addAll(firstSet.get(p.left));
                if (!Objects.equals(firstSet.get(p.left), rhs)) {
                    changed = true;
                    firstSet.put(p.left, rhs);
                }
            }
        }
    }
    
    public void generateFollowSet() {
        followSet.clear();
        for (String s : grammar.NT) {
            followSet.put(s, new HashSet<>());
        }
        followSet.get(S).add(EOF);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Production p : grammar.prods) {
                HashSet<String> trailer = new HashSet<>(followSet.get(p.left));
                for (int i = p.rights.size() - 1; i >= 0; i--) {
                    String bi = p.rights.get(i);
                    if (isNT(bi)) {
                        HashSet<String> newFollowSet = new HashSet<>(trailer);
                        newFollowSet.addAll(followSet.get(bi));
                        if (!Objects.equals(newFollowSet, followSet.get(bi))) {
                            changed = true;
                            followSet.put(bi, newFollowSet);
                        }
                        if (firstSet.get(bi).contains(EPS)) {
                            firstSet.get(bi).stream().filter(s -> !s.equals(EPS))
                                .forEach(trailer::add);
                        } else {
                            trailer = new HashSet<>(firstSet.get(bi));
                        }
                    } else {
                        trailer = new HashSet<>(firstSet.get(bi));
                    }
                }
            }
        }
    }
    
    public HashSet<String> getFirstSetOfStrings(ArrayList<String> symbols) {
        HashSet<String> res = new HashSet<>();
        for (String s : symbols) {
            res.addAll(firstSet.get(s));
            if (!firstSet.get(s).contains(EPS)) {
                break;
            }
        }
        return res;
    }
    
    public void generateFirstPlusSet() {
        firstPlusSet.clear();
        for (Production p : grammar.prods) {
            if (!p.rights.isEmpty()) {
                HashSet<String> newFirstPlusSet = getFirstSetOfStrings(p.rights);
                if (!newFirstPlusSet.contains(EPS)) {
                    firstPlusSet.put(p, newFirstPlusSet);
                } else {
                    newFirstPlusSet.addAll(followSet.get(p.left));
                    firstPlusSet.put(p, newFirstPlusSet);
                }
            }
        }
    }
    
    public boolean isT(String s) {
        return grammar.T.contains(s);
    }
    
    public boolean isNT(String s) {
        return grammar.NT.contains(s);
    }
    
    /**
     * 产生式
     */
    public static class Production {
        public final String left; // 左部符号
        public final ArrayList<String> rights = new ArrayList<>();  // 右部符号串
        public final int handler;
        private static int HANDLER = 0;
        
        public Production(String left, String[] rights) {
            this.left = left;
            this.rights.addAll(Arrays.asList(rights));
            this.handler = HANDLER++;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Production that = (Production) o;
            return handler == that.handler && Objects.equals(left, that.left) &&
                Objects.equals(rights, that.rights);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(left, rights, handler);
        }
        
        @Override
        public String toString() {
            return left + " -> " + rights;
        }
    }
    
    /**
     * 文法
     */
    public static class Grammar {
        public final ArrayList<String> T = new ArrayList<>();   // 终结符
        public final ArrayList<String> NT = new ArrayList<>();   // 非终结符
        public final ArrayList<Production> prods = new ArrayList<>();  //产生式
        public final HashMap<String, ArrayList<Production>> nt2prods = new HashMap<>();
        
        public void addProduction(Production p) {
            prods.add(p);
            nt2prods.computeIfAbsent(p.left, k -> new ArrayList<>());
            nt2prods.get(p.left).add(p);
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Production p : prods) {
                sb.append(p.toString()).append("\n");
            }
            return sb.toString();
        }
    }
}
