/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.query;

import java.util.HashMap;
import java.util.Map;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.Rating;

/**
 * A user preference is a query. For a given decision class, there may be MANY user preferences in
 * play. This class finds them all by walking over the space of properties.
 * 
 * So it needs model access of some kind, a fetcher or fetcher factory?
 * 
 * @author joel
 */
public class QueryBuilder {
    private RecordReaderFactory<ModelEntity> readerFactory;

    public QueryBuilder(RecordReaderFactory<ModelEntity> readerFactory) {
        setReaderFactory(readerFactory);
    }

    // the query for a single stakeholder. the whole decision query is a combination of the queries
    // for each stakeholder
    public String buildQuery(ExternalKey stakeholderKey) throws QueryException {
        if (stakeholderKey == null)
            throw new QueryException("null stakeholderkey in buildQuery"); //$NON-NLS-1$
        Map<String, Object> constraints = new HashMap<String, Object>();
        constraints.put(Rating.STAKEHOLDER, stakeholderKey);
        ReaderConstraint ratingConstraint = new ReaderConstraint(Rating.class, constraints);
        RecordReader<ModelEntity> ratingReader = getReaderFactory().newInstance(ratingConstraint);
        ModelEntity entity = ratingReader.read();

        return new String();
    }

    //
    //

    public RecordReaderFactory<ModelEntity> getReaderFactory() {
        return this.readerFactory;
    }

    public void setReaderFactory(RecordReaderFactory<ModelEntity> readerFactory) {
        this.readerFactory = readerFactory;
    }

}
