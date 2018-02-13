package parser;
import java.util.*;

public class CreateParserImpl implements ParseInterface {
    public void parse(Node node, String[] SQLCmdArray){
        ParseHelper parseHelper = new ParseHelper();
        Node table = new Node(SQLCmdArray[2], null);
        node.addNodeToChildrenList(table);

        List<String> removingCompletedSQLCmdArray = parseHelper.removeSpecificChars(SQLCmdArray);
        for (int index = 3; index < removingCompletedSQLCmdArray.size(); index = index + 2) {
            table.addToAttributeTypeList(removingCompletedSQLCmdArray.get(index));
            table.addToAttributeList(removingCompletedSQLCmdArray.get(index + 1));
        }
    }
}
