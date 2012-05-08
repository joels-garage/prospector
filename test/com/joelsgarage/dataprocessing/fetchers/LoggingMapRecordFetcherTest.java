/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.joelsgarage.dataprocessing.fetchers.LoggingMapRecordFetcher;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class LoggingMapRecordFetcherTest extends TestCase {
	public void testSimple() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("tempus", "fugit");
		data.put("blind me", "with science");
		data.put("ceaselessly into", "the past");

		LoggingMapRecordFetcher<String, String> fetcher = new LoggingMapRecordFetcher<String, String>(
			data) {

			@Override
			protected String extractForeignKey(String fieldName, String instance) {
				// The foreign key field of the string value is the value itself.
				return instance;
			}
		};

		assertEquals("fugit", fetcher.get("tempus"));
		assertEquals("the past", fetcher.get("field name is ignored", "the past"));

		Iterator<LoggingMapRecordFetcher<String, String>.Log> iter = fetcher.getAccessLogIterator();

		assertTrue(iter.hasNext());
		LoggingMapRecordFetcher<String, String>.Log entry = iter.next();
		assertEquals("tempus", entry.key);
		assertNull(entry.fieldName);

		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("the past", entry.key);
		assertEquals("field name is ignored", entry.fieldName);
		
		assertFalse(iter.hasNext());
	}

}
