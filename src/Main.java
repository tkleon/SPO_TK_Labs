import java.util.ArrayList;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        LinkedList<Token> tokens = lexer.lex("a = 9; b = 6; x = 5; y = 8; l new List; l.add(17); l.add(5-8); l.add(545); s new Set; s.add(5); s.add(9); x = l.get(1); if(y < 5){y = y + 1;}  while (a > b) {a = 0;}");
        Poliz.setTokens(tokens);
        System.out.println("\n"+"Tokens:");
        for (int i = 0; i < tokens.size(); i++)
        {

            System.out.println("\t[" + tokens.get(i) + "]");
        }
        Parser parser = new Parser();
        try {
            parser.createAST(new ArrayList<>(tokens));
            parser.CheckTokens();
            parser.print();
        }catch (Exception ex)
        { System.err.println(ex);
            System.exit(1);
        }

        int j = 0;
        for (Token token : Poliz.poliz) {
            System.out.println(j + " " + "[" + token +  "]");
            j++;
        }

        System.out.println("\n"+"Variable table:");
        CalcPoliz.calculate(Poliz.poliz);
    }
}