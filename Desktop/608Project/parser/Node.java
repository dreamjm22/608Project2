package parser;

import java.util.List;
import java.util.ArrayList;

public class Node {
    private String element;
    private List<String> attributeList;
    private List<String> attributeTypeList;
    private List<String> valueList;
    private List<Node> children;

    public Node(String element, ArrayList<Node> children) {
        this.element = element;
        this.attributeList = new ArrayList<>();
        this.attributeTypeList = new ArrayList<>();
        this.valueList = new ArrayList<>();
        this.children = children;
    }

    public Node(){
        this.element = "TEMP";
    }

    public Node copyNode(Node node){
        // System.out.println("copyNode");
        // System.out.println(node.getElement());
        Node newNode = new Node(node.getElement(), null);
        if (node.getChildren()!=null) {
            newNode.setChildrenList(new ArrayList<>());
            for (int i = 0; i < node.getChildren().size(); i++) {

                Node tempNode  = copyNode(node.getChildren().get(i));
                newNode.addNodeToChildrenList(tempNode);
            }
        }
        return newNode;
    }
    public void setElement(String element) {
        this.element = element;
    }

    public void setChildrenList(List<Node> children) {
        this.children = children;
    }

    public void addNodeToChildrenList(Node node){
        children.add(node);
    }

    public void addToAttributeList (String attribute) {
        attributeList.add(attribute);
    }

    public void addToAttributeTypeList (String attributeType) {
        attributeTypeList.add(attributeType);
    }

    public void addToValueList (String value) { valueList.add(value); }

    public String getElement() {
        return element;
    }

    public List<Node> getChildren() {
        return children;
    }

    public List<String> getAttributeList() {
        return attributeList;
    }

    public List<String> getAttributeTypeList() {
        return attributeTypeList;
    }
}
