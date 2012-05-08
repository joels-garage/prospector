/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.gardening;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class StringToIndividualFactConverterTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // public void testSimple() {
    // List<ModelEntity> result = new ArrayList<ModelEntity>();
    //
    // StringToIndividualFactConverter converter = new StringToIndividualFactConverter();
    // converter.setIso8601Date("my date");
    // StringFact inputFact = new StringFact();
    // inputFact.setKey(new ExternalKey("foo", "bar", "baz"));
    // inputFact.setValue("some string value");
    // inputFact.setSubjectKey(new ExternalKey("subj foo", "subj bar", "subj baz"));
    // // note the property key is not actually read from the fact; it is assumed that the
    // // framework does that.
    //
    // StringProperty inputProperty = new StringProperty();
    // inputProperty.setKey(new ExternalKey("fooP", "barP", "bazP"));
    // inputProperty.setName("property name");
    // inputProperty.setDomainClassKey(new ExternalKey("class foo", "class bar", "class baz"));
    //
    // List<ModelEntity> newStuff = converter.convert(inputFact, inputProperty);
    //
    // assertNotNull(newStuff);
    // result.addAll(newStuff);
    //
    // inputFact = new StringFact();
    // inputFact.setKey(new ExternalKey("foo", "bar", "baazie"));
    // inputFact.setValue("another string value");
    // inputFact.setSubjectKey(new ExternalKey("subj foo", "subj bar", "subj buzz"));
    //
    // newStuff = converter.convert(inputFact, inputProperty);
    //
    // assertNotNull(newStuff);
    // result.addAll(newStuff);
    //
    // assertEquals(17, result.size());
    //
    // ModelEntity me = result.get(0);
    // assertTrue(me.getClass().getName(), me instanceof User);
    // User user = (User) me;
    // assertEquals("user/internal-agent/StringToIndividualFactConverter", user.getKey()
    // .toString());
    // assertEquals("StringToIndividualFactConverter", user.getName()); // same name
    //
    // me = result.get(1);
    // assertTrue(me.getClass().getName(), me instanceof IndividualProperty);
    // IndividualProperty ip = (IndividualProperty) me;
    // assertEquals("individual_property/fooP/bazP", ip.getKey().toString());
    // assertEquals("property name", ip.getName()); // same name
    //
    // me = result.get(2);
    // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // DerivedProvenance dp = (DerivedProvenance) me;
    // assertEquals(
    // "derived_provenance/fooP/individual_property/fooP/bazP derived from barP/fooP/bazP", dp
    // .getKey().toString());
    // assertEquals("individual_property/fooP/bazP derived from barP/fooP/bazP", dp.getName());
    //
    // me = result.get(3);
    // assertTrue(me.getClass().getName(), me instanceof Class);
    // Class cl = (Class) me;
    // assertEquals("class/fooP/Range of property name", cl.getKey().toString());
    // assertEquals("Range of property name", cl.getName());
    //
    // me = result.get(4);
    // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // DerivedProvenance dp2 = (DerivedProvenance) me;
    // assertEquals(
    // "derived_provenance/fooP/class/fooP/Range of property name derived from barP/fooP/bazP",
    // dp2.getKey().toString());
    // assertEquals("class/fooP/Range of property name derived from barP/fooP/bazP", dp2.getName());
    //
    // me = result.get(5);
    // assertTrue(me.getClass().getName(), me instanceof Individual);
    // Individual in = (Individual) me;
    // assertEquals("individual/foo/property=bazP&value=some+string+value", in.getKey().toString());
    // assertEquals("some string value", in.getName());
    //
    // me = result.get(6);
    // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // DerivedProvenance dp3 = (DerivedProvenance) me;
    // assertEquals(
    // "derived_provenance/foo/individual/foo/property=bazP&value=some+string+value derived from
    // bar/foo/baz",
    // dp3.getKey().toString());
    // assertEquals(
    // "individual/foo/property=bazP&value=some+string+value derived from bar/foo/baz", dp3
    // .getName());
    //
    // me = result.get(7);
    // assertTrue(me.getClass().getName(), me instanceof ClassMember);
    // assertEquals(
    // "class_member/foo/individual/foo/property=bazP&value=some+string+value member of
    // class/fooP/Range of property name",
    // me.getKey().toString());
    // assertEquals(
    // "individual/foo/property=bazP&value=some+string+value member of class/fooP/Range of property
    // name",
    // me.getName());
    //
    // me = result.get(8);
    // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // DerivedProvenance dp4 = (DerivedProvenance) me;
    // assertEquals(
    // "derived_provenance/foo/individual/foo/property=bazP&value=some+string+value derived from
    // bar/foo/baz",
    // dp4.getKey().toString());
    // assertEquals(
    // "individual/foo/property=bazP&value=some+string+value derived from bar/foo/baz", dp4
    // .getName());
    //
    // me = result.get(9);
    // assertTrue(me.getClass().getName(), me instanceof IndividualFact);
    // IndividualFact indF = (IndividualFact) me;
    // assertEquals(
    // "individual_fact/foo/subject=subj bar/subj foo/subj
    // baz&property=individual_property/fooP/bazP&object=individual/foo/property=bazP&value=some+string+value",
    // indF.getKey().toString());
    // assertEquals("", indF.getName()); // no name
    //
    // me = result.get(10);
    // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // DerivedProvenance dp5 = (DerivedProvenance) me;
    // assertEquals(
    // "derived_provenance/foo/individual_fact/foo/subject=subj bar/subj foo/subj
    // baz&property=individual_property/fooP/bazP&object=individual/foo/property=bazP&value=some+string+value
    // derived from bar/foo/baz",
    // dp5.getKey().toString());
    // assertEquals(
    // "individual_fact/foo/subject=subj bar/subj foo/subj
    // baz&property=individual_property/fooP/bazP&object=individual/foo/property=bazP&value=some+string+value
    // derived from bar/foo/baz",
    // dp5.getName());
    //
    // me = result.get(11);
    // assertTrue(me.getClass().getName(), me instanceof Individual);
    // Individual in2 = (Individual) me;
    // assertEquals("individual/foo/property=bazP&value=another+string+value", in2.getKey()
    // .toString());
    // assertEquals("another string value", in2.getName());
    //
    // me = result.get(12);
    // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // DerivedProvenance dp6 = (DerivedProvenance) me;
    // assertEquals(
    // "derived_provenance/foo/individual/foo/property=bazP&value=another+string+value derived from
    // bar/foo/baazie",
    // dp6.getKey().toString());
    // assertEquals(
    // "individual/foo/property=bazP&value=another+string+value derived from bar/foo/baazie",
    // dp6.getName());
    //
    // me = result.get(13);
    // assertTrue(me.getClass().getName(), me instanceof ClassMember);
    // assertEquals(
    // "class_member/foo/individual/foo/property=bazP&value=another+string+value member of
    // class/fooP/Range of property name",
    // me.getKey().toString());
    // assertEquals(
    // "individual/foo/property=bazP&value=another+string+value member of class/fooP/Range of
    // property name",
    // me.getName());
    //
    // me = result.get(14);
    // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // DerivedProvenance dp7 = (DerivedProvenance) me;
    // assertEquals(
    // "derived_provenance/foo/individual/foo/property=bazP&value=another+string+value derived from
    // bar/foo/baazie",
    // dp7.getKey().toString());
    // assertEquals(
    // "individual/foo/property=bazP&value=another+string+value derived from bar/foo/baazie",
    // dp7.getName());
    //
    // me = result.get(15);
    // assertTrue(me.getClass().getName(), me instanceof IndividualFact);
    // IndividualFact indF2 = (IndividualFact) me;
    // assertEquals(
    // "individual_fact/foo/subject=subj bar/subj foo/subj
    // buzz&property=individual_property/fooP/bazP&object=individual/foo/property=bazP&value=another+string+value",
    // indF2.getKey().toString());
    // assertEquals("", indF2.getName()); // no name
    //
    // me = result.get(16);
    // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // DerivedProvenance dp8 = (DerivedProvenance) me;
    // assertEquals(
    // "derived_provenance/foo/individual_fact/foo/subject=subj bar/subj foo/subj
    // buzz&property=individual_property/fooP/bazP&object=individual/foo/property=bazP&value=another+string+value
    // derived from bar/foo/baazie",
    // dp8.getKey().toString());
    // assertEquals(
    // "individual_fact/foo/subject=subj bar/subj foo/subj
    // buzz&property=individual_property/fooP/bazP&object=individual/foo/property=bazP&value=another+string+value
    // derived from bar/foo/baazie",
    // dp8.getName());
    // }
}
