package Optimization;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Relations {

    Set<String> otherRelation;

    int tupleNum;
    int fieldNum;
    int blockNum;
    int cost = Integer.MAX_VALUE;

    public Relations(Set<String> s, int block, int tuple) {
        this.blockNum = block;
        this.tupleNum = tuple;
        otherRelation = new HashSet<>(s);
    }


    public List<Relations> joinedRelations;
}