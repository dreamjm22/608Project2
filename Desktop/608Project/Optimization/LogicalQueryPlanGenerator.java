package Optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LogicalQueryPlanGenerator {

    public static Relations Optimizition(List<HashMap<Set<String>, Relations>> relationsDPtable, Set<String> goal, int memorySize) {

        if (relationsDPtable.get(goal.size()-1).containsKey(goal)) {
            // System.out.println("Returned");
            return relationsDPtable.get(goal.size()-1).get(goal);
        }
        int block=0;
        int tuple=0;
        int fieldNumber=0;
        int minimumCost=Integer.MAX_VALUE;

        List<Relations> initial = null;
        List<makePair> removed = removeFromSet(goal);

        for (makePair pair: removed) {
            Set<String> s1 =pair.s1;
            Set<String> s2= pair.s2;
            Relations r1= Optimizition(relationsDPtable, s1, memorySize);
            Relations r2= Optimizition(relationsDPtable, s2, memorySize);
            if (r1.cost + r2.cost + calculateCost(memorySize, r1.blockNum, r2.blockNum) < minimumCost) {
                initial =new ArrayList<>();
                initial.add(r1);
                initial.add(r2);
                tuple =r1.tupleNum * r2.tupleNum;
                block =calculateBlocksAfterJoin(r1.tupleNum, r2.tupleNum, 8,r1.fieldNum + r2.fieldNum);
                fieldNumber= r1.fieldNum + r2.fieldNum;
                minimumCost=r1.cost + r2.cost + calculateCost(memorySize, r1.blockNum, r2.blockNum);
            }
        }

        Relations res =new Relations(goal, block, tuple);
        // System.out.println(initial);
        // System.out.println(fieldNumber);
        // System.out.println(minimumCost);
        res.joinedRelations=initial;
        res.fieldNum =fieldNumber;
        res.cost =minimumCost;
        relationsDPtable.get(goal.size()-1).put(goal, res);

        return res;
    }

    static class makePair {

        Set<String> s1;
        Set<String> s2;

        public makePair(Set<String> s1, Set<String> s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        public String toString() {
            return "[" + s1.toString() + ", " + s2.toString() + "]";
        }
    }

    public static List<makePair> removeFromSet(Set<String> input) {

        List <makePair> res=new ArrayList<>();

        for (int i=1; i<=input.size()/2; i++) {
            Set<String> s1=new HashSet<>(input);
            Set<String> s2=new HashSet<>();
            calculateHelper(s1, i, 0, s2, res);
        }
        return res;
    }

    public static int calculateBlocksAfterJoin(int tupleNum1, int tupleNum2, int blockSize, int fieldPerTuple) {
        int totalTuples=calculateTuplesAfterJoin(tupleNum1, tupleNum2);
        return totalTuples*fieldPerTuple%blockSize == 0?totalTuples * fieldPerTuple/blockSize:totalTuples*fieldPerTuple/blockSize+ 1;
    }

    public static int calculateTuplesAfterJoin(int tupleNum1, int tupleNum2) {
        return tupleNum1 * tupleNum2;
    }

    public static int calculateCost(int memorySize, int blockNum1, int blockNum2) {
        // System.out.println(blockNum1 + blockNum2);
        // System.out.println(blockNum1 * blockNum2 + Math.min(blockNum1, blockNum2));
        if(Math.min(blockNum1, blockNum2)<=memorySize)
            return blockNum1+blockNum2;
        else
            return blockNum1*blockNum2+Math.min(blockNum1, blockNum2);
    }

    public static void calculateHelper(Set<String> set, int count, int startPosition, Set<String> originalSet, List<makePair> res) {
        if (count==0)
            res.add(new makePair(set, originalSet));

        List<String> inputList=new ArrayList<>(set);

        for (int i=startPosition; i<inputList.size(); i++) {
            Set<String> s1=new HashSet<>(set);
            Set<String> s2=new HashSet<>(originalSet);
            // System.out.println("Removed");
            s1.remove(inputList.get(i));
            // System.out.println("Added");
            s2.add(inputList.get(i));
            calculateHelper(s1, count - 1, i, s2, res);
        }
    }

    public static void travesal(Relations relations , int level) {
        /*
        for (int i = 0; i < level; i++) {
            System.out.print(" ");
        }
        for (String str: relations.otherRelation) {
            System.out.print(str+" ");
        }
        System.out.println();*/
        if (relations.joinedRelations!=null) {
            for (Relations relations1:relations.joinedRelations) {
                travesal(relations1, level + 1);
            }
        }
    }

}
