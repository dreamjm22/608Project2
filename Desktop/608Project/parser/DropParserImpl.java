package parser;

public class DropParserImpl implements ParseInterface {
    public void parse(Node node, String[] SQLCmdArray){
        Node table = new Node (SQLCmdArray[2],null);
        node.addNodeToChildrenList(table);
    }
}
