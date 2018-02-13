package operator;

import parser.Node;
import storageManager.FieldType;
import storageManager.Tuple;

import java.util.HashSet;
import java.util.Set;

public class Expression {

    private Set<String> hashSet;

    class ExpressionObject {
        String comparisonType;
        String comparisonString;
        int comparisonNumber;
        private boolean sameAs(ExpressionObject expressionObject) {
            // System.out.println(this.comparisonType);
            // System.out.println(expressionObject.comparisonType);
            if (!this.comparisonType.equals(expressionObject.comparisonType)) {
                // System.out.println("WTF");
                return false;
            } else if (this.comparisonType.equals("STR")) {
                // System.out.println("Comparing String");
                return this.comparisonString.equals(expressionObject.comparisonString);
            } else {
                // System.out.println("Comparing INT");
                return this.comparisonNumber == expressionObject.comparisonNumber;
            }
        }

        private boolean biggerThan(ExpressionObject expressionObject) {
            // System.out.println(this.comparisonType);
            // System.out.println(expressionObject.comparisonType);
            if (!this.comparisonType.equals(expressionObject.comparisonType)) {
                // System.out.println("WTF");
                return false;
            } else {
                // System.out.println("Comparing INT");
                return this.comparisonNumber > expressionObject.comparisonNumber;
            }
        }

        private boolean smallerThan(ExpressionObject expressionObject) {
            // System.out.println(this.comparisonType);
            // System.out.println(expressionObject.comparisonType);
            if (!this.comparisonType.equals(expressionObject.comparisonType)) {
                // System.out.println("WTF");
                return false;
            } else {
                // System.out.println("Comparing INT");
                return this.comparisonNumber < expressionObject.comparisonNumber;
            }
        }

        private int add(ExpressionObject expressionObject) {
            // System.out.println(this.comparisonType);
            // System.out.println(expressionObject.comparisonType);
            if (!this.comparisonType.equals(expressionObject.comparisonType)) {
                System.out.println("WTF");
                return 0;
            } else {
                // System.out.println("Comparing INT");
                return this.comparisonNumber + expressionObject.comparisonNumber;
            }
        }
    }

    public Node node;

    public Expression(Node node) {
        this.node = node;
        hashSet = new HashSet<>();
        hashSet.add("+");
        hashSet.add("-");
        hashSet.add("*");
        hashSet.add("/");
    }

    public boolean evaluateExpression(Tuple tuple) {
        // System.out.println("evaluateExpression");
        // System.out.println(node.getElement());
        switch (node.getElement()) {
            case "AND": {
                boolean temp = new Expression(node.getChildren().get(0)).evaluateExpression(tuple);
                if (temp == false)
                    return false;
                else
                    return temp && new Expression(node.getChildren().get(1)).evaluateExpression(tuple);
            }
            case "OR":
                return new Expression(node.getChildren().get(0)).evaluateExpression(tuple) || new Expression(node.getChildren().get(1)).evaluateExpression(tuple);
            case "NOT": {
                // System.out.println("HEHE");
                // System.out.println(node.getElement());
                return !new Expression(node.getChildren().get(0)).evaluateExpression(tuple);
            }
            case ">":
                //return (new Expression(node.getChildren().get(0)).evaluateOthers(tuple)).biggerThan(new Expression(node.getChildren().get(1)).evaluateOthers(tuple));
                return new Expression(node.getChildren().get(0)).evaluateNumber(tuple) > new Expression(node.getChildren().get(1)).evaluateNumber(tuple);
            case "<":
                return new Expression(node.getChildren().get(0)).evaluateNumber(tuple) < new Expression(node.getChildren().get(1)).evaluateNumber(tuple);
                //return (new Expression(node.getChildren().get(0)).evaluateOthers(tuple)).smallerThan(new Expression(node.getChildren().get(1)).evaluateOthers(tuple));
            case "=":
                return (new Expression(node.getChildren().get(0)).evaluateOthers(tuple)).sameAs(new Expression(node.getChildren().get(1)).evaluateOthers(tuple));
        }
        try {
            throw new Exception("Unknown Expression");
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return false;
    }

    private int evaluateNumber(Tuple tuple) {
        if (isInteger(node.getElement()))
            return Integer.parseInt(node.getElement());
        switch (node.getElement()) {
            case "+":
                return new Expression(node.getChildren().get(0)).evaluateNumber(tuple) + new Expression(node.getChildren().get(1)).evaluateNumber(tuple);
            case "-":
                return new Expression(node.getChildren().get(0)).evaluateNumber(tuple) - new Expression(node.getChildren().get(1)).evaluateNumber(tuple);
            case "*":
                return new Expression(node.getChildren().get(0)).evaluateNumber(tuple) * new Expression(node.getChildren().get(1)).evaluateNumber(tuple);
            case "/":
                return new Expression(node.getChildren().get(0)).evaluateNumber(tuple) / new Expression(node.getChildren().get(1)).evaluateNumber(tuple);
        }

        if (isInteger(node.getElement()))
            return Integer.parseInt(node.getElement());
        else if (node.getElement().charAt(0) != '"') {
            //System.out.println(node.getElement());
            String name = node.getElement();
            return tuple.getField(name).integer;
        }

        try {
            throw new Exception("Unknown Number in Expression");
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return Integer.MAX_VALUE;
    }

    private ExpressionObject evaluateOthers(Tuple tuple) {
        // System.out.println("evaluateOthers");
        // System.out.println(node.getElement());

        ExpressionObject expressionObject = new ExpressionObject();
        if (node.getElement().charAt(0) == '"') {
            // System.out.println("STRSTR");
            expressionObject.comparisonType = "STR";
            expressionObject.comparisonString = node.getElement().substring(1,node.getElement().length()-1);
        } else if (isInteger(node.getElement())) {
            // System.out.println("INTINT");
            expressionObject.comparisonType = "INT";
            expressionObject.comparisonNumber = Integer.parseInt(node.getElement());
        } else if (hashSet.contains(node.getElement())){
            expressionObject.comparisonType = "INT";
            expressionObject.comparisonNumber = evaluateNumber(tuple);
        } else {
            // System.out.println("WTFWTF");
            FieldType fieldType = tuple.getSchema().getFieldType(node.getElement());
            if (fieldType == FieldType.INT) {
                // System.out.println("WTFINT");
                expressionObject.comparisonType = "INT";
                expressionObject.comparisonNumber = tuple.getField(node.getElement()).integer;
            } else {
                // System.out.println("WTFSTR");
                expressionObject.comparisonType = "STR";
                expressionObject.comparisonString = tuple.getField(node.getElement()).str;
            }
        }
        return expressionObject;
    }

    private boolean isInteger (String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        if (str.charAt(0) == '-' && str.length() == 1) {
            return false;
        }
        int strIndex = 0;
        if (str.charAt(0) == '-') {
            strIndex = 1;
        }
        for (; strIndex < str.length(); strIndex++) {
            if (str.charAt(strIndex) < '0' || str.charAt(strIndex) > '9') {
                return false;
            }
        }
        return true;
    }
}
