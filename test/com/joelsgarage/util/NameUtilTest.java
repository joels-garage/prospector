/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class NameUtilTest extends TestCase {

    // /** Test all different namespaces */
    // public void testDifferent() {
    // String namespace = "fact namespace";
    // String value = "fact value";
    // ExternalKey subjectKey = new ExternalKey("subject namespace", "subject type", "subject key");
    // ExternalKey propertyKey = new ExternalKey("property namespace", "property type",
    // "property key");
    // String name = NameUtil.makeFactName(namespace, value, subjectKey, propertyKey);
    // assertEquals(
    // "subject=subject+type%2Fsubject+namespace%2Fsubject+key&property=property+type%2Fproperty+namespace%2Fproperty+key&value=fact+value",
    // name);
    // }
    //
    // /** Test fact namespace same as subject */
    // public void testSameSubjectNamespace() {
    // String namespace = "fact namespace";
    // String value = "fact value";
    // ExternalKey subjectKey = new ExternalKey("fact namespace", "subject type", "subject key");
    // ExternalKey propertyKey = new ExternalKey("property namespace", "property type",
    // "property key");
    // String name = NameUtil.makeFactName(namespace, value, subjectKey, propertyKey);
    // assertEquals(
    // "subject=subject+key&property=property+type%2Fproperty+namespace%2Fproperty+key&value=fact+value",
    // name);
    // }
    //
    // /** Test fact namespace same as property */
    // public void testSamePropertyNamespace() {
    // String namespace = "fact namespace";
    // String value = "fact value";
    // ExternalKey subjectKey = new ExternalKey("subject namespace", "subject type", "subject key");
    // ExternalKey propertyKey = new ExternalKey("fact namespace", "property type", "property key");
    // String name = NameUtil.makeFactName(namespace, value, subjectKey, propertyKey);
    // assertEquals(
    // "subject=subject+type%2Fsubject+namespace%2Fsubject+key&property=property+key&value=fact+value",
    // name);
    // }
    //
    // /** Test all same namespace */
    // public void testAllSameNamespace() {
    // String namespace = "fact namespace";
    // String value = "fact value";
    // ExternalKey subjectKey = new ExternalKey("fact namespace", "subject type", "subject key");
    // ExternalKey propertyKey = new ExternalKey("fact namespace", "property type", "property key");
    // String name = NameUtil.makeFactName(namespace, value, subjectKey, propertyKey);
    // assertEquals("subject=subject+key&property=property+key&value=fact+value", name);
    // }

	public void testEncode() {
		String input = "A %$# (*& difficult []\\/{}| input";
		assertEquals("A+%25%24%23+%28*%26+difficult+%5B%5D%5C%2F%7B%7D%7C+input", NameUtil.encode(input));
	}
}
