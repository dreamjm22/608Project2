package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class ParseHelper {
    private HashMap<String, Integer> priorityTable;
    public ParseHelper () {
        priorityTable = new HashMap<String, Integer>();
        priorityTable.put("OR", 0);
        priorityTable.put("AND",1);
        priorityTable.put("NOT",2);
        priorityTable.put("=", 3);
        priorityTable.put(">", 3);
        priorityTable.put("<", 3);
        priorityTable.put("+", 4);
        priorityTable.put("-", 4);
        priorityTable.put("*", 5);
        priorityTable.put("/", 5);
    }

    // remove the ',' '(' ')' '[' ']' characters in SQL command
    public List<String> removeSpecificChars (String[] SQLCmdArray) {
        List<String> removingCompletedSQLCmdArray = new ArrayList<>();
        for (String SQLCmd : SQLCmdArray) {
            /*
            if (SQLCmd.charAt(0) == ',' || SQLCmd.charAt(0) == '(' || SQLCmd.charAt(0) == '[') {
                SQLCmd = SQLCmd.substring(1, SQLCmd.length());
            }
            if (SQLCmd.charAt(SQLCmd.length() - 1) == ',' || SQLCmd.charAt(SQLCmd.length() - 1) == ')' || SQLCmd.charAt(SQLCmd.length() - 1) == ']') {
                SQLCmd = SQLCmd.substring(0, SQLCmd.length() - 1);
            }*/
            // improvements
            SQLCmd = removeSpecificChars(SQLCmd);
            removingCompletedSQLCmdArray.add(SQLCmd);
        }
        return removingCompletedSQLCmdArray;
    }

    public String removeSpecificChars(String SQLCmd) {
        if (SQLCmd.charAt(0) == ',' || SQLCmd.charAt(0) == '(' || SQLCmd.charAt(0) == '[') {
            SQLCmd = SQLCmd.substring(1, SQLCmd.length());
        }
        if (SQLCmd.charAt(SQLCmd.length() - 1) == ',' || SQLCmd.charAt(SQLCmd.length() - 1) == ')' || SQLCmd.charAt(SQLCmd.length() - 1) == ']') {
            SQLCmd = SQLCmd.substring(0, SQLCmd.length() - 1);
        }
        return SQLCmd;
    }

    public List<String> removeSpecificQuotes (List<String> SQLCmdArray) {
        List<String> removingCompletedSQLCmdArray = new ArrayList<>();
        for (String SQLCmd : SQLCmdArray) {
            removingCompletedSQLCmdArray.add(SQLCmd.replace("\"",""));
        }
        return removingCompletedSQLCmdArray;
    }

    public Node findCondition (String[] SQLCmdArray) {
        // System.out.println("findCondition");
        // System.out.println(SQLCmdArray.length);
        Stack<Node> SQLCmdStack = new Stack<>();
        for (int i = 1; i < SQLCmdArray.length; i++) {
            String SQLCmd = SQLCmdArray[i];
            //System.out.println(SQLCmd);
            //System.out.println("i="+ i);
            if (priorityTable.containsKey(SQLCmd)) {
                if (SQLCmdStack.size() >= 3) {
                    Node stackTopNode = SQLCmdStack.pop();
                    if (priorityTable.get(SQLCmdStack.peek().getElement()) <= priorityTable.get(SQLCmd)) {
                        // improvements
                        // System.out.println("In if");
                        // System.out.println(priorityTable.get(SQLCmdStack.peek().getElement()));
                        // System.out.println(priorityTable.get(SQLCmd));
                        SQLCmdStack.push(stackTopNode);
                        SQLCmdStack.push(new Node(SQLCmd, null));
                    } else {
                        // System.out.println("In else");
                        while (SQLCmdStack.size() > 0 && priorityTable.get(SQLCmdStack.peek().getElement()) > priorityTable.get(SQLCmd)) {
                            Node operationNode = SQLCmdStack.pop();
                            if (operationNode.getChildren() == null) {
                                operationNode.setChildrenList(new ArrayList<>());
                            }
                            if (SQLCmdStack.size() > 0)
                                operationNode.addNodeToChildrenList(SQLCmdStack.pop());
                            operationNode.addNodeToChildrenList(stackTopNode);
                            stackTopNode = operationNode;
                        }
                        // improvements
                        SQLCmdStack.push(stackTopNode);
                        SQLCmdStack.push(new Node(SQLCmd, null));
                    }
                } else {
                    // System.out.println("hehe");
                    SQLCmdStack.push(new Node(SQLCmd, null));
                }
            } else if (isInteger(SQLCmd) || SQLCmd.charAt(0) == '"') {
                SQLCmdStack.push(new Node(SQLCmd, null));
            } else if (SQLCmd.equals("(")) {
                //System.out.println("In the brackets");
                ArrayList<String> whereCondition = new ArrayList<String>();
                // i++;
                while(!SQLCmdArray[i].equals(")")) {
                    whereCondition.add(SQLCmdArray[i]);
                    i++;
                }
                // i++;
                //System.out.println("i haha =" + i);
                String[] whereRealCondition = whereCondition.toArray(new String[0]);
                //System.out.println(whereRealCondition.length);
                SQLCmdStack.push(findCondition(whereRealCondition));
            }
            else if (SQLCmd.equals("[")) {
                //System.out.println("In the brackets");
                ArrayList<String> whereCondition = new ArrayList<String>();
                // i++;
                while(!SQLCmdArray[i].equals("]")) {
                    whereCondition.add(SQLCmdArray[i]);
                    i++;
                }
                // i++;
                //System.out.println("i haha =" + i);
                String[] whereRealCondition = whereCondition.toArray(new String[0]);
                //System.out.println(whereRealCondition.length);
                SQLCmdStack.push(findCondition(whereRealCondition));
            } else {
                // (operation = operation)
                //System.out.println("In the else");
                SQLCmdStack.push(new Node(SQLCmd, null));
            }
            // System.out.println("i hahaee =" + i);
        }
        //System.out.println("size" + SQLCmdStack.size());
        if (SQLCmdStack.size() >= 3) {
            Node stackTopNode = SQLCmdStack.pop();
            // System.out.println(stackTopNode.getElement());
            while (SQLCmdStack.size() >= 2) {
                // System.out.println("hehe");
                Node operationNode = SQLCmdStack.pop();

                if (operationNode.getElement().equals("NOT")) {
                    // System.out.println("In NOT");
                    if (operationNode.getChildren() == null) {
                        operationNode.setChildrenList(new ArrayList<>());
                    }
                    // System.out.println(operationNode.getElement());
                    operationNode.addNodeToChildrenList(stackTopNode);
                    stackTopNode = operationNode;
                } else {
                    if (operationNode.getChildren() == null) {
                        operationNode.setChildrenList(new ArrayList<>());
                    }
                    // System.out.println(operationNode.getElement());
                    // System.out.println(SQLCmdStack.peek().getElement());
                    if (SQLCmdStack.size() > 0)
                        operationNode.addNodeToChildrenList(SQLCmdStack.pop());
                    operationNode.addNodeToChildrenList(stackTopNode);
                    stackTopNode = operationNode;
                }
            }

            if (SQLCmdStack.size() == 1 && SQLCmdStack.peek().getElement().equals("NOT")) {
                Node operationNode = SQLCmdStack.pop();
                if (operationNode.getChildren() == null) {
                    operationNode.setChildrenList(new ArrayList<>());
                }
                operationNode.addNodeToChildrenList(stackTopNode);
                stackTopNode = operationNode;
            } else if (SQLCmdStack.size() == 1 ) {
                System.out.println("Unexpected token");
            }

            // System.out.println("DONE");
            return stackTopNode;
        } else {
            return SQLCmdStack.peek();
        }
    }

    private boolean isInteger (String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        if (str.charAt(0) == '-' && str.length() == 1) {
            return false;
        }
        int strIndex = 0;
        if (str.charAt(0) == '-') {
            strIndex = 1;
        }
        for (; strIndex < str.length(); strIndex++) {
            if (str.charAt(strIndex) < '0' || str.charAt(strIndex) > '9') {
                return false;
            }
        }
        return true;
    }

}
