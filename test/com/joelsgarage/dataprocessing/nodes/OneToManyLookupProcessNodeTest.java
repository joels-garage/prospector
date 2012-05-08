/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.dataprocessing.lookup.SomeDataType;
import com.joelsgarage.dataprocessing.lookup.SomeJoinNode;
import com.joelsgarage.dataprocessing.lookup.SomeJoinType;
import com.joelsgarage.dataprocessing.lookup.SomeRecordType;
import com.joelsgarage.dataprocessing.readers.ListRecordReader;
import com.joelsgarage.dataprocessing.readers.MapRecordReaderFactory;
import com.joelsgarage.dataprocessing.writers.ListRecordWriter;
import com.joelsgarage.util.FatalException;

/**
 * Tests the joins using standalone data types and mock input and output streams.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class OneToManyLookupProcessNodeTest extends TestCase {

    public void testSimple() {
        ListRecordWriter<String> writer = new ListRecordWriter<String>();

        List<SomeRecordType> mainList = new ArrayList<SomeRecordType>() {
            private static final long serialVersionUID = 1L;
            {
                add(new SomeJoinType("fooKey", "fooData", "fooJoinKey"));
                add(new SomeJoinType("barKey", "barData", "barJoinKey"));
            }
        };

        List<SomeRecordType> sideList = new ArrayList<SomeRecordType>() {
            private static final long serialVersionUID = 1L;
            {
                add(new SomeDataType("fooJoinKey", "fooJoinData"));
                add(new SomeDataType("barJoinKey", "barJoinData"));
            }
        };

        ListRecordReader<SomeRecordType> mainReader =
            new ListRecordReader<SomeRecordType>(mainList);
        ListRecordReader<SomeRecordType> sideReader =
            new ListRecordReader<SomeRecordType>(sideList);

        Map<ReaderConstraint, RecordReader<SomeRecordType>> map =
            new HashMap<ReaderConstraint, RecordReader<SomeRecordType>>();
        // I magically know this is the main one
        map.put(new ReaderConstraint(SomeJoinType.class), mainReader);
        map.put(new ReaderConstraint(SomeDataType.class), sideReader);

        RecordReaderFactory<SomeRecordType> factory =
            new MapRecordReaderFactory<SomeRecordType>(map);

        SomeJoinNode node = new SomeJoinNode(factory, writer, 0, 0);

        try {
            node.run();
        } catch (FatalException e) {
            e.printStackTrace();
            fail();
        }

        List<String> output = writer.getList();

        assertNotNull(output);
        assertEquals(2, output.size());
        assertEquals("fooDatafooJoinData", output.get(0));
        assertEquals("barDatabarJoinData", output.get(1));

    }
}
