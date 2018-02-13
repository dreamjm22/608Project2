package operator;

import parser.Node;
import storageManager.Disk;
import storageManager.MainMemory;
import storageManager.Relation;
import storageManager.SchemaManager;
import storageManager.Tuple;

import java.util.ArrayList;
import java.util.List;

public class DeleteOperatorImpl implements OperatorInterface {
    public void operatorImpl (List<Node> list, Disk disk, MainMemory mainMemory, SchemaManager schemaManager){
        String tableName = list.get(0).getElement();
        Relation relation = schemaManager.getRelation(tableName);
        // System.out.println(relation.getNumOfBlocks());
        if (list.size() == 1) {
            relation.deleteBlocks(0);
        } else {
            int relationDiskBlockIndex = 0;
            Expression expression = new Expression(list.get(1).getChildren().get(0));
            for (int i = 0; i < relation.getNumOfBlocks(); i++) {
                relation.getBlock(i, 0);
                ArrayList<Tuple> tuplesListInMemoryBlock = mainMemory.getBlock(0).getTuples();
                for (int j = 0; j < tuplesListInMemoryBlock.size(); j++) {
                    if (expression.evaluateExpression(tuplesListInMemoryBlock.get(j))) {
                        mainMemory.getBlock(0).invalidateTuple(j);
                        if (mainMemory.getBlock(0).getNumTuples() != 0) {
                            relation.setBlock(relationDiskBlockIndex, 0);
                            relationDiskBlockIndex++;
                        }
                    } else {
                        relation.setBlock(relationDiskBlockIndex, 0);
                        relationDiskBlockIndex++;
                    }
                }
            }
            relation.deleteBlocks(relationDiskBlockIndex);
        }
        // System.out.println("DELETE COMPLETE");
        // System.out.println(relation.getNumOfBlocks());
    }
}


