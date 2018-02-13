package parser;

import java.util.Arrays;
import java.util.List;

public class InsertParserImpl implements ParseInterface {
    public void parse(Node node, String[] SQLCmdArray){
        ParseHelper parseHelper = new ParseHelper();
        Node table = new Node(SQLCmdArray[2], null);
        node.addNodeToChildrenList(table);

        List<String> removingSpecificCharsCompletedSQLCmdArray = parseHelper.removeSpecificChars(SQLCmdArray);
        List<String> removingCompletedSQLCmdArray = parseHelper.removeSpecificQuotes(removingSpecificCharsCompletedSQLCmdArray);

        int index = 3;
        int attributeNumber = 0;
        while (! (removingCompletedSQLCmdArray.get(index).equals("VALUES") || removingCompletedSQLCmdArray.get(index).equals("SELECT"))) {
            table.addToAttributeTypeList(removingCompletedSQLCmdArray.get(index));
            index++;
            attributeNumber++;
        }


        if (SQLCmdArray[index].equals("VALUES")) {
            // Jump over "VALUE"
            index = index + 1;
            while (attributeNumber > 0) {
                // System.out.println(removingCompletedSQLCmdArray.get(index));
                table.addToAttributeList(removingCompletedSQLCmdArray.get(index));
                attributeNumber--;
                index++;
            }
        } else {
            // SELECT
            ParseTree parseTree = new ParseTree();
            /*
            Node select = parseTree.initialParse(Arrays.copyOfRange(SQLCmdArray, index , SQLCmdArray.length));
            parseTree.advancedParse(select, Arrays.copyOfRange(SQLCmdArray, index , SQLCmdArray.length));
            node.addNodeToChildrenList(select);*/


            Node select = parseTree.parse(concatString(SQLCmdArray, index, SQLCmdArray.length));
            node.addNodeToChildrenList(select);
        }
    }

    private String concatString(String[] SQLCmdArray, int startIndex, int endIndex) {
        StringBuilder stringBuilder = new StringBuilder("");
        for(int i = startIndex; i < endIndex; i++) {
            stringBuilder.append(SQLCmdArray[i]);
            stringBuilder.append(" ");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        // System.out.print(stringBuilder.toString());
        return stringBuilder.toString();
    }
}
