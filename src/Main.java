import frontend.Lexer;
import frontend.Symbol;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer("int main{}");
        
        while (lexer.getSym() != Symbol.EOF) {
            lexer.nextToken();
        }
    }
}
