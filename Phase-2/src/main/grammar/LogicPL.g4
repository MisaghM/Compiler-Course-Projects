grammar LogicPL;

@header{
import ast.node.*;
import ast.node.expression.*;
import ast.node.statement.*;
import ast.node.declaration.*;
import ast.node.expression.values.*;
import ast.node.expression.operators.*;
import ast.type.primitiveType.*;
import ast.type.*;
}

program returns[Program p]:
    {$p = new Program(); $p.setLine(0);}
    (f = functionDec {$p.addFunc($f.functionDeclaration);})*
    main = mainBlock {$p.setMain($main.main) ;}
    ;

functionDec returns[FuncDeclaration functionDeclaration]:
    {ArrayList<ArgDeclaration> args = new ArrayList<>();
     ArrayList<Statement> statements = new ArrayList<>();}
    FUNCTION name = identifier
    LPAR (arg1 = functionVarDec {args.add($arg1.argDeclaration);} (COMMA arg = functionVarDec {args.add($arg.argDeclaration);})*)? RPAR COLON returnType = type
    LBRACE ((stmt = statement {statements.add($stmt.statementRet);})+) RBRACE
    {$functionDeclaration = new FuncDeclaration($name.identifierRet, $returnType.typeRet, args, statements); $functionDeclaration.setLine($name.identifierRet.getLine());}
    ;

functionVarDec returns [ArgDeclaration argDeclaration]:
    t = type arg_iden = identifier {$argDeclaration = new ArgDeclaration($arg_iden.identifierRet, $t.typeRet); $argDeclaration.setLine($arg_iden.identifierRet.getLine());}
    ;

mainBlock returns [MainDeclaration main]:
    {ArrayList<Statement> mainStmts = new ArrayList<>();}
    m = MAIN LBRACE (s = statement {mainStmts.add($s.statementRet);})+ RBRACE
    {$main = new MainDeclaration(mainStmts); $main.setLine($m.getLine());}
    ;

statement:
    assignSmt | ( predicate SEMICOLON )
    | implication | returnSmt
    | printSmt | forLoop | localVarDeclaration
    ;

assignSmt:
    variable ASSIGN expression SEMICOLON
    ;

variable:
    identifier | identifier LBRACKET expression RBRACKET
    ;

localVarDeclaration:
     varDeclaration
    | arrayDeclaration
    ;

varDeclaration:
    type identifier (ASSIGN expression )? SEMICOLON
    ;

arrayDeclaration:
    type LBRACKET INT_NUMBER RBRACKET identifier
    (arrayInitialValue )? SEMICOLON
    ;

arrayInitialValue:
    ASSIGN arrayList
    ;

arrayList:
    LBRACKET ( value | identifier ) (COMMA ( value | identifier ))* RBRACKET
    ;

printSmt:
    PRINT LPAR printExpr RPAR SEMICOLON
    ;

printExpr:
    variable
    | query
    ;

query:
      queryType1
     | queryType2
    ;

queryType1:
    LBRACKET QUARYMARK predicateIdentifier LPAR variable RPAR RBRACKET
    ;

queryType2:
    LBRACKET predicateIdentifier LPAR QUARYMARK RPAR RBRACKET
    ;

returnSmt:
    RETURN (value  | identifier)? SEMICOLON
    ;

forLoop:
    FOR LPAR identifier COLON identifier RPAR
    LBRACE ((statement)*) RBRACE
    ;

predicate:
    predicateIdentifier LPAR variable RPAR
    ;

implication:
    LPAR expression RPAR ARROW LPAR ((statement)+) RPAR
    ;

expression:
    andExpr expression2
    ;

expression2:
    OR andExpr expression2
    |
    ;

andExpr:
    eqExpr andExpr2
    ;

andExpr2:
    AND eqExpr andExpr2
    |
    ;

eqExpr:
    compExpr eqExpr2
    ;

eqExpr2:
    ( EQ | NEQ ) compExpr eqExpr2
    |
    ;

compExpr:
    additive compExpr2
    ;

compExpr2:
    ( LT | LTE | GT | GTE) additive compExpr2
    |
    ;

additive:
    multicative additive2
    ;

additive2:
    ( PLUS | MINUS ) multicative additive2
    |
    ;

multicative:
    unary multicative2
    ;

multicative2:
    ( MULT | MOD | DIV ) unary multicative2
    |
    ;

unary:
    other
    |
     ( PLUS | MINUS | NOT ) other
    ;

other:
    LPAR expression RPAR | variable | value
    | queryType1 | functionCall
    ;

functionCall:
    identifier LPAR (expression (COMMA expression)*)? RPAR
    ;

value:
    numericValue
    | TRUE
    | FALSE
    | MINUS numericValue
    ;

numericValue:
    INT_NUMBER
    | FLOAT_NUMBER
    ;

identifier:
    IDENTIFIER
    ;

predicateIdentifier:
    PREDICATE_IDENTIFIER
    ;

type:
    BOOLEAN
    | INT
    | FLOAT
    ;




FUNCTION : 'function';
BOOLEAN : 'boolean';
INT : 'int';
FLOAT: 'float';
MAIN: 'main';
PRINT: 'print';
RETURN: 'return';
FOR: 'for';
TRUE: 'true';
FALSE: 'false';

LPAR: '(';
RPAR: ')';
COLON: ':';
COMMA: ',';
LBRACE: '{';
RBRACE: '}';
SEMICOLON: ';';
ASSIGN: '=';
LBRACKET: '[';
RBRACKET: ']';
QUARYMARK: '?';
ARROW: '=>';
OR: '||';
AND: '&&';
EQ: '==';
GT: '>';
LT: '<';
GTE: '>=';
LTE: '<=';
PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
MOD: '%';
NEQ: '!=';
NOT: '!';


WS : [ \t\r\n]+ -> skip ;
COMMENT : '#' ~[\r\n]* -> skip ;

IDENTIFIER : [a-z][a-zA-Z0-9_]* ;
PREDICATE_IDENTIFIER : [A-Z][a-zA-Z0-9]* ;
INT_NUMBER : [0-9]+;
FLOAT_NUMBER: ([0-9]*[.])?[0-9]+;