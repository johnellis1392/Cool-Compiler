/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

%{

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */

    // Integer for counting nested comments
    int nestedCommentCount=0;

    // Boolean value for determining if string is too long
    boolean stringTooLong=false;

    // Boolean value for determining if null character occurs in string
    boolean nullInString=false;

    // Boolean value for some error occuring in string
    boolean stringError=false;

    // Boolean for if an EOF is encountered
    boolean eof_encountered=false;

    // Boolean for escape character found
    boolean backslashEscaped=false;
    
    // Max size of string constants
    static int MAX_STR_CONST = 1025;

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();

    private int curr_lineno = 1;
    int get_curr_lineno() {
	return curr_lineno;
    }

    private AbstractSymbol filename;

    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
	return filename;
    }
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */

    // empty for now
%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */

    if(eof_encountered) return new Symbol(TokenConstants.EOF);
    switch(yy_lexical_state) {
    case YYINITIAL:
	/* nothing special to do in the initial state */
	break;
	/* If necessary, add code for other states here, e.g:
	   case COMMENT:
	   ...
	   break;
	*/
    case STRING:
      if(eof_encountered) break;
      eof_encountered=true;
      if(backslashEscaped) return new Symbol(TokenConstants.ERROR, "String contains escaped null character.");
      else return new Symbol(TokenConstants.ERROR, "Error: EOF Encountered in String.");
    case BLOCK_COMMENT:
      eof_encountered=true;
      return new Symbol(TokenConstants.ERROR, "Error: EOF Encountered in Block Comment.");
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup

%state STRING
%state BLOCK_COMMENT

%%


<YYINITIAL> \"   { 
  string_buf.delete(0, string_buf.length());
  yybegin(STRING); 
  stringTooLong=false;
  nullInString=false;
  stringError=false;
  backslashEscaped=false;
 }

<STRING> \" { // If an escape character appears before a quote, we must put a quote into the string
  if(stringTooLong) {
    yybegin(YYINITIAL);
    return new Symbol(TokenConstants.ERROR, "String constant too long");
  } else if(nullInString) {
    yybegin(YYINITIAL);
    eof_encountered=true;
    return new Symbol(TokenConstants.ERROR, "String contains escaped null character.");
  }

  if(string_buf.length()>0 && string_buf.charAt(string_buf.length()-1)=='\\' && backslashEscaped) {
    string_buf.setCharAt(string_buf.length()-1, '\"');
  } else {
    yybegin(YYINITIAL); 
    return new Symbol(TokenConstants.STR_CONST, AbstractTable.stringtable.addString(string_buf.toString())); 
  }
 }


<STRING> \n { // Code for newline characters
  // If a newline appears in a string, it must be escaped, else error
  if(!backslashEscaped || string_buf.length()==0) {
    curr_lineno++;
    yybegin(YYINITIAL);
    return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
  } else {
    // Replace '\' character in string buffer with newline
    string_buf.setCharAt(string_buf.length()-1, '\n');
    curr_lineno++;
  }
 }

<STRING> [^\n\"] { // Code for handling other characters encountered in strings
  // including special cases for escaped characters
  if(stringTooLong || nullInString) {
    // Don't do anything
  } else if(string_buf.length()==0) {
    string_buf.append(yytext());
    if(string_buf.charAt(0)=='\\') backslashEscaped=true;
  } else {
    
    // Check if null character appears in input stream
    if(yytext().charAt(0)=='\0' || nullInString) {
      nullInString=true;
    } else {
      int length=string_buf.length();
      if(string_buf.charAt(string_buf.length()-1)=='\\' && backslashEscaped) {
	switch(yytext().charAt(0)) {
	case 'b':
	  string_buf.setCharAt(length-1, '\b');
	  break;
	case 't':
	  string_buf.setCharAt(length-1, '\t');
	  break;
	case 'n':
	  string_buf.setCharAt(length-1, '\n');
	  break;
	case 'f':
	  string_buf.setCharAt(length-1, '\f');
	  break;
	case '\\':
	  //string_buf.append('\\');
	  break;
	default:
	  string_buf.setCharAt(length-1, yytext().charAt(0));
	}
	backslashEscaped=false;
      } else if(yytext().charAt(0)=='\\') {
	backslashEscaped=true;
	string_buf.append(yytext());
      } else {
	string_buf.append(yytext());
      }

      if(string_buf.length()>=MAX_STR_CONST) {
	stringTooLong=true;
      }
    }
  }
 }

<YYINITIAL> \n { curr_lineno++; }
<YYINITIAL> "(*" { yybegin(BLOCK_COMMENT); }
<YYINITIAL> "*)" { return new Symbol(TokenConstants.ERROR,"Mismatched '*)'"); }
<BLOCK_COMMENT> "("   { }
<BLOCK_COMMENT> "*"   { }
<BLOCK_COMMENT> ")"   { }
<BLOCK_COMMENT> "(*"  { nestedCommentCount++; }
<BLOCK_COMMENT> "*)" {
  if(nestedCommentCount!=0) {
    nestedCommentCount--;
  } else {
    yybegin(YYINITIAL);
  }
 }
<BLOCK_COMMENT> \n    { curr_lineno++; }
<BLOCK_COMMENT> [^*\n\(\)]+  { /* Do Nothing */ }





<YYINITIAL> [t][rR][uU][eE]                  { return new Symbol(TokenConstants.BOOL_CONST, new Boolean(true)); }
<YYINITIAL> [f][aA][lL][sS][eE]              { return new Symbol(TokenConstants.BOOL_CONST, new Boolean(false)); }
<YYINITIAL> [cC][aA][sS][eE]                 { return new Symbol(TokenConstants.CASE); }
<YYINITIAL> [cC][lL][aA][sS][sS]             { return new Symbol(TokenConstants.CLASS); }
<YYINITIAL> [eE][lL][sS][eE]                 { return new Symbol(TokenConstants.ELSE); }
<YYINITIAL> [fF][iI]                         { return new Symbol(TokenConstants.FI); }
<YYINITIAL> [iI][fF]                         { return new Symbol(TokenConstants.IF); }
<YYINITIAL> [iI][nN]                         { return new Symbol(TokenConstants.IN); }
<YYINITIAL> [iI][nN][hH][eE][rR][iI][tT][sS] { return new Symbol(TokenConstants.INHERITS); }
<YYINITIAL> [iI][sS][vV][oO][iI][dD]         { return new Symbol(TokenConstants.ISVOID); }
<YYINITIAL> [lL][eE][tT]                     { return new Symbol(TokenConstants.LET); }
<YYINITIAL> [lL][oO][oO][pP]                 { return new Symbol(TokenConstants.LOOP); }
<YYINITIAL> [pP][oO][oO][lL]                 { return new Symbol(TokenConstants.POOL); }
<YYINITIAL> [tT][hH][eE][nN]                 { return new Symbol(TokenConstants.THEN); }
<YYINITIAL> [wW][hH][iI][lL][eE]             { return new Symbol(TokenConstants.WHILE); }
<YYINITIAL> [eE][sS][aA][cC]                 { return new Symbol(TokenConstants.ESAC); }
<YYINITIAL> [nN][eE][wW]                     { return new Symbol(TokenConstants.NEW); }
<YYINITIAL> [oO][fF]                         { return new Symbol(TokenConstants.OF); }
<YYINITIAL> [nN][oO][tT]                     { return new Symbol(TokenConstants.NOT); }

<YYINITIAL>"=>"	  { return new Symbol(TokenConstants.DARROW); }
<YYINITIAL> "*"   { return new Symbol(TokenConstants.MULT);   }
<YYINITIAL> "("   { return new Symbol(TokenConstants.LPAREN); }
<YYINITIAL> ";"   { return new Symbol(TokenConstants.SEMI);   }
<YYINITIAL> "-"   { return new Symbol(TokenConstants.MINUS);  }
<YYINITIAL> ")"   { return new Symbol(TokenConstants.RPAREN); }
<YYINITIAL> "<"   { return new Symbol(TokenConstants.LT);     }
<YYINITIAL> ","   { return new Symbol(TokenConstants.COMMA);  }
<YYINITIAL> "/"   { return new Symbol(TokenConstants.DIV);    }
<YYINITIAL> "+"   { return new Symbol(TokenConstants.PLUS);   }
<YYINITIAL> "<-"  { return new Symbol(TokenConstants.ASSIGN); }
<YYINITIAL> "."   { return new Symbol(TokenConstants.DOT);    }
<YYINITIAL> "<="  { return new Symbol(TokenConstants.LE);     }
<YYINITIAL> "="   { return new Symbol(TokenConstants.EQ);     }
<YYINITIAL> ":"   { return new Symbol(TokenConstants.COLON);  }
<YYINITIAL> "{"   { return new Symbol(TokenConstants.LBRACE); }
<YYINITIAL> "}"   { return new Symbol(TokenConstants.RBRACE); }
<YYINITIAL> "@"   { return new Symbol(TokenConstants.AT);     }
<YYINITIAL> "~"   { return new Symbol(TokenConstants.NEG);    }
<YYINITIAL> "--".* { /* End of Line comment: Do nothing */ }

<YYINITIAL> [ \t\n\r\f\013]+    { /* Get rid of whitespace */ }

<YYINITIAL> [a-z][_a-zA-Z0-9]* { return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
<YYINITIAL> [A-Z][_a-zA-Z0-9]* { return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
<YYINITIAL> [0-9]*             { return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext())); }

<YYINITIAL> [^\n] { return new Symbol(TokenConstants.ERROR, new String(yytext())); }

.                               { /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
                                  return new Symbol(TokenConstants.ERROR, yytext()); }


