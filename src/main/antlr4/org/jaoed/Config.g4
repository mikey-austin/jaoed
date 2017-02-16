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
    | bool # boolEntry
    ;

section
    : 'logger' SYMBOL '{' loggerStatements '}'       # loggerSection
    | 'interface' SYMBOL '{' interfaceStatements '}' # interfaceSection
    | 'acl' SYMBOL '{' aclStatements '}'             # aclSection
    | 'device' '{' deviceStatements '}'              # deviceSection
    ;

logLevel
    : 'info'
    | 'debug'
    | 'trace'
    ;

bool
    : 'true'
    | 'false'
    ;

//
// Logger section syntax.
//
loggerStatements
    : EOL* loggerAssignment (EOL* loggerAssignment)* EOL*
    ;

loggerAssignment
    : 'type' '=' ('file' | 'syslog') # loggerType
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
    : 'mtu' '=' ('auto' | INTEGER) # interfaceMtu
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
    | 'broadcast' '=' bool             # deviceBroadcast
    | 'logger' '=' SYMBOL              # deviceLogger
    | 'log-level' '=' logLevel         # deviceLogLevel
    | deviceAclSection                 # deviceAcl
    ;

deviceAclSection
    : 'acl' '{' deviceAclStatements '}'
    ;

deviceAclStatements
    : EOL* deviceAclAssignment (EOL* deviceAclAssignment)* EOL*
    ;

deviceAclAssignment
    : 'cfg-read' '=' SYMBOL # deviceAclCfgRead
    | 'cfg-set' '=' SYMBOL  # deviceAclCfgSet
    | 'read' '=' SYMBOL     # deviceAclRead
    | 'write' '=' SYMBOL    # deviceAclWrite
    ;

//
// Acl section syntax.
//
aclStatements
    : EOL* aclAssignment (EOL* aclAssignment)* EOL*
    ;

aclAssignment
    : 'policy' '=' ('accept' | 'reject') # aclPolicy
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
