/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.gardening;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class StringFactGardenerTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        Logger.getLogger(StringFactGardenerTest.class).info("StringFactGardenerTest");
        super.setUp();
    }

    public void testNothing() {
        Logger.getLogger(StringFactGardenerTest.class).info("StringFactGardenerTest");
        assertTrue(true);
    }
    //
    // // // This just logs what it finds.
    // // public static class FakeStringToMeasurementFactGardener extends
    // // StringToMeasurementFactGardener {
    // // public List<StringFact> inputFacts;
    // // public List<StringProperty> inputProperties;
    // // public List<String> newPropertyNames;
    // // public List<String> numbers;
    // // public List<ExternalKey> measurementUnitKeys;
    // //
    // // public FakeStringToMeasurementFactGardener(List<StringFact> f, List<StringProperty> p,
    // // List<String> pn, List<String> n, List<ExternalKey> k,
    // // RecordReaderFactory<ModelEntity> factory, RecordWriter<ModelEntity> writer,
    // // int inlimit, int outlimit) {
    // // super(factory, writer, inlimit, outlimit);
    // // this.inputFacts = f;
    // // this.inputProperties = p;
    // // this.newPropertyNames = pn;
    // // this.numbers = n;
    // // this.measurementUnitKeys = k;
    // // }
    // //
    // // @Override
    // // protected List<ModelEntity> makeNewEntities(StringFact inputFact,
    // // StringProperty inputProperty, String newPropertyName, String number,
    // // ExternalKey measurementUnitKey) {
    // // this.inputFacts.add(inputFact);
    // // this.inputProperties.add(inputProperty);
    // // this.newPropertyNames.add(newPropertyName);
    // // this.numbers.add(number);
    // // this.measurementUnitKeys.add(measurementUnitKey);
    // // return null;
    // // }
    // // }
    //
    // public void testExtractForeignKey() throws FatalException {
    // Logger.getLogger(StringFactGardenerTest.class).info("StringFactGardenerTest");
    //
    // StringFact fact =
    // new StringFact(new Individual("individual", "namespace").makeKey(), //
    // new StringProperty(
    // //
    // new com.joelsgarage.model.Class("class-name", "namespace").makeKey(), "foo",
    // "namespace").makeKey(), "value", "namespace");
    //
    // StringFactGardener gardener = new StringFactGardener(null, null, 0, 0);
    // ExternalKey extractedKey = gardener.extractForeignKey(fact);
    // assertEquals("nomatch", extractedKey.toString());
    // }
    //
    // public void testExtractForeignKeyFail() throws FatalException {
    // Logger.getLogger(StringFactGardenerTest.class).info("StringFactGardenerTest");
    //
    // Individual ind = new Individual("individual", "namespace");
    // StringFactGardener gardener = new StringFactGardener(null, null, 0, 0);
    // ExternalKey extractedKey = gardener.extractForeignKey(ind);
    // assertNull(extractedKey);
    // }
    //
    // public void testExtractPrimaryKey() throws FatalException {
    // Logger.getLogger(StringFactGardenerTest.class).info("StringFactGardenerTest");
    //
    // StringProperty prop =
    // new StringProperty(
    // new com.joelsgarage.model.Class("class-name", "namespace").makeKey(),
    // "property-name", "namespace");
    // StringFactGardener gardener = new StringFactGardener(null, null, 0, 0);
    // ExternalKey extractedKey = gardener.extractLookupKey(prop);
    // assertNotNull(extractedKey);
    // assertEquals("bar/foo/baz", extractedKey.toString());
    // }
    //
    // public void testExtractPrimaryKeyOtherType() throws FatalException {
    // Logger.getLogger(StringFactGardenerTest.class).info("StringFactGardenerTest");
    //
    // Individual ind = new Individual("individual", "namespace");
    // StringFactGardener gardener = new StringFactGardener(null, null, 0, 0);
    // ExternalKey extractedKey = gardener.extractLookupKey(ind);
    // assertNotNull(extractedKey);
    // assertEquals("bar/foo/baz", extractedKey.toString());
    // }
    //
    // public void testLookupConstraint() {
    // Logger.getLogger(StringFactGardenerTest.class).info("StringFactGardenerTest");
    //
    // StringFactGardener gardener = new StringFactGardener(null, null, 0, 0);
    // assertEquals(StringProperty.class, gardener.getLookupConstraint().getClassConstraint());
    // }
    //
    // public void testMainConstraint() {
    // Logger.getLogger(StringFactGardenerTest.class).info("StringFactGardenerTest");
    //
    // StringFactGardener gardener = new StringFactGardener(null, null, 0, 0);
    // assertEquals(StringFact.class, gardener.getMainConstraint().getClassConstraint());
    // }
    //
    // public void testStart() throws FatalException {
    // Logger.getLogger(StringFactGardenerTest.class).info("StringFactGardenerTest");
    //
    // List<ModelEntity> units = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new UnitSynonym(new StandardMeasurementUnit( //
    // new MeasurementQuantity("length", "namespace").makeKey(), //
    // "foot-unit", //
    // "namespace").makeKey(), "foot", Boolean.TRUE, Boolean.FALSE, Boolean.FALSE,
    // "namespace"));
    // add(new UnitSynonym(new StandardMeasurementUnit( //
    // new MeasurementQuantity("length", "namespace").makeKey(), //
    // "meter-unit", //
    // "namespace").makeKey(), "meter", Boolean.TRUE, Boolean.FALSE, Boolean.FALSE,
    // "namespace"));
    // }
    // };
    //
    // ListRecordReader<ModelEntity> reader = new ListRecordReader<ModelEntity>(units);
    //
    // Map<ReaderConstraint, RecordReader<ModelEntity>> map =
    // new HashMap<ReaderConstraint, RecordReader<ModelEntity>>();
    // map.put(new ReaderConstraint(UnitSynonym.class), reader);
    // // This is incomplete but we should fail gracefully.
    // RecordReaderFactory<ModelEntity> factory = new MapRecordReaderFactory<ModelEntity>(map);
    //
    // StringFactGardener gardener = new StringFactGardener(factory, null, 0, 0);
    // try {
    // gardener.start();
    // } catch (FatalException e) {
    // e.printStackTrace();
    // fail();
    // }
    // }
    //
    // // /** Verify matching the constructed strings and patterns */
    // // public void testMatching() {
    // // List<ModelEntity> units = new ArrayList<ModelEntity>() {
    // // private static final long serialVersionUID = 1L;
    // // {
    // // UnitSynonym u = new UnitSynonym();
    // // u.setKey(new ExternalKey("foo", "unit_synonym", "foot unit"));
    // // u.setValue("foot");
    // // u.setMeasurementUnitKey(new ExternalKey("foo", "bar", "baz"));
    // // add(u);
    // // u = new UnitSynonym();
    // // u.setKey(new ExternalKey("foo", "unit_synonym", "meter unit"));
    // // u.setValue("meter");
    // // u.setMeasurementUnitKey(new ExternalKey("squeeze", "the", "charmin"));
    // // add(u);
    // // }
    // // };
    // // List<ModelEntity> facts = new ArrayList<ModelEntity>() {
    // // private static final long serialVersionUID = 1L;
    // // {
    // // StringFact fact = new StringFact();
    // // fact.setKey(new ExternalKey("foo", "string_fact", "more than 9"));
    // // fact.setPropertyKey(new ExternalKey("foo", "string_property", "how high"));
    // // fact.setValue("10");
    // // add(fact);
    // // fact = new StringFact();
    // // fact.setKey(new ExternalKey("foo", "string_fact", "more than 4"));
    // // fact.setPropertyKey(new ExternalKey("foo", "string_property", "how wide"));
    // // fact.setValue("5");
    // // add(fact);
    // // fact = new StringFact();
    // // fact.setKey(new ExternalKey("foo", "string_fact", "foo"));
    // // fact.setPropertyKey(new ExternalKey("foo", "string_property", "other"));
    // // fact.setValue("foo");
    // // add(fact);
    // // }
    // // };
    // // List<ModelEntity> properties = new ArrayList<ModelEntity>() {
    // // private static final long serialVersionUID = 1L;
    // // {
    // // StringProperty property = new StringProperty();
    // // property.setKey(new ExternalKey("foo", "string_property", "how high"));
    // // property.setName("height (meter)");
    // // add(property);
    // // property = new StringProperty();
    // // property.setKey(new ExternalKey("foo", "string_property", "how wide"));
    // // property.setName("length (foot)");
    // // add(property);
    // // property = new StringProperty();
    // // property.setKey(new ExternalKey("foo", "string_property", "other"));
    // // property.setName("length (foo)");
    // // add(property);
    // // }
    // // };
    // //
    // // ListRecordReader<ModelEntity> unitReader = new ListRecordReader<ModelEntity>(units);
    // // ListRecordReader<ModelEntity> factReader = new ListRecordReader<ModelEntity>(facts);
    // // ListRecordReader<ModelEntity> propertyReader = new
    // ListRecordReader<ModelEntity>(properties);
    // //
    // // // This is incomplete but we should fail gracefully.
    // // MapRecordReaderFactory<ModelEntity> factory = new MapRecordReaderFactory<ModelEntity>();
    // //
    // // factory.putReader(new ReaderConstraint(UnitSynonym.class), unitReader);
    // // factory.putReader(new ReaderConstraint(StringFact.class), factReader);
    // // factory.putReader(new ReaderConstraint(StringProperty.class), propertyReader);
    // //
    // // List<StringFact> inputFacts = new ArrayList<StringFact>();
    // // List<StringProperty> inputProperties = new ArrayList<StringProperty>();
    // // List<String> newPropertyNames = new ArrayList<String>();
    // // List<String> numbers = new ArrayList<String>();
    // // List<ExternalKey> measurementUnitKeys = new ArrayList<ExternalKey>();
    // //
    // // ListRecordWriter<ModelEntity> writer = new ListRecordWriter<ModelEntity>();
    // //
    // // StringToMeasurementFactGardener gardener = new FakeStringToMeasurementFactGardener(
    // // inputFacts, inputProperties, newPropertyNames, numbers, measurementUnitKeys, factory,
    // // writer, 0, 0);
    // //
    // // try {
    // // gardener.run();
    // // } catch (FatalException e) {
    // // e.printStackTrace();
    // // fail();
    // // }
    // //
    // // assertEquals(2, inputFacts.size());
    // // assertEquals(2, inputProperties.size());
    // // assertEquals(2, newPropertyNames.size());
    // // assertEquals(2, numbers.size());
    // // assertEquals(2, measurementUnitKeys.size());
    // // assertEquals("height", newPropertyNames.get(0));
    // // assertEquals("length", newPropertyNames.get(1));
    // // assertEquals("10", numbers.get(0));
    // // assertEquals("5", numbers.get(1));
    // // assertEquals("the/squeeze/charmin", measurementUnitKeys.get(0).toString());
    // // assertEquals("bar/foo/baz", measurementUnitKeys.get(1).toString());
    // // }
    //
    // public void testEndToEnd() throws FatalException {
    // Logger.getLogger(StringFactGardenerTest.class).info("StringFactGardenerTest");
    //
    // List<ModelEntity> affine = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new AffineMeasurementUnit(Double.valueOf(1.0), new StandardMeasurementUnit(
    // new MeasurementQuantity("quantity", "namespace").makeKey(), "unit-name",
    // "namespace").makeKey(), Double.valueOf(0.0), "namespace"));
    // }
    // };
    // List<ModelEntity> standard = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new StandardMeasurementUnit(new MeasurementQuantity("quantity", "namespace")
    // .makeKey(), "unit-name", "namespace"));
    // }
    // };
    //
    // List<ModelEntity> units = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new UnitSynonym(new StandardMeasurementUnit(new MeasurementQuantity("length",
    // "namespace").makeKey(), "foot-unit", "namespace").makeKey(), "foot",
    // Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, "namespace"));
    // add(new UnitSynonym(new StandardMeasurementUnit(new MeasurementQuantity("length",
    // "namespace").makeKey(), "meter-unit", "namespace").makeKey(), "meter",
    // Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, "namespace"));
    // }
    // };
    // List<ModelEntity> facts = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new StringFact(new Individual("sumthin", "foo").makeKey(),
    // new IndividualProperty(new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "how high",
    // new com.joelsgarage.model.Class("class-name", "foo").makeKey(), "foo")
    // .makeKey(), "10", "foo"));
    // add(new StringFact(new Individual("sumthin", "foo").makeKey(),
    // new IndividualProperty(new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "how wide",
    // new com.joelsgarage.model.Class("class-name", "foo").makeKey(), "foo")
    // .makeKey(), "5", "foo"));
    // add(new StringFact(new Individual("sumthin", "foo").makeKey(),
    // new IndividualProperty(new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "other", new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "foo").makeKey(), "foo-value", "foo"));
    // add(new StringFact(new Individual("subj baz", "subj foo").makeKey(),
    // new IndividualProperty(new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "bazP", new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "foo").makeKey(), "some string value", "foo"));
    // add(new StringFact(new Individual("subj buzz", "subj foo").makeKey(),
    // new IndividualProperty(new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "bazP", new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "foo").makeKey(), "another string value", "foo"));
    // add(new StringFact(new Individual("subj buzz", "subj foo").makeKey(),
    // new IndividualProperty(new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "bazP", new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "foo").makeKey(), "", "foo")); // note empty
    //
    // // for the splitter.
    // add(new StringFact(new Individual("subject name", "subject namespace").makeKey(),
    // new IndividualProperty(new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "property name", new com.joelsgarage.model.Class("class-name",
    // "foo").makeKey(), "property namespace").makeKey(), "foo value, bar value",
    // "original key namespace"));
    //
    // // ignore numbers
    // add(new StringFact(new Individual("subject name", "subject namespace").makeKey(),
    // new IndividualProperty(new com.joelsgarage.model.Class("class-name", "foo")
    // .makeKey(), "property name", new com.joelsgarage.model.Class("class-name",
    // "foo").makeKey(), "property namespace").makeKey(), "123.5",
    // "original key namespace"));
    // }
    // };
    // List<ModelEntity> properties = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new StringProperty(new com.joelsgarage.model.Class("sumthin", "foo").makeKey(),
    // "height (meter)", "foo"));
    // add(new StringProperty(new com.joelsgarage.model.Class("sumthin", "foo").makeKey(),
    // "length (foot)", "foo"));
    // add(new StringProperty(new com.joelsgarage.model.Class("sumthin", "foo").makeKey(),
    // "length (foo)", "foo"));
    // // for individual conversion
    // add(new StringProperty(new com.joelsgarage.model.Class("class baz", "foo")
    // .makeKey(), "a property", "fooP"));
    // }
    // };
    //
    // ListRecordReader<ModelEntity> affineReader = new ListRecordReader<ModelEntity>(affine);
    // ListRecordReader<ModelEntity> standardReader = new ListRecordReader<ModelEntity>(standard);
    // ListRecordReader<ModelEntity> unitReader = new ListRecordReader<ModelEntity>(units);
    // ListRecordReader<ModelEntity> factReader = new ListRecordReader<ModelEntity>(facts);
    // ListRecordReader<ModelEntity> propertyReader =
    // new ListRecordReader<ModelEntity>(properties);
    //
    // Map<ReaderConstraint, RecordReader<ModelEntity>> map =
    // new HashMap<ReaderConstraint, RecordReader<ModelEntity>>();
    // map.put(new ReaderConstraint(AffineMeasurementUnit.class), affineReader);
    // map.put(new ReaderConstraint(StandardMeasurementUnit.class), standardReader);
    // map.put(new ReaderConstraint(UnitSynonym.class), unitReader);
    // map.put(new ReaderConstraint(StringFact.class), factReader);
    // map.put(new ReaderConstraint(StringProperty.class), propertyReader);
    //
    // RecordReaderFactory<ModelEntity> factory = new MapRecordReaderFactory<ModelEntity>(map);
    //
    // ListRecordWriter<ModelEntity> writer = new ListRecordWriter<ModelEntity>();
    //
    // StringFactGardener gardener = new StringFactGardener(factory, writer, 0, 0);
    //
    // Logger.getLogger(StringFactGardenerTest.class).info("Just before run()");
    // try {
    // gardener.run();
    // } catch (FatalException e) {
    // e.printStackTrace();
    // fail();
    // }
    //
    // List<ModelEntity> output = writer.getList();
    // assertNotNull(output);
    // assertEquals(33, output.size());
    // assertEquals("user/internal-agent/FactValueSplitter", output.get(0).makeKey().toString());
    // assertEquals("user/internal-agent/StringToMeasurementFactConverter", output.get(1)
    // .makeKey().toString());
    // assertEquals(
    // "quantity_property/foo/length for quantity measurement_quantity/ns/name in domain
    // class/foo/sumthin",
    // output.get(2).makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/quantity_property/foo/length for quantity
    // measurement_quantity/ns/name in domain class/foo/sumthin derived from string_property/foo/how
    // wide",
    // output.get(3).makeKey().toString());
    // assertEquals(
    // "quantity_fact/foo/subject=individual/foo/sumthin&property=quantity_property/foo/length+for+quantity+measurement_quantity%2Fns%2Fname+in+domain+class%2Ffoo%2Fsumthin&value=5.0",
    // output.get(4).makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/quantity_fact/foo/subject=individual/foo/sumthin&property=quantity_property/foo/length+for+quantity+measurement_quantity%2Fns%2Fname+in+domain+class%2Ffoo%2Fsumthin&value=5.0
    // derived from string_fact/foo/more than 4",
    // output.get(5).makeKey().toString());
    // assertEquals("user/internal-agent/StringToIndividualFactConverter", output.get(6).makeKey()
    // .toString());
    // assertEquals("individual_property/foo/other", output.get(7).makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/individual_property/foo/other derived from
    // string_property/foo/other",
    // output.get(8).makeKey().toString());
    // assertEquals("class/foo/Range of length (foo)", output.get(9).makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/class/foo/Range of length (foo) derived from
    // string_property/foo/other",
    // output.get(10).makeKey().toString());
    // assertEquals("individual/foo/property=other&value=foo", output.get(11).makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/individual/foo/property=other&value=foo derived from
    // string_fact/foo/foo",
    // output.get(12).makeKey().toString());
    // assertEquals(
    // "class_member/foo/individual/foo/property=other&value=foo member of class/foo/Range of length
    // (foo)",
    // output.get(13).makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/individual/foo/property=other&value=foo derived from
    // string_fact/foo/foo",
    // output.get(14).makeKey().toString());
    // assertEquals(
    // "individual_fact/foo/subject=individual/foo/sumthin&property=individual_property/foo/other&object=individual/foo/property=other&value=foo",
    // output.get(15).makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/individual_fact/foo/subject=individual/foo/sumthin&property=individual_property/foo/other&object=individual/foo/property=other&value=foo
    // derived from string_fact/foo/foo",
    // output.get(16).makeKey().toString());
    // assertEquals("individual_property/fooP/bazP", output.get(17).makeKey().toString());
    // assertEquals(
    // "derived_provenance/fooP/individual_property/fooP/bazP derived from barP/fooP/bazP",
    // output.get(18).makeKey().toString());
    // assertEquals("class/fooP/Range of a property", output.get(19).makeKey().toString());
    // assertEquals(
    // "derived_provenance/fooP/class/fooP/Range of a property derived from barP/fooP/bazP",
    // output.get(20).makeKey().toString());
    // assertEquals("individual/foo/property=bazP&value=some+string+value", output.get(21)
    // .makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/individual/foo/property=bazP&value=some+string+value derived from
    // bar/foo/baz",
    // output.get(22).makeKey().toString());
    // assertEquals(
    // "class_member/foo/individual/foo/property=bazP&value=some+string+value member of
    // class/fooP/Range of a property",
    // output.get(23).makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/individual/foo/property=bazP&value=some+string+value derived from
    // bar/foo/baz",
    // output.get(24).makeKey().toString());
    // assertEquals(
    // "individual_fact/foo/subject=subj bar/subj foo/subj
    // baz&property=individual_property/fooP/bazP&object=individual/foo/property=bazP&value=some+string+value",
    // output.get(25).makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/individual_fact/foo/subject=subj bar/subj foo/subj
    // baz&property=individual_property/fooP/bazP&object=individual/foo/property=bazP&value=some+string+value
    // derived from bar/foo/baz",
    // output.get(26).makeKey().toString());
    // assertEquals("individual/foo/property=bazP&value=another+string+value", output.get(27)
    // .makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/individual/foo/property=bazP&value=another+string+value derived from
    // bar/foo/baazie",
    // output.get(28).makeKey().toString());
    // assertEquals(
    // "class_member/foo/individual/foo/property=bazP&value=another+string+value member of
    // class/fooP/Range of a property",
    // output.get(29).makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/individual/foo/property=bazP&value=another+string+value derived from
    // bar/foo/baazie",
    // output.get(30).makeKey().toString());
    // assertEquals(
    // "individual_fact/foo/subject=subj bar/subj foo/subj
    // buzz&property=individual_property/fooP/bazP&object=individual/foo/property=bazP&value=another+string+value",
    // output.get(31).makeKey().toString());
    // assertEquals(
    // "derived_provenance/foo/individual_fact/foo/subject=subj bar/subj foo/subj
    // buzz&property=individual_property/fooP/bazP&object=individual/foo/property=bazP&value=another+string+value
    // derived from bar/foo/baazie",
    // output.get(32).makeKey().toString());
    // }
    // // public void testIndividuals() {
    // // List<ModelEntity> result = new ArrayList<ModelEntity>();
    // //
    // // StringToIndividualFactConverter converter = new StringToIndividualFactConverter();
    // // converter.setIso8601Date("my date");
    // //
    // // // note the property key is not actually read from the fact; it is assumed that the
    // // // framework does that.
    // //
    // // List<ModelEntity> newStuff = converter.convert(inputFact, inputProperty);
    // //
    // // assertNotNull(newStuff);
    // // result.addAll(newStuff);
    // //
    // // newStuff = converter.convert(inputFact, inputProperty);
    // //
    // // assertNotNull(newStuff);
    // // result.addAll(newStuff);
    // //
    // // assertEquals(16, result.size());
    // //
    // // ModelEntity me = result.get(0);
    // // assertTrue(me.getClass().getName(), me instanceof IndividualProperty);
    // // IndividualProperty ip = (IndividualProperty) me;
    // // assertEquals("individual_property/fooP/bazP", ip.getKey().toString());
    // // assertEquals("property name", ip.getName()); // same name
    // // me = result.get(1);
    // // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // // DerivedProvenance dp = (DerivedProvenance) me;
    // // assertEquals(
    // // "derived_provenance/fooP/individual_property/fooP/bazP derived from barP/fooP/bazP", dp
    // // .getKey().toString());
    // // assertEquals("individual_property/fooP/bazP derived from barP/fooP/bazP", dp.getName());
    // // me = result.get(2);
    // // assertTrue(me.getClass().getName(), me instanceof Class);
    // // Class cl = (Class) me;
    // // assertEquals("class/fooP/Range of property name", cl.getKey().toString());
    // // assertEquals("Range of property name", cl.getName());
    // //
    // // me = result.get(3);
    // // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // // DerivedProvenance dp2 = (DerivedProvenance) me;
    // // assertEquals(
    // // "derived_provenance/fooP/class/fooP/Range of property name derived from barP/fooP/bazP",
    // // dp2.getKey().toString());
    // // assertEquals("class/fooP/Range of property name derived from barP/fooP/bazP",
    // dp2.getName());
    // //
    // // me = result.get(4);
    // // assertTrue(me.getClass().getName(), me instanceof Individual);
    // // Individual in = (Individual) me;
    // // assertEquals("individual/foo/property=bazP&value=some+string+value",
    // in.getKey().toString());
    // // assertEquals("some string value", in.getName());
    // //
    // // me = result.get(5);
    // // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // // DerivedProvenance dp3 = (DerivedProvenance) me;
    // // assertEquals(
    // // "derived_provenance/foo/individual/foo/property=bazP&value=some+string+value derived from
    // // bar/foo/baz",
    // // dp3.getKey().toString());
    // // assertEquals(
    // // "individual/foo/property=bazP&value=some+string+value derived from bar/foo/baz", dp3
    // // .getName());
    // //
    // // me = result.get(6);
    // // assertTrue(me.getClass().getName(), me instanceof ClassMember);
    // // assertEquals(
    // // "class_member/foo/individual/foo/property=bazP&value=some+string+value member of
    // // class/fooP/Range of property name",
    // // me.getKey().toString());
    // // assertEquals(
    // // "individual/foo/property=bazP&value=some+string+value member of class/fooP/Range of
    // property
    // // name",
    // // me.getName());
    // //
    // // me = result.get(7);
    // // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // // DerivedProvenance dp4 = (DerivedProvenance) me;
    // // assertEquals(
    // // "derived_provenance/foo/individual/foo/property=bazP&value=some+string+value derived from
    // // bar/foo/baz",
    // // dp4.getKey().toString());
    // // assertEquals(
    // // "individual/foo/property=bazP&value=some+string+value derived from bar/foo/baz", dp4
    // // .getName());
    // //
    // // me = result.get(8);
    // // assertTrue(me.getClass().getName(), me instanceof IndividualFact);
    // // IndividualFact indF = (IndividualFact) me;
    // // assertEquals(
    // //
    // "individual_fact/foo/subject=subj+bar%2Fsubj+foo%2Fsubj+baz&property=individual_property%2FfooP%2FbazP&value=some+string+value",
    // // indF.getKey().toString());
    // // assertEquals(
    // //
    // "subject=subj+bar%2Fsubj+foo%2Fsubj+baz&property=individual_property%2FfooP%2FbazP&value=some+string+value",
    // // indF.getName());
    // //
    // // me = result.get(9);
    // // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // // DerivedProvenance dp5 = (DerivedProvenance) me;
    // // assertEquals(
    // //
    // "derived_provenance/foo/individual_fact/foo/subject=subj+bar%2Fsubj+foo%2Fsubj+baz&property=individual_property%2FfooP%2FbazP&value=some+string+value
    // // derived from bar/foo/baz",
    // // dp5.getKey().toString());
    // // assertEquals(
    // //
    // "individual_fact/foo/subject=subj+bar%2Fsubj+foo%2Fsubj+baz&property=individual_property%2FfooP%2FbazP&value=some+string+value
    // // derived from bar/foo/baz",
    // // dp5.getName());
    // //
    // // me = result.get(10);
    // // assertTrue(me.getClass().getName(), me instanceof Individual);
    // // Individual in2 = (Individual) me;
    // // assertEquals("individual/foo/property=bazP&value=another+string+value", in2.getKey()
    // // .toString());
    // // assertEquals("another string value", in2.getName());
    // //
    // // me = result.get(11);
    // // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // // DerivedProvenance dp6 = (DerivedProvenance) me;
    // // assertEquals(
    // // "derived_provenance/foo/individual/foo/property=bazP&value=another+string+value derived
    // from
    // // bar/foo/baazie",
    // // dp6.getKey().toString());
    // // assertEquals(
    // // "individual/foo/property=bazP&value=another+string+value derived from bar/foo/baazie",
    // // dp6.getName());
    // //
    // // me = result.get(12);
    // // assertTrue(me.getClass().getName(), me instanceof ClassMember);
    // // assertEquals(
    // // "class_member/foo/individual/foo/property=bazP&value=another+string+value member of
    // // class/fooP/Range of property name",
    // // me.getKey().toString());
    // // assertEquals(
    // // "individual/foo/property=bazP&value=another+string+value member of class/fooP/Range of
    // // property name",
    // // me.getName());
    // //
    // // me = result.get(13);
    // // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // // DerivedProvenance dp7 = (DerivedProvenance) me;
    // // assertEquals(
    // // "derived_provenance/foo/individual/foo/property=bazP&value=another+string+value derived
    // from
    // // bar/foo/baazie",
    // // dp7.getKey().toString());
    // // assertEquals(
    // // "individual/foo/property=bazP&value=another+string+value derived from bar/foo/baazie",
    // // dp7.getName());
    // //
    // // me = result.get(14);
    // // assertTrue(me.getClass().getName(), me instanceof IndividualFact);
    // // IndividualFact indF2 = (IndividualFact) me;
    // // assertEquals(
    // //
    // "individual_fact/foo/subject=subj+bar%2Fsubj+foo%2Fsubj+buzz&property=individual_property%2FfooP%2FbazP&value=another+string+value",
    // // indF2.getKey().toString());
    // // assertEquals(
    // //
    // "subject=subj+bar%2Fsubj+foo%2Fsubj+buzz&property=individual_property%2FfooP%2FbazP&value=another+string+value",
    // // indF2.getName());
    // //
    // // me = result.get(15);
    // // assertTrue(me.getClass().getName(), me instanceof DerivedProvenance);
    // // DerivedProvenance dp8 = (DerivedProvenance) me;
    // // assertEquals(
    // //
    // "derived_provenance/foo/individual_fact/foo/subject=subj+bar%2Fsubj+foo%2Fsubj+buzz&property=individual_property%2FfooP%2FbazP&value=another+string+value
    // // derived from bar/foo/baazie",
    // // dp8.getKey().toString());
    // // assertEquals(
    // //
    // "individual_fact/foo/subject=subj+bar%2Fsubj+foo%2Fsubj+buzz&property=individual_property%2FfooP%2FbazP&value=another+string+value
    // // derived from bar/foo/baazie",
    // // dp8.getName());
    // // }
}
