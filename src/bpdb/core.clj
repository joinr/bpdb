(ns bpdb.core
  (:require [instaparse.core :as insta]))

#_
(def as-and-bs
  (insta/parser
   "S = AB*
    AB = A B
    A = 'a'+
    B = 'b'+"))

;;blueprints are lua objects...

;;ClassName '{' Properties+ '}' ;;denotes class
;;Property = CLASS | VALUE
;;STRING = ''' [a-Z0-9]* '''

;;whitespace = #'\\s+'
(def basic (insta/parser
            "sentence = token (<whitespace> token)*
             <token> = word | number
             word = #'[a-zA-Z]+'
             number = #'[0-9]+'"))

(def basic (insta/parser
            "<object> = TABLE|ARRAY|PRIM
             <PRIM>  = NUMBER|IDENTIFIER|STRING|BOOLEAN|NIL
             NIL     = 'nil'
             BOOLEAN = 'true' | 'false'
             NUMBER = INT|FLOAT
             <INT>   =  #'\\-?[0-9]+'
             <FLOAT> =  #'\\-?0*\\.[0-9]+'
             QUOTE = '\\''
             STRING = <QUOTE> #'[a-zA-Z0-9]*'  <QUOTE>
             IDENTIFIER =  #'[a-zA-Z_]+[a-zA-Z0-9_]*'
             LB    = '{'
             RB    = '}'
             ARRAY = <LB> PRIM+ <RB>
             TABLE = <LB> PRIM '=' (PRIM|TABLE) <LB> 
             LP    = '('
             RP    = ')'
             ARGS  = <LP><RP>
             "))


(def lua (insta/parser "
	chunk ::= block

	block ::= {stat} [retstat]

	stat ::=  ';' |
		 varlist '=' explist |
		 functioncall |
		 label |
		 'break' |
		 'goto' Name |
		 'do' block 'end' |
		 'while' exp 'do' block 'end' |
		 'repeat' block 'until' exp |
		 'if' exp 'then' block {'elseif' exp 'then' block} ['else' block] 'end' |
		 'for' Name '=' exp ',' exp [',' exp] 'do' block 'end' |
		 'for' namelist 'in' explist 'do' block 'end' |
		 'function' funcname funcbody |
		 'local' 'function' Name funcbody |
		 'local' namelist ['=' explist]

	retstat ::= 'return' [explist] [';']

	label ::= '::' Name '::'

	funcname ::= Name {'.' Name} [':' Name]

	varlist ::= var {',' var}

	var ::=  Name | prefixexp '[' exp ']' | prefixexp '.' Name

	namelist ::= Name {',' Name}

	explist ::= exp {',' exp}


	exp ::=  'nil' | 'false' | 'true' | Numeral | LiteralString | '...' | functiondef | 
		 prefixexp | tableconstructor | exp binop exp | unop exp

	prefixexp ::= var | functioncall | '(' exp ')'

	functioncall ::=  prefixexp args | prefixexp ':' Name args 

	args ::=  '(' [explist] ')' | tableconstructor | LiteralString 

	functiondef ::= 'function' funcbody

	funcbody ::= '(' [parlist] ')' block 'end'

	parlist ::= namelist [',' '...'] | '...'

	tableconstructor ::= '{' [fieldlist] '}'

  QUOTE ::= '\\''

  Name ::=  #'[a-zA-Z_]+[a-zA-Z0-9_]*'

  Numeral ::= INT|FLOAT
  <INT>   ::=  #'\\-?[0-9]+'
  <FLOAT> ::=  #'\\-?0*\\.[0-9]+'

  LiteralString ::=  <QUOTE> #'[a-zA-Z0-9]*'  <QUOTE>

	fieldlist ::= field {fieldsep field} [fieldsep]

	field ::= '[' exp ']' '=' exp | Name '=' exp | exp

	fieldsep ::= ',' | ';'

	binop ::=  '+' | '-' | '*' | '/' | '//' | '^' | '%' | 
		 '&' | '~' | '|' | '>>' | '<<' | '..' | 
		 '<' | '<=' | '>' | '>=' | '==' | '~=' | 
		 'and' | 'or'

	unop ::= '-' | 'not' | '#' | '~'
" ))

(def lua (insta/parser "
	chunk ::= block

	block ::= {stat|stat<s>+} [<s>retstat]

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
		 'local'<s>namelist [<s>'='<s>explist]

	retstat ::= 'return'<s>[explist] [<s>';']

	label ::= '::'<s> Name<s> '::'

	funcname ::= Name {'.' Name} [':' Name]

	varlist ::= var {','<s> var}

	var ::=  Name | prefixexp '[' exp ']' | prefixexp '.' Name

	namelist ::= Name {<s>',' Name}

	explist ::= exp {<s>',' exp}


	exp ::=  'nil' | 'false' | 'true' | Numeral | LiteralString | '...' | functiondef | 
		 prefixexp | tableconstructor | exp<s>binop<s>exp | unop<s>exp

	prefixexp ::= var | functioncall | '(' exp ')'

	functioncall ::=  prefixexp args | prefixexp ':' Name args 

	args ::=  '(' [explist] ')' | tableconstructor | LiteralString 

	functiondef ::= 'function'<s>funcbody

	funcbody ::= '(' [parlist] ')'<s> block <s>'end'

	parlist ::= namelist [',' '...'] | '...'

	tableconstructor ::= '{' [fieldlist] '}'

  QUOTE ::= '\\''
  DQUOTE ::= '\"'

  Name ::=  #'[a-zA-Z_]+[a-zA-Z0-9_]*'

  Numeral ::= INT|FLOAT
  <INT>   ::=  #'\\-?[0-9]+'
  <FLOAT> ::=  #'\\-?0*\\.[0-9]+'

  LiteralString ::=  (<QUOTE> #'[a-zA-Z0-9]*'  <QUOTE>) |
                     (<DQUOTE> #'[a-zA-Z0-9]*'  <DQUOTE>)

	fieldlist ::= field<s>{<s>fieldsep<s>field} [fieldsep<s>]

	field ::= '[' exp ']' '=' exp | Name '=' exp | exp

	fieldsep ::= ',' | ';'

	binop ::=  '+' | '-' | '*' | '/' | '//' | '^' | '%' |
		 '&' | '~' | '|' | '>>' | '<<' | '..' |
		 '<' | '<=' | '>' | '>=' | '==' | '~=' |
		 'and' | 'or'

	unop ::= '-' | 'not' | '#' | '~'
  s ::= #'\\s*'
" ))
