/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.dataprocessing.fetchers.ModelEntityMapRecordFetcher;
import com.joelsgarage.dataprocessing.readers.ListRecordReader;
import com.joelsgarage.dataprocessing.readers.MapRecordReaderFactory;
import com.joelsgarage.model.AnnotatedAlternative;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.Membership;
import com.joelsgarage.util.TNorm;
import com.joelsgarage.util.TestData;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ScorerTest extends TestCase {
    public TestData testData;
    public RecordFetcher<ExternalKey, ModelEntity> fetcher;
    public RecordReader<ModelEntity> reader;
    public RecordReaderFactory<ModelEntity> readerFactory;
    public TNorm tNorm;
    public Scorer scorer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.testData = new TestData();
        this.fetcher = new ModelEntityMapRecordFetcher(this.testData.entities);
        this.reader =
            new ListRecordReader<ModelEntity>(new ArrayList<ModelEntity>(this.testData.entities
                .values()));
        Map<ReaderConstraint, RecordReader<ModelEntity>> readers =
            new HashMap<ReaderConstraint, RecordReader<ModelEntity>>();
        readers.put(new ReaderConstraint(Individual.class), this.reader);
        this.readerFactory = new MapRecordReaderFactory<ModelEntity>(readers);
        this.scorer = new Scorer(this.readerFactory, this.fetcher);
    }

    /** Garbage query => exception */
    public void testBadQuery() {
        String query = "blah";
        try {
            @SuppressWarnings("unused")
            ResultSet scores = this.scorer.score(query);
            fail();
        } catch (QueryException e) {
            assertTrue(true);
        }
    }

    /** Test a null query: should get all individuals */
    public void testGetAll() {
        Logger.getLogger(ScorerTest.class).info("testGetAll");
        String query = "[{" //
            + "  \"scan\": { }," //
            + "  \"score\": \"min\"," //
            + "  \"filter\":[{ }]"//
            + "}]";
        try {
            ResultSet scores = this.scorer.score(query);
            assertNotNull(scores);

            List<AnnotatedAlternative> top = new ArrayList<AnnotatedAlternative>();
            scores.sortedTop(top);
            assertEquals(14, top.size()); // how many individuals
            for (AnnotatedAlternative a : top) {
                assertEquals(1.0, a.getScore().doubleValue(), 0.001);
            }

            List<AnnotatedAlternative> bottom = new ArrayList<AnnotatedAlternative>();
            scores.sortedBottom(bottom);
            assertEquals(14, bottom.size()); // how many individuals
            for (AnnotatedAlternative a : bottom) {
                assertEquals(1.0, a.getScore().doubleValue(), 0.001);
            }
        } catch (QueryException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testScale() {
        try {
            assertEquals(0.0, Matcher.scale(Membership.newInstance(0.0), Membership.FALSE,
                Membership.TRUE).getM().doubleValue(), 0.001);
            assertEquals(0.5, Matcher.scale(Membership.newInstance(0.5), Membership.FALSE,
                Membership.TRUE).getM().doubleValue(), 0.001);
            assertEquals(1.0, Matcher.scale(Membership.newInstance(1.0), Membership.FALSE,
                Membership.TRUE).getM().doubleValue(), 0.001);

            assertEquals(0.25, Matcher.scale(Membership.newInstance(0.0),
                Membership.newInstance(0.25), Membership.TRUE).getM().doubleValue(), 0.001);
            assertEquals(0.625, Matcher.scale(Membership.newInstance(0.5),
                Membership.newInstance(0.25), Membership.TRUE).getM().doubleValue(), 0.001);
            assertEquals(1.0, Matcher.scale(Membership.newInstance(1.0),
                Membership.newInstance(0.25), Membership.TRUE).getM().doubleValue(), 0.001);

            assertEquals(0.0, Matcher.scale(Membership.newInstance(0.0), Membership.FALSE,
                Membership.newInstance(0.75)).getM().doubleValue(), 0.001);
            assertEquals(0.375, Matcher.scale(Membership.newInstance(0.5), Membership.FALSE,
                Membership.newInstance(0.75)).getM().doubleValue(), 0.001);
            assertEquals(0.75, Matcher.scale(Membership.newInstance(1.0), Membership.FALSE,
                Membership.newInstance(0.75)).getM().doubleValue(), 0.001);

            assertEquals(0.25, Matcher.scale(Membership.newInstance(0.0),
                Membership.newInstance(0.25), Membership.newInstance(0.75)).getM().doubleValue(),
                0.001);
            assertEquals(0.5, Matcher.scale(Membership.newInstance(0.5),
                Membership.newInstance(0.25), Membership.newInstance(0.75)).getM().doubleValue(),
                0.001);
            assertEquals(0.75, Matcher.scale(Membership.newInstance(1.0),
                Membership.newInstance(0.25), Membership.newInstance(0.75)).getM().doubleValue(),
                0.001);

        } catch (QueryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }

    /** Test a null query with a score: should score all individuals the same */
    public void testGetAllScored() {
        Logger.getLogger(ScorerTest.class).info("testGetAllScored");
        String query = "[{" //
            + "  \"scan\": { }," //
            + "  \"score\": \"min\"," //
            + "  \"filter\":[{ \"score\": [0.2, 0.8]}]" // should score everything 0.8
            + "}]";
        try {
            ResultSet scores = this.scorer.score(query);
            assertNotNull(scores);

            List<AnnotatedAlternative> top = new ArrayList<AnnotatedAlternative>();
            scores.sortedTop(top);
            assertEquals(14, top.size()); // how many individuals
            for (AnnotatedAlternative a : top) {
                assertEquals(0.8, a.getScore().doubleValue(), 0.001);
            }

            List<AnnotatedAlternative> bottom = new ArrayList<AnnotatedAlternative>();
            scores.sortedBottom(bottom);
            assertEquals(14, bottom.size()); // how many individuals
            for (AnnotatedAlternative a : bottom) {
                assertEquals(0.8, a.getScore().doubleValue(), 0.001);
            }
        } catch (QueryException e) {
            e.printStackTrace();
            fail();
        }
    }

    /** Verify that the filter actually filters */
    public void testFilter() {
        Logger.getLogger(ScorerTest.class).info("testFilter");
        String query = "" //
            + "[{" //
            + "  \"scan\": { }," //
            + "  \"score\": \"min\"," //
            + "  \"filter\": [" //
            + "    {\"TYPE\":\"" + this.testData.actor.makeKey().toString() + "\" }" //
            + "  ]" //
            + "}]";

        try {
            ResultSet scores = this.scorer.score(query);
            assertNotNull(scores);
            // there are three actors in TestData.

            List<AnnotatedAlternative> top = new ArrayList<AnnotatedAlternative>();
            scores.sortedTop(top);
            assertEquals(3, top.size());
            for (AnnotatedAlternative a : top) {
                assertEquals(1.0, a.getScore().doubleValue(), 0.001);
            }

            List<AnnotatedAlternative> bottom = new ArrayList<AnnotatedAlternative>();
            scores.sortedBottom(bottom);
            assertEquals(3, bottom.size());
            for (AnnotatedAlternative a : bottom) {
                assertEquals(1.0, a.getScore().doubleValue(), 0.001);
            }
        } catch (QueryException e) {
            e.printStackTrace();
            fail();
        }
    }

    /** Verify multiple filters */
    public void testMultiFilter() {
        String query = "" //
            + "[{" //
            + "  \"scan\": { }," //
            + "  \"score\": \"min\"," //
            + "  \"filter\": [" //
            + "    { }," // // null should do nothing, with "min"
            + "    {\"TYPE\":\"" + this.testData.actor.makeKey().toString() + "\" }" //
            + "  ]" //
            + "}]";

        try {
            ResultSet scores = this.scorer.score(query);
            assertNotNull(scores);
            // there are three actors in TestData.

            List<AnnotatedAlternative> top = new ArrayList<AnnotatedAlternative>();
            scores.sortedTop(top);
            assertEquals(3, top.size());
            for (AnnotatedAlternative a : top) {
                assertEquals(1.0, a.getScore().doubleValue(), 0.001);
            }

            List<AnnotatedAlternative> bottom = new ArrayList<AnnotatedAlternative>();
            scores.sortedBottom(bottom);
            assertEquals(3, bottom.size());
            for (AnnotatedAlternative a : bottom) {
                assertEquals(1.0, a.getScore().doubleValue(), 0.001);
            }
        } catch (QueryException e) {
            e.printStackTrace();
            fail();
        }
    }

    /** Verify multiple filters that actually do something */
    public void testMultiFilter2() {
        String query = "" //
            + "[{" //
            + "  \"scan\": { }," //
            + "  \"score\": \"min\"," //
            + "  \"filter\": [" //
            + "    {\"" //
            + this.testData.stringProperty.makeKey().toString() + "\": \"" //
            + this.testData.harrisonFordStringFact.getValue() + "\" }," //
            + "    {\"TYPE\":\"" + this.testData.actor.makeKey().toString() + "\" }" //
            + "  ]" //
            + "}]";

        try {
            ResultSet scores = this.scorer.score(query);
            assertNotNull(scores);
            // there should be just one result.

            List<AnnotatedAlternative> top = new ArrayList<AnnotatedAlternative>();
            scores.sortedTop(top);
            assertEquals(1, top.size());
            assertEquals(1.0, top.get(0).getScore().doubleValue(), 0.001);
            assertEquals(this.testData.harrisonFord.makeKey(), top.get(0).getIndividual().makeKey());

            List<AnnotatedAlternative> bottom = new ArrayList<AnnotatedAlternative>();
            scores.sortedBottom(bottom);
            assertEquals(1, bottom.size());
            assertEquals(1.0, bottom.get(0).getScore().doubleValue(), 0.001);
            assertEquals(this.testData.harrisonFord.makeKey(), top.get(0).getIndividual().makeKey());
        } catch (QueryException e) {
            e.printStackTrace();
            fail();
        }
    }

    /** Verify multiple filters that actually do something; should not exclude anything. */
    public void testMultiFilterScored() {
        Logger.getLogger(ScorerTest.class).info("testMultiFilterScored");
        String query = "" //
            + "[{" //
            + "  \"scan\": { }," //
            + "  \"score\": \"min\"," //
            + "  \"filter\": [" //
            + "    {\"score\": [0.2, 0.8],\"" //
            + this.testData.stringProperty.makeKey().toString() + "\": \"" //
            + this.testData.harrisonFordStringFact.getValue() + "\" }," //
            + "    {\"score\": [0.2, 0.8]," //
            + "\"TYPE\":\"" + this.testData.actor.makeKey().toString() + "\" }" //
            + "  ]" //
            + "}]";

        try {
            ResultSet scores = this.scorer.score(query);
            assertNotNull(scores);

            List<AnnotatedAlternative> top = new ArrayList<AnnotatedAlternative>();
            scores.sortedTop(top);
            assertEquals(14, top.size());
            assertEquals(0.8, top.get(0).getScore().doubleValue(), 0.001);
            assertEquals(this.testData.harrisonFord.makeKey(), top.get(0).getIndividual().makeKey());

            List<AnnotatedAlternative> bottom = new ArrayList<AnnotatedAlternative>();
            scores.sortedBottom(bottom);
            assertEquals(14, bottom.size());
            assertEquals(0.2, bottom.get(0).getScore().doubleValue(), 0.001);
            assertEquals(this.testData.harrisonFord.makeKey(), top.get(0).getIndividual().makeKey());
        } catch (QueryException e) {
            e.printStackTrace();
            fail();
        }
    }

    /** Verify that the scanlimit limits the number of scanned items. */
    public void testScanLimit() {
        Logger.getLogger(ScorerTest.class).info("testScanLimit");
        String query = "" //
            + "[{ " //
            + "  \"scan\": { \"limit\": 10 }, " //
            + "  \"score\": \"min\"," //
            + "  \"filter\": [{ }]" //
            + "}]";
        try {
            ResultSet scores = this.scorer.score(query);
            assertNotNull(scores);

            List<AnnotatedAlternative> top = new ArrayList<AnnotatedAlternative>();
            scores.sortedTop(top);
            assertEquals(10, top.size()); // how many individuals
            for (AnnotatedAlternative a : top) {
                assertEquals(1.0, a.getScore().doubleValue(), 0.001);
            }

            List<AnnotatedAlternative> bottom = new ArrayList<AnnotatedAlternative>();
            scores.sortedBottom(bottom);
            assertEquals(10, bottom.size()); // how many individuals
            for (AnnotatedAlternative a : bottom) {
                assertEquals(1.0, a.getScore().doubleValue(), 0.001);
            }
        } catch (QueryException e) {
            e.printStackTrace();
            fail();
        }
    }
}
