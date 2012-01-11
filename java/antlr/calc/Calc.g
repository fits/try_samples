grammar Calc;

expr
	: term ((PLUS|MINUS) term)*
	;
term
	: factor ((MULTIPLY|DIVIDE) factor)*
	;
factor
	: INT | LP expr RP
	;

INT
	:	'0'..'9'+
	;
LP
	:	'('
	;
RP
	:	')'
	;
PLUS
	:	'+'
	;
MINUS
 	:	'-'
	;
MULTIPLY
 	:	'*'
	;
DIVIDE
 	:	'/'
	;
