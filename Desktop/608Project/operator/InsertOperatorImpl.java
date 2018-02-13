package operator;

import parser.Node;
import storageManager.Block;
import storageManager.Disk;
import storageManager.FieldType;
import storageManager.MainMemory;
import storageManager.Relation;
import storageManager.SchemaManager;
import storageManager.Tuple;

import java.util.ArrayList;
import java.util.List;

public class InsertOperatorImpl implements OperatorInterface {
    public void operatorImpl (List<Node> list, Disk disk, MainMemory mainMemory, SchemaManager schemaManager){
        String tableName = list.get(0).getElement();
        ArrayList<String> attributeTypeList = new ArrayList<>(list.get(0).getAttributeTypeList());
        Relation relation = schemaManager.getRelation(tableName);
        Tuple tuple = relation.createTuple();

        if (list.size() == 1) {
            ArrayList<String> attributeList = new ArrayList<>(list.get(0).getAttributeList());


            for (int index = 0; index < attributeTypeList.size(); index++) {
                if (tuple.getSchema().getFieldType(attributeTypeList.get(index)).equals(FieldType.INT)) {
                    if ((attributeList.get(index).equals("NULL"))) {
                        // NULL Situation
                        tuple.setField(attributeTypeList.get(index), Integer.MIN_VALUE);
                    } else {
                        tuple.setField(attributeTypeList.get(index), Integer.parseInt(attributeList.get(index)));
                    }
                } else {
                    tuple.setField(attributeTypeList.get(index), attributeList.get(index));
                }
            }
            appendTupleToRelation(relation, mainMemory, 0, tuple);
        } else {
            /*
            System.out.println(list.get(0).getElement());
            System.out.println(list.get(1).getElement());
            System.out.println(list.get(1).getChildren().size());
            System.out.println(list.get(1).getChildren().get(0).getElement());
            System.out.println(list.get(1).getChildren().get(1).getElement());*/

            List<String> attributeList = list.get(1).getChildren().get(0).getAttributeList();
            List<Node> tableList = list.get(1).getChildren().get(1).getChildren();
            if (tableList.size() == 1) {
                // One table
                // System.out.println("One table");
                if (attributeList.size() == 1 && attributeList.get(0).equals("*")) {
                    // SELECT *
                    // System.out.println("Select *");
                    basicSelectOperation(mainMemory, schemaManager, tableList);
                }
            }
        }
        // System.out.println("Insert complete");
    }

    private void basicSelectOperation(MainMemory mainMemory, SchemaManager schemaManager, List<Node> tableList) {
        Relation relation = schemaManager.getRelation(tableList.get(0).getElement());
        for (String s :  relation.getSchema().getFieldNames()) {
            System.out.print(s + "  ");
        }
        System.out.println();
        int relationNumberOfBlocks = relation.getNumOfBlocks();
        for (int i = 0;i < relationNumberOfBlocks; i++) {
            relation.getBlock(i, 0);
            Block block = mainMemory.getBlock(0);
            for (int j = 0; j < block.getNumTuples(); j++) {
                Tuple tuple = block.getTuple(j);
                appendTupleToRelation(relation, mainMemory, 0, tuple);
            }
        }
    }

    private static void appendTupleToRelation(Relation relation_reference, MainMemory mem, int memory_block_index, Tuple tuple) {
        Block block_reference;
        if (relation_reference.getNumOfBlocks()==0) {
            block_reference=mem.getBlock(memory_block_index);
            block_reference.clear(); //clear the block
            block_reference.appendTuple(tuple); // append the tuple
            relation_reference.setBlock(relation_reference.getNumOfBlocks(),memory_block_index);
        } else {
            relation_reference.getBlock(relation_reference.getNumOfBlocks()-1,memory_block_index);
            block_reference=mem.getBlock(memory_block_index);

            if (block_reference.isFull()) {
                block_reference.clear(); //clear the block
                block_reference.appendTuple(tuple); // append the tuple
                relation_reference.setBlock(relation_reference.getNumOfBlocks(),memory_block_index); //write back to the relation
            } else {
                block_reference.appendTuple(tuple); // append the tuple
                relation_reference.setBlock(relation_reference.getNumOfBlocks()-1,memory_block_index); //write back to the relation
            }
        }
    }
}
