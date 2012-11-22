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

%%

%{
  private String instruction_count;
  private int actualInCount;
  private String starting_address;
  private int actualStartAddr;
  private String line_type = "";
  private String line_checkSum;
  private int line_checkSumTotal = 0;
%} 

%eofval{
  return (new Yytoken(VinceLexerUtil.EOF,yytext(),yyline,yychar,yychar+1));
%eofval}
%line
%char
%state LINE1, LINE2, LINE3, LINE4, LINE5

HEXDIGIT=[A-Fa-f0-9]
INSTRCOUNTX2 = {HEXDIGIT}{HEXDIGIT}
STARTADDRX2 = {HEXDIGIT}{HEXDIGIT}{HEXDIGIT}{HEXDIGIT}
INSTRUCTION = {HEXDIGIT}{HEXDIGIT}{HEXDIGIT}{HEXDIGIT}
CHKSUM = {HEXDIGIT}{HEXDIGIT}

%% 

<YYINITIAL> [\r\n] {
}
<YYINITIAL> ":" {
	instruction_count = "";  
	yybegin(LINE1); 
}
<LINE1> {INSTRCOUNTX2} {
	instruction_count = yytext();
	line_checkSumTotal = Integer.parseInt(yytext(), 16);
        actualInCount = Integer.parseInt(instruction_count, 16)/2 - 1;
	yybegin(LINE2);
}
<LINE1> . { 
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}

<LINE2> {STARTADDRX2} {
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
<LINE2> . {
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
<LINE3> "00" {
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(yytext(), 16);
	line_type = "data record";
	yybegin(LINE4);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
<LINE3> "01" {
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(yytext(), 16);
	line_type = "end of file record";
	yybegin(LINE5);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
<LINE3> "02" {
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(yytext(), 16);
	line_type = "extendedSAR";
	yybegin(LINE4);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
<LINE3> "03" {
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(yytext(), 16);
	line_type = "startSAR";
	yybegin(LINE4);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
<LINE3> "04" {
	line_checkSumTotal = line_checkSumTotal + Integer.parseInt(yytext(), 16);
	line_type = "extendedLAR";
	yybegin(LINE4);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
<LINE3> "05" {
	line_type = "startLAR";
	yybegin(LINE5);
	return (new Yytoken(VinceLexerUtil.tRECORDTYPE,line_type,yyline,yychar,yychar + yytext().length()));
}
<LINE3> . {
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
<LINE4> {INSTRUCTION} {
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
<LINE4> . {
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
<LINE5> {CHKSUM} {
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
<LINE5> . {
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
<YYINITIAL> . {
        VinceLexerUtil.error(VinceLexerUtil.E_UNMATCHED);
        return (new Yytoken(VinceLexerUtil.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
