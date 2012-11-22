//=============================================
// hexFileTokenizer Class
//
// lexer driver class
//
// AUTHOR:  Paul Pearce
//   DATE:  25/04/2003
//
//  This class is designed provide a simple 
// driver for the lexer, pulling out tokens and
// printing them. In this case,
//
//	token#0 -> end-of-file
//	token#1 -> error
//	token#2 -> instruction (in hexadecimal)
//	token#3 -> starting address for the line (in hexadecimal)
//
//  In terms of using the lexer, this is the only
// file that really needs to be modified. (unless
// the reader wishes to tweak the hexfile.lex file)
//
// MODIFICATION HISTORY:
//=============================================


import java.io.*;

class hexFileTokenizer {

	public static void main(String[] args) throws IOException {
        	
        	//print an error if an incorrect number of 
        	//commandline arguments are provided (only one 
        	//argument (args[0]) is allowed)
       		if(args.length != 1){
            	System.err.println("Usage: java hexfile <filename>");
			return;
        	}
        	
        	String thisFile = args[0];
        	        	        	
        	try{        		
           		//take input file and tokenize
           		FileInputStream fileIn = new FileInputStream(thisFile);
           		Yylex yy = new Yylex(fileIn);
           		Yytoken t;
			do {
				t = yy.yylex();
				System.out.println(t);
			} while (t.m_index != sym.EOF);
            
        	}catch(FileNotFoundException Exception) {
            		System.out.println("The file `" +args[0]+ "' could not be found in the current directory");
	   		System.exit(0);
		}             	  	
	}
}