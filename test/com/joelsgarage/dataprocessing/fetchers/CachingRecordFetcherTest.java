/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.fetchers.CachingRecordFetcher;
import com.joelsgarage.dataprocessing.fetchers.LoggingMapRecordFetcher;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class CachingRecordFetcherTest extends TestCase {
	protected void verifyLog(LoggingMapRecordFetcher<String, String> fetcher) {
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

	protected void verifyLongerLog(LoggingMapRecordFetcher<String, String> fetcher) {
		Iterator<LoggingMapRecordFetcher<String, String>.Log> iter = fetcher.getAccessLogIterator();

		assertTrue(iter.hasNext());
		LoggingMapRecordFetcher<String, String>.Log entry = iter.next();
		assertEquals("tempus", entry.key);
		assertNull(entry.fieldName);

		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("the past", entry.key);
		assertEquals("field name is ignored", entry.fieldName);

		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("blind me", entry.key);
		assertNull(entry.fieldName);

		assertFalse(iter.hasNext());
	}

	protected void verifyEvenLongerLog(LoggingMapRecordFetcher<String, String> fetcher) {
		Iterator<LoggingMapRecordFetcher<String, String>.Log> iter = fetcher.getAccessLogIterator();

		assertTrue(iter.hasNext());
		LoggingMapRecordFetcher<String, String>.Log entry = iter.next();
		assertEquals("tempus", entry.key);
		assertNull(entry.fieldName);

		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("the past", entry.key);
		assertEquals("field name is ignored", entry.fieldName);

		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("blind me", entry.key);
		assertNull(entry.fieldName);

		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("foo", entry.key);
		assertNull(entry.fieldName);

		assertFalse(iter.hasNext());
	}

	protected void verifyReallyLongLog(LoggingMapRecordFetcher<String, String> fetcher) {
		Iterator<LoggingMapRecordFetcher<String, String>.Log> iter = fetcher.getAccessLogIterator();

		assertTrue(iter.hasNext());
		LoggingMapRecordFetcher<String, String>.Log entry = iter.next();
		assertEquals("tempus", entry.key);
		assertNull(entry.fieldName);

		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("the past", entry.key);
		assertEquals("field name is ignored", entry.fieldName);

		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("blind me", entry.key);
		assertNull(entry.fieldName);

		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("foo", entry.key);
		assertNull(entry.fieldName);

		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("tempus", entry.key);
		assertNull(entry.fieldName);

		assertFalse(iter.hasNext());
	}

	/** Test a cache that doesn't fill up */
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

		CachingRecordFetcher<String, String> cache = new CachingRecordFetcher<String, String>(4,
			fetcher);

		// Verify same behavior as the fetcher

		assertEquals("fugit", cache.get("tempus"));
		assertEquals("the past", cache.get("field name is ignored", "the past"));
		verifyLog(fetcher);

		// Now if we repeat the gets, the log should remain unchanged.

		assertEquals("fugit", cache.get("tempus"));
		assertEquals("the past", cache.get("field name is ignored", "the past"));
		verifyLog(fetcher);

		// Then another get, and the log expands
		assertEquals("with science", cache.get("blind me"));
		verifyLongerLog(fetcher);

		// Now everything is in cache.
		assertEquals("fugit", cache.get("tempus"));
		assertEquals("the past", cache.get("field name is ignored", "the past"));
		verifyLongerLog(fetcher);
	}

	/** Test a cache that does fill up */
	public void testFillingUp() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("tempus", "fugit");
		data.put("blind me", "with science");
		data.put("ceaselessly into", "the past");
		data.put("foo", "bar");

		LoggingMapRecordFetcher<String, String> fetcher = new LoggingMapRecordFetcher<String, String>(
			data) {

			@Override
			protected String extractForeignKey(String fieldName, String instance) {
				// The foreign key field of the string value is the value itself.
				return instance;
			}
		};

		CachingRecordFetcher<String, String> cache = new CachingRecordFetcher<String, String>(2,
			fetcher);

		// Verify same behavior as the fetcher

		Logger.getLogger(CachingRecordFetcherTest.class).info("get tempus: miss");
		assertEquals("fugit", cache.get("tempus"));
		Logger.getLogger(CachingRecordFetcherTest.class).info("get past: miss");
		assertEquals("the past", cache.get("field name is ignored", "the past"));
		verifyLog(fetcher);

		// Now if we repeat the gets, the log should remain unchanged; all hits.

		Logger.getLogger(CachingRecordFetcherTest.class).info("get tempus: hit");
		assertEquals("fugit", cache.get("tempus"));
		Logger.getLogger(CachingRecordFetcherTest.class).info("get past: hit");
		assertEquals("the past", cache.get("field name is ignored", "the past"));
		verifyLog(fetcher);

		// Then another get, and the log expands. Now there are 3 entries.
		Logger.getLogger(CachingRecordFetcherTest.class).info("get blind me: miss");
		assertEquals("with science", cache.get("blind me"));
		verifyLongerLog(fetcher);

		// This should be a cache miss.
		assertEquals("bar", cache.get("foo"));
		Logger.getLogger(CachingRecordFetcherTest.class).info("get foo: miss");
		verifyEvenLongerLog(fetcher);

		// Finally, what used to be in a hit, is now a miss
		assertEquals("fugit", cache.get("tempus"));
		Logger.getLogger(CachingRecordFetcherTest.class).info("get tempus: miss");
		verifyReallyLongLog(fetcher);
	}

}
