/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.joelsgarage.model.ModelEntity;

/**
 * The general case of reading data involves multiple streams, e.g. one big table and some lookup
 * tables.
 * 
 * The process node then gets a map of readers, with this class as the key.
 * 
 * It can specify which readers it wants, so the main() code would be something like this:
 * 
 * <pre>
 * RecordWriter&lt;T&gt; writer = new RecordWriter&lt;T&gt;();
 * MyProcessNode node = new MyProcessNode(writer);
 * Iterator&lt;ReaderConstraint&gt; constraints = node.getConstraints();
 * while (constraints.hasNext()) {
 *     ReaderConstraint constraint = constraints.next();
 *     node.addReader(constraint, ReaderFactory.getInstance(constraint));
 * }
 * node.run();
 * </pre>
 * 
 * @author joel
 * 
 */
public class ReaderConstraint {
    /** The class to read. This further constrains the reader class, i.e. it must be a subclass. */
    private final Class<? extends ModelEntity> classConstraint;
    /** Field constraints on the class, fieldname, constraint (e.g. string or key object) <K, V> */
    private final Map<String, Object> constraints;

    /**
     * @param classConstraint
     *            the class of entity to scan
     */
    public ReaderConstraint(Class<? extends ModelEntity> classConstraint,
        Map<String, Object> constraints) {
        super();
        this.classConstraint = classConstraint;
        this.constraints = constraints;
    }

    public ReaderConstraint(Class<? extends ModelEntity> classConstraint) {
        this(classConstraint, null);
    }

    /** Hash the class name */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result
                + ((getClassConstraint().getName() == null) ? 0 : getClassConstraint().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ReaderConstraint other = (ReaderConstraint) obj;
        return classEquals(other) && mapEquals(other);
    }

    protected boolean classEquals(ReaderConstraint other) {
        if (getClassConstraint() == null && other.getClassConstraint() == null)
            return true;
        if (getClassConstraint() == null || other.getClassConstraint() == null)
            return false;
        return getClassConstraint().equals(other.getClassConstraint());
    }

    protected boolean mapEquals(ReaderConstraint other) {
        if (getConstraints() == null && other.getConstraints() == null)
            return true;
        if (getConstraints() == null || other.getConstraints() == null)
            return false;
        Set<Map.Entry<String, Object>> entries = getConstraints().entrySet();
        Set<Map.Entry<String, Object>> otherEntries = other.getConstraints().entrySet();
        if (entries.size() != otherEntries.size())
            return false;
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        Iterator<Map.Entry<String, Object>> otherIterator = otherEntries.iterator();
        Map.Entry<String, Object> item, otherItem;
        while (iterator.hasNext() && otherIterator.hasNext()) {
            item = iterator.next();
            otherItem = otherIterator.next();
            if (!(item.getKey().equals(otherItem.getKey())))
                return false;
            if (!(item.getValue().equals(otherItem.getValue())))
                return false;
        }
        return true;
    }

    /** The class of entity to scan */
    public Class<? extends ModelEntity> getClassConstraint() {
        return this.classConstraint;
    }

    public Map<String, Object> getConstraints() {
        return this.constraints;
    }
}