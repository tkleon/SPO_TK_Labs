import java.util.*;

public class Parser {

    private ArrayList<Token> tokens;
    private String StrTokens="";
    private boolean duplicated = false;
    private Node root;
    public Parser(){}

    public void createAST(ArrayList<Token> tok) throws Exception {
        tokens = new ArrayList<>(tok);
        root = lang();
    }

    private Node lang() throws Exception {
        Node node = new Node("lang");

        while (tokens.size() > 0 && currTokenType().matches("VAR|IF|WHILE")) {
            node.addChild(expr());
        }
        Poliz.endPoliz();
        return node;
    }

    private Node expr() throws Exception {
        Node node = new Node("expr");
        switch (currTokenType()) {
            case ("VAR") -> node.addChild(expr_head());
            case ("IF") -> node.addChild(if_expr());
            case ("WHILE") -> node.addChild(while_expr());
            default -> throw new Exception("Expression error");
        }
        return node;
    }

    private Node expr_head() throws Exception {
        Node node = new Node("expr_head");
        match("VAR", node);
        switch (currTokenType()) {
            case ("DEC"), ("INC") -> node.addChild(unary_operation());
            case ("ASSIGN_OP"),("TYPE_W") -> node.addChild(assign_expr());
            case ("METHOD") -> node.addChild(call_method());
            default -> throw new Exception("Expression head error");
        }
        return node;
    }

    private Node unary_operation() throws Exception {
        Node node = new Node("unary_operation");
        switch (currTokenType()) {
            case ("DEC") -> match("DEC", node);
            case ("INC") -> match("INC", node);
            default -> throw new Exception("Unary operation error");
        }
        matchSemicolon();

        return node;

    }

    private Node call_method() throws Exception {
        Node node = new Node("call_method");
        match("METHOD", node);
        node.addChild(method());
        return node;
    }

    private Node method() throws Exception {
        Node node = new Node("method");
        match("FUNC_OP", node);
        match("LB", node);
        node.addChild(inner_expression());
        match("RB", node);
        matchSemicolon();
        return node;

    }

    private Node inner_expression() throws Exception {
        Node node = new Node("inner_expression");
        switch (currTokenType()) {
            case ("NUM"), ("VAR") -> node.addChild(inner_value());
            case ("LB") -> {
                match("LB", node);
                node.addChild(inner_expression());
                match("RB", node);
            }
            default -> throw new Exception("Inner expression error");
        }

        while (currTokenType().matches("OP")) {
            match("OP", node);
            node.addChild(inner_expression());
        }
        return node;
    }

    private Node inner_value() throws Exception {
        Node node = new Node("inner_value");
        switch (currTokenType()) {
            case ("NUM") -> match("NUM", node);
            case ("VAR") -> match("VAR", node);
            default -> throw new Exception("Value error");
        }
        return node;

    }

    private Node assign_expr() throws Exception {
        Node node = new Node("assign_expr");
        switch (currTokenType()) {
            case ("ASSIGN_OP") -> {match("ASSIGN_OP", node); node.addChild(value_expr());}
            case ("TYPE_W") -> {match("TYPE_W", node); node.addChild(assign_struct());}
            default -> throw new Exception("Assign expression error");
        }
        return node;
    }

    private Node value_expr() throws Exception {
        Node node = new Node("value_expr");
        switch (currTokenType()) {
            case ("NUM"), ("VAR") -> node.addChild(value());
            case ("LB") -> {
                match("LB", node);
                node.addChild(value_expr());
                match("RB", node);
            }
            default -> throw new Exception("Value expression error");
        }

        while (currTokenType().matches("OP")) {
            match("OP", node);
            node.addChild(value_expr());
        }

        if(!duplicated)matchSemicolon();
        duplicated=false;
        return node;
    }

    private Node value() throws Exception {
        Node node = new Node("value");
        switch (currTokenType()) {
            case ("NUM") -> match("NUM", node);
            case ("VAR") -> match("VAR", node);
            default -> throw new Exception("Value error");
        }

        switch (currTokenType()) {
            case ("METHOD") -> {node.addChild(call_method()); duplicated=true;}
        }
        return node;

    }

    private Node assign_struct() throws Exception{
        Node node = new Node("assign_struct");
        match("TYPE", node);
        matchSemicolon();
        return node;
    }

    private Node if_expr() throws Exception {
        Node node = new Node("if_expr");
        node.addChild(if_head());
        node.addChild(if_else_body());

        if (currTokenType().matches("ELSE")) {
            node.addChild(else_expr());
        }
        return node;

    }

    private Node if_head() throws Exception {
        Node node = new Node("if_head");
        match("IF", node);
        node.addChild(if_condition());
        return node;
    }

    private Node else_expr() throws Exception {
        Node node = new Node("else_expr");
        match("ELSE_KW", node);
        node.addChild(if_else_body());
        return node;
    }

    private Node if_condition() throws Exception {
        Node node = new Node("if_condition");
        match("LB", node);
        node.addChild(logical_expr());
        match("RB", node);
        return node;
    }

    private Node logical_expr() throws Exception {
        Node node = new Node("logical_expr");
        switch (currTokenType()){
            case ("NUM"), ("VAR") -> node.addChild(inner_expression());
            default -> throw new Exception("Error in logical expr");
        }

        while (currTokenType().matches("BOOL_OP")) {
            match("BOOL_OP", node);
            node.addChild(inner_expression());
        }
        return node;
    }

    private Node if_else_body() throws Exception{
        Node node = new Node("ifelse_body");
        match("LCB", node);
        node.addChild(expr());
        while (currTokenType().matches("VAR|IF|WHILE")) node.addChild(expr());
        match("RCB", node);
        return node;
    }

    private Node while_expr() throws Exception{
        Node node = new Node("while_expr");
        node.addChild(while_head());
        node.addChild(while_body());
        return node;
    }

    private Node while_head() throws Exception{
        Node node = new Node("while_head");
        match("WHILE", node);
        node.addChild(while_condition());
        return node;
    }

    private Node while_condition() throws Exception{
        Node node = new Node("while_condition");
        match("LB", node);
        node.addChild(logical_expr());
        match("RB", node);
        return node;
    }

    private Node while_body() throws Exception{
        Node node = new Node("while_body");
        match("LCB", node);

        while (currTokenType().matches("VAR|IF|WHILE")) node.addChild(expr());
        match("RCB", node);
        return node;
    }

    private String currTokenType() {
        if (!tokens.isEmpty()){
            return tokens.get(0).getType();
        } else {return "";}
    }

    private String currToken() {
        if (!tokens.isEmpty()){
            return tokens.get(0).getData();
        }else {return "";}
    }

    private Token currTokenn() {
        if (!tokens.isEmpty()){
            return tokens.get(0);
        } else {return null;}
    }

    private void match(String token, Node currNode) {
        String t = currTokenType();
        StrTokens += currToken()+" ";
        assert (t.equals(token)) : "Current Token != " + token;
        currNode.addToken(tokens.get(0));
        Poliz.makePolizPerToken(currTokenn());
        tokens.remove(0);
    }

    private void matchSemicolon() {
        String t = currTokenType();
        if(t.equals("C_OP"))StrTokens += currToken()+" ";
        assert (t.equals("C_OP")) : "Semicolon missing";
        if(t.equals("C_OP")){
            Poliz.makePolizPerToken(currTokenn());
            tokens.remove(0);
        }
    }

    public void CheckTokens(){
        System.out.println("\n\n" + "All tokens: " + "\n" +StrTokens);
    }

    public void print(){
        System.out.println("\n\n"+"This is an abstract Syntax Tree"+"\n");
        print(root, 0);
    }

    //print tree
    private void print(Node root, int level){
        String tab = "";

        for (int i = 0; i< level; i++){tab += "    ";}
        System.out.println(tab+root.getName());

        for (Token l: root.getTokens()) {
            System.out.println(tab+"->"+l.getData());
        }

        for (Node n: root.getChild()) {
            print(n, level+1);
        }
    }
}

class Node {
    private String name;
    private ArrayList<Node> children = new ArrayList<>();
    private ArrayList<Token> tokens = new ArrayList<>();
    public Node(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void addChild(Node node) { children.add(node); }
    public ArrayList<Node> getChild() {
        return children;
    }
    public void addToken(Token token) {
        tokens.add(token);
    }
    public ArrayList<Token> getTokens(){return tokens;}
}


class Poliz {

    public static LinkedList<Token> poliz = new LinkedList<>();
    private static Stack<Token> stack = new Stack<>();
    public static Queue<Token> tokens;

    public static void makePolizPerToken(Token token) {

        if (token.type == Lexem.WHILE) {
            stack.add(new Token(Lexem.GOTO, "!"));
            stack.add(new Token(Lexem.GOTO_INDEX, Integer.toString(poliz.size())));
            stack.add(new Token(Lexem.GOTO, "!F"));
            stack.add(new Token(Lexem.GOTO_INDEX, Integer.toString(p(poliz.size(), tokens))));
        }

        if (token.type == Lexem.IF) {
            stack.add(new Token(Lexem.GOTO, "!F"));
            stack.add(new Token(Lexem.GOTO_INDEX, Integer.toString(p(poliz.size(), tokens))));
        }

        if(token.type == Lexem.ELSE){
            poliz.add(new Token(Lexem.GOTO_INDEX, Integer.toString(p(poliz.size(), tokens))));
            poliz.add(new Token(Lexem.GOTO, "!"));
        }

        if (token.type == Lexem.TYPE_W) {
            System.out.println("new");
            stack.add(token);
        }

        if (token.type == Lexem.TYPE) {
            poliz.add(token);
            poliz.add(stack.pop());
        }

        if (token.type == Lexem.INC || token.type == Lexem.DEC)
        {
            poliz.add(poliz.getLast());
            Token tmpToken = new Token(Lexem.NUM, "1");
            poliz.add(tmpToken);
            if (token.type == Lexem.INC)
            {
                tmpToken = new Token(Lexem.OP, "+");
            }
            else {tmpToken = new Token(Lexem.OP, "-");}
            poliz.add(tmpToken);
            poliz.add(new Token(Lexem.ASSIGN_OP, "="));
        }

        if (token.type == Lexem.VAR || token.type == Lexem.NUM) {
            poliz.add(token);
        }

        if (token.type == Lexem.OP || token.type == Lexem.BOOL_OP || token.type == Lexem.ASSIGN_OP || token.type == Lexem.FUNC_OP) {
            if (!stack.empty()) {
                while (getPriorOfOp(token.data) >= getPriorOfOp(stack.peek().data)) {
                    if(stack.peek().type != Lexem.GOTO && stack.peek().type != Lexem.GOTO_INDEX)poliz.add(stack.pop());
                    if (stack.empty() || stack.peek().type == Lexem.GOTO || stack.peek().type == Lexem.GOTO_INDEX){
                        break;
                    }
                }
            }
            stack.push(token);
        }

        if (token.type == Lexem.LB) {
            stack.push(token);
        }

        if (token.type == Lexem.RB) {
            if (!stack.empty()) {
                while (!stack.empty() && stack.peek().type != Lexem.LB) {
                    poliz.add(stack.pop());
                }
                if (!stack.empty() && stack.peek().type == Lexem.LB) {
                    stack.pop();
                }
                if (!stack.empty() && (stack.peek().type == Lexem.GOTO||stack.peek().type == Lexem.GOTO_INDEX)) {
                    poliz.add(stack.pop());
                    poliz.add(stack.pop());
                }
            }
        }

        if (token.type == Lexem.LCB) {
            //stack.push(token);
        }

        if (token.type == Lexem.RCB) {
            if (!stack.empty()) {
                while (!stack.empty() && stack.peek().type != Lexem.LCB) {
                    poliz.add(stack.pop());
                }
                if (!stack.empty() && stack.peek().type == Lexem.LCB) {
                    stack.pop();
                }
                if (!stack.empty() && (stack.peek().type == Lexem.GOTO||stack.peek().type == Lexem.GOTO_INDEX)) {
                    poliz.add(stack.pop());
                    poliz.add(stack.pop());
                }
            }
        }

        if (token.type == Lexem.C_OP) {
            while (!stack.empty()) {
                poliz.add(stack.pop());
            }
        }

    }

    public static void endPoliz(){
        while (!stack.empty()) {
            poliz.add(stack.pop());
        }
    }

    public static void setTokens(Queue<Token> input){
        tokens = input;
    }

    public static LinkedList<Token> makePoliz(Queue<Token> input) {
        while (!input.isEmpty()) {
            Token token = input.peek();
            if (!(token.type == Lexem.WHILE || token.type == Lexem.IF)) {
                makePolizFromExpr(input);
            }
            else {
                makePolizFromWhile(input, token);
            }
        }

        return poliz;
    }

    private static void makePolizFromWhile(Queue<Token> input, Token tmp) {
        Queue<Token> boolExpr = new LinkedList<>();
        input.poll();
        Token token = input.poll();
        int index = poliz.size();
        while (token.type != Lexem.LCB) {
            boolExpr.add(token);
            token = input.poll();
        }

        makePolizFromExpr(boolExpr);
        if (tmp.type == Lexem.WHILE) {
            poliz.add(new Token(Lexem.GOTO_INDEX, Integer.toString(p(poliz.size(), input))));
        }
        String p = Integer.toString(p(poliz.size(), input));
        if (tmp.type == Lexem.IF) {
            poliz.add(new Token(Lexem.GOTO_INDEX, p));
        }
        poliz.add(new Token(Lexem.GOTO, "!F"));

        Queue<Token> expr = new LinkedList<>();
        token = input.poll();
        while (token.type != Lexem.RCB) {
            if (token.type == Lexem.WHILE || token.type == Lexem.IF) {
                makePolizFromExpr(expr);
                makePolizFromWhile(input, token);
            }
            if (!(token.type == Lexem.WHILE || token.type == Lexem.IF))
                expr.add(token);
            token = input.poll();
        }
        makePolizFromExpr(expr);
        if (tmp.type != Lexem.IF)
            poliz.add(new Token(Lexem.GOTO_INDEX, Integer.toString(index)));
        if (tmp.type != Lexem.WHILE)
            poliz.add(new Token(Lexem.GOTO_INDEX, p));
        poliz.add(new Token(Lexem.GOTO, "!"));
    }

    private static int p(int size, Queue<Token> tokens) {
        int p = size;
        int i = 1;

        Queue<Token> newtokens = new LinkedList<>(tokens);
        Token newtoken = newtokens.poll();

        while (i > 0){
            if (newtokens.isEmpty())
            {
                break;
            }
            else
            {
                newtoken = newtokens.poll();
            }
            if (newtoken.type == Lexem.WHILE || newtoken.type == Lexem.IF) {
                i++;
                p--;
            }
            if (newtoken.type == Lexem.RCB) {
                i--;
            }
            if (newtoken.type != Lexem.C_OP) {
                if(!(newtoken.type == Lexem.INC || newtoken.type == Lexem.DEC)){
                    p++;
                }
                else p += 4;
            }
        }
        return p;
    }

    private static void makePolizFromExpr(Queue<Token> input) {
        Stack<Token> stack = new Stack<>();

        while (!input.isEmpty()) {
            Token token = input.peek();

            if (token.type == Lexem.WHILE || token.type == Lexem.IF) {
                break;
            }

            if (token.type == Lexem.TYPE_W) {
                stack.add(token);
            }

            if (token.type == Lexem.TYPE) {
                poliz.add(token);
                poliz.add(stack.pop());
            }

            token = input.poll();
            if (token.type == Lexem.INC || token.type == Lexem.DEC)
            {
                poliz.add(poliz.getLast());
                Token tmpToken = new Token(Lexem.NUM, "1");
                poliz.add(tmpToken);
                if (token.type == Lexem.INC)
                {
                    tmpToken = new Token(Lexem.OP, "+");
                }
                else {tmpToken = new Token(Lexem.OP, "-");}
                poliz.add(tmpToken);
                poliz.add(new Token(Lexem.ASSIGN_OP, "="));
            }

            if (token.type == Lexem.VAR || token.type == Lexem.NUM) {
                poliz.add(token);
            }

            if (token.type == Lexem.OP || token.type == Lexem.BOOL_OP || token.type == Lexem.ASSIGN_OP || token.type == Lexem.FUNC_OP) {
                if (!stack.empty()) {
                    while (getPriorOfOp(token.data) >= getPriorOfOp(stack.peek().data)) {
                        poliz.add(stack.pop());
                        if (stack.empty()){
                            break;
                        }
                    }
                }
                stack.push(token);
            }

            if (token.type == Lexem.LB) {
                stack.push(token);
            }

            if (token.type == Lexem.RB) {
                if (!stack.empty()) {
                    while (!stack.empty() && stack.peek().type != Lexem.LB) {
                        poliz.add(stack.pop());
                    }
                    if (!stack.empty() && stack.peek().type == Lexem.LB) {
                        stack.pop();
                    }
                }
            }

            if (token.type == Lexem.C_OP) {
                while (!stack.empty()) {
                    poliz.add(stack.pop());
                }
            }
        }

        while (!stack.empty()) {
            poliz.add(stack.pop());
        }
    }

    private static int getPriorOfOp(String op) {
        if (op.equals("*") || op.equals("/"))
            return 0;
        else if (op.equals("*") || op.equals("/"))
            return 1;
        else if (op.equals("+") || op.equals("-"))
            return 2;
        else if (op.equals(">") || op.equals(">=") || op.equals("<") || op.equals("<=") || op.equals("==") || op.equals("!="))
            return 3;
        else if (op.equals("="))
            return 5;
        else
            return 4;
    }

    public static LinkedList<Token> getPoliz(){return poliz;}

}