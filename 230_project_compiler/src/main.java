import java.io.*;
import java.util.ArrayList;

/**
 * Created by asus- on 24.10.2017.
 */
public class main {


    public static void main(String[] args) {
        String inFileName = "input.txt";
        String outFileName = "output.txt";

        if ( args.length > 1 )
            inFileName = args[1];

        if ( args.length > 2 )
            outFileName = args[2];

        Tokenizer t =  new Tokenizer(inFileName);
        Parser p = new Parser(t.tokenize());
        /*ArrayList<Tokenizer.Token> tok = t.tokenize();
        for (int i = 0; i < tok.size(); i++) {
            System.out.println(tok.get(i).token_type);
        }*/

        try {
            new FileOutputStream("output.txt")
                    .write(p.parse().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(p.parse());
    }

}
