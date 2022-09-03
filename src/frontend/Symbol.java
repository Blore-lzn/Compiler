package frontend;

public enum Symbol {
    IDSY("name"),   // 标识符
    INTSY("num"),   // 数字
    
    EQ,
    NE,
    LE,
    LT,
    GE,
    GT,
    
    LPAREN("("),
    RPAREN(")"),
    LBRACKT,
    RBRACKT,
    
    ADD("+"),
    SUB("-"),
    STAR("*"),
    DIV("/"),
    MOD,
    
    COMMENT,
    COMMA,
    SEMI,
    
    INVALID,
    EOF("EOF");
    public final String name;
    
    Symbol(String name) {
        this.name = name;
    }
    
    Symbol() {
        this.name = null;
    }
}
