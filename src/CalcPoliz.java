import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

public class CalcPoliz {
    public static Map<String, String> types = new HashMap<>();
    public static Map<String, Object> values  = new HashMap<>();

    public static void calculate(LinkedList<Token> poliz) {
        Stack<Token> stack = new Stack<>();
        for (int i = 0; i < poliz.size(); i++) {
            Token token = poliz.get(i);

            if (token.type == Lexem.VAR || token.type == Lexem.NUM || token.type == Lexem.GOTO_INDEX || token.type == Lexem.TYPE) {
                stack.add(token);
            }

            if (token.type == Lexem.TYPE_W) {
                Token type = stack.pop();
                Token var = stack.pop();
                if (!values.containsKey(var.data)) {
                    types.put(var.data, type.data);
                    if (!type.data.equals("Number")) {
                        values.put(var.data, null);
                    } else {
                        values.put(var.data, 0);
                    }
                }
            }

            if (token.type == Lexem.FUNC_OP) {
                Token token2 = stack.pop();
                Token token1 = stack.pop();
                if (values.get(token1.data) == null) {
                    if (types.get(token1.data).equals("List")) {
                        values.put(token1.data, new MyLinkedList());
                    }
                    else if (types.get(token1.data).equals("Set")) {
                        values.put(token1.data, new MyHashSet());
                    }
                }
                if (types.get(token1.data).equals("List")) {
                    if (token2.type == Lexem.NUM) {
                        if (token.data.equals("add")) {
                            ((MyLinkedList) values.get(token1.data)).add(token2.data);
                        } else if (token.data.equals("get")) {
                            Token tmptoken = stack.peek();
                            stack.add(new Token(Lexem.NUM, (String) ((MyLinkedList) values.get(token1.data)).get(Integer.parseInt(token2.data))));
                            //     values.put(tmptoken.data, Integer.parseInt(stack.peek().data));
                        } else if (token.data.equals("remove")) {
                            ((MyLinkedList) values.get(token1.data)).remove(Integer.parseInt(token2.data));
                        } else if (token.data.equals("contains")) {
                            ((MyLinkedList) values.get(token1.data)).contains(token2.data);
                        }
                    }
                    else if (token2.type == Lexem.VAR) {
                        if (token.data.equals("add")) {
                            ((MyLinkedList) values.get(token1.data)).add(values.get(token2.data));
                        } else if (token.data.equals("get")) {
                            stack.add(new Token(Lexem.NUM, (String) ((MyLinkedList) values.get(token1.data)).get((Integer)values.get(token2.data))));
                        } else if (token.data.equals("remove")) {
                            ((MyLinkedList) values.get(token1.data)).remove((Integer)(values.get(token2.data)));
                        } else if (token.data.equals("contains")) {
                            stack.add(new Token(Lexem.BOOL, Boolean.toString(((MyLinkedList) values.get(token1.data)).contains(values.get(token2.data)))));
                        }
                    }
                }
                else if (types.get(token1.data).equals("Set")) {
                    if (token2.type == Lexem.NUM) {
                        if (token.data.equals("add")) {
                            ((MyHashSet) values.get(token1.data)).add(token2.data);
                        } else if (token.data.equals("remove")) {
                            ((MyHashSet) values.get(token1.data)).remove(Integer.parseInt(token2.data));
                        } else if (token.data.equals("contains")) {
                            ((MyHashSet) values.get(token1.data)).contains(token2.data);
                        }
                    }
                    else if (token2.type == Lexem.VAR) {
                        if (token.data.equals("add")) {
                            ((MyHashSet) values.get(token1.data)).add(values.get(token2.data));
                        } else if (token.data.equals("remove")) {
                            ((MyHashSet) values.get(token1.data)).remove(values.get(token2.data));
                        } else if (token.data.equals("contains")) {
                            stack.add(new Token(Lexem.BOOL, Boolean.toString(((MyHashSet) values.get(token1.data)).contains(values.get(token2.data)))));
                        }
                    }
                }
            }

            if (token.type == Lexem.BOOL_OP) {
                int a1, a2;
                Token token2 = stack.pop();
                Token token1 = stack.pop();
                if (token1.type == Lexem.VAR) {
                    a1 = (Integer)values.get(token1.data);
                }
                else {
                    a1 = Integer.parseInt(token1.data);
                }
                if (token2.type == Lexem.VAR) {
                    a2 = (Integer)values.get(token2.data);
                }
                else {
                    a2 = Integer.parseInt(token2.data);
                }
                if (token.data.equals("==")) {
                    if (a1 == a2) {
                        stack.add(new Token(Lexem.BOOL, "true"));
                    }
                    else {
                        stack.add(new Token(Lexem.BOOL, "false"));
                    }
                }
                if (token.data.equals("<=")) {
                    if (a1 <= a2) {
                        stack.add(new Token(Lexem.BOOL, "true"));
                    }
                    else {
                        stack.add(new Token(Lexem.BOOL, "false"));
                    }
                }
                if (token.data.equals(">=")) {
                    if (a1 >= a2) {
                        stack.add(new Token(Lexem.BOOL, "true"));
                    }
                    else {
                        stack.add(new Token(Lexem.BOOL, "false"));
                    }
                }
                if (token.data.equals("<")) {
                    if (a1 < a2) {
                        stack.add(new Token(Lexem.BOOL, "true"));
                    }
                    else {
                        stack.add(new Token(Lexem.BOOL, "false"));
                    }
                }
                if (token.data.equals(">")) {
                    if (a1 > a2) {
                        stack.add(new Token(Lexem.BOOL, "true"));
                    }
                    else {
                        stack.add(new Token(Lexem.BOOL, "false"));
                    }
                }
                if (token.data.equals("!=")) {
                    if (a1 != a2) {
                        stack.add(new Token(Lexem.BOOL, "true"));
                    }
                    else {
                        stack.add(new Token(Lexem.BOOL, "false"));
                    }
                }
            }

            if (token.type == Lexem.ASSIGN_OP) {
                int a2;
                Token token1, token2;
                if (!stack.empty())
                    token2 = stack.pop();
                else
                    continue;
                if (!stack.empty())
                    token1 = stack.pop();
                else
                    continue;
                if (token2.type == Lexem.VAR) {
                    a2 = (Integer)values.get(token2.data);
                }
                else {
                    a2 = Integer.parseInt(token2.data);
                }
                values.put(token1.data, a2);
            }

            if (token.type == Lexem.OP) {
                int a1, a2;
                Token token1, token2;
                if (!stack.empty())
                    token2 = stack.pop();
                else
                    continue;
                if (!stack.empty())
                    token1 = stack.pop();
                else
                    continue;
                if (token1.type == Lexem.VAR) {
                    a1 = (Integer)values.get(token1.data);
                }
                else {
                    a1 = Integer.parseInt(token1.data);
                }
                if (token2.type == Lexem.VAR) {
                    a2 = (Integer)values.get(token2.data);
                }
                else {
                    a2 = Integer.parseInt(token2.data);
                }
                if (token.data.equals("*")) {
                    stack.add(new Token(Lexem.NUM, Integer.toString(a1 * a2)));
                }
                if (token.data.equals("/")) {
                    stack.add(new Token(Lexem.NUM, Integer.toString(a1 / a2)));
                }
                if (token.data.equals("+")) {
                    stack.add(new Token(Lexem.NUM, Integer.toString(a1 + a2)));
                }
                if (token.data.equals("-")) {
                    stack.add(new Token(Lexem.NUM, Integer.toString(a1 - a2)));
                }
            }

            if (token.type == Lexem.GOTO) {
                Token index;
                if (!stack.empty()) {
                    index = stack.pop();
                }
                else
                {
                    break;
                }
                if (token.data.equals("!F")) {
                    Token res = stack.pop();
                    if (res.data.equals("true")) {
                        continue;
                    }
                    else {
                        i = Integer.parseInt(index.data) - 1;
                    }
                }
                else {
                    i = Integer.parseInt(index.data) - 1;
                }
            }
        }
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            System.out.println(entry.getKey() + " \t = \t " + entry.getValue() + ";");
        }
    }
}