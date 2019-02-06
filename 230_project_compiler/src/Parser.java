import java.util.ArrayList;


/**
 * Created by asus- on 24.10.2017.
 */
public class Parser {
    public String output = "";
    ArrayList<Tokenizer.Token> tokens;

    public Parser(ArrayList<Tokenizer.Token> tokens) {
        this.tokens = tokens;
    }

    public Tokenizer.Token peek() {
        return tokens.get(0);
    }

    public Tokenizer.Token peekNext() {
        return tokens.get(1);
    }

    public void consume(int tokenType) {
        if (tokens.get(0).token_type == tokenType) {
            tokens.remove(0);
        } else {
            System.out.println("syntax error : tokentype not matched - " + tokenType + " - " + tokens.get(0).token_type);
        }
    }

    public String parse() {
        expression();
        while (peek().token_type != Tokenizer.TOKEN_END_OF_FILE) {
            if (peek().token_type == Tokenizer.TOKEN_END_OF_LINE) {
                consume(Tokenizer.TOKEN_END_OF_LINE);
                expression();
            }
            expression();

        }
        return output;
    }

    public void expression() {
        term();
        if (peek().token_type == Tokenizer.TOKEN_OP_OR) {
            moreterms();
        }
    }

    public void term() {
        factor();
        if (peek().token_type == Tokenizer.TOKEN_OP_AND) {
            morefactors();
        }
    }

    public void factor() {

        if (peek().token_type == Tokenizer.TOKEN_PARAN_OP) {
            consume(Tokenizer.TOKEN_PARAN_OP);
            expression();
            consume(Tokenizer.TOKEN_PARAN_CL);
        } else if (peek().token_type == Tokenizer.TOKEN_EQUALSIGN) {
            consume(Tokenizer.TOKEN_EQUALSIGN);
            expression();
            output = output + "pop ax\npop bx\nmov[bx],ax";
        } else if (peek().token_type == Tokenizer.TOKEN_IDENTIFIER) {
            if (peekNext().token_type == Tokenizer.TOKEN_EQUALSIGN) {
                output = output + "push offset " + peek().matched + "\n";
            } else if (peekNext().token_type == Tokenizer.TOKEN_END_OF_LINE || peekNext().token_type == Tokenizer.TOKEN_END_OF_FILE) {
                //TODO print the output statement
                output = output + "PRINT STATEMENT";
            } else {
                output = output + "push w " + peek().matched + "\n";
            }
            consume(Tokenizer.TOKEN_IDENTIFIER);
        } else if (peek().token_type == Tokenizer.TOKEN_NUMBER) {
            if(peek().matched.charAt(0) >= '0' && peek().matched.charAt(0) <= '9'){
                output = output + "push 0" + peek().matched + "h\n";
            }
            else{
                output = output + "push " + peek().matched + "h\n";
            }

            consume(Tokenizer.TOKEN_NUMBER);
        } else if (peek().token_type == Tokenizer.TOKEN_OP_XOR) {

            consume(Tokenizer.TOKEN_OP_XOR);
            if (peek().token_type == Tokenizer.TOKEN_PARAN_OP) {
                consume(Tokenizer.TOKEN_PARAN_OP);
                expression();
                //virg�l case
                if (peek().token_type == Tokenizer.TOKEN_COMMA) {
                    consume(Tokenizer.TOKEN_COMMA);
                } else {
                    System.out.println("syntax error : no comma");
                }
                expression();
                consume(Tokenizer.TOKEN_PARAN_CL);
            }
            output = output + "pop ax\npop bx\nxor ax,bx\npush ax\n";
        } else if (peek().token_type == Tokenizer.TOKEN_OP_NOT) {
            consume(Tokenizer.TOKEN_OP_NOT);
            if (peek().token_type == Tokenizer.TOKEN_PARAN_OP) {
                consume(Tokenizer.TOKEN_PARAN_OP);
                expression();
                consume(Tokenizer.TOKEN_PARAN_CL);
            }
            output = output + "pop ax\nnot ax\npush ax\n";
        } else if (peek().token_type == Tokenizer.TOKEN_OP_LS) {
            consume(Tokenizer.TOKEN_OP_LS);
            if (peek().token_type == Tokenizer.TOKEN_PARAN_OP) {
                consume(Tokenizer.TOKEN_PARAN_OP);
                expression();
                //virg�l case
                if (peek().token_type == Tokenizer.TOKEN_COMMA) {
                    consume(Tokenizer.TOKEN_COMMA);
                } else {
                    System.out.println("syntax error : no comma");
                }
                expression();
                consume(Tokenizer.TOKEN_PARAN_CL);
            }
            output = output + "pop ax\npop bx\nshl ax,bx\npush ax\n";

        } else if (peek().token_type == Tokenizer.TOKEN_OP_RS) {
            consume(Tokenizer.TOKEN_OP_RS);
            if (peek().token_type == Tokenizer.TOKEN_PARAN_OP) {
                consume(Tokenizer.TOKEN_PARAN_OP);
                expression();
                //virg�l case
                if (peek().token_type == Tokenizer.TOKEN_COMMA) {
                    consume(Tokenizer.TOKEN_COMMA);
                } else {
                    System.out.println("syntax error : no comma");
                }
                expression();
                consume(Tokenizer.TOKEN_PARAN_CL);
            }
            output = output + "pop ax\npop bx\nshr ax,bx\npush ax\n";
        } else if (peek().token_type == Tokenizer.TOKEN_OP_LR) {
            consume(Tokenizer.TOKEN_OP_LR);
            if (peek().token_type == Tokenizer.TOKEN_PARAN_OP) {
                consume(Tokenizer.TOKEN_PARAN_OP);
                expression();
                //virg�l case
                if (peek().token_type == Tokenizer.TOKEN_COMMA) {
                    consume(Tokenizer.TOKEN_COMMA);
                } else {
                    System.out.println("syntax error : no comma");
                }
                expression();
                consume(Tokenizer.TOKEN_PARAN_CL);
            }
            output = output + "pop ax\npop bx\nrol ax,bx\npush ax\n";
        } else if (peek().token_type == Tokenizer.TOKEN_OP_RR) {
            consume(Tokenizer.TOKEN_OP_RR);
            if (peek().token_type == Tokenizer.TOKEN_PARAN_OP) {
                consume(Tokenizer.TOKEN_PARAN_OP);
                expression();
                //virg�l case
                if (peek().token_type == Tokenizer.TOKEN_COMMA) {
                    consume(Tokenizer.TOKEN_COMMA);
                } else {
                    System.out.println("syntax error : no comma");
                }
                expression();
                consume(Tokenizer.TOKEN_PARAN_CL);
            }
            output = output + "pop ax\npop bx\nror ax,bx\npush ax\n";
        } else {
            System.out.println("How it happens?! Hhe factor method cant handle this tokentype: " + peek().token_type);
            consume( peek().token_type );
        }

    }

    public void moreterms() {
        consume(Tokenizer.TOKEN_OP_OR);
        term();
        output = output + "pop ax\npop bx\nor ax,bx\npush ax\n";
        if (peek().token_type == Tokenizer.TOKEN_OP_OR) {
            moreterms();
        }
    }

    public void morefactors() {
        consume(Tokenizer.TOKEN_OP_AND);
        factor();
        output = output + "pop ax\npop bx\nand ax,bx\npush ax\n";
        if (peek().token_type == Tokenizer.TOKEN_OP_AND) {
            morefactors();
        }
    }


}
