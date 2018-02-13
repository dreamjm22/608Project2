package operator;

import Optimization.PhysicalQueryPlanGenerator;
import Optimization.ImplementationOfPhysicalOperator;
import parser.Node;
import parser.ParseHelper;
import storageManager.Disk;
import storageManager.FieldType;
import storageManager.MainMemory;
import storageManager.Relation;
import storageManager.Block;
import storageManager.SchemaManager;
import storageManager.Tuple;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class SelectOperatorImpl implements OperatorInterface {
    public void operatorImpl (List<Node> list, Disk disk, MainMemory mainMemory, SchemaManager schemaManager){
        // DISTINCT->attribute->table->WHERE
        // System.out.println("=======Select operator ======");
        /*
        System.out.println(list.get(0).getElement());
        System.out.println(list.get(1).getElement());
        System.out.println(list.get(2).getElement());
        System.out.println(list.get(2).getChildren().get(0).getElement());
        System.out.println(list.get(1).getAttributeList().get(0));*/
        Expression expression = null;

        int DistinctMark=0;
        ArrayList<String> OrderAttributes = new ArrayList<>();
        ArrayList<String> DistinctAttributes = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(list.get(i).getElement().equals("ORDER")){
                OrderAttributes= new ArrayList<String>(list.get(i).getAttributeList());
                list.remove(i);
                break;
            }
        }
        if(list.get(0).getElement().equals("DISTINCT")){
            DistinctAttributes = new ArrayList<String>(list.get(0).getAttributeList());
            DistinctMark=1;
            list.remove(0);
        }

        if (list.get(1).getElement().equals("table")){
            // System.out.println("table");
            List<String> attributeList = list.get(0).getAttributeList();
            List<Node> tableList = list.get(1).getChildren();
            if (tableList.size() == 1) {
                // One table
                // System.out.println("One table");
                if (list.size() > 2 && list.get(2).getElement().equals("WHERE")) {
                    // Have WHERE
                    expression = new Expression(list.get(2).getChildren().get(0));
                    // System.out.println(expression.node.getElement());
                    basicSelectOperation(mainMemory, schemaManager, tableList, expression, DistinctMark, OrderAttributes);
                } else {
                    if (attributeList.size() == 1 && attributeList.get(0).equals("*")) {
                        // SELECT * FROM Table WHERE
                        // System.out.println("Select *");
                        basicSelectOperation(mainMemory, schemaManager, tableList, expression, DistinctMark, OrderAttributes);
                    } else {
                        // SELECT sid, grade FROM course
                        // SELECT sid, course.grade FROM course
                        basicSelectOperationForSpecificFields(mainMemory, schemaManager, tableList, attributeList, DistinctMark, OrderAttributes);
                    }
                }
            } else {
                if (tableList.size() == 2 && list.size() > 2 && list.get(2).getElement().equals("WHERE") && list.get(2).getChildren().get(0).getElement().equals("=")) {
                    // SELECT course.sid, course.grade, course2.grade FROM course, course2 WHERE course.sid = course2.sid
                    // System.out.println("We are here in where");
                    // Have WHERE
                    expression = new Expression(list.get(2).getChildren().get(0));
                    ParseHelper parseHelper = new ParseHelper();
                    String table1 = parseHelper.removeSpecificChars(tableList.get(0).getElement());
                    String table2 = parseHelper.removeSpecificChars(tableList.get(1).getElement());
                    String field1 = attributeHelper(list.get(2).getChildren().get(0).getChildren().get(0).getElement());
                    String field2 = attributeHelper(list.get(2).getChildren().get(0).getChildren().get(1).getElement());
                    ArrayList<String> fields = new ArrayList<>(attributeList);
                    List<Tuple> tuples;
                    if(field1.equals(field2)) {
                        PhysicalQueryPlanGenerator physicalQueryPlanGenerator = new PhysicalQueryPlanGenerator();
                        Relation r = physicalQueryPlanGenerator.NaturalJoinOperation2(schemaManager, mainMemory, table1, table2, field1);
                        if(DistinctMark==0 && OrderAttributes.size()==0) {
                            // System.out.println("SELECT course.sid, course.grade, course2.grade FROM course, course2 WHERE course.sid = course2.sid");
                            // System.out.println("We are here in where if");
                            tuples=physicalQueryPlanGenerator.filter1(schemaManager, mainMemory, r, expression, fields);
                        } else if(DistinctMark ==0){
                            // System.out.println("We are here in where in else if");
                            Relation ra = physicalQueryPlanGenerator.filter2(schemaManager, mainMemory, r, expression, fields);
                            tuples = ImplementationOfPhysicalOperator.executeOrder1(mainMemory, ra, OrderAttributes);
                        }else {

                            // System.out.println("We are here in where in else");
                            Relation ra = physicalQueryPlanGenerator.filter2(schemaManager, mainMemory, r, expression, fields);
                            if (DistinctAttributes.size() != 0) {
                                // System.out.println("We are here in where in else if");
                                fields = DistinctAttributes;
                            }
                            if (OrderAttributes.size() == 0) {
                                // System.out.println("We are here in where in else if if ");
                                tuples = ImplementationOfPhysicalOperator.DinstinctOperation1(mainMemory, schemaManager, ra, fields);

                            } else {
                                tuples = new ArrayList<>();
                                // Never been here
                            }
                        }
                        for(String str: tuples.get(0).getSchema().getFieldNames()) {
                            int index = str.lastIndexOf(".");
                            String substring = str.substring(0,index);
                            int index2 = substring.lastIndexOf(".");
                            // Change here
                            String substring2 = str.substring(index2+1, str.length());
                            System.out.print(str.substring(index2+1, str.length()) + " ");
                        }
                        System.out.println();

                        for (Tuple t : tuples) {
                            System.out.println(t);
                        }
                    }
                } else if (tableList.size() == 2 && list.size() > 2 && list.get(2).getElement().equals("WHERE")) {
                    // System.out.println("SELECT * FROM course, course2 WHERE course.sid = course2.sid AND course.exam = 100 AND course2.exam = 100");
                    expression = new Expression(list.get(2).getChildren().get(0));
                    PhysicalQueryPlanGenerator physicalQueryPlanGenerator = new PhysicalQueryPlanGenerator();
                    ParseHelper parseHelper = new ParseHelper();
                    String table1 = parseHelper.removeSpecificChars(tableList.get(0).getElement());
                    String table2 = parseHelper.removeSpecificChars(tableList.get(1).getElement());
                    ArrayList<String> relationList = new ArrayList<>();
                    relationList.add(table1);
                    relationList.add(table2);

                    if(DistinctMark==0 && OrderAttributes.size()==0){
                        // System.out.println("TEmp Function");
                        physicalQueryPlanGenerator.tempFunction(schemaManager, mainMemory, relationList, expression);
                    }
                    else if(DistinctMark ==0){
                        // Never been here

                    }else{
                        // System.out.println("Unknown else");
                        Relation relationAfterCross = physicalQueryPlanGenerator.tempFunction2(schemaManager, mainMemory, relationList, expression);
                        // System.out.println(relationAfterCross.getNumOfTuples());

                        ArrayList<String> fields = relationAfterCross.getSchema().getFieldNames();
                        List<Tuple> tuples;
                        // System.out.println("HEHE");
                        if(DistinctAttributes.size()!= 0) {
                            // System.out.println("Unknown else if ");
                            fields = DistinctAttributes;
                        }
                        if(OrderAttributes.size()==0) {
                            // Never been here

                        }else{
                            // System.out.println("Unknown else if else");
                            // System.out.println("HEHEHE");
                            // Relation ra = physicalQueryPlanGenerator.filter2(schemaManager, mainMemory, relationAfterCross, expression, fields);
                            Relation r = ImplementationOfPhysicalOperator.DinstinctOperation2(mainMemory, schemaManager, relationAfterCross, DistinctAttributes);
                            tuples = ImplementationOfPhysicalOperator.executeOrder1(mainMemory, r, OrderAttributes);
                            // System.out.println(r.getSchema().getFieldNames());
                            // ArrayList<String> fields0 = new ArrayList<>(attributeList);
                            // System.out.println(fields0);

                            System.out.print(attributeList.get(0));
                            System.out.print(" "+ attributeList.get(1));
                            System.out.println();

                            int index1 = r.getSchema().getFieldNames().indexOf(attributeList.get(0));
                            int index2 = r.getSchema().getFieldNames().indexOf(attributeList.get(1));
                            Set<String> set = new HashSet<>();

                            for(Tuple t:tuples) {
                                set.add(t.getField(index1) + " " + t.getField(index2));
                            }
                            for(String s: set)
                                System.out.println(s);

                            // System.out.println(t);
                        }
                    }
                } else if (tableList.size() == 2){
                    // SELECT * FROM course, course2
                    // System.out.println("We are here");
                    PhysicalQueryPlanGenerator physicalQueryPlanGenerator = new PhysicalQueryPlanGenerator();
                    ArrayList<String> relationName = new ArrayList<>();
                    for (int i = 0; i < tableList.size(); i++) {
                        ParseHelper parseHelper = new ParseHelper();
                        String removeSpecificCharsString = parseHelper.removeSpecificChars(tableList.get(i).getElement());
                        relationName.add(removeSpecificCharsString);
                    }
                    ArrayList<Tuple> tuples;
                    if(DistinctMark==0 && OrderAttributes.size()==0) {
                        // System.out.println("We are here if");
                        tuples = physicalQueryPlanGenerator.CrossJoinOperation1(schemaManager, mainMemory, relationName, new Node());
                        for(String str: tuples.get(0).getSchema().getFieldNames()) {
                            int index = str.lastIndexOf(".");
                            String substring = str.substring(0,index);
                            int index2 = substring.lastIndexOf(".");
                            // Change here
                            // String substring2 = str.substring(index2+1, str.length());

                            System.out.print(str.substring(index2+1, str.length()) + " ");
                        }
                        System.out.println();

                        for (Tuple t : tuples) {
                            System.out.println(t);
                        }
                    }
                    else {
                        // Never been here

                    }
                } else if (tableList.size() == 6) {
                    ArrayList<String> relationName = new ArrayList<>();
                    for (int i = 0; i < tableList.size(); i++) {
                        ParseHelper parseHelper = new ParseHelper();
                        String removeSpecificCharsString = parseHelper.removeSpecificChars(tableList.get(i).getElement());
                        relationName.add(removeSpecificCharsString);
                    }
                    PhysicalQueryPlanGenerator.MultiRelationCrossJoin1(schemaManager, mainMemory, relationName, new Node());
                } else if (tableList.size() == 3 && list.size() > 2 && list.get(2).getElement().equals("WHERE")) {

                    /*
                    expression = new Expression(list.get(2).getChildren().get(0));
                    ArrayList<String> relationName = new ArrayList<>();
                    for (int i = 0; i < tableList.size(); i++) {
                        ParseHelper parseHelper = new ParseHelper();
                        String removeSpecificCharsString = parseHelper.removeSpecificChars(tableList.get(i).getElement());
                        relationName.add(removeSpecificCharsString);
                    }
                    PhysicalQueryPlanGenerator.tempFunction3(schemaManager, mainMemory, relationName, expression);
                    return;*/

                    // expression = new Expression(list.get(2).getChildren().get(0));
                    PhysicalQueryPlanGenerator physicalQueryPlanGenerator = new PhysicalQueryPlanGenerator();
                    ArrayList<String> relationName = new ArrayList<>();
                    for (int i = 0; i < tableList.size(); i++) {
                        ParseHelper parseHelper = new ParseHelper();
                        String removeSpecificCharsString = parseHelper.removeSpecificChars(tableList.get(i).getElement());
                        relationName.add(removeSpecificCharsString);
                    }
                    Node node = list.get(2).getChildren().get(0);
                    physicalQueryPlanGenerator.MultiRelationCrossJoin2(schemaManager, mainMemory, relationName, node);
                    return;

                } else {
                    System.out.println("Error");
                }
            }
        } else {
            System.out.println("Error");
        }
    }


    private void basicSelectOperation(MainMemory mainMemory, SchemaManager schemaManager, List<Node> tableList, Expression expression, int DistinctMark, ArrayList<String> OrderAttributes) {
        Relation relation = schemaManager.getRelation(tableList.get(0).getElement());
        ArrayList<String> field = relation.getSchema().getFieldNames();
        ArrayList<Tuple> tuples;
        for (String s : relation.getSchema().getFieldNames()) {
            System.out.print(s + "  ");
        }
        System.out.println();

        if(DistinctMark==1) {
            tuples = ImplementationOfPhysicalOperator.DinstinctOperation1(mainMemory, schemaManager, relation, field);
            //return;
            for(Tuple tuple : tuples){
                System.out.println(tuple);
            }
        } else if(OrderAttributes.size()!=0){
            // System.out.println("order execution: ");
            // System.out.println(OrderAttributes.get(0));

            tuples = ImplementationOfPhysicalOperator.executeOrder1(mainMemory, relation, OrderAttributes);
            for(Tuple tuple : tuples){
                System.out.println(tuple);
            }
        } else {
            for (int i = 0;i < relation.getNumOfBlocks(); i++) {
                relation.getBlock(i, 0);
                Block block = mainMemory.getBlock(0);
                for (int j = 0; j < block.getNumTuples(); j++) {
                    Tuple tuple = block.getTuple(j);
                    if (expression == null) {

                        for(int index=0;index<tuple.getNumOfFields();index++) {
                            if (tuple.getField(index).type == FieldType.INT && tuple.getField(index).integer == Integer.MIN_VALUE)
                                System.out.print("NULL" + " ");
                            else
                                System.out.print(tuple.getField(index) + " ");
                        }
                        System.out.println();


                        // System.out.println(tuple);
                    } else {
                        if (expression.evaluateExpression(tuple)) {
                            System.out.println(tuple);
                        }
                    }
                }
            }
        }


    }

    private void basicSelectOperationForSpecificFields(MainMemory mainMemory, SchemaManager schemaManager, List<Node> tableList, List<String> attributeList, int DistinctMark, ArrayList<String> OrderAttributes) {
        Relation relation = schemaManager.getRelation(tableList.get(0).getElement());
        attributeList = attributeListHelper(attributeList);
        ArrayList<String> fields = new ArrayList<String>(attributeList);
        ArrayList<Tuple> tuples;
        for(String s : attributeList) {
            System.out.print(s + "  ");
        }
        System.out.println();

        if(DistinctMark==1){
            tuples =ImplementationOfPhysicalOperator.DinstinctOperation1(mainMemory,schemaManager,relation,fields);
            for (Tuple tuple:tuples) {
                //Tuple tuple = block.getTuple(j);
                for (int k = 0; k < attributeList.size(); k++) {
                    if (tuple.getSchema().getFieldType(attributeList.get(k)) == FieldType.INT)
                        System.out.print(tuple.getField(attributeList.get(k)).integer + "  ");
                    else
                        System.out.print(tuple.getField(attributeList.get(k)).str + "  ");
                }
                System.out.println();
            }
        }else if(OrderAttributes.size()!=0){
            tuples = ImplementationOfPhysicalOperator.executeOrder1(mainMemory, relation, OrderAttributes);
            for (Tuple tuple:tuples) {
                //Tuple tuple = block.getTuple(j);
                for (int k = 0; k < attributeList.size(); k++) {
                    if (tuple.getSchema().getFieldType(attributeList.get(k)) == FieldType.INT)
                        System.out.print(tuple.getField(attributeList.get(k)).integer + "  ");
                    else
                        System.out.print(tuple.getField(attributeList.get(k)).str + "  ");
                }
                System.out.println();
            }
        } else {

            for (int i = 0; i < relation.getNumOfBlocks(); i++) {
                relation.getBlock(i, 0);
                Block block = mainMemory.getBlock(0);
                for (int j = 0; j < block.getNumTuples(); j++) {
                    Tuple tuple = block.getTuple(j);
                    for (int k = 0; k < attributeList.size(); k++) {
                        if (tuple.getSchema().getFieldType(attributeList.get(k)) == FieldType.INT)
                            System.out.print(tuple.getField(attributeList.get(k)).integer + "  ");
                        else
                            System.out.print(tuple.getField(attributeList.get(k)).str + "  ");
                    }
                    System.out.println();
                }
            }
        }
    }

    private List<String> attributeListHelper(List<String> attributeList) {
        List<String> attributeListResult = new ArrayList<>();
        for(String s : attributeList) {
            if (s.indexOf('.') > 0) {
                attributeListResult.add(s.substring(s.indexOf('.') + 1));
            } else {
                attributeListResult.add(s);
            }
        }
        return attributeListResult;
    }

    private String attributeHelper(String attribute) {
        if (attribute.indexOf('.') > 0) {
            return (attribute.substring(attribute.indexOf('.') + 1));
        } else {
            return attribute;
        }
    }

}
