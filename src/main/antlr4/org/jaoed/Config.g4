//
// Main grammar for the configuration file.
//
grammar Config;

config
    : statements EOL* EOF
    ;

statements
    : statement (EOL* statement)*
    ;

statement
    : assignment
    | section
    | EOL
    ;

assignment
    : assignmentName '=' NUMBER
    | assignmentName '=' STRING
    | assignmentName '=' BOOLEAN
    | assignmentName '=' list
    | assignmentName '=' NAME
    ;

assignmentName
    : NAME
    | sectionType
    ;

list
    : '[' ']'
    | '[' EOL* listStatements EOL* ']'
    ;

listStatements
    : listValue (',' EOL* listValue)*
    ;

listValue
    : NUMBER
    | STRING
    | BOOLEAN
    ;

section
    : sectionType '{' EOL* sectionStatements EOL* '}'
    ;

sectionStatements
    : assignment (EOL* assignment)*
    ;

sectionType
    : 'logger'
    | 'interface'
    | 'device'
    | 'access-list'
    ;

//
// Token definitions.
//

NUMBER
    : '-'?([0-9]+'.')*[0-9]+
    ;

NAME
    : [a-zA-Z0-9_-]+
    ;

BOOLEAN
    : 'true'
    | 'false'
    ;

COMMENT
    : '#' ~[\r\n]* -> skip
    ;

STRING
    : '"' ~('\r' | '\n' | '"')* '"'
    ;

EOL
    : ('\r'?'\n')
    ;

WHITESPACE
    : ('\t' | ' ' | '\r' | '\u000C')+ -> skip
    ;
