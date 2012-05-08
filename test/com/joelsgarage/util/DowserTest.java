/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.dowser.AnotherJoinClass;
import com.joelsgarage.util.dowser.JoinClass;
import com.joelsgarage.util.dowser.ScanInput;
import com.joelsgarage.util.dowser.SubScanInput;
import com.joelsgarage.util.dowser.SubSubScanInput;

/**
 * This is a test of the annotation scanner.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class DowserTest extends TestCase {

	/** Sort Class<? extends ModelEntity> by name */
	public static class ClassComparator implements Comparator<Class<? extends ModelEntity>> {
		@Override
		public int compare(Class<? extends ModelEntity> o1, Class<? extends ModelEntity> o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

	/** Sort ForeignKey naturally */
	public static class ForeignKeyComparator implements Comparator<ForeignKey> {
		@Override
		public int compare(ForeignKey o1, ForeignKey o2) {
			if (o1 == o2) // same object
				return 0;
			int nameCmp = o1.getClas().getName().compareTo(o2.getClas().getName());
			if (nameCmp != 0)
				return nameCmp;
			int methodCmp = o1.getMethod().getName().compareTo(o2.getMethod().getName());
			if (methodCmp != 0)
				return methodCmp;
			return o1.getLabel().compareTo(o2.getLabel());
		}
	}

	public void testComparator() {
		try {
			ForeignKey o1 = new ForeignKey(ScanInput.class, JoinClass.class.getMethod(
				"getJoinField", new Class<?>[0]), "foo");
			ForeignKey o2 = new ForeignKey(ScanInput.class, AnotherJoinClass.class.getMethod(
				"getSomething", new Class<?>[0]), "bar");
			ForeignKeyComparator foo = new ForeignKeyComparator();
			assertTrue(foo.compare(o1, o2) < 0);

		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
			fail();
		}
	}

	/** Verify that the DowserFactory can read properties */
	public void testDowserFactoryFromMap() {
		Map<String, String> propertiesMap = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put("dowser.packages", "com.foo, org.bar");
			}
		};
		DowserFactory.newDowser(propertiesMap);
		assertEquals(2, DowserFactory.packages.size());
		assertEquals("com.foo", DowserFactory.packages.get(0));
		assertEquals("org.bar", DowserFactory.packages.get(1));

	}

	/** Verify internals of the DowserFactory */
	public void testDowserFactoryFromFile() {
		DowserFactory.newDowser("/util/test.dowser.properties");
		assertEquals(1, DowserFactory.packages.size());
		assertEquals("com.joelsgarage.util.dowser", DowserFactory.packages.get(0));

		Set<Class<? extends ModelEntity>> matches = DowserFactory.matches;

		List<Class<? extends ModelEntity>> matchList = new ArrayList<Class<? extends ModelEntity>>();
		matchList.addAll(matches);
		ClassComparator classComparator = new ClassComparator();
		// Sort it so that the ordering in the tests below is guaranteed (set doesn't guarantee an
		// order)
		Collections.sort(matchList, classComparator);

		assertEquals(5, matchList.size());

		Iterator<Class<? extends ModelEntity>> iter = matchList.iterator();

		assertTrue(iter.hasNext());

		{
			Class<? extends ModelEntity> clas = iter.next();
			assertEquals("com.joelsgarage.util.dowser.AnotherJoinClass", clas.getName());
			Annotation[] annotations = clas.getAnnotations();
			assertEquals(1, annotations.length);
			assertEquals("@com.joelsgarage.util.VisibleType(value=user)", annotations[0].toString());
			VisibleType v = clas.getAnnotation(VisibleType.class);
			assertEquals("user", v.value());
			VisibleJoin j = clas.getAnnotation(VisibleJoin.class);
			assertNull(j);
		}

		assertTrue(iter.hasNext());

		{
			Class<? extends ModelEntity> clas = iter.next();
			assertEquals("com.joelsgarage.util.dowser.JoinClass", clas.getName());
			Annotation[] annotations = clas.getAnnotations();
			assertEquals(1, annotations.length);
			assertEquals("@com.joelsgarage.util.VisibleType(value=individual)", annotations[0]
				.toString());
			VisibleType v = clas.getAnnotation(VisibleType.class);
			assertEquals("individual", v.value());
			VisibleJoin j = clas.getAnnotation(VisibleJoin.class);
			assertNull(j);
		}

		assertTrue(iter.hasNext());

		{
			Class<? extends ModelEntity> clas = iter.next();
			assertEquals("com.joelsgarage.util.dowser.ScanInput", clas.getName());
			Annotation[] annotations = clas.getAnnotations();
			assertEquals(1, annotations.length);
			assertEquals("@com.joelsgarage.util.VisibleType(value=decision)", annotations[0]
				.toString());
			VisibleType v = clas.getAnnotation(VisibleType.class);
			assertEquals("decision", v.value());
			VisibleJoin j = clas.getAnnotation(VisibleJoin.class);
			assertNull(j);
		}

		assertTrue(iter.hasNext());

		{
			Class<? extends ModelEntity> clas = iter.next();
			assertEquals("com.joelsgarage.util.dowser.SubScanInput", clas.getName());
			Annotation[] annotations = clas.getAnnotations();
			assertEquals(1, annotations.length);
			assertEquals("@com.joelsgarage.util.VisibleType(value=property)", annotations[0]
				.toString());
			VisibleType v = clas.getAnnotation(VisibleType.class);
			assertEquals("property", v.value());
			VisibleJoin j = clas.getAnnotation(VisibleJoin.class);
			assertNull(j);
		}

		assertTrue(iter.hasNext());

		{
			Class<? extends ModelEntity> clas = iter.next();
			assertEquals("com.joelsgarage.util.dowser.SubSubScanInput", clas.getName());
			Annotation[] annotations = clas.getAnnotations();
			assertEquals(1, annotations.length);
			assertEquals("@com.joelsgarage.util.VisibleType(value=string_property)", annotations[0]
				.toString());
			VisibleType v = clas.getAnnotation(VisibleType.class);
			assertEquals("string_property", v.value());
			VisibleJoin j = clas.getAnnotation(VisibleJoin.class);
			assertNull(j);
		}

		assertFalse(iter.hasNext());

	}

	/** Verify the class visibility */
	public void testDowserFromFileVisibleTypes() {
		Dowser dowser = DowserFactory.newDowser("/util/test.dowser.properties");
		assertEquals(5, dowser.getAllowedTypes().size());
		// Verify bijection
		assertEquals(ScanInput.class, dowser.getAllowedTypes().get("decision"));
		assertEquals(JoinClass.class, dowser.getAllowedTypes().get("individual"));
		assertEquals(SubScanInput.class, dowser.getAllowedTypes().get("property"));
		assertEquals(SubSubScanInput.class, dowser.getAllowedTypes().get("string_property"));
		assertEquals(AnotherJoinClass.class, dowser.getAllowedTypes().get("user"));

		assertEquals("decision", dowser.getTypeNames().get(ScanInput.class));
		assertEquals("individual", dowser.getTypeNames().get(JoinClass.class));
		assertEquals("property", dowser.getTypeNames().get(SubScanInput.class));
		assertEquals("string_property", dowser.getTypeNames().get(SubSubScanInput.class));
		assertEquals("user", dowser.getTypeNames().get(AnotherJoinClass.class));

	}

	/** Verify the subclass map */
	public void testDowserFromFileSubclasses() {
		Dowser dowser = DowserFactory.newDowser("/util/test.dowser.properties");
		// Verify subclass map
		Set<Class<? extends ModelEntity>> children = dowser.getAllowedSubtypes().get(
			ScanInput.class);
		assertNotNull(children);
		assertEquals(2, children.size());
		assertTrue(children.contains(SubScanInput.class));
		assertTrue(children.contains(SubSubScanInput.class));
		children = dowser.getAllowedSubtypes().get(JoinClass.class);
		assertNull(children);
		children = dowser.getAllowedSubtypes().get(SubScanInput.class);
		assertNotNull(children);
		assertEquals(1, children.size());
		assertTrue(children.contains(SubSubScanInput.class));
	}

	public void testDowserFromFileJoins() {
		Dowser dowser = DowserFactory.newDowser("/util/test.dowser.properties");
		JoinMap joinMap = dowser.getAllowedJoins();
		{
			// SubSubScanInput, a final class, is joined to:
			// AnotherJoinClass#getSomething (inherited)
			// AnotherJoinClass#getWhatever (inherited)
			// JoinClass#getJoinField (inherited)
			Set<ForeignKey> foreignKeySet = joinMap.get(SubSubScanInput.class);
			assertNotNull(foreignKeySet);
			List<ForeignKey> foreignKeyList = new ArrayList<ForeignKey>();
			foreignKeyList.addAll(foreignKeySet);
			ForeignKeyComparator foreignKeyComparator = new ForeignKeyComparator();
			Collections.sort(foreignKeyList, foreignKeyComparator);
			Iterator<ForeignKey> iter = foreignKeyList.iterator();
			assertTrue(iter.hasNext());

			{
				ForeignKey key = iter.next();
				assertEquals("com.joelsgarage.util.dowser.AnotherJoinClass", key.getClas()
					.getName());
				assertEquals("getSomething", key.getMethod().getName());
				assertEquals("bar", key.getLabel());
			}
			assertTrue(iter.hasNext());
			{
				ForeignKey key = iter.next();
				assertEquals("com.joelsgarage.util.dowser.AnotherJoinClass", key.getClas()
					.getName());
				assertEquals("getWhatever", key.getMethod().getName());
				assertEquals("baz", key.getLabel());
			}
			assertTrue(iter.hasNext());
			{
				ForeignKey key = iter.next();
				assertEquals("com.joelsgarage.util.dowser.JoinClass", key.getClas().getName());
				assertEquals("getJoinField", key.getMethod().getName());
				assertEquals("foo", key.getLabel());
			}
			assertFalse(iter.hasNext());
		}
		{
			// Only final classes are joined.
			Set<ForeignKey> foreignKeySet = joinMap.get(SubScanInput.class);
			assertNull(foreignKeySet);
		}
	}
}
