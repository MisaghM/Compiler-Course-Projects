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
