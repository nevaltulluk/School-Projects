import java.io.*;
import java.util.ArrayList;

/**
 * Created by asus- on 24.10.2017.
 */
public class Tokenizer {

    public static  final int TOKEN_IDENTIFIER = 0;
    public static  final int TOKEN_OP_AND = 1;
    public static  final int TOKEN_OP_OR = 2;
    public static  final int TOKEN_OP_XOR = 3;
    public static  final int TOKEN_OP_NOT = 4;
    public static  final int TOKEN_OP_LS = 5;
    public static  final int TOKEN_OP_RS = 6;
    public static  final int TOKEN_OP_LR = 7;
    public static  final int TOKEN_OP_RR = 8;
    public static  final int TOKEN_PARAN_OP = 9;
    public static  final int TOKEN_PARAN_CL = 10;
    public static  final int TOKEN_END_OF_LINE = 11;
    public static  final int TOKEN_END_OF_FILE = 12;
    public static  final int TOKEN_EQUALSIGN = 13;
    public static  final int TOKEN_NUMBER = 14;
    public static  final int TOKEN_CARRIAGE_FEED =13;
    public static  final int TOKEN_COMMA = 14;
    public ArrayList<Token> tokens = new ArrayList<>();

    public static class Token{
        public int token_type;
        public String matched = "";
    }
    public static boolean is_letter(int character){
        if((character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z')){
            return true;
        }
        return false;
    }
    public static boolean is_number(int character){
        if((character >= 'a' && character <= 'f') || (character >= '0' && character <= '9')){
            return true;
        }
        return false;
    }
    BufferedReader reader;
    public Tokenizer(String filename){
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public ArrayList<Token> tokenize(){
        int ch;
        try {
            while ((ch = reader.read()) != -1) {
                Token t = new Token();
                if (ch == '$') {
                    t.token_type = TOKEN_IDENTIFIER;
                    while ((ch = reader.read()) != -1) {
                        if (is_letter(ch)) {
                            t.matched = t.matched + (char) ch;
                            reader.mark(1);
                        }else {
                            reader.reset();
                            break;
                        }
                    }
                    tokens.add(t);
                }
                else if(is_number(ch)){
                    t.token_type = TOKEN_NUMBER;
                    t.matched = t.matched + (char) ch;
                    while ((ch = reader.read()) != -1) {
                        if (is_number(ch)) {
                            t.matched = t.matched + (char) ch;
                            reader.mark(1);
                        }else {
                            //reader.reset();
                            break;
                        }
                    }
                    tokens.add(t);
                }
                else if (ch == ',') {
                    t.token_type = TOKEN_COMMA;
                    tokens.add(t);
                }else if (ch == '\n') {
                    t.token_type = TOKEN_END_OF_LINE;
                    tokens.add(t);
                } else if (ch == '=') {
                    t.token_type = TOKEN_EQUALSIGN;
                    tokens.add(t);
                } else if (ch == '|') {
                    t.token_type = TOKEN_OP_OR;
                    tokens.add(t);
                } else if (ch == '&') {
                    t.token_type = TOKEN_OP_AND;
                    tokens.add(t);
                } else if (ch == '(') {
                    t.token_type = TOKEN_PARAN_OP;
                    tokens.add(t);
                } else if (ch == ')') {
                    t.token_type = TOKEN_PARAN_CL;
                    tokens.add(t);
                } else if (ch == 'l') {
                    t.matched = t.matched + (char) ch;
                    ch = reader.read();
                    if (ch == 's') {
                        t.matched = t.matched + (char) ch;
                        t.token_type = TOKEN_OP_LS;
                    } else if (ch == 'r') {
                        t.matched = t.matched + (char) ch;
                        t.token_type = TOKEN_OP_LR;
                    } else {
                        System.out.println("syntax error : ls or lr expected");
                        System.exit(-1);
                    }
                    tokens.add(t);
                } else if (ch == 'r') {
                    t.matched = t.matched + (char) ch;
                    ch = reader.read();
                    if (ch == 's') {
                        t.matched = t.matched + (char) ch;
                        t.token_type = TOKEN_OP_RS;
                    } else if (ch == 'r') {
                        t.matched = t.matched + (char) ch;
                        t.token_type = TOKEN_OP_RR;
                    } else {
                        System.out.println("syntax error : rs or rr expected");
                        System.exit(-1);
                    }
                    tokens.add(t);
                } else if (ch == 'n') {
                    t.matched = t.matched + (char) ch;
                    ch = reader.read();
                    if (ch == 'o') {
                        t.matched = t.matched + (char) ch;
                        ch = reader.read();
                        if (ch == 't') {
                            t.matched = t.matched + (char) ch;
                            t.token_type = TOKEN_OP_NOT;
                            tokens.add(t);
                        } else {
                            System.out.println("syntax error : not expected");
                            System.exit(-1);
                        }
                    } else {
                        System.out.println("syntax error : not expected");
                        System.exit(-1);
                    }
                } else if (ch == 'x') {
                    t.matched = t.matched + (char) ch;
                    ch = reader.read();
                    if (ch == 'o') {
                        t.matched = t.matched + (char) ch;
                        ch = reader.read();
                        if (ch == 'r') {
                            t.matched = t.matched + (char) ch;
                            t.token_type = TOKEN_OP_XOR;
                            tokens.add(t);
                        } else {
                            System.out.println("syntax error : xor expected");
                            System.exit(-1);
                        }
                    } else {
                        System.out.println("syntax error : xor expected");
                        System.exit(-1);
                    }
                }else if (ch == ' '){
                }
                else if(ch == TOKEN_CARRIAGE_FEED){}
                else {
                    System.out.println("syntax error");
                    System.exit(-1);
                }
            }
            Token fileEnd = new Token();
            fileEnd.token_type = TOKEN_END_OF_FILE;
            tokens.add(fileEnd);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return tokens;

    }

}
