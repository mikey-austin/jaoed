//
// Main grammar for the configuration file.
//
grammar Config;

config
    : (statement EOL)*
    ;

statement
    : assignment
    | section
    ;

assignment
    : NAME '=' NUMBER
    | NAME '=' STRING
    | NAME '=' BOOLEAN
    | NAME '=' list
    ;

list
    : '[' list_statements ']'
    | '[' list_statements EOL ']'
    | '[' EOL list_statements ']'
    | '[' EOL list_statements EOL ']'
    ;

list_statements
    : list_value list_values
    ;

list_values
    : ',' EOL list_value list_values
    | ',' list_value list_values
    |
    ;

list_value
    : NUMBER
    | STRING
    | BOOLEAN
    ;

section
    : section_type NAME* '{' EOL (statement EOL)* '}'
    ;

section_type
    : 'logging'
    | 'interface'
    | 'default'
    | 'device'
    | 'access-list'
    ;

//
// Token definitions.
//

EOL
    : [\r\n]+
    ;

NUMBER
    : '-'?([0-9]+'.')*[0-9]+
    ;

NAME
    : [a-zA-Z0-9]+
    ;

BOOLEAN
    : 'true'
    | 'false'
    ;

COMMENT
    : '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;

STRING
    : '"' ~('\r' | '\n' | '"')* '"'
    ;

WHITESPACE
    : ('\t' | ' ' | '\r' | '\u000C')+ -> skip
    ;
