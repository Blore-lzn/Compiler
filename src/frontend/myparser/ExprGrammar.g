Goal:
    Expr
    ;
Expr:
    Term Expr'
    ;
Expr':
    + Term Expr'
    | - Term Expr'
    | eps
    ;
Term:
    Factor Term'
    ;
Term':
    * Factor Term'
    | / Factor Term'
    | eps
    ;
Factor:
    ( Expr )
    | num
    | name
    ;
END_OF_GRAMMAR
