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
    : assignmentName '=' INTEGER # intVal
    | assignmentName '=' STRING  # strVal
    | assignmentName '=' BOOLEAN # boolVal
    | assignmentName '=' list    # listVal
    | assignmentName '=' SYMBOL  # symVal
    ;

assignmentName
    : SYMBOL
    | ('device' | 'interface' | 'access-list' | 'logger')
    ;

list
    : '[' ']'
    | '[' EOL* listStatements EOL* ']'
    ;

listStatements
    : listEntry (',' EOL* listEntry)*
    ;

listEntry
    : INTEGER # intEntry
    | STRING  # strEntry
    | BOOLEAN # boolEntry
    ;

section
    : 'logger' '{' sectionStatements '}'    # loggerSection
    | 'interface' '{' sectionStatements '}' # interfaceSection
    | 'device' '{' sectionStatements '}'    # deviceSection
    | 'access-list' '{' aclStatements '}'   # aclSection
    ;

sectionStatements
    : EOL* assignment (EOL* assignment)* EOL*
    ;

//
// Acl section syntax.
//
aclStatements
    : EOL* aclAssignment (EOL* aclAssignment)* EOL*
    ;

aclAssignment
    : 'name' '=' STRING                  # aclName
    | 'policy' '=' ('accept' | 'reject') # aclPolicy
    | 'accept' '=' list                  # aclAccept
    | 'reject' '=' list                  # aclReject
    | 'logger' '=' SYMBOL                # aclLogger
    ;

//
// Token definitions.
//

INTEGER
    : '-'?[0-9]+
    ;

SYMBOL
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
