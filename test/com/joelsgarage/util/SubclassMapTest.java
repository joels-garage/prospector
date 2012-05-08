/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class SubclassMapTest extends TestCase {

	public void testBasic() {
		SubclassMap<String> foo = new SubclassMap<String>();
		foo.put("hi", "there");
		foo.put("hi", "bye");
		Set<String> fooSet = foo.get("hi");
		assertEquals(2, fooSet.size());
		Iterator<String> iter = fooSet.iterator();
		assertTrue(iter.hasNext());
		assertEquals("there", iter.next());
		assertTrue(iter.hasNext());
		assertEquals("bye", iter.next());
		assertFalse(iter.hasNext());
	}

	public void testGetChildrenNone() {
		SubclassMap<String> foo = new SubclassMap<String>();
		foo.put("hi", "there");
		foo.put("hi", "bye");
		Set<String> children = new HashSet<String>();
		foo.getChildren("foo", children);
		assertEquals(0, children.size());
	}

	public void testGetChildrenSimple() {
		SubclassMap<String> foo = new SubclassMap<String>();
		foo.put("hi", "there");
		foo.put("hi", "bye");
		Set<String> children = new HashSet<String>();
		foo.getChildren("hi", children);
		assertEquals(2, children.size());
		Iterator<String> iter = children.iterator();
		assertTrue(iter.hasNext());
		assertEquals("there", iter.next());
		assertTrue(iter.hasNext());
		assertEquals("bye", iter.next());
		assertFalse(iter.hasNext());
	}

	public void testGetChildrenRecursive() {
		SubclassMap<String> foo = new SubclassMap<String>();
		foo.put("hi", "there");
		foo.put("hi", "bye");
		foo.put("bye", "boo");
		foo.put("boo", "foo");
		Set<String> children = new HashSet<String>();
		foo.getChildren("hi", children);
		assertEquals(4, children.size());
		Iterator<String> iter = children.iterator();
		assertTrue(iter.hasNext());
		assertEquals("boo", iter.next());
		assertTrue(iter.hasNext());
		assertEquals("there", iter.next());
		assertTrue(iter.hasNext());
		assertEquals("foo", iter.next());
		assertTrue(iter.hasNext());
		assertEquals("bye", iter.next());
		assertFalse(iter.hasNext());
	}

	public void testGetChildrenWithCycle() {
		SubclassMap<String> foo = new SubclassMap<String>();
		foo.put("hi", "there");
		foo.put("hi", "bye");
		foo.put("bye", "hi");
		foo.put("bye", "foo");
		Set<String> children = new HashSet<String>();
		foo.getChildren("hi", children);
		assertEquals(4, children.size());
		Iterator<String> iter = children.iterator();
		assertTrue(iter.hasNext());
		assertEquals("there", iter.next());
		assertTrue(iter.hasNext());
		assertEquals("foo", iter.next());
		assertTrue(iter.hasNext());
		assertEquals("hi", iter.next());
		assertTrue(iter.hasNext());
		assertEquals("bye", iter.next());
		assertFalse(iter.hasNext());
	}

	public void testTransitiveClosure() {
		SubclassMap<String> foo = new SubclassMap<String>();
		foo.put("hi", "there");
		foo.put("hi", "bye");
		foo.put("bye", "hi");
		foo.put("bye", "foo");
		foo.put("island", "islandchild");
		foo.put("islandchild", "grandchild");
		foo.transitiveClosure();
		Set<String> children = foo.get("hi");
		assertNotNull(children);
		assertEquals(4, children.size());
		assertTrue(children.contains("there"));
		assertTrue(children.contains("bye"));
		assertTrue(children.contains("foo"));
		assertTrue(children.contains("hi"));
		children = foo.get("there");
		assertNull(children);
		children = foo.get("bye");
		assertNotNull(children);
		assertEquals(4, children.size());
		assertTrue(children.contains("hi"));
		assertTrue(children.contains("foo"));
		assertTrue(children.contains("there"));
		assertTrue(children.contains("bye"));
		children = foo.get("foo");
		assertNull(children);
		children = foo.get("island");
		assertEquals(2, children.size());
		assertNotNull(children);
		assertTrue(children.contains("islandchild"));
		assertTrue(children.contains("grandchild"));
		children = foo.get("islandchild");
		assertNotNull(children);
		assertEquals(1, children.size());
		assertTrue(children.contains("grandchild"));
		children = foo.get("grandchild");
		assertNull(children);
	}
}
