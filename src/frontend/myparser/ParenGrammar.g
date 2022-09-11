Goal:
    List
    ;
List:
    List Pair
    | Pair
    ;
Pair:
    ( Pair )
    | ( )
    ;
END_OF_GRAMMAR