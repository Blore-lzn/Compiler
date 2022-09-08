//SysY2022.g4
grammar SysY2022;

//parser
compUnit:
         (decl | funcDef)+
         ;

decl:
        constDecl
        | varDecl
        ;

constDecl:
        CONST BType constDef (COMMA constDef)* SEMICOLON
        ;

constDef:
        Ident (LSBRACKET constExp RSBRACKET)* ASSIGN constInitVal
        ;

constInitVal:
        constExp
        | (LBRACE (constInitVal (COMMA constInitVal)*)? RBRACE)
        ;

varDecl:
        BType varDef (COMMA varDef)* SEMICOLON
        ;

varDef:
        (Ident (LSBRACKET constExp RSBRACKET)*)
        | (Ident (LSBRACKET constExp RSBRACKET)* ASSIGN initVal)
        ;

initVal:
        exp
        | (LBRACE (initVal (COMMA initVal)* )? RBRACE)
        ;

funcDef:
        (BType | VOID) Ident LPAREN (funcFParams)? RPAREN block
        ;

funcFParams:
        funcFParam (COMMA funcFParam)*
        ;

funcFParam:
        BType Ident ( LSBRACKET RSBRACKET (LSBRACKET exp RSBRACKET)* )?
        ;

block:
        LBRACE (blockItem)* RBRACE
        ;

blockItem:
        decl
        | stmt
        ;

stmt:
        (lVal ASSIGN exp SEMICOLON)
        | ((exp)? SEMICOLON)
        | block
        | ( IF LPAREN cond RPAREN stmt (ELSE stmt)? )
        | ( WHILE LPAREN cond RPAREN stmt)
        | ( BREAK SEMICOLON)
        | ( CONTINUE SEMICOLON)
        | ( RETURN (exp)? SEMICOLON)
        ;

exp:
        addExp
        ;


cond:
        lOrExp
        ;

lVal:
        Ident (LSBRACKET exp RSBRACKET)*
        ;

primaryExp:
        (LPAREN exp RPAREN)
        | lVal
        | number
        ;

number:
        IntConst
        | FloatConst
        ;

unaryExp:
        primaryExp
        | (Ident LPAREN (funcRParams)? RPAREN)
        | (unaryOp unaryExp)
        ;

unaryOp:
        PLUS
        | SUB
        | NOT
        ;

funcRParams:
        exp (COMMA exp)*
        ;

mulExp:
        unaryExp
        | (unaryExp (MULT | DIV | MOD) mulExp)
        ;

addExp:
        mulExp
        | (mulExp (PLUS | SUB) addExp)
        ;

relExp:
        addExp
        | (addExp (LT | GT | LTOE | GTOE) relExp)
        ;

eqExp:
        relExp
        | (relExp (EQUALS | NEQUALS) eqExp)
        ;

lAndExp:
        eqExp
        | (eqExp AND lAndExp)
        ;

lOrExp:
        lAndExp
        | (lAndExp OR lOrExp)
        ;

constExp:
        addExp
        ;

// lexer

BType:
        'int'
        | 'float'
        ;
VOID:
        'void'
        ;

LPAREN: '(';
RPAREN: ')';
LSBRACKET: '[';
RSBRACKET: ']';
LBRACE: '{';
RBRACE: '}';
COMMA: ',';
SEMICOLON: ';';
ASSIGN: '=';
PLUS: '+';
SUB: '-';
NOT: '!';
MULT: '*';
DIV: '/';
MOD: '%';
GT: '>';
LT: '<';        //less than
GTOE: '>=';     //greater than or equals
LTOE: '<=';     //less than or equals
EQUALS: '==';
NEQUALS: '!=';  //not equals
AND: '&&';
OR: '||';
CONST: 'const';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
BREAK: 'break';
CONTINUE: 'continue';
RETURN: 'return';

Ident:
        [_a-zA-Z]
        | ([_a-zA-Z] [_a-zA-Z0-9]+)
        ;

IntConst:
        DecimalConst
        | OctalConst
        | HexadecimalConst
        ;

fragment
HexadecimalDigit:
        [0-9a-fA-F]
        ;

fragment
NonzeroDigit:
        [1-9]
        ;

fragment
Digit:
        [0-9]
        ;

fragment
OctalDigit:
        [0-7]
        ;

fragment
HexadecimalPrefix:
        '0x'
        | '0X'
        ;

fragment
DecimalConst:
        NonzeroDigit Digit*
        ;

fragment
OctalConst:
        '0' OctalDigit*
        ;

fragment
HexadecimalConst:
        HexadecimalPrefix HexadecimalDigit+
        ;

FloatConst:
        DecimalFloatingConstant
        | HexadecimalFloatingConstant
        ;

fragment
DecimalFloatingConstant:
        FractionalConstant ExponentPart? FloatingSuffix?
        | DigitSequence ExponentPart FloatingSuffix?
        ;

fragment
HexadecimalFloatingConstant:
        HexadecimalPrefix HexadecimalFractionalConstant BinaryExponentPart FloatingSuffix?
        | HexadecimalPrefix

        ;

fragment
FractionalConstant:
        DigitSequence? '.' DigitSequence
        | DigitSequence '.'
        ;

fragment
ExponentPart:
        ('e' | 'E' ) Sign? DigitSequence
        ;

fragment
Sign:
        [+-]
        ;

fragment
DigitSequence:
        Digit+
        ;

fragment
HexadecimalFractionalConstant:
        HexadecimalDigitSequence? '.' HexadecimalDigitSequence
        | HexadecimalDigitSequence '.'
        ;

fragment
BinaryExponentPart:
        ('p' | 'P') Sign? DigitSequence
        ;

fragment
HexadecimalDigitSequence:
        HexadecimalDigit+
        ;

fragment
FloatingSuffix:
        [flFL]
        ;

// ignore
LineComment:
        '//' ~[\r\n]*
        -> skip
        ;

Whitespace:
        [ \t]+
        -> skip
        ;

Newline:
        ('\r' '\n'?
        | '\n'
        )
        -> skip
        ;

BlockComment:
        '/*' .*? '*/'
        -> skip
        ;
