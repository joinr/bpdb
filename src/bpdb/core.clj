(ns bpdb.core
  (:require [instaparse.core :as insta]))

;;blueprints are lua objects...

;;ClassName '{' Properties+ '}' ;;denotes class
;;Property = CLASS | VALUE
;;STRING = ''' [a-Z0-9]* '''

;; (def lua (insta/parser "
;; 	chunk ::= block

;; 	block ::= {stat} [retstat]

;; 	stat ::=  ';' |
;; 		 varlist '=' explist |
;; 		 functioncall |
;; 		 label |
;; 		 'break' |
;; 		 'goto' Name |
;; 		 'do' block 'end' |
;; 		 'while' exp 'do' block 'end' |
;; 		 'repeat' block 'until' exp |
;; 		 'if' exp 'then' block {'elseif' exp 'then' block} ['else' block] 'end' |
;; 		 'for' Name '=' exp ',' exp [',' exp] 'do' block 'end' |
;; 		 'for' namelist 'in' explist 'do' block 'end' |
;; 		 'function' funcname funcbody |
;; 		 'local' 'function' Name funcbody |
;; 		 'local' namelist ['=' explist]

;; 	retstat ::= 'return' [explist] [';']

;; 	label ::= '::' Name '::'

;; 	funcname ::= Name {'.' Name} [':' Name]

;; 	varlist ::= var {',' var}

;; 	var ::=  Name | prefixexp '[' exp ']' | prefixexp '.' Name

;; 	namelist ::= Name {',' Name}

;; 	explist ::= exp {',' exp}


;; 	exp ::=  'nil' | 'false' | 'true' | Numeral | LiteralString | '...' | functiondef | 
;; 		 prefixexp | tableconstructor | exp binop exp | unop exp

;; 	prefixexp ::= var | functioncall | '(' exp ')'

;; 	functioncall ::=  prefixexp args | prefixexp ':' Name args 

;; 	args ::=  '(' [explist] ')' | tableconstructor | LiteralString 

;; 	functiondef ::= 'function' funcbody

;; 	funcbody ::= '(' [parlist] ')' block 'end'

;; 	parlist ::= namelist [',' '...'] | '...'

;; 	tableconstructor ::= '{' [fieldlist] '}'

;;   QUOTE ::= '\\''

;;   Name ::=  #'[a-zA-Z_]+[a-zA-Z0-9_]*'

;;   Numeral ::= INT|FLOAT
;;   <INT>   ::=  #'\\-?[0-9]+'
;;   <FLOAT> ::=  #'\\-?0*\\.[0-9]+'

;;   LiteralString ::=  <QUOTE> #'[a-zA-Z0-9]*'  <QUOTE>

;; 	fieldlist ::= field {fieldsep field} [fieldsep]

;; 	field ::= '[' exp ']' '=' exp | Name '=' exp | exp

;; 	fieldsep ::= ',' | ';'

;; 	binop ::=  '+' | '-' | '*' | '/' | '//' | '^' | '%' | 
;; 		 '&' | '~' | '|' | '>>' | '<<' | '..' | 
;; 		 '<' | '<=' | '>' | '>=' | '==' | '~=' | 
;; 		 'and' | 'or'

;; 	unop ::= '-' | 'not' | '#' | '~'
;; " ))

;;Basic working lua parser; some rough edges but this is mostly
;;copy and paste, with some adaptation for instaparse...
(def lua (insta/parser "
	chunk ::= block

	block ::= {stat|(stat<s>)+} [<s>retstat]

	stat ::=  ';' |
		 varlist<s>'='<s>explist |
		 functioncall |
		 label |
		 'break' |
		 'goto'<s>Name |
		 'do'<s>block<s>'end' |
		 'while'<s>exp<s>'do'<s>block<s>'end' |
		 'repeat'<s>block<s>'until'<s>exp |
		 'if'<s>exp<s>'then'<s>block<s>{'elseif'<s>exp<s>'then'<s>block} [<s>'else'<s>block]<s>'end' |
		 'for'<s>Name<s>'='<s>exp<s>','<s>exp [','<s>exp]<s>'do'<s>block<s>'end' |
		 'for'<s>namelist<s>'in'<s>explist<s>'do'<s>block<s>'end'<s>|
		 'function'<s>funcname<s>funcbody|
		 'local'<s>'function'<s>Name<s>funcbody |
		 'local'<s>namelist [<s>'='<s>explist] |
      comment

	retstat ::= 'return'<s>[explist] [<s>';']

	label ::= '::'<s> Name<s> '::'

  EOL ::= #'.*$'
  comment ::= linecomment | blockcomment
  linecomment   ::=  '--'  #'(?!\\[\\[).*'  <s> <EOL>
  blockcomment  ::=  #'^' <s> '--[[' <s>  #'.*' <s> #'.*\\]\\]'   <EOL>

	funcname ::= Name {'.' Name} [':' Name]

	varlist ::= var {','<s> var}

	var ::=  Name | prefixexp '[' exp ']' | prefixexp '.' Name

	namelist ::= Name {<s>',' Name}

	explist ::= exp {<s>',' exp}


	exp ::=  'nil' | 'false' | 'true' | Numeral | LiteralString | '...' | functiondef |
		 prefixexp | tableconstructor | exp<s>binop<s>exp | unop<s>exp | comment

	prefixexp ::= var | functioncall | '(' exp ')'

	functioncall ::=  prefixexp args | prefixexp ':' Name args | Name <s> tableconstructor

	args ::=  '(' [explist] ')' | tableconstructor | LiteralString

	functiondef ::= 'function'<s>funcbody

	funcbody ::= '(' [parlist] ')'<s> block <s>'end'

	parlist ::= namelist [',' '...'] | '...'

	tableconstructor ::= '{'<s> [fieldlist]<s> '}'

  QUOTE ::= '\\''
  DQUOTE ::= '\"'

  Name ::=  #'[a-zA-Z_]+[a-zA-Z0-9_]*'

  Numeral ::= INT|FLOAT|SCIENTIFIC
  <INT>   ::=  #'\\d+'
  <FLOAT> ::=  #'\\d*\\.\\d+'
  <SCIENTIFIC> ::=  #'\\d*[eE]\\d+'

  LiteralString ::=  qstring | dstring
  qstring   ::= <QUOTE> #'[/ <>a-zA-Z0-9_]*'  <QUOTE>
  dstring   ::= <DQUOTE> #'[/ <>a-zA-Z0-9_]*'  <DQUOTE>

	fieldlist ::= field<s>{<s>fieldsep<s>field} [fieldsep<s>]

	field ::= '[' exp ']'<s> '=' <s> exp | Name <s> '=' <s> exp | exp

	fieldsep ::= ',' | ';'

	binop ::=  '+' | '-' | '*' | '/' | '//' | '^' | '%' |
		 '&' | '~' | '|' | '>>' | '<<' | '..' |
		 '<' | '<=' | '>' | '>=' | '==' | '~=' |
		 'and' | 'or'

	unop ::= '-' | 'not' | '#' | '~'
  s ::= #'\\s*'
" ))



;;linecomment   ::=  #'^.*'  <s> '--' <s> #'(?!\\[\\[).*'  <s> <EOL>
                        ;;                        blockcomment  ::=  #'^' <s> '--[[' <s>  #'.*' <s> #'.*\\]\\]'   <EOL>
