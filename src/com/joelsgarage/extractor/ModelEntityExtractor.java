/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.User;
import com.joelsgarage.util.ClassUtil;
import com.joelsgarage.util.DateUtil;
import com.joelsgarage.util.FatalException;

/**
 * Produces ModelEntities.
 * 
 * @author joel
 */
public abstract class ModelEntityExtractor<T> extends ProcessNode<T, ModelEntity> {
    /** Used as the creator name */
    private String shortClassName;
    private String iso8601Date;
    private ExternalKey creatorKey;

    public ModelEntityExtractor(RecordReader<T> reader, RecordWriter<ModelEntity> writer,
        int inLimit, int outLimit) throws FatalException {
        super(reader, writer, inLimit, outLimit);
        setShortClassName(ClassUtil.shortClassName(this.getClass()));
        setIso8601Date(DateUtil.formatDateToISO8601(DateUtil.now()));
        setCreatorKey(extractorUser().makeKey());
    }

    /** Output a User record corresponding to this process, in case it doesn't already exist */
    @Override
    protected void start() {
        super.start();
        try {
            output(extractorUser());
        } catch (FatalException e) {
            e.printStackTrace();
        }
    }

    /** The namespace to put the output in */
    protected abstract String namespace();

    /**
     * Make a User record corresponding to this process
     * 
     * @throws FatalException
     */
    protected User extractorUser() throws FatalException {
        return new User(getShortClassName(), namespace());
    }

    //
    //

    public String getShortClassName() {
        return this.shortClassName;
    }

    public void setShortClassName(String shortClassName) {
        this.shortClassName = shortClassName;
    }

    public String getIso8601Date() {
        return this.iso8601Date;
    }

    public void setIso8601Date(String iso8601Date) {
        this.iso8601Date = iso8601Date;
    }

    public ExternalKey getCreatorKey() {
        return this.creatorKey;
    }

    public void setCreatorKey(ExternalKey creatorKey) {
        this.creatorKey = creatorKey;
    }
}
