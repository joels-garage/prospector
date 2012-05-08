/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import junit.framework.TestCase;

/**
 * This is my first attempt at a test that actually uses the database.
 * 
 * It would be good, I guess, to have a way to drop the db, so we know what's in it.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class HibernateRecordReaderTest extends TestCase {

    public void testNothing() {
        assertTrue(true);
    }

    // private static final String HIBERNATE_CFG = "/common/hibernate-test.cfg.xml";
    //
    // @Override
    // protected void setUp() {
    // // foo
    // }
    //
    // public void testReader() {
    // try {
    // SessionFactory testFactory = HibernateUtil.createSessionFactory(HIBERNATE_CFG, null);
    // assertNotNull(testFactory);
    //
    // Session readerSession = testFactory.openSession();
    // Session writerSession = testFactory.openSession();
    // assertNotSame(readerSession, writerSession);
    //
    // QuantityFact fact1 = new QuantityFact();
    // fact1.setKey(new ExternalKey("foo", "bar", "baz"));
    // fact1.setName("foo foo");
    // HibernateRecordWriter<QuantityFact> writer =
    // new QuantityFactHibernateRecordWriter(writerSession);
    // writer.open();
    // writer.write(fact1);
    // writer.close();
    //
    // HibernateRecordReader<QuantityFact> reader =
    // new QuantityFactHibernateRecordReader(readerSession);
    // reader.open();
    // QuantityFact fact2 = reader.read();
    // QuantityFact fact3 = reader.read();
    // reader.close();
    //
    // // There's some reason for this. What is it?
    // // readerSession.close();
    // writerSession.close();
    //
    // assertNotNull(fact2);
    // assertEquals("foo", fact2.getNamespace());
    // assertEquals("bar", fact2.getKey().getType());
    // assertEquals("baz", fact2.getKey().getKey());
    // assertEquals("foo foo", fact2.getName());
    // assertNull(fact3);
    //
    // } catch (InitializationException ex) {
    // ex.printStackTrace();
    // fail();
    // }
    // }
}
