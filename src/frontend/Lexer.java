package frontend;

public class Lexer {
    private String input;
    private int ptr = -1;
    private Symbol sym;
    private StringBuilder token;
    
    public Lexer(String input) {
        this.input = input;
    }
    
    public void nextToken() {
        clearToken();
        do {
            getchar();
            if (isEOF()) {
                sym = Symbol.EOF;
                return;
            }
        } while (isSpace());
        
        if (isLetter()) {
            while (isLetter() || isDigit()) {
                catToken();
                getchar();
            }
            retract();
            
            sym = reserver();
            if (sym == Symbol.INVALID) {
                sym = Symbol.IDSY;
            }
        } else if (isDigit()) {
            while (isDigit()) {
                catToken();
                getchar();
            }
            retract();
            
            sym = Symbol.INTSY;
        } else if (isDelimiter()) {
            catToken();
            getchar();
            if (isEQ()) {
                catToken();
                getchar();
            }
            sym = reserver();
        } else if (isLpar()) {
            sym = Symbol.LPAREN;
        } else if (isRpar()) {
            sym = Symbol.RPAREN;
        } else if (isLBRACKT()) {
            sym = Symbol.LBRACKT;
        } else if (isRBRACKT()) {
            sym = Symbol.RBRACKT;
        } else if (isComma()) {
            sym = Symbol.COMMA;
        } else if (isSemi()) {
            sym = Symbol.SEMI;
        } else if (isAdd()) {
            sym = Symbol.ADD;
        } else if (isSub()) {
            sym = Symbol.SUB;
        } else if (isStar()) {
            sym = Symbol.STAR;
        } else if (isMod()) {
            sym = Symbol.MOD;
        } else if (isDiv()) {
            getchar();
            if (isStar()) {
                while (true) {
                    do {
                        getchar();
                    } while (!isStar());
                    do {
                        getchar();
                        if (isDiv()) {
                            sym = Symbol.COMMENT;
                            return;
                        }
                    } while (isStar());
                }
            }
            retract();
            sym = Symbol.DIV;
        } else {
            sym = Symbol.INVALID;
        }
    }
    
    public int getPtr() {
        return ptr;
    }
    
    public Symbol getSym() {
        return sym;
    }
    
    public String getToken() {
        return token.toString();
    }
    
    private void clearToken() {
        token = new StringBuilder();
    }
    
    public void getchar() {
        ptr++;
    }
    
    private void retract() {
        ptr--;
    }
    
    public void catToken() {
        token.append(input.charAt(ptr));
    }
    
    /* 判断 */
    // 空白符
    public boolean isSpace() {
        return Character.isSpaceChar(input.charAt(ptr));
    }
    
    public boolean isLetter() {
        return Character.isLetter(input.charAt(ptr));
    }
    
    public boolean isDigit() {
        return Character.isDigit(input.charAt(ptr));
    }
    
    private boolean isColon() {
        return input.charAt(ptr) == ':';
    }
    
    private boolean isAdd() {
        return input.charAt(ptr) == '+';
    }
    
    private boolean isSub() {
        return input.charAt(ptr) == '-';
    }
    
    private boolean isStar() {
        return input.charAt(ptr) == '*';
    }
    
    private boolean isDiv() {
        return input.charAt(ptr) == '/';
    }
    
    private boolean isMod() {
        return input.charAt(ptr) == '%';
    }
    
    private boolean isLpar() {
        return input.charAt(ptr) == '(';
    }
    
    private boolean isRpar() {
        return input.charAt(ptr) == ')';
    }
    
    private boolean isLBRACKT() {
        return input.charAt(ptr) == '{';
    }
    
    private boolean isRBRACKT() {
        return input.charAt(ptr) == '}';
    }
    
    private boolean isComma() {
        return input.charAt(ptr) == ',';
    }
    
    private boolean isSemi() {
        return input.charAt(ptr) == ';';
    }
    
    private boolean isDelimiter() {
        return input.charAt(ptr) == '=' ||
            input.charAt(ptr) == '<' ||
            input.charAt(ptr) == '>' ||
            input.charAt(ptr) == '!';
    }
    
    private boolean isEQ() {
        return input.charAt(ptr) == '=';
    }
    
    private boolean isEOF() {
        return ptr >= input.length();
    }
    
    // 查找保留关键字
    public Symbol reserver() {
        if (token.toString().equals("<=")) {
            return Symbol.LE;
        } else if (token.toString().equals("<")) {
            return Symbol.LT;
        } else if (token.toString().equals(">")) {
            return Symbol.GT;
        } else if (token.toString().equals(">=")) {
            return Symbol.GE;
        } else if (token.toString().equals("==")) {
            return Symbol.EQ;
        } else if (token.toString().equals("!=")) {
            return Symbol.NE;
        } else {
            return Symbol.INVALID;
        }
    }
}
