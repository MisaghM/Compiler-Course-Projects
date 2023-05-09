grammar LogicPL;

@header {
    import ast.node.*;
    import ast.node.expression.*;
    import ast.node.statement.*;
    import ast.node.declaration.*;
    import ast.node.expression.values.*;
    import ast.node.expression.operators.*;
    import ast.type.primitiveType.*;
    import ast.type.*;
}

program returns [Program p]:
    {
        $p = new Program();
        $p.setLine(0);
    }
    (f=functionDec { $p.addFunc($f.functionDeclaration); })*
    main=mainBlock { $p.setMain($main.main); }
    ;

functionDec returns [FuncDeclaration functionDeclaration]:
    {
        ArrayList<ArgDeclaration> args = new ArrayList<>();
        ArrayList<Statement> statements = new ArrayList<>();
    }
    FUNCTION name=identifier LPAR
    (arg1=functionVarDec { args.add($arg1.argDeclaration); } (COMMA arg=functionVarDec { args.add($arg.argDeclaration); })*)?
    RPAR COLON returnType=type LBRACE
    (stmt=statement { statements.add($stmt.statementRet); })+
    RBRACE
    {
        $functionDeclaration = new FuncDeclaration($name.identifierRet, $returnType.typeRet, args, statements);
        $functionDeclaration.setLine($name.identifierRet.getLine());
    }
    ;

functionVarDec returns [ArgDeclaration argDeclaration]:
    t=type argIden=identifier
    {
        $argDeclaration = new ArgDeclaration($argIden.identifierRet, $t.typeRet);
        $argDeclaration.setLine($argIden.identifierRet.getLine());
    }
    ;

mainBlock returns [MainDeclaration main]:
    {
        ArrayList<Statement> mainStmts = new ArrayList<>();
    }
    m=MAIN LBRACE (s=statement { mainStmts.add($s.statementRet); })+ RBRACE
    {
        $main = new MainDeclaration(mainStmts);
        $main.setLine($m.getLine());
    }
    ;

statement returns [Statement statementRet]:
    assignStmt=assignSmt             { $statementRet = $assignStmt.assignSmtRet; }
    | predStmt=predicate SEMICOLON   { $statementRet = $predStmt.predicateRet; }
    | implStmt=implication           { $statementRet = $implStmt.implicationRet; }
    | retStmt=returnSmt              { $statementRet = $retStmt.returnRet; }
    | printStmt=printSmt             { $statementRet = $printStmt.printRet; }
    | forStmt=forLoop                { $statementRet = $forStmt.forloopRet; }
    | vardecStmt=localVarDeclaration { $statementRet = $vardecStmt.varDecRet; }
    ;

assignSmt returns [AssignStmt assignSmtRet]:
    lval=variable ASSIGN rval=expression SEMICOLON
    {
        $assignSmtRet = new AssignStmt($lval.variableRet, $rval.exprRet);
        $assignSmtRet.setLine($lval.variableRet.getLine());
    }
    ;

variable returns [Variable variableRet]:
    idn=identifier
    {
        $variableRet = $idn.identifierRet;
    }
    | arrIdn=identifier LBRACKET arrIdx=expression RBRACKET
    {
        $variableRet = new ArrayAccess($arrIdn.identifierRet.getName(), $arrIdx.exprRet);
        $variableRet.setLine($arrIdn.identifierRet.getLine());
    }
    ;

localVarDeclaration returns [Statement varDecRet]:
    vd=varDeclaration        { $varDecRet = $vd.varDecRet; }
    | vdArr=arrayDeclaration { $varDecRet = $vdArr.arrDecRet; }
    ;

varDeclaration returns [VarDecStmt varDecRet]:
    t=type idn=identifier
    {
        $varDecRet = new VarDecStmt($idn.identifierRet, $t.typeRet);
        $varDecRet.setLine($idn.identifierRet.getLine());
    }
    (ASSIGN e=expression { $varDecRet.setInitialExpression($e.exprRet); })? SEMICOLON
    ;

arrayDeclaration returns [ArrayDecStmt arrDecRet]:
    t=type b=LBRACKET size=INT_NUMBER RBRACKET idn=identifier
    {
        $arrDecRet = new ArrayDecStmt($idn.identifierRet, $t.typeRet, $size.int);
        $arrDecRet.setLine($b.getLine());
    }
    (arrInit=arrayInitialValue[$arrDecRet])? SEMICOLON
    ;

arrayInitialValue [ArrayDecStmt arrDecInp]:
    ASSIGN arrayList[$arrDecInp]
    ;

arrayList [ArrayDecStmt arrDecInp]:
    {
        ArrayList<Expression> args = new ArrayList<>();
    }
    LBRACKET (v=value { args.add($v.valueRet); } | idn=identifier { args.add($idn.identifierRet); })
    (COMMA   (v=value { args.add($v.valueRet); } | idn=identifier { args.add($idn.identifierRet); }))* RBRACKET
    {
        $arrDecInp.setInitialValues(args);
    }
    ;

printSmt returns [PrintStmt printRet]:
    p=PRINT LPAR e=printExpr RPAR SEMICOLON
    {
        $printRet = new PrintStmt($e.printExprRet);
        $printRet.setLine($p.getLine());
    }
    ;

printExpr returns [Expression printExprRet]:
    v=variable { $printExprRet = $v.variableRet; }
    | q=query  { $printExprRet = $q.queryRet; }
    ;

query returns [QueryExpression queryRet]:
    t1=queryType1   { $queryRet = $t1.queryRet; }
    | t2=queryType2 { $queryRet = $t2.queryRet; }
    ;

queryType1 returns [QueryExpression queryRet]:
    line=LBRACKET QUERYMARK predIdn=predicateIdentifier LPAR var=variable RPAR RBRACKET
    {
        $queryRet = new QueryExpression($predIdn.identifierRet);
        $queryRet.setVar($var.variableRet);
        $queryRet.setLine($line.getLine());
    }
    ;

queryType2 returns [QueryExpression queryRet]:
    line=LBRACKET predIdn=predicateIdentifier LPAR QUERYMARK RPAR RBRACKET
    {
        $queryRet = new QueryExpression($predIdn.identifierRet);
        $queryRet.setVar(null);
        $queryRet.setLine($line.getLine());
    }
    ;

returnSmt returns [ReturnStmt returnRet]:
    line=RETURN
    ( v=value        { $returnRet = new ReturnStmt($v.valueRet); }
    | idn=identifier { $returnRet = new ReturnStmt($idn.identifierRet); })?
    SEMICOLON
    {
        if ($returnRet == null) {
            $returnRet = new ReturnStmt(null);
        }
        $returnRet.setLine($line.getLine());
    }
    ;

forLoop returns [ForloopStmt forloopRet]:
    {
        ArrayList<Statement> stmts = new ArrayList<>();
    }
    line=FOR LPAR name=identifier COLON target=identifier RPAR LBRACE (stmt=statement { stmts.add($stmt.statementRet); })* RBRACE
    {
        $forloopRet = new ForloopStmt($name.identifierRet, $target.identifierRet, stmts);
        $forloopRet.setLine($line.getLine());
    }
    ;

predicate returns [PredicateStmt predicateRet]:
    name=predicateIdentifier LPAR var=variable RPAR
    {
        $predicateRet = new PredicateStmt($name.identifierRet, $var.variableRet);
        $predicateRet.setLine($name.identifierRet.getLine());
    }
    ;

implication returns [ImplicationStmt implicationRet]:
    {
        ArrayList<Statement> stmts = new ArrayList<>();
    }
    line=LPAR expr=expression RPAR ARROW LPAR (stmt=statement { stmts.add($stmt.statementRet); })+ RPAR
    {
        $implicationRet = new ImplicationStmt($expr.exprRet, stmts);
        $implicationRet.setLine($line.getLine());
    }
    ;

expression returns [Expression exprRet]:
    left=andExpr right=expression2
    {
        if ($right.expr2Ret != null) {
            $exprRet = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            $exprRet.setLine($right.expr2Ret.getLine());
        }
        else {
            $exprRet = $left.exprRet;
        }
    }
    ;

expression2 returns [BinaryExpression expr2Ret]:
    op=OR { BinaryOperator opEnum = BinaryOperator.or; }
    left=andExpr right=expression2
    {
        if ($right.expr2Ret != null) {
            BinaryExpression binExpr = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            binExpr.setLine($right.expr2Ret.getLine());
            $expr2Ret = new BinaryExpression(null, binExpr, opEnum);
        }
        else {
            $expr2Ret = new BinaryExpression(null, $left.exprRet, opEnum);
        }
        $expr2Ret.setLine($op.getLine());
    }
    | { $expr2Ret = null; } // epsilon
    ;

andExpr returns [Expression exprRet]:
    left=eqExpr right=andExpr2
    {
        if ($right.expr2Ret != null) {
            $exprRet = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            $exprRet.setLine($right.expr2Ret.getLine());
        }
        else {
            $exprRet = $left.exprRet;
        }
    }
    ;

andExpr2 returns [BinaryExpression expr2Ret]:
    op=AND { BinaryOperator opEnum = BinaryOperator.and; }
    left=eqExpr right=andExpr2
    {
        if ($right.expr2Ret != null) {
            BinaryExpression binExpr = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            binExpr.setLine($right.expr2Ret.getLine());
            $expr2Ret = new BinaryExpression(null, binExpr, opEnum);
        }
        else {
            $expr2Ret = new BinaryExpression(null, $left.exprRet, opEnum);
        }
        $expr2Ret.setLine($op.getLine());
    }
    | { $expr2Ret = null; } // epsilon
    ;

eqExpr returns [Expression exprRet]:
    left=compExpr right=eqExpr2
    {
        if ($right.expr2Ret != null) {
            $exprRet = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            $exprRet.setLine($right.expr2Ret.getLine());
        }
        else {
            $exprRet = $left.exprRet;
        }
    }
    ;

eqExpr2 returns [BinaryExpression expr2Ret]:
    {
        BinaryOperator opEnum;
    }
    ( op=EQ  { opEnum = BinaryOperator.eq; }
    | op=NEQ { opEnum = BinaryOperator.neq; })
    left=compExpr right=eqExpr2
    {
        if ($right.expr2Ret != null) {
            BinaryExpression binExpr = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            binExpr.setLine($right.expr2Ret.getLine());
            $expr2Ret = new BinaryExpression(null, binExpr, opEnum);
        }
        else {
            $expr2Ret = new BinaryExpression(null, $left.exprRet, opEnum);
        }
        $expr2Ret.setLine($op.getLine());
    }
    | { $expr2Ret = null; } // epsilon
    ;

compExpr returns [Expression exprRet]:
    left=additive right=compExpr2
    {
        if ($right.expr2Ret != null) {
            $exprRet = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            $exprRet.setLine($right.expr2Ret.getLine());
        }
        else {
            $exprRet = $left.exprRet;
        }
    }
    ;

compExpr2 returns [BinaryExpression expr2Ret]:
    {
        BinaryOperator opEnum;
    }
    ( op=LT  { opEnum = BinaryOperator.lt; }
    | op=LTE { opEnum = BinaryOperator.lte; }
    | op=GT  { opEnum = BinaryOperator.gt; }
    | op=GTE { opEnum = BinaryOperator.gte; })
    left=additive right=compExpr2
    {
        if ($right.expr2Ret != null) {
            BinaryExpression binExpr = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            binExpr.setLine($right.expr2Ret.getLine());
            $expr2Ret = new BinaryExpression(null, binExpr, opEnum);
        }
        else {
            $expr2Ret = new BinaryExpression(null, $left.exprRet, opEnum);
        }
        $expr2Ret.setLine($op.getLine());
    }
    | { $expr2Ret = null; } // epsilon
    ;

additive returns [Expression exprRet]:
    left=multicative right=additive2
    {
        if ($right.expr2Ret != null) {
            $exprRet = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            $exprRet.setLine($right.expr2Ret.getLine());
        }
        else {
            $exprRet = $left.exprRet;
        }
    }
    ;

additive2 returns [BinaryExpression expr2Ret]:
    {
        BinaryOperator opEnum;
    }
    ( op=PLUS  { opEnum = BinaryOperator.add; }
    | op=MINUS { opEnum = BinaryOperator.sub; })
    left=multicative right=additive2
    {
        if ($right.expr2Ret != null) {
            BinaryExpression binExpr = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            binExpr.setLine($right.expr2Ret.getLine());
            $expr2Ret = new BinaryExpression(null, binExpr, opEnum);
        }
        else {
            $expr2Ret = new BinaryExpression(null, $left.exprRet, opEnum);
        }
        $expr2Ret.setLine($op.getLine());
    }
    | { $expr2Ret = null; } // epsilon
    ;

multicative returns [Expression exprRet]:
    left=unary right=multicative2
    {
        if ($right.expr2Ret != null) {
            $exprRet = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            $exprRet.setLine($right.expr2Ret.getLine());
        }
        else {
            $exprRet = $left.exprRet;
        }
    }
    ;

multicative2 returns [BinaryExpression expr2Ret]:
    {
        BinaryOperator opEnum;
    }
    ( op=MULT { opEnum = BinaryOperator.mult; }
    | op=MOD  { opEnum = BinaryOperator.mod; }
    | op=DIV  { opEnum = BinaryOperator.div; })
    left=unary right=multicative2
    {
        if ($right.expr2Ret != null) {
            BinaryExpression binExpr = new BinaryExpression($left.exprRet, $right.expr2Ret.getRight(), $right.expr2Ret.getBinaryOperator());
            binExpr.setLine($right.expr2Ret.getLine());
            $expr2Ret = new BinaryExpression(null, binExpr, opEnum);
        }
        else {
            $expr2Ret = new BinaryExpression(null, $left.exprRet, opEnum);
        }
        $expr2Ret.setLine($op.getLine());
    }
    | { $expr2Ret = null; } // epsilon
    ;

unary returns [Expression exprRet]:
    ot=other { $exprRet = $ot.exprRet; }
    | { UnaryOperator uop = UnaryOperator.not; }
    (
        PLUS { uop = UnaryOperator.plus; } |
        MINUS { uop = UnaryOperator.minus; } |
        NOT { uop = UnaryOperator.not; }
    ) ot=other
    {
        $exprRet = new UnaryExpression(uop, $ot.exprRet);
        $exprRet.setLine($ot.exprRet.getLine());
    }
    ;

other returns [Expression exprRet]:
    LPAR expr=expression RPAR { $exprRet = $expr.exprRet; }
    | exprVar=variable        { $exprRet = $exprVar.variableRet; }
    | exprVal=value           { $exprRet = $exprVal.valueRet; }
    | exprQuery=queryType1    { $exprRet = $exprQuery.queryRet; }
    | exprFunc=functionCall   { $exprRet = $exprFunc.functionCallRet; }
    ;

functionCall returns [FunctionCall functionCallRet]:
    {
        ArrayList<Expression> exprs = new ArrayList<>();
    }
    name=identifier LPAR
    (e1=expression { exprs.add($e1.exprRet); } (COMMA e=expression { exprs.add($e.exprRet); })*)?
    RPAR
    {
        $functionCallRet = new FunctionCall(exprs, $name.identifierRet);
        $functionCallRet.setLine($name.identifierRet.getLine());
    }
    ;

value returns [Value valueRet]:
    n=numericValue         { $valueRet = $n.valueRet; }
    | t=TRUE               { $valueRet = new BooleanValue(true);  $valueRet.setLine($t.getLine()); }
    | f=FALSE              { $valueRet = new BooleanValue(false); $valueRet.setLine($f.getLine()); }
    | MINUS m=numericValue { $m.valueRet.negateConstant(); $valueRet = $m.valueRet; }
    ;

numericValue returns [Value valueRet]:
    i=INT_NUMBER     { $valueRet = new IntValue($i.int);                      $valueRet.setLine($i.getLine()); }
    | f=FLOAT_NUMBER { $valueRet = new FloatValue(Float.parseFloat($f.text)); $valueRet.setLine($f.getLine()); }
    ;

identifier returns [Identifier identifierRet]:
    idn=IDENTIFIER           { $identifierRet = new Identifier($idn.text); $identifierRet.setLine($idn.getLine()); }
    ;

predicateIdentifier returns [Identifier identifierRet]:
    idn=PREDICATE_IDENTIFIER { $identifierRet = new Identifier($idn.text); $identifierRet.setLine($idn.getLine()); }
    ;

type returns [Type typeRet]:
    b=BOOLEAN { $typeRet = new BooleanType(); }
    | i=INT   { $typeRet = new IntType(); }
    | f=FLOAT { $typeRet = new FloatType(); }
    ;


FUNCTION: 'function';
BOOLEAN:  'boolean';
INT:      'int';
FLOAT:    'float';
MAIN:     'main';
PRINT:    'print';
RETURN:   'return';
FOR:      'for';
TRUE:     'true';
FALSE:    'false';

LPAR:      '(';
RPAR:      ')';
COLON:     ':';
COMMA:     ',';
LBRACE:    '{';
RBRACE:    '}';
SEMICOLON: ';';
ASSIGN:    '=';
LBRACKET:  '[';
RBRACKET:  ']';
QUERYMARK: '?';
ARROW:     '=>';
OR:        '||';
AND:       '&&';
EQ:        '==';
GT:        '>';
LT:        '<';
GTE:       '>=';
LTE:       '<=';
PLUS:      '+';
MINUS:     '-';
MULT:      '*';
DIV:       '/';
MOD:       '%';
NEQ:       '!=';
NOT:       '!';

WS: [ \t\r\n]+ -> skip;
COMMENT: '#' ~[\r\n]* -> skip;

IDENTIFIER: [a-z][a-zA-Z0-9_]*;
PREDICATE_IDENTIFIER: [A-Z][a-zA-Z0-9]*;
INT_NUMBER: [0-9]+;
FLOAT_NUMBER: ([0-9]*[.])?[0-9]+;
