package parser;

import java.util.*;

public class ParseTree {
    HashMap<String, ParseInterface> parseLib = new HashMap<>();
    public ParseTree () {
        parseLib.put("CREATE", new CreateParserImpl());
        parseLib.put("INSERT", new InsertParserImpl());
        parseLib.put("SELECT", new SelectParserImpl());
        parseLib.put("DELETE", new DeleteParserImpl());
        parseLib.put("DROP", new DropParserImpl());
    }

    public Node parse (String SQLCmd){
        String[] SQLCmdArray = SQLCmd.split(" ");      // split the SQLCmd by space
        Node node = initialParse(SQLCmdArray);                // build the first node: CREATE/DROP/SELECT/INSERT/DELETE
        advancedParse(node, SQLCmdArray);                     // call function of each kind of SQL operations
        return node;
    }

    private Node initialParse(String[] SQLCmdArray){
        Node node = new Node(SQLCmdArray[0], new ArrayList<>());
        return node;
    }

    //call different functions: 'CREATE', 'INSERT', 'SELECT', 'DROP', 'DELETE'
    private void advancedParse(Node node, String[] SQLCmdArray){
        ParseInterface parseInterface = parseLib.get(node.getElement());
        parseInterface.parse(node, SQLCmdArray);
    }
}
