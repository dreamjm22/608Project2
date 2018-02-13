package Optimization;

import parser.Node;
import parser.ParseHelper;
import storageManager.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import operator.Expression;


public class PhysicalQueryPlanGenerator {

    public static void tempFunction(SchemaManager schemaManager, MainMemory mainMemory, ArrayList<String> relationName, Expression expression) {
        boolean check = true;

        Relation relation1 = schemaManager.getRelation(relationName.get(0));
        Relation relation2 = schemaManager.getRelation(relationName.get(1));
        int blocks1 = relation1.getNumOfBlocks();
        int blocks2 = relation2.getNumOfBlocks();

        ArrayList<String> field_names_single = new ArrayList<>();
        ArrayList<FieldType> field_types_single = new ArrayList<>();

        Schema schema = mergeSchemas(schemaManager, relationName.get(0), relationName.get(1));
        if (check) {
            for(String s: schema.getFieldNames())
                System.out.print(s + "  ");
            System.out.println();
            check = false;
        }
        if (schemaManager.relationExists("naturaljoin_relation"))
            schemaManager.deleteRelation("naturaljoin_relation");
        Relation temprelation = schemaManager.createRelation("naturaljoin_relation", schema);

            for(int i=0;i<blocks1;i++){
                relation1.getBlock(i,1);
                Block block=mainMemory.getBlock(1);
                for(int j=0;j<block.getNumTuples();j++){
                    Tuple tuple1=block.getTuple(j);

                    for(int m=0;m<blocks2;m++){
                        relation2.getBlock(m,2);
                        Block block2=mainMemory.getBlock(2);
                        for(int n=0;n<block2.getNumTuples();n++){
                            int offset_num = 0;
                            Tuple tuple2=block2.getTuple(n);
                            Tuple new_tuple = schemaManager.getRelation("naturaljoin_relation").createTuple();

                            field_names_single = schemaManager.getSchema(relationName.get(0)).getFieldNames();
                            field_types_single = schemaManager.getSchema(relationName.get(0)).getFieldTypes();


                            // System.out.println(field_names_single);
                            // System.out.println(field_types_single);
                            for(int p = 0; p < field_types_single.size(); p++){
                                if(field_types_single.get(p).equals(FieldType.INT)){
                                    new_tuple.setField(offset_num++,tuple1.getField(field_names_single.get(p)).integer);
                                }
                                else{
                                    new_tuple.setField(offset_num++,tuple1.getField(field_names_single.get(p)).str);
                                }
                            }

                            field_names_single = schemaManager.getSchema(relationName.get(1)).getFieldNames();
                            field_types_single = schemaManager.getSchema(relationName.get(1)).getFieldTypes();

                            for(int p = 0; p < field_types_single.size(); p++){
                                if(field_types_single.get(p).equals(FieldType.INT)){
                                    new_tuple.setField(offset_num++,tuple2.getField(field_names_single.get(p)).integer);
                                }
                                else{
                                    new_tuple.setField(offset_num++,tuple2.getField(field_names_single.get(p)).str);
                                }
                            }
                            if (expression.evaluateExpression(new_tuple))
                                System.out.println(new_tuple);

                        }
                    }

                }
            }
            schemaManager.deleteRelation("naturaljoin_relation");


    }

    public static Relation tempFunction2(SchemaManager schemaManager, MainMemory mainMemory, ArrayList<String> relationName, Expression expression) {
        boolean check = true;

        Relation relation1 = schemaManager.getRelation(relationName.get(0));
        Relation relation2 = schemaManager.getRelation(relationName.get(1));
        int blocks1 = relation1.getNumOfBlocks();
        int blocks2 = relation2.getNumOfBlocks();

        ArrayList<String> field_names_single = new ArrayList<>();
        ArrayList<FieldType> field_types_single = new ArrayList<>();

        Schema schema = mergeSchemas(schemaManager, relationName.get(0), relationName.get(1));
        if (check) {
            for(String s: schema.getFieldNames())
                System.out.print(s + "  ");
            System.out.println();
            check = false;
        }
        if (schemaManager.relationExists("naturaljoin_relation2"))
            schemaManager.deleteRelation("naturaljoin_relation2");
        Relation temprelation = schemaManager.createRelation("naturaljoin_relation2", schema);

        for(int i=0;i<blocks1;i++){
            relation1.getBlock(i,1);
            Block block=mainMemory.getBlock(1);
            for(int j=0;j<block.getNumTuples();j++){
                Tuple tuple1=block.getTuple(j);

                for(int m=0;m<blocks2;m++){
                    relation2.getBlock(m,2);
                    Block block2=mainMemory.getBlock(2);
                    for(int n=0;n<block2.getNumTuples();n++){
                        int offset_num = 0;
                        Tuple tuple2=block2.getTuple(n);
                        Tuple new_tuple = schemaManager.getRelation("naturaljoin_relation2").createTuple();

                        field_names_single = schemaManager.getSchema(relationName.get(0)).getFieldNames();
                        field_types_single = schemaManager.getSchema(relationName.get(0)).getFieldTypes();


                        // System.out.println(field_names_single);
                        // System.out.println(field_types_single);
                        for(int p = 0; p < field_types_single.size(); p++){
                            if(field_types_single.get(p).equals(FieldType.INT)){
                                new_tuple.setField(offset_num++,tuple1.getField(field_names_single.get(p)).integer);
                            }
                            else{
                                new_tuple.setField(offset_num++,tuple1.getField(field_names_single.get(p)).str);
                            }
                        }

                        field_names_single = schemaManager.getSchema(relationName.get(1)).getFieldNames();
                        field_types_single = schemaManager.getSchema(relationName.get(1)).getFieldTypes();

                        for(int p = 0; p < field_types_single.size(); p++){
                            if(field_types_single.get(p).equals(FieldType.INT)){
                                new_tuple.setField(offset_num++,tuple2.getField(field_names_single.get(p)).integer);
                            }
                            else{
                                new_tuple.setField(offset_num++,tuple2.getField(field_names_single.get(p)).str);
                            }
                        }
                        if (expression.evaluateExpression(new_tuple)) {
                            // System.out.println(new_tuple);
                            appendTupleToRelation(temprelation, mainMemory, 0, new_tuple);
                        }

                    }
                }

            }
        }
        // System.out.println("==========");
        return temprelation;
        // schemaManager.deleteRelation("naturaljoin_relation");
    }

    public static ArrayList<Tuple> CrossJoinOperation1(SchemaManager schemaManager, MainMemory mainMemory, ArrayList<String> relationName, Node node) {
        ArrayList<Tuple> tupleArrayList = new ArrayList<>();

        if (relationName.size() == 2) {
            Relation relation1 =schemaManager.getRelation(relationName.get(0));
            Relation relation2= schemaManager.getRelation(relationName.get(1));

            Relation minRelation =(relation1.getNumOfBlocks() <= relation2.getNumOfBlocks())?relation1:relation2;

            if (minRelation.getNumOfBlocks() < mainMemory.getMemorySize() - 1) {
                // System.out.println("onePassCrossJoin");
                tupleArrayList = onePassCrossJoin(schemaManager, mainMemory, relationName.get(0), relationName.get(1));
            } else {
                // System.out.println("nestedJoinOperation");
                tupleArrayList = nestedJoinOperation(schemaManager, mainMemory, relationName.get(0), relationName.get(1));
            }

        }
        return tupleArrayList;
    }

    public static Relation CrossJoinOperation2(SchemaManager schemaManager, MainMemory mainMemory, ArrayList<String> relationName, Node node) {
        ArrayList<Tuple> tuples = new ArrayList<>();
        Relation relation;
        if (relationName.size() == 2) {

            String relationName1 = relationName.get(0);
            String relationName2 = relationName.get(1);
            Relation relation1 = schemaManager.getRelation(relationName.get(0));
            Relation relation2 = schemaManager.getRelation(relationName.get(1));
            // System.out.println(relation1.getNumOfTuples());
            // System.out.println(relation2.getNumOfTuples());
            /*
            if (node.getElement().equals("TEMP2")) {
                if (schemaManager.relationExists("TEMP1"))
                    schemaManager.deleteRelation("TEMP1");
                Relation temprelation1 = schemaManager.createRelation("TEMP1", relation1.getSchema());
                if (schemaManager.relationExists("TEMP2"))
                    schemaManager.deleteRelation("TEMP2");
                Relation temprelation2 = schemaManager.createRelation("TEMP2", relation2.getSchema());
                Expression expression1 = getExpression1(16);
                for (int i = 0;i < relation1.getNumOfBlocks(); i++) {
                    relation1.getBlock(i, 0);
                    Block tempBlock = mainMemory.getBlock(0);
                    for (int j = 0; j < tempBlock.getNumTuples(); j++) {
                        Tuple tempTuple = tempBlock.getTuple(j);
                        if (!expression1.evaluateExpression(tempTuple)) {
                            System.out.println(tempTuple);
                            appendTupleToRelation(temprelation1, mainMemory, 0, tempTuple);
                        }
                    }
                }
                //System.out.println(temprelation1.getNumOfTuples());

                Expression expression2 = getExpression2(18);
                for (int i = 0;i < relation2.getNumOfBlocks(); i++) {
                    relation2.getBlock(i, 0);
                    Block tempBlock = mainMemory.getBlock(0);
                    for (int j = 0; j < tempBlock.getNumTuples(); j++) {
                        Tuple tempTuple = tempBlock.getTuple(j);
                        if (!expression2.evaluateExpression(tempTuple)) {
                            System.out.println(tempTuple);
                            appendTupleToRelation(temprelation2, mainMemory, 0, tempTuple);
                        }
                    }
                }
                // System.out.println(temprelation2.getNumOfTuples());

                relation1 = temprelation1;
                relation2 = temprelation2;
                relationName.set(0, "TEMP1");
                relationName.set(1, "TEMP2");
            }*/



            Relation minRelation = (relation1.getNumOfBlocks() <= relation2.getNumOfBlocks()) ? relation1 : relation2;

            if (minRelation.getNumOfBlocks() < mainMemory.getMemorySize() - 1) {
                tuples = onePassCrossJoin(schemaManager, mainMemory, relationName.get(0), relationName.get(1));
            } else {
                tuples = nestedJoinOperation(schemaManager, mainMemory, relationName.get(0), relationName.get(1));
            }
            Schema schema = mergeSchemas(schemaManager, relationName.get(0), relationName.get(1));
            if (schemaManager.relationExists(relationName1 + "CrossJoin" + relationName2))
                schemaManager.deleteRelation(relationName1 + "CrossJoin" + relationName2);
            relation = schemaManager.createRelation(relationName1 + "CrossJoin" + relationName2, schema);
            // System.out.println("0000000");
            int count = 0;
            Block block = mainMemory.getBlock(0);
            if (!node.getElement().equals("TEMP") && relation.getRelationName().length() < 14) {
                int num = schema.getTuplesPerBlock();
                while (!tuples.isEmpty()) {
                    block.clear();
                    for (int i = 0; i < num; i++) {
                        if (!tuples.isEmpty()) {
                            Tuple tuple = tuples.get(0);
                            block.setTuple(i, tuple);
                            tuples.remove(tuple);
                        }
                    }
                    relation.setBlock(count++, 0);
                }

                ArrayList<String> fields = relation.getSchema().getFieldNames();
                Node node2 = node.copyNode(node);
                node2 = deadlwithExpression2(node2, relation.getRelationName(), relationName1, relationName2);
                // System.out.println("original: " + relation.getNumOfBlocks());
                relation = filter2(schemaManager, mainMemory, relation, new Expression(node2), fields);
                node = deadlwithExpression1(node, relation.getRelationName(), relationName1, relationName2);
                // System.out.println("current: " + relation.getNumOfBlocks());
            }

            ///
            else if (!node.getElement().equals("TEMP")){
                System.out.println("Fuck");

                for(String str: tuples.get(0).getSchema().getFieldNames()) {
                    int index = str.lastIndexOf(".");
                    String substring = str.substring(0,index);
                    int index2 = substring.lastIndexOf(".");
                    // Change here
                    String substring2 = str.substring(index2+1, str.length());
                    System.out.print(str.substring(index2+1, str.length()) + " ");
                }
                System.out.println();


                Expression expression = new Expression(node);
                for(Tuple tuple: tuples) {
                    if (expression.evaluateExpression(tuple))
                        System.out.println(tuple);
                }

                return relation;
            }
            else {
                // int count = 0;
                //Block block = mainMemory.getBlock(0);
                while (!tuples.isEmpty()) {
                    block.clear();
                    for (int i = 0; i < schema.getTuplesPerBlock(); i++) {
                        if (!tuples.isEmpty()) {
                            Tuple tuple = tuples.get(0);
                            block.setTuple(i, tuple);
                            tuples.remove(tuple);
                        }
                    }
                    relation.setBlock(count++, 0);
                }

                // System.out.println(relation.getSchema());
                return relation;
            }




            ///
            // System.out.println(relation.getSchema());
            return relation;
        }
        return null;

    }

    public static Relation CrossJoinOperation3(SchemaManager schemaManager, MainMemory mainMemory, ArrayList<String> relationName, Node node) {
        ArrayList<Tuple> tuples = new ArrayList<>();
        Relation relation;
        if (relationName.size() == 2) {

            String relationName1 = relationName.get(0);
            String relationName2 = relationName.get(1);
            Relation relation1 = schemaManager.getRelation(relationName.get(0));
            Relation relation2 = schemaManager.getRelation(relationName.get(1));
            // System.out.println(relation1.getNumOfTuples());
            // System.out.println(relation2.getNumOfTuples());
            /*
            if (node.getElement().equals("TEMP2")) {
                if (schemaManager.relationExists("TEMP1"))
                    schemaManager.deleteRelation("TEMP1");
                Relation temprelation1 = schemaManager.createRelation("TEMP1", relation1.getSchema());
                if (schemaManager.relationExists("TEMP2"))
                    schemaManager.deleteRelation("TEMP2");
                Relation temprelation2 = schemaManager.createRelation("TEMP2", relation2.getSchema());
                Expression expression1 = getExpression1(16);
                for (int i = 0;i < relation1.getNumOfBlocks(); i++) {
                    relation1.getBlock(i, 0);
                    Block tempBlock = mainMemory.getBlock(0);
                    for (int j = 0; j < tempBlock.getNumTuples(); j++) {
                        Tuple tempTuple = tempBlock.getTuple(j);
                        if (!expression1.evaluateExpression(tempTuple)) {
                            System.out.println(tempTuple);
                            appendTupleToRelation(temprelation1, mainMemory, 0, tempTuple);
                        }
                    }
                }
                //System.out.println(temprelation1.getNumOfTuples());

                Expression expression2 = getExpression2(18);
                for (int i = 0;i < relation2.getNumOfBlocks(); i++) {
                    relation2.getBlock(i, 0);
                    Block tempBlock = mainMemory.getBlock(0);
                    for (int j = 0; j < tempBlock.getNumTuples(); j++) {
                        Tuple tempTuple = tempBlock.getTuple(j);
                        if (!expression2.evaluateExpression(tempTuple)) {
                            System.out.println(tempTuple);
                            appendTupleToRelation(temprelation2, mainMemory, 0, tempTuple);
                        }
                    }
                }
                // System.out.println(temprelation2.getNumOfTuples());

                relation1 = temprelation1;
                relation2 = temprelation2;
                relationName.set(0, "TEMP1");
                relationName.set(1, "TEMP2");
            }*/



            Relation minRelation = (relation1.getNumOfBlocks() <= relation2.getNumOfBlocks()) ? relation1 : relation2;

            if (minRelation.getNumOfBlocks() < mainMemory.getMemorySize() - 1) {
                tuples = onePassCrossJoin(schemaManager, mainMemory, relationName.get(0), relationName.get(1));
            } else {
                tuples = nestedJoinOperation(schemaManager, mainMemory, relationName.get(0), relationName.get(1));
            }
            Schema schema = mergeSchemas(schemaManager, relationName.get(0), relationName.get(1));
            if (schemaManager.relationExists(relationName1 + "CrossJoin" + relationName2))
                schemaManager.deleteRelation(relationName1 + "CrossJoin" + relationName2);
            relation = schemaManager.createRelation(relationName1 + "CrossJoin" + relationName2, schema);
            // System.out.println("0000000");
            int count = 0;
            Block block = mainMemory.getBlock(0);
            if (!node.getElement().equals("TEMP") && relation.getRelationName().length() < 14) {
                System.out.println("Fuck1");
                int num = schema.getTuplesPerBlock();
                while (!tuples.isEmpty()) {
                    block.clear();
                    for (int i = 0; i < num; i++) {
                        if (!tuples.isEmpty()) {
                            Tuple tuple = tuples.get(0);
                            block.setTuple(i, tuple);
                            tuples.remove(tuple);
                        }
                    }
                    relation.setBlock(count++, 0);
                }

                ArrayList<String> fields = relation.getSchema().getFieldNames();
                Node node2 = node.copyNode(node);
                node2 = deadlwithExpression2(node2, relation.getRelationName(), relationName1, relationName2);
                // System.out.println("original: " + relation.getNumOfBlocks());
                relation = filter2(schemaManager, mainMemory, relation, new Expression(node2), fields);
                node = deadlwithExpression1(node, relation.getRelationName(), relationName1, relationName2);
                // System.out.println("current: " + relation.getNumOfBlocks());
            }

            ///
            else if (!node.getElement().equals("TEMP")){
                System.out.println("Fuck");

                for(String str: tuples.get(0).getSchema().getFieldNames()) {
                    int index = str.lastIndexOf(".");
                    String substring = str.substring(0,index);
                    int index2 = substring.lastIndexOf(".");
                    // Change here
                    String substring2 = str.substring(index2+1, str.length());
                    System.out.print(str.substring(index2+1, str.length()) + " ");
                }
                System.out.println();


                Expression expression = new Expression(node);
                for(Tuple tuple: tuples) {
                    if (expression.evaluateExpression(tuple))
                        System.out.println(tuple);
                }

                return relation;
            }
            else {
                // int count = 0;
                //Block block = mainMemory.getBlock(0);
                while (!tuples.isEmpty()) {
                    block.clear();
                    for (int i = 0; i < schema.getTuplesPerBlock(); i++) {
                        if (!tuples.isEmpty()) {
                            Tuple tuple = tuples.get(0);
                            block.setTuple(i, tuple);
                            tuples.remove(tuple);
                        }
                    }
                    relation.setBlock(count++, 0);
                }

                // System.out.println(relation.getSchema());
                return relation;
            }




            ///
            // System.out.println(relation.getSchema());
            return relation;
        }
        return null;

    }

    private static Node deadlwithExpression1(Node node, String relation, String r1, String r2) {
        // System.out.println("deadlwithExpression1");
        for(int i=0;i<node.getChildren().size();i++) {
            if (!node.getChildren().get(i).getElement().equals("=") && !node.getChildren().get(i).getElement().equals("AND")) {
                // System.out.println(node.getChildren().get(i).getElement());
                if (node.getChildren().get(i).getElement().startsWith(r1) || node.getChildren().get(i).getElement().startsWith(r2))
                    node.getChildren().get(i).setElement(relation+ "."+node.getChildren().get(i).getElement());
            } else {
                deadlwithExpression1(node.getChildren().get(i), relation, r1, r2);
            }
        }
        return node;
    }

    private static Node deadlwithExpression2(Node node, String relation, String r1, String r2) {
        // System.out.println("deadlwithExpression2");
        Set<String> set = new HashSet<>();
        set.add(r1);
        set.add(r2);
        for(int i=0;i<node.getChildren().size();i++) {
            // System.out.println(node.getChildren().get(i).getElement());
            if (node.getChildren().get(i).getElement().equals("=") ) {
                if (set.contains(node.getChildren().get(i).getChildren().get(0).getElement().substring(0,1)) && set.contains(node.getChildren().get(i).getChildren().get(1).getElement().substring(0,1))) {
                    // System.out.println("Surprise");
                } else{
                    // System.out.println(node.getChildren().get(i).getChildren().size());
                    // System.out.println(node.getChildren().get(i).getChildren().get(0).getElement());
                    node.getChildren().get(i).getChildren().get(0).setElement("1");
                    node.getChildren().get(i).getChildren().get(1).setElement("1");
                }
            } else if (node.getChildren().get(i).getElement().equals("AND")) {
                deadlwithExpression2(node.getChildren().get(i), relation, r1, r2);
            }
        }
        return node;
    }

    private static void printNode(Node node) {
        System.out.println("Printing node");
        System.out.println(node.getElement());
        System.out.println(node.getChildren().get(0).getElement());
        System.out.println(node.getChildren().get(1).getElement());
        System.out.println(node.getChildren().get(0).getChildren().get(0).getElement());
        System.out.println(node.getChildren().get(0).getChildren().get(1).getElement());
        System.out.println(node.getChildren().get(1).getChildren().get(0).getElement());
        System.out.println(node.getChildren().get(1).getChildren().get(1).getElement());
        System.out.println(node.getChildren().get(1).getChildren().get(0).getChildren().get(0).getElement());
        System.out.println(node.getChildren().get(1).getChildren().get(0).getChildren().get(1).getElement());
        System.out.println(node.getChildren().get(1).getChildren().get(1).getChildren().get(0).getElement());
        System.out.println(node.getChildren().get(1).getChildren().get(1).getChildren().get(1).getElement());
        System.out.println("DONE");
    }

    public static ArrayList<Tuple> onePassCrossJoin(SchemaManager schemaManager, MainMemory mainMemory, String relationName1, String relationName2) {
        ArrayList<Tuple> tupleArrayList = new ArrayList<Tuple>();

        Relation r1=schemaManager.getRelation(relationName1);
        Relation r2=schemaManager.getRelation(relationName2);

        Relation minRelation=(r1.getNumOfBlocks()<=r2.getNumOfBlocks())?r1:r2;
        Relation maxRelation=(minRelation==r1)?r2:r1;
        minRelation.getBlocks(0, 0, minRelation.getNumOfBlocks());

        Schema schema=mergeSchemas(schemaManager, relationName1, relationName2);

        if (schemaManager.relationExists(relationName1 + "cross" + relationName2 + "tmp"))
            schemaManager.deleteRelation(relationName1 + "cross" + relationName2 + "tmp");
        Relation relation = schemaManager.createRelation(relationName1 + "cross" + relationName2 + "tmp", schema);
        // System.out.println("HEHEHE");
        for (int j = 0; j < maxRelation.getNumOfBlocks(); j++) {
            maxRelation.getBlock(j, mainMemory.getMemorySize() - 1);
            Block largeBlock = mainMemory.getBlock(mainMemory.getMemorySize() - 1);
            for (Tuple tuple1 : mainMemory.getTuples(0, minRelation.getNumOfBlocks())) {
                for (Tuple tuple2 : largeBlock.getTuples()) {
                    if (minRelation == r1) {
                        tupleArrayList.add(mergeTuples(schemaManager, relation, tuple1, tuple2));
                    } else {
                        // System.out.println("afadfadfasfaf");
                        tupleArrayList.add(mergeTuples(schemaManager, relation, tuple2, tuple1));
                    }
                }
            }
        }
        return tupleArrayList;
    }

    public static Schema mergeSchemas(SchemaManager schemaManager, String relationName1, String relationName2) {

        ArrayList<String> field_names = new ArrayList<>();
        ArrayList<FieldType> field_types = new ArrayList<>();
        ArrayList<String> relation_name = new ArrayList<>();
        relation_name.add(relationName1);
        relation_name.add(relationName2);
        for(int k = 0; k < relation_name.size(); k++){
            for(int i = 0; i < schemaManager.getSchema(relation_name.get(k)).getFieldNames().size(); i++){
                field_names.add(relation_name.get(k) + "." + schemaManager.getSchema(relation_name.get(k)).getFieldNames().get(i));
                field_types.add(schemaManager.getSchema(relation_name.get(k)).getFieldTypes().get(i));
            }

        }
        Schema schema = new Schema(field_names,field_types);
        return schema;
    }

    public static Schema mergeSchemas2(SchemaManager schemaManager, String relationName1, String relationName2, String relationName3) {
        ArrayList<String> field_names = new ArrayList<>();
        ArrayList<FieldType> field_types = new ArrayList<>();
        ArrayList<String> relation_name = new ArrayList<>();
        relation_name.add(relationName1);
        relation_name.add(relationName2);
        relation_name.add(relationName3);
        for(int k = 0; k < relation_name.size(); k++){
            for(int i = 0; i < schemaManager.getSchema(relation_name.get(k)).getFieldNames().size(); i++){
                field_names.add(relation_name.get(k) + "." + schemaManager.getSchema(relation_name.get(k)).getFieldNames().get(i));
                field_types.add(schemaManager.getSchema(relation_name.get(k)).getFieldTypes().get(i));
            }

        }
        Schema schema = new Schema(field_names,field_types);
        return schema;
    }

    public static Tuple mergeTuples(SchemaManager schemaManager, Relation relation, Tuple t1, Tuple t2) {
        Tuple t = relation.createTuple();
        // System.out.println("czbcbnn");
        int size1 = t1.getNumOfFields();
        int size2 = t2.getNumOfFields();

        for (int i=0; i < size1+size2; i++) {
            if (i<size1) {
                String toSet = t1.getField(i).toString();
                //System.out.println(toSet);
                if (isNumeric(toSet))
                    t.setField(i, Integer.parseInt(toSet));
                else
                    t.setField(i, toSet);
            } else {
                String toSet = t2.getField(i - size1).toString();
                if (isNumeric(toSet))
                    t.setField(i, Integer.parseInt(toSet));
                else
                    t.setField(i, toSet);
            }
        }
        return t;
    }

    public static Boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public static ArrayList<Tuple> nestedJoinOperation(SchemaManager schemaManager, MainMemory mainMemory, String relationName1, String relationName2) {
        ArrayList<Tuple> tuples = new ArrayList<Tuple>();
        Relation relation1 = schemaManager.getRelation(relationName1);
        Relation relation2 = schemaManager.getRelation(relationName2);

        Schema schema = mergeSchemas(schemaManager, relationName1, relationName2);

        if (schemaManager.relationExists(relationName1 + "cross" + relationName2 + "tmp"))
            schemaManager.deleteRelation(relationName1 + "cross" + relationName2 + "tmp");
        Relation relation = schemaManager.createRelation(relationName1 + "cross" + relationName2 + "tmp", schema);

        for (int i = 0; i < relation1.getNumOfBlocks(); i++) {
            relation1.getBlock(i, 0);
            Block block1 = mainMemory.getBlock(0);
            for (int j = 0; j < relation2.getNumOfBlocks(); j++) {
                relation2.getBlock(j, 1);
                Block block2 = mainMemory.getBlock(1);
                for (Tuple tuple1 : block1.getTuples()) {
                    for (Tuple tuple2 : block2.getTuples()) {
                        tuples.add(mergeTuples(schemaManager, relation, tuple1, tuple2));
                    }
                }
            }
        }
        return tuples;
    }

    public static Relation NaturalJoinOperation2(SchemaManager schemaManager, MainMemory mainMemory, String relationName1, String relationName2, String field){
        ArrayList<Tuple> tuples;
        Relation relation;

        Relation r1 = schemaManager.getRelation(relationName1);
        Relation r2 = schemaManager.getRelation(relationName2);
        Relation minRelation = (r1.getNumOfBlocks()<=r2.getNumOfBlocks())?r1:r2;

        if(minRelation.getNumOfBlocks() < mainMemory.getMemorySize()-1) {
            tuples = onePassNaturalJoin(schemaManager,mainMemory,relationName1,relationName2,field);
        }else{
            tuples = twoPassNaturalJoin(schemaManager,mainMemory,relationName1,relationName2,field);
        }

        Schema schema = mergeSchemas(schemaManager,relationName1,relationName2);
        if(schemaManager.relationExists(relationName1+"natural"+relationName2)){
            schemaManager.deleteRelation(relationName1+"natural"+relationName2);
        }
        relation = schemaManager.createRelation(relationName1+"natural"+relationName2,schema);

        int count = 0;
        Block block = mainMemory.getBlock(0);

        // System.out.println("ppppppp");
        while(!tuples.isEmpty()){
            block.clear();
            for(int i=0;i<schema.getTuplesPerBlock();i++){
                if(!tuples.isEmpty()){
                    Tuple t = tuples.get(0);
                    block.setTuple(i,t);
                    tuples.remove(t);
                }
            }
            relation.setBlock(count++,0);
        }
        return relation;
    }

    public static ArrayList<Tuple> onePassNaturalJoin(SchemaManager schemaManager, MainMemory mainMemory, String relationName1, String relationName2, String field){
        ArrayList<Tuple> tuples = new ArrayList<Tuple>();

        Relation relation1 = schemaManager.getRelation(relationName1);
        Relation relation2 = schemaManager.getRelation(relationName2);
        Relation minimumRelation=(relation1.getNumOfBlocks()<=relation2.getNumOfBlocks())?relation1:relation2;
        Relation maximumRelation=(minimumRelation==relation1)?relation2:relation1;
        minimumRelation.getBlocks(0,0,minimumRelation.getNumOfBlocks());

        Schema schema=mergeSchemas(schemaManager,relationName1,relationName2);

        if(schemaManager.relationExists(relationName1+"natural"+relationName2+"tmp"))
            schemaManager.deleteRelation(relationName1+"natural"+relationName2+"tmp");

        Relation relation = schemaManager.createRelation(relationName1+"natural"+relationName2+"tmp",schema);

        for(int i=0;i<maximumRelation.getNumOfBlocks();i++){
            maximumRelation.getBlock(i,mainMemory.getMemorySize()-1);
            Block block2 = mainMemory.getBlock(mainMemory.getMemorySize()-1);
            for(Tuple t2:block2.getTuples()){
                for(int j=0;j< minimumRelation.getNumOfBlocks();j++){
                    Block block1 = mainMemory.getBlock(j);
                    for(Tuple t1:block1.getTuples()){
                        String field1 = t1.getField(field).toString();
                        String field2 = t2.getField(field).toString();
                        //System.out.println(s1 + " + " + s2);
                        if(isNumeric(field1)&& isNumeric(field2)){
                            if(Integer.parseInt(field1)==Integer.parseInt(field2)){
                                if(minimumRelation==relation1) tuples.add(mergeTuples(schemaManager,relation,t1,t2));
                                else tuples.add(mergeTuples(schemaManager,relation,t2,t1));
                            }
                        }else{
                            if(field1.equals(field2)){
                                if(Integer.parseInt(field1)==Integer.parseInt(field2)){
                                    if(minimumRelation==relation1) tuples.add(mergeTuples(schemaManager,relation,t1,t2));
                                    else tuples.add(mergeTuples(schemaManager,relation,t2,t1));
                                }
                            }
                        }
                    }
                }
            }
        }
        return tuples;
    }

    public static ArrayList<Tuple> twoPassNaturalJoin(SchemaManager schemaManager, MainMemory mainMemory, String relationName1, String relationName2, final String field){
        int temp=0,printed=0,index=0;
        ArrayList<Integer> segments1=new ArrayList<Integer>();
        ArrayList<Integer> segments2=new ArrayList<Integer>();

        ArrayList<Tuple> tuples = new ArrayList<Tuple>();
        ArrayList<String> Fields = new ArrayList<String>();
        Relation relation1 = schemaManager.getRelation(relationName1);
        Relation relation2 = schemaManager.getRelation(relationName2);

        Schema schema = mergeSchemas(schemaManager,relationName1,relationName2);
        if(schemaManager.relationExists(relationName1+"natural"+relationName2+"tmp"))
            schemaManager.deleteRelation(relationName1+"natural"+relationName2+"tmp");

        Relation relation = schemaManager.createRelation(relationName1+"natural"+relationName2+"tmp",schema);

        String[] relation1Fields = relation1.getSchema().fieldNamesToString().split("\t");
        for (String tmp_field : relation1Fields) {
            if (tmp_field.charAt(tmp_field.length()-1) == field.charAt(field.length()-1)) {
                Fields.add(tmp_field);
            }
        }

        int lastSegment1 = twoPassBase(relation1,mainMemory,Fields);
        final String relation1Field = Fields.get(0);
        Fields.clear();

        String[] relation2Fields = relation2.getSchema().fieldNamesToString().split("\t");
        for (String tmp_field : relation2Fields) {
            if (tmp_field.charAt(tmp_field.length()-1) == field.charAt(field.length()-1)) {
                Fields.add(tmp_field);
            }
        }
        //System.out.println(Fields.get(0));
        //System.out.println(Fields.get(1));
        int lastSegment2 = twoPassBase(relation2,mainMemory,Fields);
        final String relation2Field = Fields.get(0);
        Fields.clear();

        while(temp<relation1.getNumOfBlocks()){
            segments1.add(temp);
            temp+=mainMemory.getMemorySize();
        }

        temp = 0;
        while(temp<relation2.getNumOfBlocks()){
            segments2.add(temp);
            temp+=mainMemory.getMemorySize();
        }
        Block block = null;
        for(int i=0;i<mainMemory.getMemorySize();i++){
            block = mainMemory.getBlock(i);
            block.clear();
        }
        int[] reads1 = new int[segments1.size()];
        int[] reads2 = new int[segments2.size()];

        Arrays.fill(reads1,1);
        Arrays.fill(reads2,1);

        ArrayList<ArrayList<Tuple>> tuples1 = new ArrayList<ArrayList<Tuple>>();
        ArrayList<ArrayList<Tuple>> tuples2 = new ArrayList<ArrayList<Tuple>>();

        for(int i=0;i<segments1.size();i++){
            relation1.getBlock(segments1.get(i),i);
            block = mainMemory.getBlock(i);
            tuples1.add(block.getTuples());
        }

        for(int i=0;i<segments2.size();i++){
            relation2.getBlock(segments2.get(i),i+segments1.size());
            block = mainMemory.getBlock(i+segments1.size());
            tuples2.add(block.getTuples());
        }

        Tuple[] minTuple1 = new Tuple[segments1.size()];
        Tuple[] minTuple2 = new Tuple[segments2.size()];

        while(!isEmpty(tuples1)&&!isEmpty(tuples2)) {
            for (int j = 0; j < segments1.size(); j++) {
                if (tuples1.get(j).isEmpty()) {
                    if (j < segments1.size() - 1 && reads1[j] < mainMemory.getMemorySize()) {
                        relation1.getBlock(segments1.get(j) + reads1[j], j);
                        block = mainMemory.getBlock(j);
                        tuples1.get(j).addAll(block.getTuples());
                        reads1[j]++;
                    } else if (j == segments1.size() - 1 && reads1[j] < lastSegment1) {
                        relation1.getBlock(segments1.get(j) + reads1[j], j);
                        block = mainMemory.getBlock(j);
                        tuples1.get(j).addAll(block.getTuples());
                        reads1[j]++;
                    }
                }
            }

            for (int j = 0; j < segments2.size(); j++) {
                if (tuples2.get(j).isEmpty()) {
                    if (j < segments2.size() - 1 && reads2[j] < mainMemory.getMemorySize()) {
                        relation2.getBlock(segments2.get(j) + reads2[j], j+segments1.size());
                        block = mainMemory.getBlock(j+segments1.size());
                        tuples2.get(j).addAll(block.getTuples());
                        reads2[j]++;
                    } else if (j == segments2.size() - 1 && reads2[j] < lastSegment2) {
                        relation2.getBlock(segments2.get(j) + reads2[j], j+segments1.size());
                        block = mainMemory.getBlock(j+segments1.size());
                        tuples2.get(j).addAll(block.getTuples());
                        reads2[j]++;
                    }
                }
            }

            for (int k = 0; k < segments1.size(); k++) {
                if (!tuples1.get(k).isEmpty()) {
                    minTuple1[k] = Collections.min(tuples1.get(k), new Comparator<Tuple>() {
                        public int compare(Tuple o1, Tuple o2) {
                            if (o1 == null) return 1;
                            if (o2 == null) return -1;
                            String field1 = o1.getField(relation1Field).toString();
                            String field2 = o2.getField(relation1Field).toString();
                            if (isNumeric(field1) && isNumeric(field2)) {
                                return (Integer.parseInt(field1) - Integer.parseInt(field2));
                            } else {
                                return field1.compareTo(field2);
                            }
                        }
                    });
                } else {
                    minTuple1[k] = null;
                }
            }
            for (int k = 0; k < segments2.size(); k++) {
                if (!tuples2.get(k).isEmpty()) {
                    minTuple2[k] = Collections.min(tuples2.get(k), new Comparator<Tuple>() {
                        public int compare(Tuple o1, Tuple o2) {
                            if (o1 == null) return 1;
                            if (o2 == null) return -1;
                            String field1 = o1.getField(relation2Field).toString();
                            String field2 = o2.getField(relation2Field).toString();
                            if (isNumeric(field1) && isNumeric(field2)) {
                                return (Integer.parseInt(field1) - Integer.parseInt(field2));
                            } else {
                                return field1.compareTo(field2);
                            }
                        }
                    });
                } else {
                    minTuple2[k] = null;
                }
            }

            ArrayList<Tuple> temptuples1 = new ArrayList<>(Arrays.asList(minTuple1));
            ArrayList<Tuple> temptuples2 = new ArrayList<>(Arrays.asList(minTuple2));

            Tuple minVal1 = Collections.min(temptuples1,new Comparator<Tuple>(){
                public int compare(Tuple o1, Tuple o2){
                    if(o1==null) return 1;
                    if(o2==null) return -1;
                    String field1 = o1.getField(relation1Field).toString();
                    String field2 = o2.getField(relation1Field).toString();
                    if (isNumeric(field1) && isNumeric(field2)) {
                        return (Integer.parseInt(field1) - Integer.parseInt(field2));
                    } else {
                        return field1.compareTo(field2);
                    }
                }
            });

            Tuple minVal2 = Collections.min(temptuples2,new Comparator<Tuple>(){
                public int compare(Tuple o1, Tuple o2){
                    if(o1==null) return 1;
                    if(o2==null) return -1;
                    String field1 = o1.getField(relation2Field).toString();
                    String field2 = o2.getField(relation2Field).toString();
                    if (isNumeric(field1) && isNumeric(field2)) {
                        return (Integer.parseInt(field1) - Integer.parseInt(field2));
                    } else {
                        return field1.compareTo(field2);
                    }
                }
            });

            String minField1=null,minField2=null;
            if(minVal1!=null){
                minField1 = minVal1.getField(relation1Field).toString();
            }

            if(minVal2!=null){
                minField2 = minVal2.getField(relation2Field).toString();
            }

            if(minField1!=null&&minField2!=null&&minField1.equals(minField2)){
                int count1 = getMinCount(tuples1,relation1Field,minField1);
                int count2 = getMinCount(tuples2,relation2Field,minField2);

                ArrayList<Tuple> minTuples1 = getMinTuples(tuples1,relation1Field,minField1);
                ArrayList<Tuple> minTuples2 = getMinTuples(tuples2,relation2Field,minField2);

                for(int i=0;i<count1;i++){
                    for(int j=0;j<count2;j++)
                        tuples.add(mergeTuples(schemaManager,relation,minTuples1.get(i),minTuples2.get(j)));
                }

                Boolean mark1 = false, mark2 = false;
                for(int i=0;i<segments1.size();i++){
                    Block tmpblock = mainMemory.getBlock(i);
                    if(getBlockMinCount(tuples1.get(i),relation1Field,minField1)==tmpblock.getNumTuples()){
                        mark1 = true;
                        break;
                    }
                }

                for(int i=0;i<segments2.size();i++){
                    Block tmpblock = mainMemory.getBlock(i+segments1.size());
                    if(getBlockMinCount(tuples2.get(i),relation2Field,minField1)==tmpblock.getNumTuples()){
                        mark2 = true;
                        break;
                    }
                }
                if(mark1 && !mark2)
                    tuples1= deleteMinField(tuples1,relation1Field,minField1);

                if(!mark1 && mark2)
                    tuples2= deleteMinField(tuples2,relation2Field,minField2);

                if((mark1 && mark2) || (!mark1 && !mark2)){
                    tuples1= deleteMinField(tuples1,relation1Field,minField1);
                    tuples2= deleteMinField(tuples2,relation2Field,minField2);
                }

            }else if(minField1!=null&&minField2!=null){
                if(isNumeric(minField1)&& isNumeric(minField2)){
                    if((Integer.parseInt(minField1)-Integer.parseInt(minField2))<0)
                        tuples1= deleteMinField(tuples1,relation1Field,minField1);
                    else
                        tuples2= deleteMinField(tuples2,relation2Field,minField2);
                }else{
                    if(minField1.compareTo(minField2)<0)
                        tuples1= deleteMinField(tuples1,relation1Field,minField1);
                    else
                        tuples2= deleteMinField(tuples2,relation2Field,minField2);
                }
            }
        }
        return tuples;
    }

    public static ArrayList<Tuple> getMinTuples(ArrayList<ArrayList<Tuple>> tuples,String field, String minVal){
        ArrayList<Tuple> result = new ArrayList<Tuple>();
        for(ArrayList<Tuple> iter:tuples){
            for(Tuple t:iter){
                if(t.getField(field).toString().equals(minVal)){
                    result.add(t);
                }
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<Tuple>> deleteMinField(ArrayList<ArrayList<Tuple>> tuples, String field, String val){
        for(int i=0;i<tuples.size();i++){
            for(int j=0;j<tuples.get(i).size();j++){
                Tuple t = tuples.get(i).get(j);
                if(t.getField(field).toString().equals(val)){
                    tuples.get(i).remove(t);
                }
            }
        }
        return tuples;
    }

    public static int getBlockMinCount(ArrayList<Tuple> tuples,String field, String minVal){
        int result=0;
        for(Tuple t:tuples){
            if(t.getField(field).toString().equals(minVal)){
                result++;
            }
        }
        return result;
    }

    public static int getMinCount(ArrayList<ArrayList<Tuple>> tuples,String field, String minVal){
        int result=0;
        for(ArrayList<Tuple> tuple:tuples){
            for(Tuple t:tuple){
                if(t.getField(field).toString().equals(minVal)){
                    result++;
                }
            }
        }
        return result;
    }

    public static Boolean isEmpty(ArrayList<ArrayList<Tuple>> tuples){
        for(ArrayList<Tuple> t:tuples){
            if(t.size()!=0) return false;
        }
        return true;
    }

    public static int twoPassBase(Relation relation, MainMemory mainMemory, final ArrayList<String> Fields){
        int readIn=0, sortedBlocks = 0;
        while(sortedBlocks<relation.getNumOfBlocks()){
            readIn = ((relation.getNumOfBlocks()-sortedBlocks)>mainMemory.getMemorySize())?mainMemory.getMemorySize():(relation.getNumOfBlocks()-sortedBlocks);
            relation.getBlocks(sortedBlocks,0,readIn);
            ArrayList<Tuple> tuples = mainMemory.getTuples(0,readIn);
            Collections.sort(tuples,new Comparator<Tuple>(){
                public int compare(Tuple o1, Tuple o2){
                    int[] result = new int[Fields.size()];
                    for(int i=0;i<Fields.size();i++){
                        String field1 = o1.getField(Fields.get(i)).toString();
                        String field2 = o2.getField(Fields.get(i)).toString();
                        //System.out.println(field1 + " + " + field2);
                        if(isNumeric(field1) && isNumeric(field2)){
                            result[i] = Integer.parseInt(field1)-Integer.parseInt(field2);
                        }
                        else
                            result[i] = field1.compareTo(field2);
                    }
                    for(int i=0;i<Fields.size();i++){
                        if(result[i]>0) return 1;
                        else if(result[i]<0) return -1;
                    }
                    return 0;
                }
            });
            mainMemory.setTuples(0,tuples);
            relation.setBlocks(sortedBlocks,0,readIn);
            sortedBlocks+=readIn;
        }
        return readIn;
    }

    public static List<Tuple> filter1(SchemaManager schemaManager, MainMemory mainMemory, Relation inputRelation, Expression expression, ArrayList<String> fields){
        List<Tuple> filtered = new ArrayList<>();
        for(int i=0;i<inputRelation.getNumOfBlocks();i++){
            inputRelation.getBlock(i,0);
            Block block = mainMemory.getBlock(0);
            for(Tuple t:block.getTuples()){
                if(expression.evaluateExpression(t)){
                    filtered.add(t);
                }
            }
        }

        ArrayList<FieldType> fieldTypes = new ArrayList<>();

        if(fields.size()==1 && fields.get(0).equals("*")){
            fields.remove(0);
            for(int i=0;i<inputRelation.getSchema().getNumOfFields();i++)
                fields.add(inputRelation.getSchema().getFieldName(i));
        }

        for(String field:fields){
            fieldTypes.add(inputRelation.getSchema().getFieldType(field));
        }

        Schema newSchema = new Schema(fields,fieldTypes);
        List<Tuple> tuples = new ArrayList<>();
        if(schemaManager.relationExists(inputRelation.getRelationName()+"filtered"))
            schemaManager.deleteRelation(inputRelation.getRelationName()+"filtered");

        Relation relation = schemaManager.createRelation(inputRelation.getRelationName()+"filtered",newSchema);

        for(Tuple t:filtered){
            Tuple tuple = relation.createTuple();
            for(String s:newSchema.getFieldNames()){
                if(t.getField(s).type==FieldType.INT)
                    tuple.setField(s,Integer.parseInt(t.getField(s).toString()));
                else
                    tuple.setField(s,t.getField(s).toString());
            }
            tuples.add(tuple);
        }
        return tuples;
    }

    public static Relation filter2(SchemaManager schemaManager, MainMemory mainMemory, Relation inputRelation, Expression expression, ArrayList<String> fields){
        List<Tuple> filtered = new ArrayList<>();
        for(int i=0;i<inputRelation.getNumOfBlocks();i++){
            inputRelation.getBlock(i,0);
            Block block = mainMemory.getBlock(0);
            for(Tuple t:block.getTuples()){
                if(expression.evaluateExpression(t)){
                    filtered.add(t);
                }
            }
        }

        ArrayList<FieldType> fieldTypes = new ArrayList<>();

        if(fields.size()==1 && fields.get(0).equals("*")){
            fields.remove(0);
            for(int i=0;i<inputRelation.getSchema().getNumOfFields();i++)
                fields.add(inputRelation.getSchema().getFieldName(i));
        }

        for(String s:fields){
            fieldTypes.add(inputRelation.getSchema().getFieldType(s));
        }

        Schema newSchema = new Schema(fields,fieldTypes);
        List<Tuple> tuples = new ArrayList<>();
        if(schemaManager.relationExists(inputRelation.getRelationName()+"filtered"))
            schemaManager.deleteRelation(inputRelation.getRelationName()+"filtered");

        Relation relation = schemaManager.createRelation(inputRelation.getRelationName()+"filtered",newSchema);

        for(Tuple t:filtered){
            Tuple tuple = relation.createTuple();
            for(String s:newSchema.getFieldNames()){
                if(t.getField(s).type==FieldType.INT)
                    tuple.setField(s,Integer.parseInt(t.getField(s).toString()));
                else
                    tuple.setField(s,t.getField(s).toString());
            }
            tuples.add(tuple);
        }

        int count = 0;
        Block block = mainMemory.getBlock(0);
        while(!tuples.isEmpty()){
            block.clear();
            for(int i=0;i<newSchema.getTuplesPerBlock();i++){
                if(!tuples.isEmpty()){
                    Tuple tuple = tuples.get(0);
                    block.setTuple(i,tuple);
                    tuples.remove(tuple);
                }
            }
            relation.setBlock(count++,0);
        }
        return relation;
    }

    public static void MultiRelationCrossJoin1(SchemaManager schemaManager, MainMemory mainMemory, ArrayList<String> relationName, Node node) {
        int memorySize = mainMemory.getMemorySize();
        if (relationName.size() == 2) {
        } else {
            HashMap<Set<String> , Relations> singleRelation = new HashMap<>();
            for (String name: relationName) {
                HashSet<String> set = new HashSet<>();
                set.add(name);
                Relation relation = schemaManager.getRelation(name);
                Relations temp = new Relations(set, relation.getNumOfBlocks(), relation.getNumOfTuples());
                temp.cost = relation.getNumOfBlocks();
                temp.fieldNum = relation.getSchema().getNumOfFields();
                singleRelation.put(set, temp);
            }
            List<HashMap<Set<String> , Relations>> costRelationList = new ArrayList<>();
            costRelationList.add(singleRelation);
            for (int i = 1; i < relationName.size(); i++) {
                costRelationList.add(new HashMap<Set<String> , Relations>());
            }

            Set<String> finalGoal = new HashSet<>(relationName);
            Relations relations = LogicalQueryPlanGenerator.Optimizition(costRelationList, finalGoal, memorySize);
            LogicalQueryPlanGenerator.travesal(relations, 0);
            helper1(relations, mainMemory, schemaManager, node);
        }
    }

    public static Relation MultiRelationCrossJoin2(SchemaManager schemaManager, MainMemory mainMemory, ArrayList<String> relationName, Node node) {
        int memorySize = mainMemory.getMemorySize();
        if (relationName.size() == 2) {
            return CrossJoinOperation2(schemaManager, mainMemory, relationName, node);
        } else {
            HashMap<Set<String> , Relations> singleRelation = new HashMap<>();
            for (String name: relationName) {
                HashSet<String> set = new HashSet<>();
                set.add(name);
                Relation relation = schemaManager.getRelation(name);
                Relations temp = new Relations(set, relation.getNumOfBlocks(), relation.getNumOfTuples());
                temp.cost = relation.getNumOfBlocks();
                temp.fieldNum = relation.getSchema().getNumOfFields();
                singleRelation.put(set, temp);
            }
            List<HashMap<Set<String> , Relations>> costRelationList = new ArrayList<>();
            costRelationList.add(singleRelation);
            for (int i = 1; i < relationName.size(); i++) {
                costRelationList.add(new HashMap<Set<String> , Relations>());
            }

            Set<String> finalGoal = new HashSet<>(relationName);
            Relations relations = LogicalQueryPlanGenerator.Optimizition(costRelationList, finalGoal, memorySize);
            LogicalQueryPlanGenerator.travesal(relations, 0);
            return helper2(relations, mainMemory, schemaManager, node);
        }
    }

    private static Relation helper1(Relations relations, MainMemory mainMemory, SchemaManager schemaManager,Node node) {
        if(relations.joinedRelations == null||relations.joinedRelations.size()<2) {
            List<String> relation = new ArrayList<>(relations.otherRelation);
            return schemaManager.getRelation(relation.get(0));
        } else {
            String subRelation1 = helper2(relations.joinedRelations.get(0), mainMemory, schemaManager, node).getRelationName();
            String subRelation2 = helper2(relations.joinedRelations.get(1), mainMemory, schemaManager,  node).getRelationName();
            ArrayList<String> relationName = new ArrayList<>();
            relationName.add(subRelation1);
            relationName.add(subRelation2);
            ArrayList<Tuple> tuples = CrossJoinOperation1(schemaManager, mainMemory, relationName, node);
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
            return null;
        }
    }

    private static Relation helper2(Relations relations, MainMemory mainMemory, SchemaManager schemaManager,Node node) {
        if(relations.joinedRelations == null||relations.joinedRelations.size()<2) {
            List<String> relation = new ArrayList<>(relations.otherRelation);
            return schemaManager.getRelation(relation.get(0));
        } else {
            String subRelation1 = helper2(relations.joinedRelations.get(0), mainMemory, schemaManager,node).getRelationName();
            String subRelation2 = helper2(relations.joinedRelations.get(1), mainMemory, schemaManager, node).getRelationName();
            ArrayList<String> relationName = new ArrayList<>();
            relationName.add(subRelation1);
            relationName.add(subRelation2);
            return CrossJoinOperation2(schemaManager, mainMemory, relationName, node);

        }
    }

    private static void appendTupleToRelation(Relation refRelation, MainMemory mainMemory, int memoryBlockIndex, Tuple tuple) {
        Block block_reference;
        if (refRelation.getNumOfBlocks()==0) {
            block_reference=mainMemory.getBlock(memoryBlockIndex);
            block_reference.clear(); //clear the block
            block_reference.appendTuple(tuple); // append the tuple
            refRelation.setBlock(refRelation.getNumOfBlocks(),memoryBlockIndex);
        } else {
            refRelation.getBlock(refRelation.getNumOfBlocks()-1,memoryBlockIndex);
            block_reference=mainMemory.getBlock(memoryBlockIndex);

            if (block_reference.isFull()) {
                block_reference.clear(); //clear the block
                block_reference.appendTuple(tuple); // append the tuple
                refRelation.setBlock(refRelation.getNumOfBlocks(),memoryBlockIndex); //write back to the relation
            } else {
                block_reference.appendTuple(tuple); // append the tuple
                refRelation.setBlock(refRelation.getNumOfBlocks()-1,memoryBlockIndex); //write back to the relation
            }
        }
    }

    private static Expression getExpression1(int num) {
        String str = "NOT sid = " + Integer.toString(num);
        Node where1 = new Node("WHERE", new ArrayList<>());
        String[] SQLCmdArray = str.split(" ");
        ParseHelper parseHelper = new ParseHelper();
        where1.addNodeToChildrenList(parseHelper.findCondition(SQLCmdArray));
        Expression expression = new Expression(where1.getChildren().get(0));
        return expression;
    }

    private static Expression getExpression2(int num) {
        String str = "NOT sid = " + Integer.toString(num);
        Node where2 = new Node("WHERE", new ArrayList<>());
        String[] SQLCmdArray = str.split(" ");
        ParseHelper parseHelper = new ParseHelper();
        where2.addNodeToChildrenList(parseHelper.findCondition(SQLCmdArray));
        Expression expression = new Expression(where2.getChildren().get(0));
        return expression;
    }



}
