/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Log;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.Subclass;
import com.joelsgarage.model.WriteEvent;
import com.joelsgarage.model.WordSense;
import com.joelsgarage.util.FatalException;

/**
 * Transforms a the Google product taxonomy file into ModelEntities.
 * 
 * Use TranslatingXMLWriter to write the data.
 * 
 * Sample input:
 * 
 * <pre>
 * Animals
 * Animals &gt; Pet Supplies
 * Animals &gt; Pet Supplies &gt; Amphibian Supplies
 * </pre>
 * 
 * @author joel
 */
public class GoogleExtractor extends ModelEntityExtractor<String> {
    private static final String EN_US = "en-US"; //$NON-NLS-1$
    private static final String SEPARATOR = ">"; //$NON-NLS-1$
    /** The namespace everything goes in. g0 = 20081027 */
    private static final String NAMESPACE = "g0"; //$NON-NLS-1$
    /**
     * Record records we've written to avoid writing them twice. Could get large but isn't for this
     * datasource. Consider puling it up.
     */
    private Set<ExternalKey> written = new TreeSet<ExternalKey>();

    private WriteEvent update = null;

    public GoogleExtractor(RecordReader<String> reader, RecordWriter<ModelEntity> writer,
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

    @Override
    protected String namespace() {
        return NAMESPACE;
    }

    @Override
    protected boolean handleRecord(String record) {
        try {
            if (record == null)
                return true;
            String[] rawFields = record.split(SEPARATOR);
            if (rawFields.length < 1) {
                Logger.getLogger(GoogleExtractor.class).warn(
                    "short record: " + String.valueOf(record)); //$NON-NLS-1$
                return true;
            }
            String[] fields = new String[rawFields.length];
            for (int index = 0; index < rawFields.length; ++index) {
                fields[index] = rawFields[index].trim();
            }

            com.joelsgarage.model.Class first = null;
            com.joelsgarage.model.Class second = null;

            if (fields.length > 0) {
                String name = makeName(fields, 0);
                first = new com.joelsgarage.model.Class(name, namespace());
                if (getWritten().add(first.makeKey())) {
                    output(first);
                    output(new Log(first.makeKey(), getUpdate().makeKey(), namespace()));
                    WordSense firstWordSense =
                        new WordSense(EN_US, name, true, first.makeKey(), namespace());
                    output(firstWordSense);
                    output(new Log(firstWordSense.makeKey(), getUpdate().makeKey(), namespace()));
                }
            }

            // we don't really need to do this because all the parent nodes
            // have their own rows. but we do it anyway, just in case. ;-)
            for (int index = 0; index < fields.length; ++index) {
                if ((index + 1) < fields.length) {
                    String name = makeName(fields, index + 1);
                    second = new com.joelsgarage.model.Class(name, namespace());
                    if (getWritten().add(second.makeKey())) {
                        output(second);
                        output(new Log(second.makeKey(), getUpdate().makeKey(), namespace()));
                        WordSense secondWordSense =
                            new WordSense(EN_US, name, true, second.makeKey(), namespace());
                        output(secondWordSense);
                        output(new Log(secondWordSense.makeKey(), getUpdate().makeKey(),
                            namespace()));
                    }
                    Subclass subclass = makeSubclass(first, second);
                    if (getWritten().add(subclass.makeKey())) {
                        output(subclass);
                        output(new Log(subclass.makeKey(), getUpdate().makeKey(), namespace()));
                    }
                }
                first = second;
            }
        } catch (FatalException e) {
            e.printStackTrace();
        }
        return true;

    }

    /**
     * Make a class record for the index'th node in the field list
     * 
     * @throws FatalException
     */
    protected com.joelsgarage.model.Class makeClass(String[] fields, int index)
        throws FatalException {
        String name = makeName(fields, index);
        return new com.joelsgarage.model.Class(name, namespace());
    }

    /**
     * Return a subclass message with first as the object and second as the subject
     * 
     * @throws FatalException
     */
    protected Subclass makeSubclass(com.joelsgarage.model.Class first,
        com.joelsgarage.model.Class second) throws FatalException {
        return new Subclass(second.makeKey(), first.makeKey(), namespace());
    }

    /**
     * Make a name for the index'th node of the field list. Initially this was the "long" name (i.e.
     * delimited path), but for this source the "short" (leaf) name is unique, and more natural, so
     * we just use that.
     */
    protected String makeName(String[] fields, int index) {
        // String name = new String();
        // for (int component = 0; component <= index; ++component) {
        // if (component > 0) {
        // name += " " + SEPARATOR + " "; //$NON-NLS-1$//$NON-NLS-2$
        // }
        // name += fields[component];
        // }
        // return name;
        return fields[index];
    }

    public Set<ExternalKey> getWritten() {
        return this.written;
    }

    public void setWritten(Set<ExternalKey> written) {
        this.written = written;
    }

    public WriteEvent getUpdate() {
        return this.update;
    }

    public void setUpdate(WriteEvent update) {
        this.update = update;
    }
}
