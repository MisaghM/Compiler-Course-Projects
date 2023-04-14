grammar LogicPL;

logicPL
    : function* main
    ;

main
    :
    MAIN { System.out.println("MainBody"); }
    LBRACE
    'main body'
    RBRACE
    ;

function
    :
    FUNCTION
    name = IDENTIFIER { System.out.println("FunctionDec: " + $name.text); }
    LPAR
    (
    PRIMITIVE
    idn = IDENTIFIER { System.out.println("ArgumentDec: " + $idn.text); }
        (
        COMMA
        PRIMITIVE
        idn2 = IDENTIFIER { System.out.println("ArgumentDec: " + $idn2.text); }
        )*
    )?
    RPAR
    COLON
    PRIMITIVE
    LBRACE
    'function body'
    RBRACE
    ;

print
    :
    PRINT { System.out.println("Built-in: print"); }
    LPAR
    'print body'
    RPAR
    SEMICOLON
    ;

forloop
    :
    FOR { System.out.println("Loop: for"); }
    LPAR
    IDENTIFIER
    COLON
    IDENTIFIER
    RPAR
    LBRACE
    'forloop body'
    RBRACE
    ;

returnf
    :
    RETURN { System.out.println("Return"); }
    SEMICOLON
    ;

statement
    : print
    | returnf
    | declaration
    | assignment
    | initialization
    | function_call
    | predicate_def
    | query_type1
    | query_type2
    | implication
    ;

declaration
    :
    PRIMITIVE
    (
    LBRACKET
    INT_VAL
    RBRACKET
    )?
    name = IDENTIFIER { System.out.println("VarDec: " + $name.text); }
    SEMICOLON
    ;

assignment
    :
    IDENTIFIER
    (
    LBRACKET
    INT_VAL
    RBRACKET
    )?
    ASSIGN
    'rhs'
    SEMICOLON
    ;

initialization
    :
    PRIMITIVE
    (
    LBRACKET
    INT_VAL
    RBRACKET
    )?
    name = IDENTIFIER { System.out.println("VarDec: " + $name.text); }
    ASSIGN
    'rhs'
    SEMICOLON
    ;

function_call
    :
    IDENTIFIER
    LPAR
    'call body'
    RPAR
    ;

predicate_def
    :
    name = PREDICATE { System.out.println("Predicate: " + $name.text); }
    LPAR
    IDENTIFIER
    RPAR
    ;

query_type1
    :
    LBRACKET
    QUESTION
    predicate_def
    RBRACKET
    ;

query_type2
    :
    LBRACKET
    name = PREDICATE { System.out.println("Predicate: " + $name.text); }
    LPAR
    QUESTION
    RPAR
    RBRACKET
    ;

implication
    :
    LPAR { System.out.println("Implication"); }
    expr
    RPAR
    ARROW
    LPAR
    'body'
    RPAR
    ;

expr
    : expr_logic_or
    ;

expr_logic_or
    : expr_logic_and (OR expr_logic_and { System.out.println("Operator: ||"); })*
    ;

expr_logic_and
    : expr_rel_eq_neq (AND expr_rel_eq_neq { System.out.println("Operator: &&"); })*
    ;

expr_rel_eq_neq
    : expr_rel_cmp (EQL expr_rel_cmp { System.out.println("Operator: =="); } |
                    NEQ expr_rel_cmp { System.out.println("Operator: !="); })*
    ;

expr_rel_cmp
    : expr_arith_plus_minus (GTR expr_arith_plus_minus { System.out.println("Operator: >"); } |
                             GEQ expr_arith_plus_minus { System.out.println("Operator: >="); } |
                             LES expr_arith_plus_minus { System.out.println("Operator: <"); } |
                             LEQ expr_arith_plus_minus { System.out.println("Operator: <="); })*
    ;

expr_arith_plus_minus
    : expr_arith_mult_div_mod (PLUS expr_arith_mult_div_mod { System.out.println("Operator: +"); } |
                               MINUS expr_arith_mult_div_mod { System.out.println("Operator: -"); })*
    ;

expr_arith_mult_div_mod
    : expr_unary_plus_minus_not (MULT expr_unary_plus_minus_not { System.out.println("Operator: *"); } |
                                 DIV expr_unary_plus_minus_not { System.out.println("Operator: /"); } |
                                 MOD expr_unary_plus_minus_not { System.out.println("Operator: %"); })*
    ;

expr_unary_plus_minus_not
    : PLUS expr_array_bracket { System.out.println("Operator: +"); }
    | MINUS expr_array_bracket { System.out.println("Operator: -"); }
    | NOT expr_array_bracket { System.out.println("Operator: !"); }
    | expr_array_bracket
    ;

expr_array_bracket
    :
    ;

expr_parenthesis
    :
    ;

// Keywords

MAIN:     'main';
FUNCTION: 'function';
PRINT:    'print';
FOR:      'for';
RETURN:   'return';

// Types

PRIMITIVE: INT | FLOAT | BOOLEAN;
INT:       'int';
FLOAT:     'float';
BOOLEAN:   'boolean';

// Type Values

INT_VAL:     [0-9]+;
FLOAT_VAL:   INT_VAL '.' INT_VAL? | '.' INT_VAL;
BOOLEAN_VAL: 'true' | 'false';

// Parenthesis

LPAR: '(';
RPAR: ')';

// Brackets (array element access)

LBRACKET: '[';
RBRACKET: ']';

// Arithmetic Operators

NOT:   '!';
PLUS:  '+';
MINUS: '-';
MULT:  '*';
DIV:   '/';
MOD:   '%';

// Relational Operators

GEQ: '>=';
LEQ: '<=';
GTR: '>';
LES: '<';
EQL: '==';
NEQ: '!=';

// Logical Operators

AND: '&&';
OR:  '||';

// Other Operators

ASSIGN: '=';

// Symbols

LBRACE:    '{';
RBRACE:    '}';
COMMA:     ',';
DOT:       '.';
COLON:     ':';
SEMICOLON: ';';
QUESTION:  '?';

// Other

IDENTIFIER: [a-z][a-zA-Z0-9_]*;
PREDICATE:  [A-Z][a-zA-Z0-9_]*;
ARROW:      '=>';
COMMENT:    '#' ~[\r\n]* -> skip;
WS:         [ \t\r\n]+ -> skip;
