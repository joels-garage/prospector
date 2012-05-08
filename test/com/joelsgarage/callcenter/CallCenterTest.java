/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import junit.framework.TestCase;

import com.joelsgarage.dataprocessing.readers.MapRecordReaderFactory;
import com.joelsgarage.model.ModelEntity;

/**
 * @author joel
 * 
 */
public class CallCenterTest extends TestCase {
    public void testNothing() {
        CallCenter callCenter = new CallCenter(new MapRecordReaderFactory<ModelEntity>(null));
        assertNotNull(callCenter);
    }
}
