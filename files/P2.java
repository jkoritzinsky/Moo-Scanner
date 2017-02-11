import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.io.*;
import java_cup.runtime.*;  // defines Symbol

/**
 * This program is to be used to test the Scanner.
 * This version is set up to test all tokens, but more code is needed to test 
 * other aspects of the scanner (e.g., input that causes errors, character 
 * numbers, values associated with tokens)
 */
public class P2 {
    public static void main(String[] args){
        boolean testsFailed = false;
        P2 p1 = new P2();
        Method[] testMethods = P2.class.getMethods();

        for (Method method : testMethods) {
            // Only execute methods declared by the P1 class and are not main.
            // By my design, all of the rest of the methods on P1
            // are test methods.
            if (method.getDeclaringClass() == P2.class
                    && method.getName() != "main") {
                CharNum.num = 1;
                try {
                    boolean passed = (boolean) method.invoke(p1);
                    if (!passed) {
                        System.out.printf("Test %s failed\n", method.getName());
                        testsFailed = true;
                    }
                } catch (IllegalAccessException | InvocationTargetException ex){
                    System.out.printf("Test %s failed\n", method.getName());
                    ex.printStackTrace();
                    testsFailed = true;
                }
            }
        }
        System.exit(testsFailed ? 1 : 0);
    }

    /**
     * testAllTokens
     *
     * Open and read from file allTokens.in
     * For each token read, write the corresponding string to allTokens.out
     * If the input file contains all tokens, one per line, we can verify
     * correctness of the scanner by comparing the input and output files
     * (e.g., using a 'diff' command).
     */
    public boolean testAllTokens() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("allTokens.in");
            outFile = new PrintWriter(new FileWriter("allTokens.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File allTokens.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("allTokens.out cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            switch (token.sym) {
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
                outFile.println(((IdTokenVal)token.value).idVal);
                break;
            case sym.INTLITERAL:  
                outFile.println(((IntLitTokenVal)token.value).intVal);
                break;
            case sym.STRINGLITERAL: 
                outFile.println(((StrLitTokenVal)token.value).strVal);
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

            token = scanner.next_token();
        } // end while
        outFile.close();
        return true;
    }

    public boolean validStringParsesAsString() throws IOException
    {
        String test = "\"Test string with valid escape sequence\\n\"";
        try (StringReader reader = new StringReader(test)) {
            Yylex lexer = new Yylex(reader);
            Symbol token = lexer.next_token();
            StrLitTokenVal value = ((StrLitTokenVal) token.value);
            return  token.sym == sym.STRINGLITERAL
                    && CharNum.num == test.length() + 1
                    && value.strVal.equals(test)
                    && value.linenum == 1
                    && value.charnum == 1;
        }
    }

    public boolean stringWithInvalidEscapeIsNotTokenized() throws IOException {
        String test = "\"Test string with invalid escape sequence \\z\"";
        try (StringReader reader = new StringReader(test)) {
            Yylex lexer = new Yylex(reader);
            Symbol token = lexer.next_token();
            return token.sym == sym.EOF;
        }
    }

    public boolean unterminatedStringIsNotTokenized() throws IOException {
        String test = "\"Test string with valid escape sequence\\n";
        try (StringReader reader = new StringReader(test)) {
            Yylex lexer = new Yylex(reader);
            Symbol token = lexer.next_token();
            return token.sym == sym.EOF;
        }
    }

    public boolean unterminatedStringWithBadEscapeSequenceIsNotTokenized()
        throws IOException {
        String test = "\"Test string with invalid escape sequence \\z";
        try (StringReader reader = new StringReader(test)) {
            Yylex lexer = new Yylex(reader);
            Symbol token = lexer.next_token();
            return token.sym == sym.EOF;
        }
    }

    public boolean commentStartedWithSlashNotTokenized() throws IOException {
        String test = "//This is a comment";
        try (StringReader reader = new StringReader(test)) {
            Yylex lexer = new Yylex(reader);
            Symbol token = lexer.next_token();
            return token.sym == sym.EOF;
        }
    }

    public boolean commentStartedWithPoundSignNotTokenized()
            throws IOException {
        String test = "# This is a comment";
        try (StringReader reader = new StringReader(test)) {
            Yylex lexer = new Yylex(reader);
            Symbol token = lexer.next_token();
            return token.sym == sym.EOF;
        }
    }

    public boolean commentIncreasesCharNum() throws IOException {
        String test = "# This is a comment";
        try (StringReader reader = new StringReader(test)) {
            Yylex lexer = new Yylex(reader);
            Symbol token = lexer.next_token();
            return CharNum.num == test.length() + 1;
        }
    }

    public boolean canReadTokenLineAfterComment() throws IOException {
        String test="// My comment \n5";
        try (StringReader reader = new StringReader(test)) {
            Yylex lexer = new Yylex(reader);
            Symbol token = lexer.next_token();
            IntLitTokenVal value = ((IntLitTokenVal) token.value);
            return token.sym == sym.INTLITERAL
                    && CharNum.num == 2
                    && value.linenum == 2
                    && value.charnum == 1;
        }
    }

    public boolean whitespaceIsIgnoredButCharNumIsSummed() throws IOException {
        String test="\tidentifier";
        try (StringReader reader = new StringReader((test))) {
            Yylex lexer = new Yylex(reader);
            Symbol token = lexer.next_token();
            IdTokenVal value = ((IdTokenVal) token.value);
            return CharNum.num == test.length() + 1
                    && value.linenum == 1
                    && value.charnum == 2;
        }
    }
}
