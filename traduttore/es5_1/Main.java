import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class Main{
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "in.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
