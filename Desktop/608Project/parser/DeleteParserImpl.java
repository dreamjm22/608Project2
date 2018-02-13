package parser;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DeleteParserImpl implements ParseInterface {
    public void parse(Node node, String[] SQLCmdArray){
        ParseHelper parseHelper = new ParseHelper();
        Node table = new Node(SQLCmdArray[2], null);
        node.addNodeToChildrenList(table);

        //if there is "WHERE" condition
        if(SQLCmdArray.length > 3){
            // System.out.println("DeleteParserImpl");
            Node where = new Node("WHERE", new ArrayList<>());
            node.addNodeToChildrenList(where);
            where.addNodeToChildrenList(parseHelper.findCondition(Arrays.copyOfRange(SQLCmdArray, 3, SQLCmdArray.length)));

            /*
            System.out.println(table.getChildren().get(0).getElement());
            System.out.println(table.getChildren().get(1).getElement());
            System.out.println(table.getChildren().get(1).getChildren().get(0).getElement());
            System.out.println(table.getChildren().get(1).getChildren().get(1).getElement());*/
        }
    }
}
