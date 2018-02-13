package operator;

import java.util.List;

import storageManager.Disk;
import storageManager.MainMemory;
import storageManager.SchemaManager;
import parser.Node;

public interface OperatorInterface {
    public void operatorImpl(List<Node> list, Disk disk, MainMemory mainMemory, SchemaManager schemaManager);
}
