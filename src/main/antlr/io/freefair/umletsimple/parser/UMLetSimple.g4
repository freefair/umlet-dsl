// Define a grammar called Hello
grammar UMLetSimple;

@header {
package io.freefair.umletsimple.parser;
}

file: rule_* EOF;
rule_: (package_ | class_ | relation);
package_: 'package' name=id '{' (class_)* '}';
class_: 'class' name=id '{' (members+=classMember)* customBlock? '}';
customBlock: 'custom' ExceptScope;

classMember: attribute | method;
attribute: typename name=id;
method: typename name=id '(' (parameter)* ')';
parameter: typename varname=id;

relation: 'relation' name=id source=relationOperator direction destination=relationOperator;

relationOperator
	: typename ('[' multiplicity ']')?
	;

multiplicity
	: Multiplicity
	;

direction
	: Direction
	;

typename
	: id ('.' id)*
	;

id
	: ID
	;

// Strings

StringLiteral
	: '"' StringCharacters? '"'
	;

fragment
StringCharacters
    :   StringCharacter+
    ;

fragment
StringCharacter
    :   ~["\\]
    |   EscapeSequence
    ;

ExceptScope
	:   '{%' ExceptScopeCharacters+ '%}'
	;

fragment
ExceptScopeCharacters
	:   ~[}{]
	;

// Multiplicity
Multiplicity
	: '1'
	| ('0'|'1') '..' ('1'|'*')
	| '*'
	;

// Direction
Direction
	: ('<'|'<<')? ('-'|'.'|'..'|'--') ('>'|'>>')?
	;

// Escape Sequencens

EscapeSequence
	:   '\\' [btnfr"'\\]
	;

ExceptEscapeSequence
	:   '\\' [{}\\]
	;

// Literals

ID: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'-')*;
CHAR: 'a'..'z' | 'A'..'Z';
NUMBER: '0'..'9';
EXCEPT_CHAR: '{'|'}';

WS
    : [ \r\n\t] -> channel (HIDDEN)
    ;
COMMENT
	: '/*' .*? '*/' -> skip
;
LINE_COMMENT
    : '//' ~[\r\n]* -> skip
;