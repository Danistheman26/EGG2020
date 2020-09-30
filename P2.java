import java.util.*;
import java.io.*;
import java_cup.runtime.*;  // defines Symbol

/**
 * This program is to be used to test the cflat scanner.
 * This version is set up to test all tokens, but more code is needed to test 
 * other aspects of the scanner (e.g., input that causes errors, character 
 * numbers, values associated with tokens).
 */
public class P2 {
    public static void main(String[] args) throws IOException {
                                           // exception may be thrown by yylex
        // test all tokens
        testAllTokens();
        CharNum.num = 1;
        CharNumPrev.num = 1;
    	
    	testCharNums();
    }

    /**
     * testAllTokens
     *
     * Open and read from files that have tests in them
     * For each token read, write the corresponding string to allTokens.out
     * If the input file contains all tokens, one per line, we can verify
     * correctness of the scanner by comparing the input and output files
     * (e.g., using a 'diff' command).
     */
    private static void testAllTokens() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        //add input files here
        String[] files = {"allTokens", "stringsTestTokens"};
        for(int i=0; i < files.length; i++){
		try {
		    
		    inFile = new FileReader(files[i] + ".in");
		    outFile = new PrintWriter(new FileWriter(files[i] + ".out"));
		} catch (FileNotFoundException ex) {
		    System.err.println("File" + files[i] + ".in not found.");
		    System.exit(-1);
		} catch (IOException ex) {
		    System.err.println(files[i] + ".out cannot be opened.");
		    System.exit(-1);
		}

		// create and call the scanner
		Yylex my_scanner = new Yylex(inFile);
		Symbol my_token = my_scanner.next_token();
		while (my_token.sym != sym.EOF) {
		    switch (my_token.sym) {
		    case sym.BOOL:
		        outFile.println("bool"); 
		        break;
		    case sym.INT:
		        outFile.println("int");
		        break;
		    case sym.VOID:
		        outFile.println("void");
		        break;
		    case sym.TRUE:
		        outFile.println("true"); 
		        break;
		    case sym.FALSE:
		        outFile.println("false"); 
		        break;
		    case sym.STRUCT:
		        outFile.println("struct"); 
		        break;
		    case sym.CIN:
		        outFile.println("cin"); 
		        break;
		    case sym.COUT:
		        outFile.println("cout");
		        break;				
		    case sym.IF:
		        outFile.println("if");
		        break;
		    case sym.ELSE:
		        outFile.println("else");
		        break;
		    case sym.WHILE:
		        outFile.println("while");
		        break;
		    case sym.RETURN:
		        outFile.println("return");
		        break;
		    case sym.ID:
		        outFile.println(((IdTokenVal)my_token.value).idVal);
		        break;
		    case sym.INTLITERAL:  
		        outFile.println(((IntLitTokenVal)my_token.value).intVal);
		        break;
		    case sym.STRINGLITERAL: 
		        outFile.println(((StrLitTokenVal)my_token.value).strVal);
		        break;    
		    case sym.LCURLY:
		        outFile.println("{");
		        break;
		    case sym.RCURLY:
		        outFile.println("}");
		        break;
		    case sym.LPAREN:
		        outFile.println("(");
		        break;
		    case sym.RPAREN:
		        outFile.println(")");
		        break;
		    case sym.SEMICOLON:
		        outFile.println(";");
		        break;
		    case sym.COMMA:
		        outFile.println(",");
		        break;
		    case sym.DOT:
		        outFile.println(".");
		        break;
		    case sym.WRITE:
		        outFile.println("<<");
		        break;
		    case sym.READ:
		        outFile.println(">>");
		        break;				
		    case sym.PLUSPLUS:
		        outFile.println("++");
		        break;
		    case sym.MINUSMINUS:
		        outFile.println("--");
		        break;	
		    case sym.PLUS:
		        outFile.println("+");
		        break;
		    case sym.MINUS:
		        outFile.println("-");
		        break;
		    case sym.TIMES:
		        outFile.println("*");
		        break;
		    case sym.DIVIDE:
		        outFile.println("/");
		        break;
		    case sym.NOT:
		        outFile.println("!");
		        break;
		    case sym.AND:
		        outFile.println("&&");
		        break;
		    case sym.OR:
		        outFile.println("||");
		        break;
		    case sym.EQUALS:
		        outFile.println("==");
		        break;
		    case sym.NOTEQUALS:
		        outFile.println("!=");
		        break;
		    case sym.LESS:
		        outFile.println("<");
		        break;
		    case sym.GREATER:
		        outFile.println(">");
		        break;
		    case sym.LESSEQ:
		        outFile.println("<=");
		        break;
		    case sym.GREATEREQ:
		        outFile.println(">=");
		        break;
		    case sym.ASSIGN:
		        outFile.println("=");
		        break;
				default:
					outFile.println("UNKNOWN TOKEN");
		    } // end switch
		    
		    my_token = my_scanner.next_token();
		} // end while
        outFile.close();
    	}
    }
    /**
     * testCharNums
     *
     * Open and read from files that have test tokens in them
     * For each token read, confirm it increments the charNum correctly
     */
    private static void testCharNums() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        //add input files here
        String[] files = {"allTokens"};
        for(int i=0; i < files.length; i++){
		try {
		    
		    inFile = new FileReader(files[i] + ".in");
		} catch (FileNotFoundException ex) {
		    System.err.println("File" + files[i] + ".in not found.");
		    System.exit(-1);
		}

		// create and call the scanner
		Yylex my_scanner = new Yylex(inFile);
		Symbol my_token = my_scanner.next_token();
		int currentLinenum = 0;
		while (my_token.sym != sym.EOF) {
			if(CharNumPrev.num == 1)
				currentLinenum++;
			if(currentLinenum != ((TokenVal)my_token.value).linenum)
				System.out.println("Error, linenum incorrect" + (((TokenVal)my_token.value).linenum) + "curr:" + currentLinenum);
		     switch (my_token.sym) {
		    case sym.BOOL:
		        if(CharNum.num != CharNumPrev.num + 4 && CharNum.num != 1 +4)
		        	System.out.println("Error");
		        break;
		    case sym.INT:
		        if(CharNum.num != CharNumPrev.num + 3 && CharNum.num != 1 +3)
		        	System.out.println("Error");
		        break;
		    case sym.VOID:
		        if(CharNum.num != CharNumPrev.num + 4 && CharNum.num != 1 +4)
		        	System.out.println("Error");
		        break;
		    case sym.TRUE:
		        if(CharNum.num != CharNumPrev.num + 4 && CharNum.num != 1 +4)
		        	System.out.println("Error");
		        break;
		    case sym.FALSE:
		        if(CharNum.num != CharNumPrev.num + 5 && CharNum.num != 1 +5)
		        	System.out.println("Error");
		        break;
		    case sym.STRUCT:
		        if(CharNum.num != CharNumPrev.num + 5 && CharNum.num != 1 +5)
		        	System.out.println("Error");
		        break;
		    case sym.CIN:
		        if(CharNum.num != CharNumPrev.num + 3  && CharNum.num != 1 +3)
		        	System.out.println("Error");
		        break;
		    case sym.COUT:
		        if(CharNum.num != CharNumPrev.num + 4 && CharNum.num != 1 +4)
		        	System.out.println("Error");
		        break;				
		    case sym.IF:
		        if(CharNum.num != CharNumPrev.num + 2 && CharNum.num != 1 +2)
		        	System.out.println("Error");
		        break;
		    case sym.ELSE:
		        if(CharNum.num != CharNumPrev.num + 4 && CharNum.num != 1 +4)
		        	System.out.println("Error");
		        break;
		    case sym.WHILE:
		        if(CharNum.num != CharNumPrev.num + 5 && CharNum.num != 1 +5)
		        	System.out.println("Error");
		        break;
		    case sym.RETURN:
		        if(CharNum.num != CharNumPrev.num + 6 && CharNum.num != 1 +6)
		        	System.out.println("Error");
		        break;
		    case sym.ID:
		    if(CharNum.num != CharNumPrev.num + 
		    	(((IdTokenVal)my_token.value).idVal).length()){
		    	if(CharNum.num != 1 + (((IdTokenVal)my_token.value).idVal).length())
		    		System.out.println("Error");
		    	 }
		        //outFile.println(((IdTokenVal)my_token.value).idVal);
		        break;
		    case sym.INTLITERAL:  
		        if(CharNum.num != CharNumPrev.num
				+ String.valueOf(((IntLitTokenVal)my_token.value).intVal).length()
				&& CharNum.num != 1  
				+ String.valueOf(((IntLitTokenVal)my_token.value).intVal).length())
				System.out.println("Error");
		        break;
		    case sym.STRINGLITERAL: 
		    if(CharNum.num != CharNumPrev.num
			    + (((StrLitTokenVal)my_token.value).strVal).length()
			    && CharNum.num != 1 + (((StrLitTokenVal)my_token.value).strVal).length())
			    System.out.println("Error");
		        break;    
		    case sym.LCURLY:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.RCURLY:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.LPAREN:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.RPAREN:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.SEMICOLON:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.COMMA:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.DOT:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.WRITE:
		        if(CharNum.num != CharNumPrev.num + 2 && CharNum.num != 1 +2)
		        	System.out.println("Error");
		        break;
		    case sym.READ:
		         if(CharNum.num != CharNumPrev.num + 2 && CharNum.num != 1 +2)
		        	System.out.println("Error");
		        break;				
		    case sym.PLUSPLUS:
		         if(CharNum.num != CharNumPrev.num + 2 && CharNum.num != 1 +2)
		        	System.out.println("Error");
		        break;
		    case sym.MINUSMINUS:
		         if(CharNum.num != CharNumPrev.num + 2 && CharNum.num != 1 +2)
		        	System.out.println("Error");
		        break;	
		    case sym.PLUS:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.MINUS:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.TIMES:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.DIVIDE:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.NOT:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.AND:
		         if(CharNum.num != CharNumPrev.num + 2 && CharNum.num != 1 +2)
		        	System.out.println("Error");
		        break;
		    case sym.OR:
		        if(CharNum.num != CharNumPrev.num + 2 && CharNum.num != 1 +2)
		        	System.out.println("Error");
		        break;
		    case sym.EQUALS:
		         if(CharNum.num != CharNumPrev.num + 2 && CharNum.num != 1 +2)
		        	System.out.println("Error at equals");
		        break;
		    case sym.NOTEQUALS:
		         if(CharNum.num != CharNumPrev.num + 2 && CharNum.num != 1 +2)
		        	System.out.println("Error");
		        break;
		    case sym.LESS:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.GREATER:
		        if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error");
		        break;
		    case sym.LESSEQ:
		         if(CharNum.num != CharNumPrev.num + 2 && CharNum.num != 1 +2)
		        	System.out.println("Error");
		        break;
		    case sym.GREATEREQ:
		         if(CharNum.num != CharNumPrev.num + 2 && CharNum.num != 1 +2)
		        	System.out.println("Error");
		        break;
		    case sym.ASSIGN:
		         if(CharNum.num != CharNumPrev.num + 1 && CharNum.num != 1 +1)
		        	System.out.println("Error at assign");
		        break;
				default:
					outFile.println("UNKNOWN TOKEN");
		    }
		    CharNumPrev.num = CharNum.num;
		    my_token = my_scanner.next_token();
		    
		} // end while
	}
    }
}
