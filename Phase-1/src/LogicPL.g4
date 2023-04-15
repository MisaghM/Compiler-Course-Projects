grammar LogicPL;

logicPL
    : (function | COMMENT)* main COMMENT*
    ;

primitive
    : INT
    | FLOAT
    | BOOLEAN
    ;

primitive_val
    : INT_VAL
    | ZERO
    | FLOAT_VAL
    | BOOLEAN_VAL
    ;

primitive_val_sign
    : op = ('-' | '+') (INT_VAL | FLOAT_VAL) { System.out.println("Operator: " + $op.text); }
    ;

main
    :
    MAIN { System.out.println("MainBody"); }
    LBRACE
    body_function
    RBRACE
    ;

function
    :
    FUNCTION
    name = IDENTIFIER { System.out.println("FunctionDec: " + $name.text); }
    LPAR
    (
    primitive
    idn = IDENTIFIER { System.out.println("ArgumentDec: " + $idn.text); }
        (
        COMMA
        primitive
        idn2 = IDENTIFIER { System.out.println("ArgumentDec: " + $idn2.text); }
        )*
    )?
    RPAR
    COLON
    primitive
    LBRACE
    body_function
    RBRACE
    ;

body_function
    : (statement | COMMENT)*
    ;

statement
    : (print
    | returnf
    | declaration
    | assignment
    | initialization
    | initialization_array
    | function_call
    | predicate_def) SEMICOLON
    | forloop
    | implication
    ;

print
    :
    PRINT { System.out.println("Built-in: print"); }
    LPAR
    (IDENTIFIER | array | query_bool | query_list)
    RPAR
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
    body_function
    RBRACE
    ;

returnf
    :
    RETURN { System.out.println("Return"); }
    (IDENTIFIER | primitive_val | primitive_val_sign)?
    ;

array
    :
    IDENTIFIER
    LBRACKET
    expr_arith_plus_minus
    RBRACKET
    ;

declaration
    :
    primitive
    (
    LBRACKET
    INT_VAL
    RBRACKET
    )?
    name = IDENTIFIER { System.out.println("VarDec: " + $name.text); }
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
    expr
    ;

initialization
    :
    primitive
    name = IDENTIFIER { System.out.println("VarDec: " + $name.text); }
    ASSIGN
    expr
    ;

initialization_array
    :
    primitive
    LBRACKET
    INT_VAL
    RBRACKET
    name = IDENTIFIER { System.out.println("VarDec: " + $name.text); }
    ASSIGN
    LBRACKET
    (primitive_val | primitive_val_sign)
    (
    COMMA
    (primitive_val | primitive_val_sign)
    )*
    RBRACKET
    ;

function_call
    :
    IDENTIFIER
    LPAR
    (
    expr
        (
        COMMA
        expr
        )*
    )?
    RPAR
    ;

predicate_def
    :
    name = PREDICATE { System.out.println("Predicate: " + $name.text); }
    LPAR
    (IDENTIFIER | array)
    RPAR
    ;

implication
    :
    LPAR { System.out.println("Implication"); }
    expr
    RPAR
    ARROW
    LPAR
    body_function
    RPAR
    ;

query_bool
    :
    LBRACKET
    QUESTION
    predicate_def
    RBRACKET
    ;

query_list
    :
    LBRACKET
    name = PREDICATE { System.out.println("Predicate: " + $name.text); }
    LPAR
    QUESTION
    RPAR
    RBRACKET
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
    : PLUS expr_other { System.out.println("Operator: +"); }
    | MINUS expr_other { System.out.println("Operator: -"); }
    | NOT expr_other { System.out.println("Operator: !"); }
    | expr_other
    ;

expr_other
    : LPAR expr RPAR
    | array
    | IDENTIFIER
    | function_call
    | query_bool
    | primitive_val
    ;

// Keywords

MAIN:     'main';
FUNCTION: 'function';
PRINT:    'print';
FOR:      'for';
RETURN:   'return';

// Types

INT:       'int';
FLOAT:     'float';
BOOLEAN:   'boolean';

// Type Values

ZERO:        '0';
INT_VAL:     [1-9][0-9]*;
FLOAT_VAL:   INT_VAL '.' [0-9]+ | '0.' [0-9]*;
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
