package Optimization;

import storageManager.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Arrays;


public class ImplementationOfPhysicalOperator {

    private static boolean checkDup(Tuple t1, Tuple t2, ArrayList<String> Fields){
        for(String str : Fields){
            if(!(t1.getField(str).toString().equals((t2.getField(str).toString()))))
                return false;
        }
        return true;
    }

    private static boolean isNumeric(String str){
        try{
            Integer.parseInt(str);
        }catch (NumberFormatException e){
            return false;
        }catch (NullPointerException e){
            return false;
        }
        return true;
    }

    public static ArrayList<Tuple> DinstinctOperation1(MainMemory mainMemory,SchemaManager schemaManager,Relation relation, ArrayList<String> field){
        ArrayList<Tuple> tuples;
        if(relation.getNumOfBlocks() < mainMemory.getMemorySize())
            tuples=onePassDistinct(relation,mainMemory,field);
        else
            tuples=twoPassDistinct(relation,mainMemory,field);
        return tuples;
    }

    public static Relation DinstinctOperation2(MainMemory mainMemory,SchemaManager schemaManager,Relation relation, ArrayList<String> field) {
        ArrayList<Tuple> tuples;
        // System.out.println("DinstinctOperation2");
        // System.out.println(relation.getNumOfTuples());
        if (relation.getNumOfBlocks() < mainMemory.getMemorySize())
            tuples = onePassDistinct(relation, mainMemory, field);
        else
            tuples = twoPassDistinct(relation, mainMemory, field);

        // System.out.println(result.size());

        Schema schema = tuples.get(0).getSchema();
        if (schemaManager.relationExists(relation.getRelationName() + "distinct"))
            schemaManager.deleteRelation(relation.getRelationName() + "distinct");
        Relation r = schemaManager.createRelation(relation.getRelationName() + "distinct", schema);
        int count = 0;
        Block block = mainMemory.getBlock(0);
        while (!tuples.isEmpty()) {
            block.clear();
            for (int i = 0; i < schema.getTuplesPerBlock(); i++) {
                if (!tuples.isEmpty()) {
                    Tuple tuple = tuples.get(0);
                    block.setTuple(i, tuple);
                    tuples.remove(tuple);
                }
            }
            r.setBlock(count++, 0);
        }
        return r;
    }

    public static ArrayList<Tuple> executeOrder1(MainMemory mainMemory, Relation relation, ArrayList<String> fieldList){
        ArrayList<Tuple> tuples = new ArrayList<Tuple>();
        if(relation.getNumOfBlocks()<mainMemory.getMemorySize()) {
            tuples = onePassSort(relation, mainMemory, fieldList);
        }else{
            tuples = twoPassSort(relation,mainMemory,fieldList);
        }
        return tuples;
    }

    public static ArrayList<Tuple> onePassDistinct(Relation relation, MainMemory memory, ArrayList<String> Fields){
        ArrayList<Tuple> tuples = new ArrayList<Tuple>();
        ArrayList<Tuple> TupleList = new ArrayList<Tuple>();
        relation.getBlocks(0,0,relation.getNumOfBlocks());
        TupleList = memory.getTuples(0,relation.getNumOfBlocks());
        Tuple tuple;
        Tuple temp = null;
        while(TupleList.size() != 0){
            tuple = Collections.min(TupleList, new Comparator<Tuple>() {
                @Override
                public int compare(Tuple o1, Tuple o2) {
                    if(o1 == null) return 1;
                    if(o2 == null) return -1;
                    int[] result = new int[Fields.size()];
                    for(int i=0;i<Fields.size();i++){
                        String field1 = o1.getField(Fields.get(i)).toString();
                        String field2 = o2.getField(Fields.get(i)).toString();
                        if(isNumeric(field1) && isNumeric(field2)){
                            result[i]=Integer.parseInt(field1)-Integer.parseInt(field2);
                        }
                        else
                            result[i]=field1.compareTo(field2);
                    }
                    for(int i=0;i<Fields.size();i++){
                        if(result[i]>0)
                            return 1;
                        else if(result[i]<0)
                            return -1;
                    }
                    return 0;
                }
            });

            if(tuple==null || temp==null || !checkDup(tuple,temp,Fields)){
                temp=tuple;
                tuples.add(tuple);
            }
            TupleList.remove(TupleList.indexOf(tuple));
        }
        return tuples;
    }

    public static ArrayList<Tuple> onePassSort(Relation relation, MainMemory memory, ArrayList<String> FieldIndex){
        ArrayList<Tuple> tuples = new ArrayList<Tuple>();
        ArrayList<Tuple> TupleList = new ArrayList<Tuple>();
        relation.getBlocks(0,0,relation.getNumOfBlocks());
        TupleList = memory.getTuples(0,relation.getNumOfBlocks());


        Collections.sort(TupleList, new Comparator<Tuple>() {
            @Override
            public int compare(Tuple o1, Tuple o2) {
                if(o1 == null) return 1;
                if(o2 == null) return -1;
                int[] res = new int[FieldIndex.size()];
                for(int i=0;i<FieldIndex.size();i++) {
                    String field1 = o1.getField(FieldIndex.get(i)).toString();
                    String field2 = o2.getField(FieldIndex.get(i)).toString();
                    if (isNumeric(field1) && isNumeric(field2))
                        res[i] = Integer.parseInt(field1) - Integer.parseInt(field2);
                    else
                        res[i] = field1.compareTo(field2);
                }
                for(int i=0;i<FieldIndex.size();i++){
                    if(res[i]>0)
                        return 1;
                    else if(res[i]<0)
                        return -1;
                }
                return 0;
            }
        });

        for(Tuple tuple : TupleList){
            tuples.add(tuple);
        }
        return tuples;
    }

    public static int twoPassBase(Relation relation, MainMemory mainMemory, final ArrayList<String> Fields){
        int input=0;
        int sortNum = 0;
        while(sortNum<relation.getNumOfBlocks()){
            if((relation.getNumOfBlocks()-sortNum)>mainMemory.getMemorySize())
                input = mainMemory.getMemorySize();
            else
                input = relation.getNumOfBlocks()-sortNum;
            relation.getBlocks(sortNum,0,input);
            ArrayList<Tuple> tuples = mainMemory.getTuples(0,input);
            Collections.sort(tuples,new Comparator<Tuple>(){
                public int compare(Tuple o1, Tuple o2){
                    int[] result = new int[Fields.size()];
                    for(int i=0;i<Fields.size();i++){
                        String field1 = o1.getField(Fields.get(i)).toString();
                        String field2 = o2.getField(Fields.get(i)).toString();
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
            // System.out.println("hjakfafas");
            mainMemory.setTuples(0,tuples);
            relation.setBlocks(sortNum,0,input);
            sortNum+=input;
        }
        return input;
    }

    public static ArrayList<Tuple> twoPassDistinct(Relation relation, MainMemory mainMemory, final ArrayList<String> Fields){
        int temp=0;
        int printed=0;
        ArrayList<Integer> segments=new ArrayList<Integer>();
        ArrayList<Tuple> output=new ArrayList<Tuple>();

        int last_segment = twoPassBase(relation,mainMemory, Fields);
        while(temp<relation.getNumOfBlocks()){
            segments.add(temp);
            temp+=mainMemory.getMemorySize();
        }

        Block block= null;
        for(int i=0;i<mainMemory.getMemorySize();i++){
            block = mainMemory.getBlock(i);
            block.clear();
            mainMemory.setBlock(i,block);
        }
        int[] reads = new int[segments.size()];
        Arrays.fill(reads,1);

        ArrayList<ArrayList<Tuple>> tuples = new ArrayList<ArrayList<Tuple>>();
        for(int i=0;i<segments.size();i++){
            relation.getBlock(segments.get(i),i);
            block = mainMemory.getBlock(i);
            tuples.add(block.getTuples());
        }

        Tuple comparator = null;
        for(int i=0;i<relation.getNumOfTuples();i++){
            for(int j=0;j<segments.size();j++){
                if(tuples.get(j).isEmpty()){
                    if(j<segments.size()-1 && reads[j]<mainMemory.getMemorySize()){
                        relation.getBlock(segments.get(j)+reads[j],j);
                        block = mainMemory.getBlock(j);
                        tuples.get(j).addAll(block.getTuples());
                        reads[j]++;
                    }else if(j==segments.size()-1 && reads[j]<last_segment){
                        relation.getBlock(segments.get(j)+reads[j],j);
                        block = mainMemory.getBlock(j);
                        tuples.get(j).addAll(block.getTuples());
                        reads[j]++;
                    }
                }
            }
            Tuple[] minTuple = new Tuple[segments.size()];
            for(int k=0;k<segments.size();k++){
                if(!tuples.get(k).isEmpty()){
                    minTuple[k] = Collections.min(tuples.get(k),new Comparator<Tuple>(){
                        public int compare(Tuple o1, Tuple o2){
                            int[] result = new int[Fields.size()];
                            if(o1==null) return 1;
                            if(o2==null) return -1;
                            for(int f = 0; f< Fields.size(); f++){
                                String field1 = o1.getField(Fields.get(f)).toString();
                                String field2 = o2.getField(Fields.get(f)).toString();
                                if(isNumeric(field1) && isNumeric(field2)){
                                    result[f] = Integer.parseInt(field1)-Integer.parseInt(field2);
                                }
                                else
                                    result[f] = field1.compareTo(field2);
                            }
                            for(int f = 0; f< Fields.size(); f++){
                                if(result[f]>0) return 1;
                                else if(result[f]<0) return -1;
                            }
                            return 0;
                        }
                    });
                }else{
                    minTuple[k] = null;
                }
            }
            ArrayList<Tuple> tmp = new ArrayList<Tuple>(Arrays.asList(minTuple));

            Tuple minVal = Collections.min(tmp,new Comparator<Tuple>(){
                public int compare(Tuple o1, Tuple o2){
                    int[] result = new int[Fields.size()];
                    if(o1==null) return 1;
                    if(o2==null) return -1;
                    for(int f = 0; f< Fields.size(); f++){
                        String field1 = o1.getField(Fields.get(f)).toString();
                        String field2 = o2.getField(Fields.get(f)).toString();
                        if(isNumeric(field1) && isNumeric(field2)){
                            result[f] = Integer.parseInt(field1)-Integer.parseInt(field2);
                        }
                        else
                            result[f] = field1.compareTo(field2);
                    }
                    for(int f = 0; f< Fields.size(); f++){
                        if(result[f]>0) return 1;
                        else if(result[f]<0) return -1;
                    }
                    return 0;
                }
            });
            int resultIndex = tmp.indexOf(minVal);
            int tupleIndex = tuples.get(resultIndex).indexOf(minTuple[resultIndex]);
            if(minVal==null||comparator==null||!checkDup(minVal,comparator, Fields)){
                output.add(minVal);
                comparator = minVal;
                printed++;
            }
            tuples.get(resultIndex).remove(tupleIndex);
        }
        return output;
    }

    public static ArrayList<Tuple> twoPassSort(Relation relation, MainMemory mainMemory, final ArrayList<String> Fields){
        int temp=0;
        int printed=0;
        int index=0;
        ArrayList<Integer> segments=new ArrayList<Integer>();
        ArrayList<Tuple> output=new ArrayList<Tuple>();
        int last_segment = twoPassBase(relation,mainMemory,Fields);
        while(temp<relation.getNumOfBlocks()){
            segments.add(temp);
            temp+=mainMemory.getMemorySize();
        }
        Block block = null;
        for(int i=0;i<mainMemory.getMemorySize();i++){
            block = mainMemory.getBlock(i);
            block.clear();
        }
        int[] reads = new int[segments.size()];
        Arrays.fill(reads,1);
        ArrayList<ArrayList<Tuple>> tuples = new ArrayList<ArrayList<Tuple>>();
        for(int i=0;i<segments.size();i++){
            relation.getBlock(segments.get(i),i);
            block = mainMemory.getBlock(i);
            tuples.add(block.getTuples());
        }
        Tuple[] minTuple = new Tuple[segments.size()];

        for(int i=0;i<relation.getNumOfTuples();i++){
            for(int j=0;j<segments.size();j++){
                if(tuples.get(j).isEmpty()){
                    if(j<segments.size()-1 && reads[j]<mainMemory.getMemorySize()){
                        relation.getBlock(segments.get(j)+reads[j],j);
                        block = mainMemory.getBlock(j);
                        tuples.get(j).addAll(block.getTuples());
                        reads[j]++;
                    }else if(j==segments.size()-1 && reads[j]<last_segment){
                        relation.getBlock(segments.get(j)+reads[j],j);
                        block = mainMemory.getBlock(j);
                        tuples.get(j).addAll(block.getTuples());
                        reads[j]++;
                    }
                }
            }
            for(int k=0;k<segments.size();k++){
                if(!tuples.get(k).isEmpty()){
                    minTuple[k] = Collections.min(tuples.get(k),new Comparator<Tuple>(){
                        public int compare(Tuple o1, Tuple o2){
                            if(o1==null) return 1;
                            if(o2==null) return -1;
                            int[] result = new int[Fields.size()];
                            for(int i=0;i<Fields.size();i++){
                                String field1 = o1.getField(Fields.get(i)).toString();
                                String field2 = o2.getField(Fields.get(i)).toString();
                                if(isNumeric(field1) && isNumeric(field2)){
                                    result[i] = Integer.parseInt(field1)-Integer.parseInt(field2);
                                }
                                else
                                    result[i] = field1.compareTo(field2);
                            }
                            for(int i=0;i<Fields.size();i++){
                                if(result[i]>0)
                                    return 1;
                                else if(result[i]<0)
                                    return -1;
                            }
                            return 0;
                        }
                    });
                }else{
                    minTuple[k] = null;
                }
            }
            ArrayList<Tuple> tmp = new ArrayList<Tuple>(Arrays.asList(minTuple));
            Tuple minVal = Collections.min(tmp,new Comparator<Tuple>(){
                public int compare(Tuple o1, Tuple o2){
                    int[] result = new int[Fields.size()];
                    if(o1==null) return 1;
                    if(o2==null) return -1;
                    for(int i=0;i<Fields.size();i++){
                        String field1 = o1.getField(Fields.get(i)).toString();
                        String field2 = o2.getField(Fields.get(i)).toString();
                        if(isNumeric(field1) && isNumeric(field2)){
                            result[i] = Integer.parseInt(field1)-Integer.parseInt(field2);
                        }
                        else
                            result[i] = field1.compareTo(field2);
                    }
                    for(int i=0;i<Fields.size();i++){
                        if(result[i]>0)
                            return 1;
                        else if(result[i]<0)
                            return -1;
                    }
                    return 0;
                }
            });

            int resultIndex = tmp.indexOf(minVal);
            int tupleIndex = tuples.get(resultIndex).indexOf(minTuple[resultIndex]);

            tuples.get(resultIndex).remove(tupleIndex);
            output.add(minVal);
            printed++;
        }
        return output;
    }

}
