package parser;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectParserImpl implements ParseInterface {
    public void parse(Node node, String[] SQLCmdArray){
        ParseHelper parseHelper = new ParseHelper();
        int fromIndex = 0;
        int whereIndex = 0;
        int orderIndex = 0;

        // System.out.println("SelectParserImpl");



        for (int index = 0; index < SQLCmdArray.length; index++) {
            if (SQLCmdArray[index].equals("DISTINCT")) {
                // System.out.println("DISTINCT");
                Node distinct = new Node("DISTINCT", null);
                node.addNodeToChildrenList(distinct);
            } else if (SQLCmdArray[index].equals("FROM")) {
                // attribute
                // System.out.println("FROM");
                fromIndex = index;
                if (node.getChildren().size() == 0) { // No DISTINCT
                    addAttributeToNode(node, 1, fromIndex, SQLCmdArray, parseHelper);
                } else {
                    addAttributeToNode(node, 2, fromIndex, SQLCmdArray, parseHelper);
                }
            } else if (SQLCmdArray[index].equals("WHERE")) {
                // from
                // System.out.println("WHERE");
                whereIndex = index;
                Node table = new Node("table", new ArrayList<>());
                for(int i = fromIndex + 1; i < whereIndex; i++) {
                    table.addNodeToChildrenList(new Node(SQLCmdArray[i], null));
                }
                node.addNodeToChildrenList(table);
            } else if (SQLCmdArray[index].equals("ORDER")) {
                // System.out.println("ORDER");
                orderIndex = index;
                Node order = new Node("ORDER" , null);
                order.addToAttributeList(SQLCmdArray[orderIndex + 2]);
                node.addNodeToChildrenList(order);
            }
        }
        if (whereIndex ==0){
            // System.out.println("whereIndex");
            Node table = new Node("table", new ArrayList<>());
            // System.out.println(fromIndex);
            for(int i = fromIndex + 1 ; i < SQLCmdArray.length; i++) {
                // System.out.println(SQLCmdArray[i]);
                if(SQLCmdArray[i].equals("ORDER"))
                    break;
                table.addNodeToChildrenList(new Node(SQLCmdArray[i], null));

            }
            node.addNodeToChildrenList(table);
        }

        if (orderIndex == 0 && whereIndex != 0) { // No ORDER
            addWhereToNode(node, whereIndex, SQLCmdArray.length, SQLCmdArray, parseHelper);
        } else if (orderIndex !=0 && whereIndex != 0) {
            addWhereToNode(node, whereIndex, orderIndex, SQLCmdArray, parseHelper);
        }
/*
        System.out.println("===================");
        System.out.println(node.getAttributeList().get(0));
        System.out.println(node.getChildren().get(0).getElement());
        System.out.println(node.getChildren().get(1).getChildren().get(0).getElement());*/
    }

    private void addAttributeToNode(Node node, int startIndex, int endIndex, String[] SQLCmdArray, ParseHelper parseHelper) {
        // System.out.println("addAttributeToNode");
        // System.out.println(startIndex);
        // System.out.println(endIndex);
        Node attribute = new Node ("attribute", new ArrayList<>());
        for(int i = startIndex; i < endIndex; i++) {
            attribute.addToAttributeList(parseHelper.removeSpecificChars(SQLCmdArray[i]));
        }
        node.addNodeToChildrenList(attribute);
    }

    private void addWhereToNode(Node node, int startIndex, int endIndex, String[] SQLCmdArray, ParseHelper parseHelper) {
        // System.out.println("addWhereToNode");
        // System.out.println(startIndex);
        // System.out.println(endIndex);
        Node where = new Node("WHERE", new ArrayList<>());

        String[] SQLCmdArrayTemp = Arrays.copyOfRange(SQLCmdArray, startIndex, endIndex);
        ArrayList<String> ArrayListTemp = new ArrayList<>();
        for(int i=0;i<SQLCmdArrayTemp.length;i++){
            if (SQLCmdArrayTemp[i].contains("=") && SQLCmdArrayTemp[i].length()!=1) {
                // System.out.println("Wrong where parser");
                int index = SQLCmdArrayTemp[i].indexOf('=');
                ArrayListTemp.add(SQLCmdArrayTemp[i].substring(0,index));
                ArrayListTemp.add("=");
                ArrayListTemp.add(SQLCmdArrayTemp[i].substring(index+1,SQLCmdArrayTemp[i].length()));
            } else {
                ArrayListTemp.add(SQLCmdArrayTemp[i]);
            }
        }
        String [] RealSQLCmdArrayTemp = ArrayListTemp.toArray(new String[SQLCmdArrayTemp.length]);
        where.addNodeToChildrenList(parseHelper.findCondition(RealSQLCmdArrayTemp));
        node.addNodeToChildrenList(where);
    }

}
