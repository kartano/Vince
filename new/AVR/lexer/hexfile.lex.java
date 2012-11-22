package AVR;
//=============================================
// Yytoken Class and VinceLexerUtil Class
//
// intel .HEX format file lexer
//
// AUTHOR:  Paul Pearce
//   DATE:  13/07/2003
//
//  this lexer file provides the lexical analysis of
// hexfiles for AT90S8535 CPU emulator consumption
//
//  checkSum at the end of each line of the hexfile is
// checked against the sum of the bytes of each line.
//
/*  NOTES: Intel HEX File Format
	line format
	===========
	byte 1		":" indicates start of the record
	bytes 2-3	record length * 2
	bytes 4-7	load address
	bytes 8-9	record type (see below)
	bytes 10-?	data
	last two bytes	checksum
	Record Type
	===========
	00 - Data record
	01 - End of File record
	02 - Extended segment address record
	03 - Start segment address record
	04 - Extended linear segment record
	05 - Start linear address record
	Checksum Calculation
	====================
	checksum = twos complement of the sum of all bytes (not including ":" char)
*/
//
// MODIFICATION HISTORY:
//=============================================
// 1.0.1 - 5/07/2003 - Added complete list of record types, added tRECORDTYPE token#4 and corrected checksum calculation
// 1.0.2 - 6/07/2003 - Changed order of symbols - simplifies reading of tokenizer output
// 1.0.3 - 12/07/2003 - Removed some error reporting, changed name of Utility class to VinceLexerUtil
// 1.0.4 - 13/07/2003 - Removed use of sym.class: shifted all terminals to VinceLexerUtil class
import java.lang.System;
import java.io.*;
class VinceLexerUtil {
	private static final String errorMsg[] = {"Hex file semantic error: Illegal character found.", "Error: CheckSum failed."};
	public static final int E_UNMATCHED = 0; 
	public static final int E_CHECKSUM = 1;
	public static void error(int code){
		System.out.println(errorMsg[code]);
	}
	//terminals
	public static final int EOF              = 0; // Indicates lexer has run out of input
	public static final int ERROR            = 4; // illegal character..
	public static final int tINSTRUCTION     = 3; // instruction
	public static final int tSTARTINGADDRESS = 1; // starting address 
	public static final int tRECORDTYPE	 = 2; // record type
}
class Yytoken {
  Yytoken 
    (
     int index,
     String text,
     int line,
     int charBegin,
     int charEnd
     )
      {
        m_index = index;
        m_text = new String(text);
        m_line = line;
        m_charBegin = charBegin;
        m_charEnd = charEnd;
      }
  public int m_index;
  public String m_text;
  public int m_line;
  public int m_charBegin;
  public int m_charEnd;
  public String toString() {
      return "Token #"+m_index+": "+m_text+" (line "+m_line+", character pos"
          +m_charBegin+":"+m_charEnd+")";
  }
}


class Yylex {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

  private String instruction_count;
  private int actualInCount;
  private String starting_address;
  private int actualStartAddr;
  private String line_type = "";
  private String line_checkSum;
  private int line_checkSumTotal = 0;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int LINE3 = 3;
	private final int LINE2 = 2;
	private final int LINE1 = 1;
	private final int YYINITIAL = 0;
	private final int LINE5 = 5;
	private final int LINE4 = 4;
	private final int yy_state_dtrans[] = {
		0,
		20,
		26,
		29,
		30,
		33
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
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
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
		/* 0 */ YY_NOT_ACCEPT,
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
		/* 20 */ YY_NOT_ACCEPT,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NOT_ACCEPT,
		/* 27 */ YY_NOT_ACCEPT,
		/* 28 */ YY_NOT_ACCEPT,
		/* 29 */ YY_NOT_ACCEPT,
		/* 30 */ YY_NOT_ACCEPT,
		/* 31 */ YY_NOT_ACCEPT,
		/* 32 */ YY_NOT_ACCEPT,
		/* 33 */ YY_NOT_ACCEPT
	};
	private int yy_cmap[] = unpackFromString(1,130,
"4:10,1,4:2,1,4:34,5,6,7,8,9,10,3:4,2,4:6,3:6,4:26,3:6,4:25,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,34,
"0,1:19,2,3,4,5,6,7,8,9,10,11,12,13,14,15")[0];

	private int yy_nxt[][] = unpackFromString(16,11,
"1,2,3,4:8,-1:11,1,-1,5,21,5,21:6,-1:3,6,-1,6:6,-1:3,27,-1,27:6,-1:5,10,11,1" +
"2,13,14,15,-1:3,31,-1,31:6,-1:3,19,-1,19:6,1,-1,7,22,7,22:6,-1:3,28,-1,28:6" +
",-1:3,8,-1,8:6,1,-1,9:3,23,9:5,1,-1,16,24,16,24:6,-1:3,32,-1,32:6,-1:3,17,-" +
"1,17:6,1,-1,18,25,18,25:6");

	public Yytoken yylex ()
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

  return (new Yytoken(VinceLexerUtil.EOF,yytext(),yyline,yychar,yychar+1));
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
					case 1:
						
					case -2:
						break;
					case 2:
						{
}
					case -3:
						break;
					case 3:
						{
	instruction_count = "";  
	yybegin(LINE1); 
}
					case -4:
						break;
					case 4:
						{
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -5:
						break;
					case 5:
						{ 
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -6:
						break;
					case 6:
						{
	instruction_count = yytext();
	line_checkSumTotal = Integer.parseInt(yytext(), 16);
        actualInCount = Integer.parseInt(instruction_count, 16)/2 - 1;
	yybegin(LINE2);
}
					case -7:
						break;
					case 7:
						{
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -8:
						break;
					case 8:
						{
	starting_address = yytext();
	actualStartAddr = Integer.parseInt(starting_address, 16)/2;
	starting_address = Integer.toHexString(actualStartAddr);
	String str1 = yytext().substring(0,yytext().length() - 2);
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(str1, 16);
	String str2 = yytext().substring(2,yytext().length());
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(str2, 16);
	yybegin(LINE3);
	return (new Yytoken(VinceLexerUtil.tSTARTINGADDRESS,starting_address,yyline,yychar,yychar + yytext().length()));
}
					case -9:
						break;
					case 9:
						{
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -10:
						break;
					case 10:
						{
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(yytext(), 16);
	line_type = "data record";
	yybegin(LINE4);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
					case -11:
						break;
					case 11:
						{
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(yytext(), 16);
	line_type = "end of file record";
	yybegin(LINE5);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
					case -12:
						break;
					case 12:
						{
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(yytext(), 16);
	line_type = "extendedSAR";
	yybegin(LINE4);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
					case -13:
						break;
					case 13:
						{
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(yytext(), 16);
	line_type = "startSAR";
	yybegin(LINE4);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
					case -14:
						break;
					case 14:
						{
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(yytext(), 16);
	line_type = "extendedLAR";
	yybegin(LINE4);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
					case -15:
						break;
					case 15:
						{
	line_type = "startLAR";
	yybegin(LINE5);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
					case -16:
						break;
					case 16:
						{
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -17:
						break;
					case 17:
						{
	String str1 =  yytext().substring(0,yytext().length() - 2);
	String str2 =  yytext().substring(2,yytext().length());
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(str1, 16) + Integer.parseInt(str2, 16);
	if(actualInCount > 0){
		actualInCount--;
		return (new Yytoken(VinceLexerUtil.tINSTRUCTION,yytext(),yyline,yychar,yychar + yytext().length()));		
	}
	yybegin(LINE5);
	return (new Yytoken(VinceLexerUtil.tINSTRUCTION,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -18:
						break;
					case 18:
						{
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -19:
						break;
					case 19:
						{
	line_checkSumTotal = Integer.parseInt("100", 16) - line_checkSumTotal;
	String checkSumTotal = Integer.toHexString(line_checkSumTotal);
	if(checkSumTotal.length() > 1){
		checkSumTotal = checkSumTotal.substring(checkSumTotal.length() - 2, checkSumTotal.length());
	}
	line_checkSum = yytext();
	if(Integer.parseInt(checkSumTotal, 16) != Integer.parseInt(line_checkSum, 16)){
        	VinceLexerUtil.error(VinceLexerUtil.E_CHECKSUM);
        	return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
        }		
	yybegin(YYINITIAL);
}
					case -20:
						break;
					case 21:
						{ 
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -21:
						break;
					case 22:
						{
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -22:
						break;
					case 23:
						{
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -23:
						break;
					case 24:
						{
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -24:
						break;
					case 25:
						{
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -25:
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
