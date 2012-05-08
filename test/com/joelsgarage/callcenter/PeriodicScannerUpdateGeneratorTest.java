/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.dataprocessing.readers.ListRecordReader;
import com.joelsgarage.dataprocessing.readers.MapRecordReaderFactory;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.User;
import com.joelsgarage.util.FatalException;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class PeriodicScannerUpdateGeneratorTest extends TestCase {
    public static class FakeUpdateListener implements UpdateListener {
        public List<User> log;

        public FakeUpdateListener(List<User> log) {
            this.log = log;
        }

        @Override
        public void update(User user) {
            this.log.add(user);
        }
    }

    /**
     * Verify that the scanner scans
     * 
     * @throws FatalException
     */
    public void testSimple() throws FatalException {
        List<ModelEntity> list = new ArrayList<ModelEntity>();
        User user = new User("foo", "ns");
        list.add(user);
        user = new User("bar", "ns");
        list.add(user);
        RecordReader<ModelEntity> reader = new ListRecordReader<ModelEntity>(list);
        Map<ReaderConstraint, RecordReader<ModelEntity>> readers =
            new HashMap<ReaderConstraint, RecordReader<ModelEntity>>();
        readers.put(new ReaderConstraint(User.class), reader);
        RecordReaderFactory<ModelEntity> factory = new MapRecordReaderFactory<ModelEntity>(readers);
        List<User> log = new ArrayList<User>();
        UpdateListener listener = new FakeUpdateListener(log);
        PeriodicScannerUpdateGenerator generator =
            new PeriodicScannerUpdateGenerator(factory, listener);
        generator.run();
        assertEquals(2, log.size());
        assertEquals("foo", log.get(0).getEmailAddress());
        assertEquals("bar", log.get(1).getEmailAddress());
    }
}
