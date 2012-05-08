/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.WriteEvent;
import com.joelsgarage.util.FatalException;

/**
 * Transforms a the Freebase tuple file into ModelEntities.
 * 
 * Note: the input is assumed to be sorted by its first field.
 * 
 * Sample input:
 * 
 * <pre>
 * /guid/9202a8c04000641f8000000003cc0081  /freebase/type_hints/included_types     /people/person 
 * /guid/9202a8c04000641f80000000042b4caa  /type/type/properties   /guid/9202a8c04000641f80000000042b4d03  
 * /guid/9202a8c04000641f80000000045c667d  /type/object/type       /business/company  
 * /guid/9202a8c04000641f8000000005059a93  /type/type/domain       /law 
 * /guid/9202a8c04000641f800000000515aa65  /type/object/name       /lang/en        3b-Hydroxy-5-cholenoic acid
 * /guid/9202a8c04000641f80000000051618c5  /chemistry/chemical_compound/average_molar_mass         653.11548
 * /guid/9202a8c04000641f8000000005167782  /education/education/major_field_of_study       /guid/9202a8c04000641f800000000000ce36
 * /guid/9202a8c04000641f80000000052106d6  /biology/gene_ontology_group/narrower_term      /lang/en        P2Y receptor
 * /guid/9202a8c04000641f8000000008c75dd4  /film/performance/film  /guid/9202a8c04000641f8000000008033bf7  
 * /guid/9202a8c04000641f8000000008c75dd4  /type/object/type       /film/performance       
 * /guid/9202a8c04000641f8000000008c75dd4  /film/performance/actor /guid/9202a8c04000641f800000000018b9c9 
 * </pre>
 * 
 * @author joel
 * 
 */
public class FreebaseExtractor extends ModelEntityExtractor<String> {
    /** Idle thread count */
    private static final int MIN_THREADS = 8;
    /** I should be able to peg the CPU with a small number here. */
    private static final int MAX_THREADS = 32;
    /**
     * Each bundle is quite small (1k?), and there are big chunks of input with nothing happening.
     * So make a really big queue.
     */
    private static final int REDUCER_QUEUE = 300000;
    /** Input field separator */
    private static final String SEPARATOR = "\t"; //$NON-NLS-1$
    /** The namespace everything goes in. f0 = 20081017 */
    private static final String NAMESPACE = "f0"; //$NON-NLS-1$
    /** Ignore excess rows in a bundle */
    private static final int ROW_LIMIT = 1000;
    /** Key of rows in the current bundle */
    private String currentKey = new String();
    /** A bundle of rows with the same src */
    private List<String[]> rows = new ArrayList<String[]>();

    private WriteEvent update = null;

    private ThreadPoolExecutor executor =
        new ThreadPoolExecutor(MIN_THREADS, MAX_THREADS, 1, TimeUnit.HOURS,
            new ArrayBlockingQueue<Runnable>(REDUCER_QUEUE),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @SuppressWarnings("nls")
    private static final Set<String> ignoredProperties = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
        {
            // because it is too numerous and extranous; we use /type/object/type
            add("/type/type/instance");
            // common and useless
            add("/type/permission/controls");
            // redundant with /type/property/expected_type
            add("/type/type/expected_by");
            // don't care about creator or timestamp
            add("/type/object/creator");
            add("/type/object/timestamp");
            // most of type profile is irrelevant
            add("/freebase/type_profile/instance_count");
            add("/freebase/type_profile/published");
            add("/freebase/type_profile/published");
        }
    };

    public FreebaseExtractor(RecordReader<String> reader, RecordWriter<ModelEntity> writer,
        int inLimit, int outLimit) throws FatalException {
        super(reader, writer, inLimit, outLimit);
    }

    @Override
    protected void start() {
        super.start();
        try {
            setUpdate(new WriteEvent(getIso8601Date(), getCreatorKey(), namespace()));
            output(getUpdate());
        } catch (FatalException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads records, collects records with the same key into a bundle. At the end of the bundle,
     * calls flushRows(). Assumes input is clustered (not necessarily sorted) by key.
     */
    @Override
    protected boolean handleRecord(String record) {
        if (record == null)
            return true;
        String[] rawFields = record.split(SEPARATOR);
        if (rawFields.length < 3) {
            Logger.getLogger(FreebaseExtractor.class).debug(
                "short record: " + String.valueOf(record)); //$NON-NLS-1$
            return true;
        }

        if (!(getCurrentKey().equals(rawFields[0]))) {
            setCurrentKey(rawFields[0]);
            flushRows();
        }

        String property = rawFields[1].trim();
        Logger.getLogger(FreebaseExtractor.class).debug("property: " + property); //$NON-NLS-1$

        if (getRows().size() >= ROW_LIMIT) {
            Logger.getLogger(FreebaseExtractor.class).debug("row limit exceeded"); //$NON-NLS-1$
            return true;
        }
        if (ignoredProperties.contains(property)) {
            Logger.getLogger(FreebaseExtractor.class).debug("ignored property"); //$NON-NLS-1$
            return true;
        }
        if (property.indexOf(':') != -1) {
            Logger.getLogger(FreebaseExtractor.class).debug("ignore colons"); //$NON-NLS-1$
            return true;
        }

        getRows().add(rawFields);
        return true;
    }

    @Override
    protected void doProgress() {
        super.doProgress();
        Logger.getLogger(FreebaseExtractor.class).info("active: " + this.executor.getActiveCount()); //$NON-NLS-1$
        Logger.getLogger(FreebaseExtractor.class).info("queue: " + this.executor.getQueue().size()); //$NON-NLS-1$
    }

    @Override
    protected void done() {
        Logger.getLogger(FreebaseExtractor.class).info("done"); //$NON-NLS-1$
        flushRows();
        // waits for the queued tasks to complete, before returning to allow the streams to be
        // closed.
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!this.executor.isTerminated()) {
            Logger.getLogger(FreebaseExtractor.class).error("termination error"); //$NON-NLS-1$
        }
        super.done();
    }

    /** synchronized output allows multiple "reducers" to output safely */
    @Override
    public synchronized void output(ModelEntity payload) {
        super.output(payload);
    }

    /**
     * process and clear the rows
     */
    protected void flushRows() {
        FreebaseExtractorReducer reducer = new FreebaseExtractorReducer(this, getRows());
        this.executor.execute(reducer);
        // don't "clear" because the reducer needs the list.
        setRows(new ArrayList<String[]>());
    }

    @Override
    protected String namespace() {
        return NAMESPACE;
    }

    //
    //

    public String getCurrentKey() {
        return this.currentKey;
    }

    public void setCurrentKey(String currentKey) {
        this.currentKey = currentKey;
    }

    public List<String[]> getRows() {
        return this.rows;
    }

    public void setRows(List<String[]> rows) {
        this.rows = rows;
    }

    public WriteEvent getUpdate() {
        return this.update;
    }

    public void setUpdate(WriteEvent update) {
        this.update = update;
    }
}
