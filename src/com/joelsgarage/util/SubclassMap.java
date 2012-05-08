/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for transitive closure of the subclass map.
 * 
 * Does the best it can with cycles. Shouldn't get any cycles as input anyway.
 * 
 * @author joel
 * 
 */
public class SubclassMap<T> {
    private Map<T, Set<T>> map = new HashMap<T, Set<T>>();

    /**
     * Expand the map to include its transitive closure, i.e. add a direct path for every indirect
     * path.
     */
    public void transitiveClosure() {
        for (Map.Entry<T, Set<T>> entry : this.map.entrySet()) {
            T parent = entry.getKey();
            Set<T> children = new HashSet<T>();
            getChildren(parent, children);
            for (T child : children) {
                put(parent, child);
            }
        }
    }

    /** Recursively get all the children of the specified class */
    protected void getChildren(final T forKey, Set<T> children) {
        if (children == null)
            return;
        Set<T> newChildren = this.map.get(forKey);
        if (newChildren == null)
            return;
        for (T newChild : newChildren) {
            if (children.contains(newChild))
                continue; // cycle
            children.add(newChild);
            getChildren(newChild, children);
        }
    }

    /** Add the key/value pair to the underlying map */
    public void put(T key, T value) {
        Set<T> siblings = this.map.get(key);
        if (siblings == null) {
            siblings = new HashSet<T>();
            this.map.put(key, siblings);
        }
        siblings.add(value);
    }

    public Set<T> get(T key) {
        return this.map.get(key);
    }

}
