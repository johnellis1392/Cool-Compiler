/*
 *  The scanner definition for COOL.
 */
import java_cup.runtime.Symbol;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */
    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int STRING = 1;
	private final int BLOCK_COMMENT = 2;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0,
		49,
		80
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NO_ANCHOR,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NOT_ACCEPT,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NOT_ACCEPT,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"40:9,41,2,41:2,3,40:18,41,40,1,40:5,4,6,5,33,31,29,34,32,60:10,35,28,30,26," +
"27,40,38,12,59,15,23,10,16,59,19,17,59:2,13,59,18,22,24,59,8,14,20,9,21,25," +
"59:3,40:4,58,40,42,43,44,45,46,11,43,47,48,43:2,49,43,50,51,52,43,53,54,7,5" +
"5,56,57,43:3,36,40,37,39,40,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,164,
"0,1:2,2:2,3,4,1,5,6,7,1:2,8,9,1:11,10,6,11,6,1,12,1:2,6:3,10:2,6:5,10,6:4,1" +
"3,1:3,14,1,15,16,1:3,17,18,19,6,10,20,10:4,6,10:9,21,22,23,24,25,26,27,28,2" +
"9,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,5" +
"4,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,7" +
"9,80,81,82,83,84,85,86,87,10,88,89,90,91,92,93,94,95,96,97,98,99,100,101,10" +
"2,103")[0];

	private int yy_nxt[][] = unpackFromString(104,61,
"1,2,3,4,5,6,7,8,9:2,148,61,9,114,9,150,62,82,116,9,152,9,84,9,154,160,10,11" +
",12,13,14,15,16,17,18,19,20,21,22,23,11,4,147:2,149,147,151,147,81,113,115," +
"83,153,147:4,155,11,9,60,-1:63,4:2,-1:37,4,-1:24,24,-1:61,25,-1:61,147,117," +
"147:10,119,147:6,-1:16,147:5,119,147:5,117,147:7,-1:7,9:19,-1:16,9:19,-1:27" +
",30,-1:62,31,-1:57,32,-1:2,33,-1:38,147:19,-1:16,147:19,-1:7,9:12,163,9:6,-" +
"1:16,9:5,163,9:13,-1,31,-1:2,31:57,1,50,51,52:58,-1,53,-1,53,-1:3,53:54,-1:" +
"5,58,-1:61,59,-1:114,60,-1:7,147:5,121,147:4,26,147:8,-1:16,121,147:5,26,14" +
"7:12,-1:7,9:10,63,9:8,-1:16,9:6,63,9:12,-1:7,147:12,141,147:6,-1:16,147:5,1" +
"41,147:13,1,53,54,53,55,56,57,53:54,-1:7,147:4,64,147:2,131,147,64,147,65,1" +
"47:7,-1:16,147:8,65,147:3,131,147:6,-1:7,9:4,27,9:2,161,9,27,9,28,9:7,-1:16" +
",9:8,28,9:3,161,9:6,-1:7,147:4,66,147:4,66,147:9,-1:16,147:19,-1:7,9:4,29,9" +
":4,29,9:9,-1:16,9:19,-1:7,67,147:12,67,147:5,-1:16,147:19,-1:7,34,9:12,34,9" +
":5,-1:16,9:19,-1:7,147:18,68,-1:16,147:15,68,147:3,-1:7,9:18,35,-1:16,9:15," +
"35,9:3,-1:7,69,147:12,69,147:5,-1:16,147:19,-1:7,36,9:12,36,9:5,-1:16,9:19," +
"-1:7,147:3,37,147:15,-1:16,147:4,37,147:14,-1:7,9:3,39,9:15,-1:16,9:4,39,9:" +
"14,-1:7,147:11,38,147:7,-1:16,147:8,38,147:10,-1:7,9:8,40,9:10,-1:16,9:2,40" +
",9:16,-1:7,147:3,74,147:15,-1:16,147:4,74,147:14,-1:7,9:17,41,9,-1:16,9:10," +
"41,9:8,-1:7,147:3,71,147:15,-1:16,147:4,71,147:14,-1:7,9:3,42,9:15,-1:16,9:" +
"4,42,9:14,-1:7,147:8,72,147:10,-1:16,147:2,72,147:16,-1:7,9:11,70,9:7,-1:16" +
",9:8,70,9:10,-1:7,147:17,73,147,-1:16,147:10,73,147:8,-1:7,9:6,43,9:12,-1:1" +
"6,9:7,43,9:11,-1:7,147:6,75,147:12,-1:16,147:7,75,147:11,-1:7,9:7,45,9:11,-" +
"1:16,9:12,45,9:6,-1:7,147:3,44,147:15,-1:16,147:4,44,147:14,-1:7,9:3,46,9:1" +
"5,-1:16,9:4,46,9:14,-1:7,147:7,76,147:11,-1:16,147:12,76,147:6,-1:7,9:16,47" +
",9:2,-1:16,9:3,47,9:15,-1:7,147:3,77,147:15,-1:16,147:4,77,147:14,-1:7,9:7," +
"48,9:11,-1:16,9:12,48,9:6,-1:7,147:16,78,147:2,-1:16,147:3,78,147:15,-1:7,1" +
"47:7,79,147:11,-1:16,147:12,79,147:6,-1:7,147:3,85,147:11,133,147:3,-1:16,1" +
"47:4,85,147:4,133,147:9,-1:7,9:3,86,9:11,122,9:3,-1:16,9:4,86,9:4,122,9:9,-" +
"1:7,147:3,87,147:11,89,147:3,-1:16,147:4,87,147:4,89,147:9,-1:7,9:3,88,9:11" +
",90,9:3,-1:16,9:4,88,9:4,90,9:9,-1:7,147:2,91,147:16,-1:16,147:13,91,147:5," +
"-1:7,9:7,92,9:11,-1:16,9:12,92,9:6,-1:7,147:3,93,147:15,-1:16,147:4,93,147:" +
"14,-1:7,9:5,94,9:13,-1:16,94,9:18,-1:7,147:6,138,147:12,-1:16,147:7,138,147" +
":11,-1:7,9:15,96,9:3,-1:16,9:9,96,9:9,-1:7,147:7,95,147:11,-1:16,147:12,95," +
"147:6,-1:7,9:7,98,9:11,-1:16,9:12,98,9:6,-1:7,147:5,139,147:13,-1:16,139,14" +
"7:18,-1:7,9:3,100,9:15,-1:16,9:4,100,9:14,-1:7,147:7,97,147:11,-1:16,147:12" +
",97,147:6,-1:7,9:15,102,9:3,-1:16,9:9,102,9:9,-1:7,147:5,99,147:13,-1:16,99" +
",147:18,-1:7,9:7,104,9:11,-1:16,9:12,104,9:6,-1:7,147:14,140,147:4,-1:16,14" +
"7:14,140,147:4,-1:7,9:6,106,9:12,-1:16,9:7,106,9:11,-1:7,147:15,101,147:3,-" +
"1:16,147:9,101,147:9,-1:7,9:10,108,9:8,-1:16,9:6,108,9:12,-1:7,147:15,103,1" +
"47:3,-1:16,147:9,103,147:9,-1:7,110,9:12,110,9:5,-1:16,9:19,-1:7,147:10,142" +
",147:8,-1:16,147:6,142,147:12,-1:7,147:7,105,147:11,-1:16,147:12,105,147:6," +
"-1:7,147:7,107,147:11,-1:16,147:12,107,147:6,-1:7,147:15,143,147:3,-1:16,14" +
"7:9,143,147:9,-1:7,147:3,144,147:15,-1:16,147:4,144,147:14,-1:7,147:6,109,1" +
"47:12,-1:16,147:7,109,147:11,-1:7,147:10,111,147:8,-1:16,147:6,111,147:12,-" +
"1:7,147,145,147:17,-1:16,147:11,145,147:7,-1:7,147:10,146,147:8,-1:16,147:6" +
",146,147:12,-1:7,112,147:12,112,147:5,-1:16,147:19,-1:7,9:6,118,120,9:11,-1" +
":16,9:7,118,9:4,120,9:6,-1:7,147:5,123,125,147:12,-1:16,123,147:6,125,147:1" +
"1,-1:7,9:5,124,156,9:12,-1:16,124,9:6,156,9:11,-1:7,147:6,127,129,147:11,-1" +
":16,147:7,127,147:4,129,147:6,-1:7,9:12,126,9:6,-1:16,9:5,126,9:13,-1:7,147" +
":15,135,147:3,-1:16,147:9,135,147:9,-1:7,9:15,128,9:3,-1:16,9:9,128,9:9,-1:" +
"7,147:12,137,147:6,-1:16,147:5,137,147:13,-1:7,9:5,130,9:13,-1:16,130,9:18," +
"-1:7,9:10,132,9:8,-1:16,9:6,132,9:12,-1:7,9:15,134,9:3,-1:16,9:9,134,9:9,-1" +
":7,9:10,136,9:8,-1:16,9:6,136,9:12,-1:7,9:12,157,9:6,-1:16,9:5,157,9:13,-1:" +
"7,9:14,158,9:4,-1:16,9:14,158,9:4,-1:7,9,159,9:17,-1:16,9:11,159,9:7,-1:7,9" +
":3,162,9:15,-1:16,9:4,162,9:14");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

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
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 0:
						{ return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext())); }
					case -2:
						break;
					case 1:
						
					case -3:
						break;
					case 2:
						{ 
  string_buf.delete(0, string_buf.length());
  yybegin(STRING); 
  stringTooLong=false;
  nullInString=false;
  stringError=false;
  backslashEscaped=false;
 }
					case -4:
						break;
					case 3:
						{ curr_lineno++; }
					case -5:
						break;
					case 4:
						{ /* Get rid of whitespace */ }
					case -6:
						break;
					case 5:
						{ return new Symbol(TokenConstants.LPAREN); }
					case -7:
						break;
					case 6:
						{ return new Symbol(TokenConstants.MULT);   }
					case -8:
						break;
					case 7:
						{ return new Symbol(TokenConstants.RPAREN); }
					case -9:
						break;
					case 8:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -10:
						break;
					case 9:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -11:
						break;
					case 10:
						{ return new Symbol(TokenConstants.EQ);     }
					case -12:
						break;
					case 11:
						{ return new Symbol(TokenConstants.ERROR, new String(yytext())); }
					case -13:
						break;
					case 12:
						{ return new Symbol(TokenConstants.SEMI);   }
					case -14:
						break;
					case 13:
						{ return new Symbol(TokenConstants.MINUS);  }
					case -15:
						break;
					case 14:
						{ return new Symbol(TokenConstants.LT);     }
					case -16:
						break;
					case 15:
						{ return new Symbol(TokenConstants.COMMA);  }
					case -17:
						break;
					case 16:
						{ return new Symbol(TokenConstants.DIV);    }
					case -18:
						break;
					case 17:
						{ return new Symbol(TokenConstants.PLUS);   }
					case -19:
						break;
					case 18:
						{ return new Symbol(TokenConstants.DOT);    }
					case -20:
						break;
					case 19:
						{ return new Symbol(TokenConstants.COLON);  }
					case -21:
						break;
					case 20:
						{ return new Symbol(TokenConstants.LBRACE); }
					case -22:
						break;
					case 21:
						{ return new Symbol(TokenConstants.RBRACE); }
					case -23:
						break;
					case 22:
						{ return new Symbol(TokenConstants.AT);     }
					case -24:
						break;
					case 23:
						{ return new Symbol(TokenConstants.NEG);    }
					case -25:
						break;
					case 24:
						{ yybegin(BLOCK_COMMENT); }
					case -26:
						break;
					case 25:
						{ return new Symbol(TokenConstants.ERROR,"Mismatched '*)'"); }
					case -27:
						break;
					case 26:
						{ return new Symbol(TokenConstants.FI); }
					case -28:
						break;
					case 27:
						{ return new Symbol(TokenConstants.IF); }
					case -29:
						break;
					case 28:
						{ return new Symbol(TokenConstants.IN); }
					case -30:
						break;
					case 29:
						{ return new Symbol(TokenConstants.OF); }
					case -31:
						break;
					case 30:
						{ return new Symbol(TokenConstants.DARROW); }
					case -32:
						break;
					case 31:
						{ /* End of Line comment: Do nothing */ }
					case -33:
						break;
					case 32:
						{ return new Symbol(TokenConstants.LE);     }
					case -34:
						break;
					case 33:
						{ return new Symbol(TokenConstants.ASSIGN); }
					case -35:
						break;
					case 34:
						{ return new Symbol(TokenConstants.LET); }
					case -36:
						break;
					case 35:
						{ return new Symbol(TokenConstants.NEW); }
					case -37:
						break;
					case 36:
						{ return new Symbol(TokenConstants.NOT); }
					case -38:
						break;
					case 37:
						{ return new Symbol(TokenConstants.BOOL_CONST, new Boolean(true)); }
					case -39:
						break;
					case 38:
						{ return new Symbol(TokenConstants.THEN); }
					case -40:
						break;
					case 39:
						{ return new Symbol(TokenConstants.ELSE); }
					case -41:
						break;
					case 40:
						{ return new Symbol(TokenConstants.ESAC); }
					case -42:
						break;
					case 41:
						{ return new Symbol(TokenConstants.LOOP); }
					case -43:
						break;
					case 42:
						{ return new Symbol(TokenConstants.CASE); }
					case -44:
						break;
					case 43:
						{ return new Symbol(TokenConstants.POOL); }
					case -45:
						break;
					case 44:
						{ return new Symbol(TokenConstants.BOOL_CONST, new Boolean(false)); }
					case -46:
						break;
					case 45:
						{ return new Symbol(TokenConstants.CLASS); }
					case -47:
						break;
					case 46:
						{ return new Symbol(TokenConstants.WHILE); }
					case -48:
						break;
					case 47:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -49:
						break;
					case 48:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -50:
						break;
					case 50:
						{ // If an escape character appears before a quote, we must put a quote into the string
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
					case -51:
						break;
					case 51:
						{ // Code for newline characters
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
					case -52:
						break;
					case 52:
						{ // Code for handling other characters encountered in strings
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
					case -53:
						break;
					case 53:
						{ /* Do Nothing */ }
					case -54:
						break;
					case 54:
						{ curr_lineno++; }
					case -55:
						break;
					case 55:
						{ }
					case -56:
						break;
					case 56:
						{ }
					case -57:
						break;
					case 57:
						{ }
					case -58:
						break;
					case 58:
						{ nestedCommentCount++; }
					case -59:
						break;
					case 59:
						{
  if(nestedCommentCount!=0) {
    nestedCommentCount--;
  } else {
    yybegin(YYINITIAL);
  }
 }
					case -60:
						break;
					case 60:
						{ return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext())); }
					case -61:
						break;
					case 61:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -62:
						break;
					case 62:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -63:
						break;
					case 63:
						{ return new Symbol(TokenConstants.FI); }
					case -64:
						break;
					case 64:
						{ return new Symbol(TokenConstants.IF); }
					case -65:
						break;
					case 65:
						{ return new Symbol(TokenConstants.IN); }
					case -66:
						break;
					case 66:
						{ return new Symbol(TokenConstants.OF); }
					case -67:
						break;
					case 67:
						{ return new Symbol(TokenConstants.LET); }
					case -68:
						break;
					case 68:
						{ return new Symbol(TokenConstants.NEW); }
					case -69:
						break;
					case 69:
						{ return new Symbol(TokenConstants.NOT); }
					case -70:
						break;
					case 70:
						{ return new Symbol(TokenConstants.THEN); }
					case -71:
						break;
					case 71:
						{ return new Symbol(TokenConstants.ELSE); }
					case -72:
						break;
					case 72:
						{ return new Symbol(TokenConstants.ESAC); }
					case -73:
						break;
					case 73:
						{ return new Symbol(TokenConstants.LOOP); }
					case -74:
						break;
					case 74:
						{ return new Symbol(TokenConstants.CASE); }
					case -75:
						break;
					case 75:
						{ return new Symbol(TokenConstants.POOL); }
					case -76:
						break;
					case 76:
						{ return new Symbol(TokenConstants.CLASS); }
					case -77:
						break;
					case 77:
						{ return new Symbol(TokenConstants.WHILE); }
					case -78:
						break;
					case 78:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -79:
						break;
					case 79:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -80:
						break;
					case 81:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -81:
						break;
					case 82:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -82:
						break;
					case 83:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -83:
						break;
					case 84:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -84:
						break;
					case 85:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -85:
						break;
					case 86:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -86:
						break;
					case 87:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -87:
						break;
					case 88:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -88:
						break;
					case 89:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -89:
						break;
					case 90:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -90:
						break;
					case 91:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -91:
						break;
					case 92:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -92:
						break;
					case 93:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -93:
						break;
					case 94:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -94:
						break;
					case 95:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -95:
						break;
					case 96:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -96:
						break;
					case 97:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -97:
						break;
					case 98:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -98:
						break;
					case 99:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -99:
						break;
					case 100:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -100:
						break;
					case 101:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -101:
						break;
					case 102:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -102:
						break;
					case 103:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -103:
						break;
					case 104:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -104:
						break;
					case 105:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -105:
						break;
					case 106:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -106:
						break;
					case 107:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -107:
						break;
					case 108:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -108:
						break;
					case 109:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -109:
						break;
					case 110:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -110:
						break;
					case 111:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -111:
						break;
					case 112:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -112:
						break;
					case 113:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -113:
						break;
					case 114:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -114:
						break;
					case 115:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -115:
						break;
					case 116:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -116:
						break;
					case 117:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -117:
						break;
					case 118:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -118:
						break;
					case 119:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -119:
						break;
					case 120:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -120:
						break;
					case 121:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -121:
						break;
					case 122:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -122:
						break;
					case 123:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -123:
						break;
					case 124:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -124:
						break;
					case 125:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -125:
						break;
					case 126:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -126:
						break;
					case 127:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -127:
						break;
					case 128:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -128:
						break;
					case 129:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -129:
						break;
					case 130:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -130:
						break;
					case 131:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -131:
						break;
					case 132:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -132:
						break;
					case 133:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -133:
						break;
					case 134:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -134:
						break;
					case 135:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -135:
						break;
					case 136:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -136:
						break;
					case 137:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -137:
						break;
					case 138:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -138:
						break;
					case 139:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -139:
						break;
					case 140:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -140:
						break;
					case 141:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -141:
						break;
					case 142:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -142:
						break;
					case 143:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -143:
						break;
					case 144:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -144:
						break;
					case 145:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -145:
						break;
					case 146:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -146:
						break;
					case 147:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -147:
						break;
					case 148:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -148:
						break;
					case 149:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -149:
						break;
					case 150:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -150:
						break;
					case 151:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -151:
						break;
					case 152:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -152:
						break;
					case 153:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -153:
						break;
					case 154:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -154:
						break;
					case 155:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -155:
						break;
					case 156:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -156:
						break;
					case 157:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -157:
						break;
					case 158:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -158:
						break;
					case 159:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -159:
						break;
					case 160:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -160:
						break;
					case 161:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -161:
						break;
					case 162:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -162:
						break;
					case 163:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -163:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
