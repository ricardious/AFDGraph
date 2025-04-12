# Lexical Specification for AFD Files (`.lfp`)

## General Syntax (Regular Expression)
Below is a simplified regular expression describing the general syntax for the tokens that your lexer must recognize:
```regex
\s*(AFD|name|description|states|alphabet|initial|final|transitions|\{|\}|\[|\]|,|:|->|=|"([^"\\]|\\.)*"|//[^\n]*|[a-zA-Z_][a-zA-Z0-9_]*|\d+(\.\d+)?)
```
## Formal Regular Expression (simplified)

The following theoretical regular expression describes the exact required structure of your `.lfp` file, using basic regular expression symbols:

```plaintext
AFD . { . name . : . "STRING_LITERAL" . , . description . : . "STRING_LITERAL" . , 
states . : . [ . IDENTIFIER . (. , . IDENTIFIER)* . ] . ,
alphabet . : . [ . "STRING_LITERAL" . (. , . "STRING_LITERAL")* . ] . ,
initial . : . IDENTIFIER . ,
final . : . [ . IDENTIFIER . (. , . IDENTIFIER)* . ] . ,
transitions . : . { . 
    IDENTIFIER . = . ( . "STRING_LITERAL" . -> . IDENTIFIER . (. , . "STRING_LITERAL" . -> . IDENTIFIER)* . ) 
    (. , . IDENTIFIER . = . ( . "STRING_LITERAL" . -> . IDENTIFIER . (. , . "STRING_LITERAL" . -> . IDENTIFIER)* . ))* 
. } . 
}
```
## Token Table for AFD Specification

| Token Type            | Description                           | Example                |
|-----------------------|---------------------------------------|------------------------|
| AUTOMATON_NAME        | Defines start of automaton definition | "AFD"                  |
| NAME_KEYWORD          | Specifies automaton name              | "name"                 |
| DESCRIPTION_KEYWORD   | Defines automaton description         | "description"          |
| STATES_KEYWORD        | Lists all states                      | "states"               |
| ALPHABET_KEYWORD      | Defines input alphabet                | "alphabet"             |
| INITIAL_STATE_KEYWORD | Marks initial state                   | "initial"              |
| FINAL_STATES_KEYWORD  | Lists final states                    | "final"                |
| TRANSITIONS_KEYWORD   | Defines state transitions             | "transitions"          |
| LEFT_BRACE            | Opening curly brace                   | "{"                    |
| RIGHT_BRACE           | Closing curly brace                   | "}"                    |
| LEFT_BRACKET          | Opening square bracket                | "["                    |
| RIGHT_BRACKET         | Closing square bracket                | "]"                    |
| COMMA                 | Separator                             | ","                    |
| COLON                 | Key-value separator                   | ":"                    |
| ARROW                 | State transition symbol               | "->"                   |
| EQUALS                | Assignment                            | "="                    |
| IDENTIFIER            | State or symbol names                 | "S0", "a", "1"         |
| STRING_LITERAL        | Quoted text                           | "\"number automaton\"" |
| NUMBER_LITERAL        | Numeric values                        | "42", "3.14"           |
| WHITESPACE            | Ignored spaces/newlines               | " ", "\n", "\t"        |
| COMMENT               | Explanatory text (optional)           | "// This is a comment" |

## Additional Notes
- Identifiers start with a letter or underscore
- String literals are enclosed in quotes
- Number literals can be integers or decimals
- Whitespace is generally ignored
- Comments can be single-line or multi-line