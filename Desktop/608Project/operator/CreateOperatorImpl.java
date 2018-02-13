package operator;

import parser.Node;
import storageManager.Disk;
import storageManager.FieldType;
import storageManager.MainMemory;
import storageManager.Relation;
import storageManager.Schema;
import storageManager.SchemaManager;

import java.util.ArrayList;
import java.util.List;

public class CreateOperatorImpl implements OperatorInterface {
    public void operatorImpl (List<Node> list, Disk disk, MainMemory mainMemory, SchemaManager schemaManager){
        String tableName = list.get(0).getElement();
        ArrayList<String> attributeTypeList = new ArrayList<>(list.get(0).getAttributeTypeList());
        ArrayList<FieldType> attributeList = new ArrayList<>();
        for(String fieldType : list.get(0).getAttributeList()) {
            if (fieldType.equals("INT")) {
                attributeList.add(FieldType.INT);
            }
            else if (fieldType.equals("STR20")) {
                attributeList.add(FieldType.STR20);
            }
            else {
                System.out.println("Error in field type");
                return;
            }
        }
        Schema schema = new Schema(attributeTypeList, attributeList);
        Relation relation = schemaManager.createRelation(tableName, schema);
        /*
        if (relation != null) {
            System.out.println("Created relation: " + tableName);
        } else {
            System.out.println("Failed to create relation: " + tableName);
        }*/
    }
}
