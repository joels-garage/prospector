/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.util.FatalException;

/**
 * A process node that contains a set of lookup tables, for joins in which one of the join tables is
 * small enough to hold in memory. So, for example, scanning facts, while looking up the
 * corresponding properties.
 * 
 * See SideInputProcessNode, which uses cached fetching for the lookups, rather than preloaded
 * tables.
 * 
 * @author joel
 * 
 * @param <K>
 *            the key type for lookups
 * @param <T>
 *            the input reader type, i.e. supertype of all input types
 * @param <U>
 *            the record type to write
 */
public abstract class LookupProcessNode<K, T, U> extends MultiReaderProcessNode<T, U> {
    /** The two constraints used for input */
    private List<ReaderConstraint> constraintList;
    /** The lookup table */
    private Map<ReaderConstraint, Map<K, T>> lookupMap;

    /** How many records we have read */
    private int inCount = 0;

    /**
     * @param readerFactory
     *            where lookup readers come from
     * @param writer
     *            where output goes
     * @param inLimit
     *            how many input records to look at in the main scan
     * @param outLimit
     *            how many output records to produce
     */
    public LookupProcessNode(RecordReaderFactory<T> readerFactory, RecordWriter<U> writer,
        int inLimit, int outLimit) {
        super(readerFactory, writer, inLimit, outLimit);
        setConstraintList(new ArrayList<ReaderConstraint>());
        getConstraintList().add(getLookupConstraint());
        // Adding this constraint to the list means we get a reader for it, but the lookup
        // map populator excludes the main reader, so we don't slurp it.
        getConstraintList().add(getMainConstraint());
    }

    public abstract ReaderConstraint getLookupConstraint();

    public abstract ReaderConstraint getMainConstraint();

    /**
     * Fetch the iterator for the constraint list.
     */
    @Override
    public Iterator<ReaderConstraint> getConstraints() {
        return getConstraintList().iterator();
    }

    public Iterator<T> getValues(ReaderConstraint constraint) {
        Map<K, T> map = this.lookupMap.get(constraint);
        if (map == null)
            return null;
        Logger.getLogger(LookupProcessNode.class).info(
            "Get " + map.size() + " values for constraint " //$NON-NLS-1$//$NON-NLS-2$
                + constraint.getClassConstraint().getName());
        return map.values().iterator();
    }

    /**
     * This is so that subclasses can pass the map references to their contained implementation
     * classes, instead of passing this whole thing.
     */
    protected Map<K, T> getMap(ReaderConstraint constraint) {
        return this.lookupMap.get(constraint);
    }

    /**
     * Rip through all the lookup readers to populate the lookup maps.
     * 
     * Note this only works if it "all" fits, but it should work almost as well if only "most" of it
     * fit.
     * 
     * TODO: don't load the whole thing, instead, cache random lookups.
     * 
     * Ah actually I need both. To create the patterns I need a comprehensive scan. To get the
     * property I can do a cached lookup.
     * 
     * Bah.
     * 
     * Anyway for now, just load all the lookups. Note that all the value types are the same, i.e.
     * the common supertype. That's maybe kinda lame, but it seems like a big pain to do
     * differently.
     * 
     * Populate the constraint list before calling start().
     * 
     * @throws FatalException
     *             if we shouldn't proceed
     * 
     * @see com.joelsgarage.dataprocessing.nodes.MultiReaderProcessNode#start()
     */
    @Override
    protected void start() throws FatalException {
        // Populate the reader map as specified by getConstraints().
        super.start();

        setLookupMap(new HashMap<ReaderConstraint, Map<K, T>>());

        Iterator<ReaderConstraint> iter = getConstraints();
        while (iter.hasNext()) {
            ReaderConstraint constraint = iter.next();
            // Don't populate any map twice
            if (getLookupMap().containsKey(constraint))
                continue;

            // Don't slurp the main table.
            if (constraint.equals(getMainConstraint()))
                continue;

            Map<K, T> sideTable = new HashMap<K, T>();
            RecordReader<T> reader = getReader(constraint);
            if (reader == null)
                continue;
            try {
                reader.open();
            } catch (InitializationException ex) {
                ex.printStackTrace();
                return;
            }
            T lookupRecord = null;
            while ((lookupRecord = reader.read()) != null) {
                Logger.getLogger(LookupProcessNode.class).info(
                    "Working on record " + lookupRecord.toString()); //$NON-NLS-1$
                K lookupKey = extractLookupKey(lookupRecord);
                Logger.getLogger(LookupProcessNode.class).info("put key " + lookupKey.toString()); //$NON-NLS-1$
                sideTable.put(lookupKey, lookupRecord);
            }
            Logger
                .getLogger(LookupProcessNode.class)
                .info(
                    "Type: " + constraint.getClassConstraint().getName() + " records: " + sideTable.size());//$NON-NLS-1$ //$NON-NLS-2$
            getLookupMap().put(constraint, sideTable);
        }
    }

    /** Extract the primary key from the lookup record */
    protected abstract K extractLookupKey(T record);

    /** Extract the foreign key from the main record */
    protected abstract K extractForeignKey(T record);

    /** Find the specified key, or null if nonexistent */
    protected T get(K key) {
        return get(getLookupConstraint(), key);
    }

    protected T get(ReaderConstraint constraint, K key) {
        Map<K, T> sideTable = getLookupMap().get(constraint);
        if (sideTable == null)
            return null;
        return sideTable.get(key);
    }

    public void addConstraint(ReaderConstraint constraint) {
        this.constraintList.add(constraint);
    }

    protected List<ReaderConstraint> getConstraintList() {
        return this.constraintList;
    }

    protected void setConstraintList(List<ReaderConstraint> constraintList) {
        this.constraintList = constraintList;
    }

    public int getInCount() {
        return this.inCount;
    }

    public void setInCount(int inCount) {
        this.inCount = inCount;
    }

    public Map<ReaderConstraint, Map<K, T>> getLookupMap() {
        return this.lookupMap;
    }

    public void setLookupMap(Map<ReaderConstraint, Map<K, T>> lookupMap) {
        this.lookupMap = lookupMap;
    }

}
