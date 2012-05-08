/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.gardening;

import java.util.List;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.StringFact;
import com.joelsgarage.util.FatalException;

/**
 * For each stringfact that appears to contain a delimited value, split the value, and produce a set
 * of new stringfacts, one for each split, and also provenance.
 * 
 * Example input:
 * 
 * <pre>
 * Native Status: L48 (N), CAN (N)
 * </pre>
 * 
 * Example output:
 * 
 * <pre>
 * Native Status: L48 (N)
 * Native Status: CAN (N)
 * </pre>
 * 
 * @author joel
 * 
 */
public class FactValueSplitGardener extends ProcessNode<StringFact, ModelEntity> implements
    Gardener {
    private FactValueSplitter splitter;

    public FactValueSplitGardener(RecordReader<StringFact> reader,
        RecordWriter<ModelEntity> writer, int inLimit, int outLimit) {
        super(reader, writer, inLimit, outLimit);
        setProgressCount(1000);
    }

    @Override
    public void start() {
        super.start();
        try {
            this.splitter = new FactValueSplitter();
        } catch (FatalException e) {
            e.printStackTrace();
        }
    }

    /**
     * This operation is actually an aggregate. You want to find properties whose corresponding fact
     * values tend to be short and full of delimiters. One value by itself may not deserve
     * splitting.
     * 
     * So what I really want is to group the inputs by property, and load the whole property's worth
     * at once. I could do that with a reader where T is List<T>, with some kind of group-by
     * parameter in the constructor.
     * 
     * But for now just use a pattern, and for now, the pattern is just a comma.
     * 
     * @see ProcessNode#handleRecord(Object)
     */
    @Override
    protected boolean handleRecord(StringFact record) {
        try {
            List<ModelEntity> newEntities = this.splitter.convert(record);

            if (newEntities == null)
                return true;
            for (ModelEntity newEntity : newEntities) {
                output(newEntity);
            }
            return true;
        } catch (FatalException e) {
            e.printStackTrace();
            return false;
        }
    }
}
