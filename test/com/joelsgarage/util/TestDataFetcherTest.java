/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import junit.framework.TestCase;

import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.fetchers.ModelEntityMapRecordFetcher;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.ModelEntity;

/**
 * @author joel
 * 
 */
public class TestDataFetcherTest extends TestCase {
    public TestData testData;
    public RecordFetcher<ExternalKey, ModelEntity> fetcher;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.testData = new TestData();
        this.fetcher = new ModelEntityMapRecordFetcher(this.testData.entities);
    }

    public void testSimple() {
        Individual i = this.testData.individual;
        ModelEntity entity = this.fetcher.get(i.makeKey());
        assertEquals(i, entity);
    }

}
