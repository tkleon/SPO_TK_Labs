public class Token {
    public final Lexem type;
    public String data;

    public Token(Lexem type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getType(){return type.toString();}
    public String getData(){return data;}

    public String toString() {
        return type + " " + data;
    }
}