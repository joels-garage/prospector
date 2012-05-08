/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.query;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.joelsgarage.model.AnnotatedAlternative;
import com.joelsgarage.util.BoundedPriorityQueue;

/**
 * Similar to RankedAlternativeCollection, which is a collection intended for RPC, i.e. it's just a
 * list.
 * 
 * @see RankedAlternativeCollection,.
 * @author joel
 */
public class ResultSet {
    private static final int MAX_CAPACITY = 1000;
    private BoundedPriorityQueue<AnnotatedAlternative> top;
    private BoundedPriorityQueue<AnnotatedAlternative> bottom;

    public static class ScoreComparator implements Comparator<AnnotatedAlternative> {
        private boolean descending;

        public ScoreComparator(boolean descending) {
            this.descending = descending;
        }

        @Override
        public int compare(AnnotatedAlternative o1, AnnotatedAlternative o2) {
            if (this.descending)
                return o2.getScore().compareTo(o1.getScore());
            return o1.getScore().compareTo(o2.getScore());
        }
    }

    protected ResultSet() {
        // want to size the pq's using the "limit" in the query.
        // for now, could use a maximum size though.
        this(MAX_CAPACITY);
    }

    public ResultSet(int size) {
        this.top = new BoundedPriorityQueue<AnnotatedAlternative>(size, new ScoreComparator(false));
        this.bottom =
            new BoundedPriorityQueue<AnnotatedAlternative>(size, new ScoreComparator(true));
    }

    public void add(AnnotatedAlternative e) {
        this.top.add(e);
        this.bottom.add(e);
    }
    
    /** Populate and sort the list of top-scoring items. The top-scoring item is element 0. */
    public void sortedTop(List<AnnotatedAlternative> c) {
        c.addAll(this.top);
        Collections.sort(c, new ScoreComparator(true));
    }

    /** Populate and sort the list of bottom-scoring items. The bottom-scoring item is element 0. */
    public void sortedBottom(List<AnnotatedAlternative> c) {
        c.addAll(this.bottom);
        Collections.sort(c, new ScoreComparator(false));
    }
}
