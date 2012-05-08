/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.query;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.logic.CompromiseOperator;
import com.joelsgarage.model.AnnotatedAlternative;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.Membership;
import com.joelsgarage.util.Min;
import com.joelsgarage.util.Norm;

/**
 * Scores every entity from the reader. Discards entities with zero score.
 * 
 * @author joel
 */
public class Scorer {
    private static final String MIN = "min"; //$NON-NLS-1$
    private static final String MYCIN = "mycin"; //$NON-NLS-1$
    /** Controls the size of the ResultSet priority queue */
    private static final int DEFAULT_RESULT_SET_SIZE = 20;
    /** Aborts scanning after this many, if positive */
    private static final int DEFAULT_SCAN_LIMIT = 1000;
    private RecordReaderFactory<ModelEntity> readerFactory;
    /** The set of things to score */
    private RecordReader<ModelEntity> reader;
    /** Access to the rest of the graph */
    private RecordFetcher<ExternalKey, ModelEntity> fetcher;

    private final Map<String, Norm> aggregators = new HashMap<String, Norm>() {
        private static final long serialVersionUID = 1L;
        {
            put(MYCIN, new CompromiseOperator());
            put(MIN, new Min());
        }
    };

    // TODO: take a record reader factory instead of a record reader, so that the scan term in the
    // query can specify the constraint for the reader

    public Scorer(RecordReaderFactory<ModelEntity> readerFactory,
        RecordFetcher<ExternalKey, ModelEntity> fetcher) {
        setReaderFactory(readerFactory);
        setFetcher(fetcher);
    }

    /**
     * Use the "scan" query to produce an appropriate reader.
     * 
     * @param query
     *            the query value of the "scan" property
     * @return
     * @throws QueryException
     */
    protected RecordReader<ModelEntity> makeReader(String query) throws QueryException {
        if (query == null)
            return null;
        try {
            JSONObject jo = new JSONObject(query);
            Logger.getLogger(Scorer.class).info("scan query: " + jo.toString(2)); //$NON-NLS-1$
        } catch (JSONException e) {
            throw new QueryException("bad query: " + query); //$NON-NLS-1$
        }
        // for now just return a simple reader.
        // TODO: actually read the scan property.
        // TODO: do something with the "score" property of "scan" (maybe)
        return getReaderFactory().newInstance(new ReaderConstraint(Individual.class));
    }

    /**
     * Do the scoring work
     * 
     * @param query
     *            a JSON query, which should be an array, i.e. the result set is a *set*. :-) Though
     *            it is an array with just one element.
     * @return a ResultSet.
     * @throws QueryException
     *             if the query is crap.
     */
    public ResultSet score(String query) throws QueryException {
        try {
            // the query at this stage should be an array.

            JSONArray ja = new JSONArray(query);
            Object o = ja.get(0);
            if (!(o instanceof JSONObject)) {
                Logger.getLogger(Matcher.class).error("Bad query: " + query); //$NON-NLS-1$
                throw new QueryException("Bad query: " + query); //$NON-NLS-1$
            }

            JSONObject jo = (JSONObject) o;

            int resultSetSize = jo.optInt(PropertyRestriction.LIMIT, DEFAULT_RESULT_SET_SIZE);

            JSONObject scanQuery = jo.optJSONObject(PropertyRestriction.SCAN);
            if (scanQuery == null)
                throw new QueryException("no scan object in query: " + query); //$NON-NLS-1$
            int scanLimit = scanQuery.optInt(PropertyRestriction.LIMIT, DEFAULT_SCAN_LIMIT);

            JSONArray filterArray = jo.optJSONArray(PropertyRestriction.FILTER);
            if (filterArray == null)
                throw new QueryException("no filter array in query: " + query); //$NON-NLS-1$
            String filterQuery = filterArray.toString(2);

            String aggregator = jo.optString(PropertyRestriction.SCORE);
            if (aggregator == null || aggregator.isEmpty())
                throw new QueryException("no score property in query: " + query); //$NON-NLS-1$
            Norm norm = this.aggregators.get(aggregator);
            if (norm == null)
                throw new QueryException("unrecognized aggregator in query: " + query); //$NON-NLS-1$

            ResultSet result = new ResultSet(resultSetSize);

            Matcher matcher = new Matcher(getFetcher(), norm);

            // *****************************
            // *****************************
            // *****************************

            // *****************************
            //
            // FIXME: this scans INDIVIDUALS. But I don't really need to fetch the individual, I
            // just need
            // its key, and I imagine the query will be a class member thing.

            setReader(makeReader(scanQuery.toString(2)));

            getReader().open();
            ModelEntity entity;
            int scanCount = 0;
            while ((entity = getReader().read()) != null) {
                // weird types don't count toward the scan limit
                if (!(entity instanceof Individual)) {
                    Logger.getLogger(Scorer.class).error(
                        "weird type: " + entity.getClass().getName()); //$NON-NLS-1$
                    continue;
                }

                ++scanCount;
                if (scanLimit > 0 && scanCount > scanLimit)
                    break;

                Individual individual = (Individual) entity;
                Membership m =
                    matcher.matchArray(filterQuery, individual.makeKey(), individual.getNamespace());
                if (m.isFalse())
                    continue;
                // TODO: the actual "Score" is a transformed version of membership.
                // TODO: so do the transform.
                result.add(new AnnotatedAlternative(individual, m.getM()));
            }
            getReader().close();
            return result;
        } catch (InitializationException e) {
            // TODO: throw a different exception here
            throw new QueryException(e);
        } catch (JSONException e1) {
            Logger.getLogger(Matcher.class).error("Bad query: " + query); //$NON-NLS-1$
            throw new QueryException(e1);
        }
    }

    //

    public RecordReader<ModelEntity> getReader() {
        return this.reader;
    }

    public void setReader(RecordReader<ModelEntity> reader) {
        this.reader = reader;
    }

    public RecordFetcher<ExternalKey, ModelEntity> getFetcher() {
        return this.fetcher;
    }

    public void setFetcher(RecordFetcher<ExternalKey, ModelEntity> fetcher) {
        this.fetcher = fetcher;
    }

    public RecordReaderFactory<ModelEntity> getReaderFactory() {
        return this.readerFactory;
    }

    public void setReaderFactory(RecordReaderFactory<ModelEntity> readerFactory) {
        this.readerFactory = readerFactory;
    }
}
