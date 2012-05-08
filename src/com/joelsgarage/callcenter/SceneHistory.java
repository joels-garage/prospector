/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import com.joelsgarage.model.Scene;

/**
 * Scene History contains scenes, sorted chronologically at all times.
 * 
 * @author joel
 */
public class SceneHistory {
    private TreeSet<Scene> history = new TreeSet<Scene>(timestampComparator);

    /** Compare scenes by start timestamp, for processing the history */
    private static Comparator<Scene> timestampComparator = new Comparator<Scene>() {
        @Override
        public int compare(Scene o1, Scene o2) {
            String o1Start = ""; //$NON-NLS-1$
            String o2Start = ""; //$NON-NLS-1$
            if (o1 != null)
                o1Start = o1.getStart();
            if (o2 != null)
                o2Start = o2.getStart();
            if (!(o1Start.equals(o2Start))) {
                return o1Start.compareTo(o2Start);
            }
            // tiebreaker
            String o1Key = ""; //$NON-NLS-1$
            String o2Key = ""; //$NON-NLS-1$
            if (o1 != null)
                o1Key = o1.makeKey().toString();
            if (o2 != null)
                o2Key = o2.makeKey().toString();
            return o1Key.compareTo(o2Key);
        }
    };

    public Scene last() {
        if (this.history.isEmpty())
            return null;
        return this.history.last();
    }

    public boolean add(Scene scene) {
        return this.history.add(scene);
    }

    /** Exposes the underlying collection. */
    public Collection<Scene> getHistory() {
        return this.history;
    }

    public Iterator<Scene> descendingIterator() {
        return this.history.descendingIterator();
    }
}
