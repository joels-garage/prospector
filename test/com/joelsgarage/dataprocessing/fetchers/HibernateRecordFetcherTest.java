/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class HibernateRecordFetcherTest extends TestCase {

   
    public void testNothing() {
        assertTrue(true);
    }
    // private static final String HIBERNATE_CFG = "/common/hibernate-test.cfg.xml";

    // protected void populateDB() {
    //
    // try {
    // SessionFactory testFactory = HibernateUtil.createSessionFactory(HIBERNATE_CFG, null);
    // assertNotNull(testFactory);
    //
    // Session writerSession = testFactory.openSession();
    //
    // QuantityFact fact1 = new QuantityFact();
    // fact1.setKey(new ExternalKey("foo", "bar", "baz"));
    // fact1.setName("foo foo");
    // fact1.setCreatorKey(new ExternalKey("francis", "scott", "key"));
    // HibernateRecordWriter<QuantityFact> writer = new QuantityFactHibernateRecordWriter(
    // writerSession);
    // writer.open();
    // writer.write(fact1);
    // writer.close();
    //
    // writerSession.close();
    //
    // } catch (InitializationException ex) {
    // ex.printStackTrace();
    // fail();
    // }
    // }
    //
    // public void testSimple() {
    // populateDB();
    // SessionFactory testFactory = HibernateUtil.createSessionFactory(HIBERNATE_CFG, null);
    // assertNotNull(testFactory);
    //
    // Session fetcherSession = testFactory.openSession();
    // ReaderConstraint constraint = new ReaderConstraint(QuantityFact.class);
    // HibernateRecordFetcher fetcher = new HibernateRecordFetcher(fetcherSession, constraint);
    //
    // // This does not exist
    // ModelEntity item = fetcher.get(new ExternalKey("feh", "ber", "bez"));
    // assertNull(item);
    //
    // item = fetcher.get(new ExternalKey("foo", "bar", "baz"));
    // assertNotNull(item);
    // assertEquals("foo foo", item.getName());
    //
    // item = fetcher.get(HibernateProperty.CREATOR_KEY,
    // new ExternalKey("francis", "scott", "key"));
    // assertNotNull(item);
    // assertEquals("foo foo", item.getName());
    //
    // item = fetcher.get(HibernateProperty.CREATOR_KEY, new ExternalKey("a", "b", "c"));
    // assertNull(item);
    // fetcherSession.close();
    // }
}
