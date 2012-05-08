/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author joel
 */
@SuppressWarnings("nls")
public class BoundedPriorityQueueTest extends TestCase {
    public static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            if (o1 == null && o2 == null)
                return 0;
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            return o1.compareTo(o2);
        }
    }

    public void testSimple() {
        BoundedPriorityQueue<String> bpq =
            new BoundedPriorityQueue<String>(3, new StringComparator());
        bpq.add("a");
        bpq.add("b");
        assertEquals(2, bpq.size());
        assertEquals("a", bpq.poll()); // return least element
        assertEquals("b", bpq.poll());
        assertNull(bpq.poll());
    }

    public void testAddAll() {
        BoundedPriorityQueue<String> bpq =
            new BoundedPriorityQueue<String>(3, new StringComparator());
        List<String> l = new ArrayList<String>();
        l.add("a");
        l.add("b");
        bpq.addAll(l);
        assertEquals(2, bpq.size());
        assertEquals("a", bpq.poll()); // return least element
        assertEquals("b", bpq.poll());
        assertNull(bpq.poll());
    }

    public void testLimit() {
        BoundedPriorityQueue<String> bpq =
            new BoundedPriorityQueue<String>(3, new StringComparator());
        bpq.add("a");
        bpq.add("b");
        bpq.add("c");
        bpq.add("d");
        bpq.add("e");
        assertEquals(3, bpq.size());
        assertEquals("c", bpq.poll()); // return least element
        assertEquals("d", bpq.poll());
        assertEquals("e", bpq.poll());
        assertNull(bpq.poll());
    }

    public void testSort() {
        BoundedPriorityQueue<String> bpq =
            new BoundedPriorityQueue<String>(3, new StringComparator());
        bpq.add("a");
        bpq.add("b");
        bpq.add("c");
        bpq.add("d");
        bpq.add("e");
        assertEquals(3, bpq.size());
        List<String> l = new ArrayList<String>();
        l.addAll(bpq);
        Collections.sort(l, new StringComparator());
        assertEquals(3, l.size());
        assertEquals("c", l.get(0));
        assertEquals("d", l.get(1));
        assertEquals("e", l.get(2));
    }
}
