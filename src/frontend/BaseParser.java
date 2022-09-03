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
    private final HashMap<String, HashSet<String>> firstSet = new HashMap<>();
    private final HashMap<String, HashSet<String>> followSet = new HashMap<>();
    private final HashMap<Production, HashSet<String>> firstPlusSet = new HashMap<>();
    private final Grammar grammar = new Grammar();
    
    private static final String S = "Goal"; // 标识符
    private static final String EOF = "EOF"; // 结束符
    private static final String EPS = "eps"; // 空串
    
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
                grammar.prods.add(new Production(left, rights));
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
    
    public void generateFirstPlusSet() {
        for (Production p : grammar.prods) {
            if (p.rights.size() == 1) {
                String b = p.rights.get(0);
                HashSet<String> newFirstPlusSet = new HashSet<>(firstSet.get(b));
                if (!firstSet.get(b).contains(EPS)) {
                    firstPlusSet.put(p, newFirstPlusSet);
                } else {
                    newFirstPlusSet.addAll(followSet.get(p.left));
                    firstPlusSet.put(p, newFirstPlusSet);
                }
            }
        }
    }
    
    private boolean isT(String s) {
        return grammar.T.contains(s);
    }
    
    private boolean isNT(String s) {
        return grammar.NT.contains(s);
    }
    
    /**
     * 产生式
     */
    static class Production {
        private final String left; // 左部符号
        private final ArrayList<String> rights = new ArrayList<>();  // 右部符号串
        
        public Production(String left, String[] rights) {
            this.left = left;
            this.rights.addAll(Arrays.asList(rights));
        }
    }
    
    /**
     * 文法
     */
    static class Grammar {
        private final ArrayList<String> T = new ArrayList<>();   // 终结符
        private final ArrayList<String> NT = new ArrayList<>();   // 非终结符
        private final ArrayList<Production> prods = new ArrayList<>();  //产生式
    }
}
