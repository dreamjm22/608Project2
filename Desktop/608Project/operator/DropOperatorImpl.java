package operator;

import parser.Node;
import storageManager.Disk;
import storageManager.MainMemory;
import storageManager.SchemaManager;

import java.util.List;

public class DropOperatorImpl implements OperatorInterface {
    public void operatorImpl (List<Node> list, Disk disk, MainMemory mainMemory, SchemaManager schemaManager){
        String tableName = list.get(0).getElement();
        schemaManager.deleteRelation(tableName);
        System.out.println("Dropped relation: " + tableName);
    }
}
