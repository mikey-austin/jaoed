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
    : section
    | EOL
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
    : 'logger' '{' loggerStatements '}'       # loggerSection
    | 'interface' '{' interfaceStatements '}' # interfaceSection
    | 'device' '{' deviceStatements '}'       # deviceSection
    | 'acl' '{' aclStatements '}'             # aclSection
    ;

logLevel
    : 'info'
    | 'debug'
    | 'trace'
    ;

//
// Logger section syntax.
//
loggerStatements
    : EOL* loggerAssignment (EOL* loggerAssignment)* EOL*
    ;

loggerAssignment
    : 'name' '=' STRING              # loggerName
    | 'type' '=' ('file' | 'syslog') # loggerType
    | 'file' '=' STRING              # loggerFile
    | 'syslog-level' '=' INTEGER     # loggerSyslogLevel
    | 'syslog-facility' '=' INTEGER  # loggerSyslogFacility
    ;

//
// Interface section syntax.
//
interfaceStatements
    : EOL* interfaceAssignment (EOL* interfaceAssignment)* EOL*
    ;

interfaceAssignment
    : 'name' '=' STRING            # interfaceName
    | 'mtu' '=' ('auto' | INTEGER) # interfaceMtu
    | 'logger' '=' SYMBOL          # interfaceLogger
    | 'log-level' '=' logLevel     # interfaceLogLevel
    ;

//
// Device section syntax.
//
deviceStatements
    : EOL* deviceAssignment (EOL* deviceAssignment)* EOL*
    ;

deviceAssignment
    : 'shelf' '=' INTEGER              # deviceShelf
    | 'slot' '=' INTEGER               # deviceSlot
    | 'target' '=' STRING              # deviceTarget
    | 'interface' '=' SYMBOL           # deviceInterface
    | 'write-cache' '=' ('on' | 'off') # deviceWriteCache
    | 'broadcast' '=' BOOLEAN          # deviceBroadcast
    | 'logger' '=' SYMBOL              # deviceLogger
    | 'log-level' '=' logLevel         # deviceLogLevel
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
    | 'log-level' '=' logLevel           # aclLogLevel
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
