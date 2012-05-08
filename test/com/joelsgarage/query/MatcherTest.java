/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.query;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.fetchers.ModelEntityMapRecordFetcher;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.Membership;
import com.joelsgarage.util.ProductTNorm;
import com.joelsgarage.util.TNorm;
import com.joelsgarage.util.TestData;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class MatcherTest extends TestCase {
    public TestData testData;
    public RecordFetcher<ExternalKey, ModelEntity> fetcher;
    public TNorm tNorm;
    public Matcher matcher;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.testData = new TestData();
        this.fetcher = new ModelEntityMapRecordFetcher(this.testData.entities);
        this.tNorm = new ProductTNorm();
        this.matcher = new Matcher(this.fetcher, this.tNorm);
    }

    /** query for matcher must be an object, not an array */
    public void testBadQuery() {
        // this is an array and should fail.
        String query = "[{ }]";
        try {
            @SuppressWarnings("unused")
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);
            fail();
        } catch (QueryException e) {
            assertTrue(true);
        }
    }

    /** completely crap query */
    public void testBadQuery2() {
        // this is just crap and should fail.
        String query = "crap";

        try {
            @SuppressWarnings("unused")
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);
            fail();
        } catch (QueryException e) {
            assertTrue(true);
        }
    }

    /** Verify class_member restriction */
    public void testSimple() {
        String query = "{" //
            + " \"TYPE\":\"" + this.testData.actor.makeKey().toString() + "\"" //
            + "}";

        try {
            Logger.getLogger(MatcherTest.class).info("query: " + query);
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);

            assertNotNull(m);
            assertTrue(m.isTrue());
            assertEquals(1.0, m.getM().doubleValue(), 0.001);
        } catch (QueryException e) {
            fail();
        }
    }

    /** TODO: implement this */
    public void testWithName() {
        String query = "{" //
            + " \"/classname/propertyname\":\"" + this.testData.actor.makeKey().toString() + "\"" //
            + "}";

        try {
            Logger.getLogger(MatcherTest.class).info("query: " + query);
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);

            // should not work yet.

            assertNotNull(m);
            assertTrue(m.isTrue());
            assertEquals(1.0, m.getM().doubleValue(), 0.001);
        } catch (QueryException e) {
            fail("THIS WILL FAIL UNTIL CLASSNAME/PROPERTYNAME IS IMPLEMENTED");
        }
    }

    /** completely bogus property */
    public void testWithBadKey() {
        String query = "{" //
            + " \"i am bogus\":\"" + this.testData.actor.makeKey().toString() + "\"" //
            + "}";

        try {
            Logger.getLogger(MatcherTest.class).info("query: " + query);
            @SuppressWarnings("unused")
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);
            fail();
        } catch (QueryException e) {
            Logger.getLogger(MatcherTest.class).info("exception: " + e.getMessage());
            assertTrue(true);
        }
    }

    /** must be either string_property, quantity_property or individual_property */
    public void testUnsupportedProperty() {
        String query = "{" //
            + " \"individual/abcabcabcabc\":\"" + this.testData.actor.makeKey().toString() + "\"" //
            + "}";
        try {
            Logger.getLogger(MatcherTest.class).info("query: " + query);
            @SuppressWarnings("unused")
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);
            fail();
        } catch (QueryException e) {
            Logger.getLogger(MatcherTest.class).info("exception: " + e.getMessage());
            assertTrue(true);
        }
    }

    /** Can't reverse a string property */
    public void testReverseStringProperty() {
        String query =
            "{" //
                + " \"!string_property/abcabcabcabc\":\""
                + this.testData.actor.makeKey().toString() + "\"" //
                + "}";
        try {
            Logger.getLogger(MatcherTest.class).info("query: " + query);
            @SuppressWarnings("unused")
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);
            fail();
        } catch (QueryException e) {
            Logger.getLogger(MatcherTest.class).info("exception: " + e.getMessage());
            assertTrue(true);
        }
    }

    /** Can't reverse a quantity property */
    public void testReverseQuantityProperty() {
        String query =
            "{" //
                + " \"!quantity_property/abcabcabcabc\":\""
                + this.testData.actor.makeKey().toString() + "\"" //
                + "}";
        try {
            Logger.getLogger(MatcherTest.class).info("query: " + query);
            @SuppressWarnings("unused")
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);
            fail();
        } catch (QueryException e) {
            Logger.getLogger(MatcherTest.class).info("exception: " + e.getMessage());
            assertTrue(true);
        }
    }

    /** can't use an operator with a string property */
    public void testOperatorStringProperty() {
        String query =
            "{" //
                + " \"string_property/abcabcabcabc@@\":\""
                + this.testData.actor.makeKey().toString() + "\"" //
                + "}";
        try {
            Logger.getLogger(MatcherTest.class).info("query: " + query);
            @SuppressWarnings("unused")
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);
            fail();
        } catch (QueryException e) {
            Logger.getLogger(MatcherTest.class).info("exception: " + e.getMessage());
            assertTrue(true);
        }
    }

    /** Can't use an operator with an individual property */
    public void testOperatorIndividualProperty() {
        String query =
            "{" //
                + " \"individual_property/abcabcabcabc@@\":\""
                + this.testData.actor.makeKey().toString() + "\"" //
                + "}";
        try {
            Logger.getLogger(MatcherTest.class).info("query: " + query);
            @SuppressWarnings("unused")
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);
            fail();
        } catch (QueryException e) {
            Logger.getLogger(MatcherTest.class).info("exception: " + e.getMessage());
            assertTrue(true);
        }
    }

    /** This extant stringfact should yield TRUE */
    public void testRealStringProperty() {
        String query =
            "{" //
                + " \"" + this.testData.stringProperty.makeKey().toString()
                + "\":\""
                + this.testData.harrisonFordStringFact.getValue() + "\"" //
                + "}";
        try {
            Logger.getLogger(MatcherTest.class).info("query: " + query);
            @SuppressWarnings("unused")
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);
            assertNotNull(m);
            assertTrue(m.isTrue());
            assertEquals(1.0, m.getM().doubleValue(), 0.001);
        } catch (QueryException e) {
            Logger.getLogger(MatcherTest.class).info("exception: " + e.getMessage());
            fail();
        }
    }

    /** Thie value does not exist so we yield FALSE */
    public void testBadValueStringProperty() {
        String query = "{" //
            + " \"" + this.testData.stringProperty.makeKey().toString() + "\":\"bad value here\"" //
            + "}";
        try {
            Logger.getLogger(MatcherTest.class).info("query: " + query);
            @SuppressWarnings("unused")
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);
            assertNotNull(m);
            assertTrue(m.isFalse());
            assertEquals(0.0, m.getM().doubleValue(), 0.001);
        } catch (QueryException e) {
            Logger.getLogger(MatcherTest.class).info("exception: " + e.getMessage());
            fail();
        }
    }

    /** nonexistent string property is a query exception. */
    public void testNonexistentStringProperty() {
        String query = "{ \"string_property/crap\":\"bad value here\" }";
        try {
            Logger.getLogger(MatcherTest.class).info("query: " + query);
            @SuppressWarnings("unused")
            Membership m =
                this.matcher.match(query, this.testData.harrisonFord.makeKey(),
                    TestData.BLANK_NAMESPACE);
            fail();
        } catch (QueryException e) {
            Logger.getLogger(MatcherTest.class).info("exception: " + e.getMessage());
            assertTrue(true);
        }
    }
}
