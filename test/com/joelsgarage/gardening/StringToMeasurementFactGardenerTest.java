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
public class StringToMeasurementFactGardenerTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }

    // // This just logs what it finds.
    // public static class FakeStringToMeasurementFactGardener extends
    // StringToMeasurementFactGardener {
    // public List<StringFact> inputFacts;
    // public List<StringProperty> inputProperties;
    // public List<String> newPropertyNames;
    // public List<String> numbers;
    // public List<ExternalKey> measurementUnitKeys;
    //
    // public FakeStringToMeasurementFactGardener(List<StringFact> f, List<StringProperty> p,
    // List<String> pn, List<String> n, List<ExternalKey> k,
    // RecordReaderFactory<ModelEntity> factory, RecordWriter<ModelEntity> writer,
    // int inlimit, int outlimit) {
    // super(factory, writer, inlimit, outlimit);
    // this.inputFacts = f;
    // this.inputProperties = p;
    // this.newPropertyNames = pn;
    // this.numbers = n;
    // this.measurementUnitKeys = k;
    // }
    //
    // @Override
    // protected List<ModelEntity> makeNewEntities(StringFact inputFact,
    // StringProperty inputProperty, String newPropertyName, String number,
    // ExternalKey measurementUnitKey) {
    // this.inputFacts.add(inputFact);
    // this.inputProperties.add(inputProperty);
    // this.newPropertyNames.add(newPropertyName);
    // this.numbers.add(number);
    // this.measurementUnitKeys.add(measurementUnitKey);
    // return null;
    // }
    // }
    //
    // public void testExtractForeignKey() throws FatalException {
    // StringFact fact =
    // new StringFact(new Individual("sumthin", "foo").getKey(), //
    // new StringProperty(new com.joelsgarage.model.Class("class-name",
    // "foo").getKey(), "how high", "foo").getKey(), "10", "foo");
    // StringToMeasurementFactGardener gardener =
    // new StringToMeasurementFactGardener(null, null, 0, 0);
    // ExternalKey extractedKey = gardener.extractForeignKey(fact);
    // assertEquals("bar/foo/baz", extractedKey.toString());
    // }
    //
    // public void testExtractForeignKeyFail() throws FatalException {
    // Individual ind = new Individual("baz", "ns");
    // StringToMeasurementFactGardener gardener =
    // new StringToMeasurementFactGardener(null, null, 0, 0);
    // ExternalKey extractedKey = gardener.extractForeignKey(ind);
    // assertNull(extractedKey);
    // }
    //
    // public void testExtractPrimaryKey() throws FatalException {
    // StringProperty prop =
    // new StringProperty(new com.joelsgarage.model.Class("class-name",
    // "foo").getKey(), "baz", "foo");
    // StringToMeasurementFactGardener gardener =
    // new StringToMeasurementFactGardener(null, null, 0, 0);
    // ExternalKey extractedKey = gardener.extractLookupKey(prop);
    // assertNotNull(extractedKey);
    // assertEquals("bar/foo/baz", extractedKey.toString());
    // }
    //
    // public void testExtractPrimaryKeyOtherType() throws FatalException {
    // Individual ind = new Individual("baz", "ns");
    // StringToMeasurementFactGardener gardener =
    // new StringToMeasurementFactGardener(null, null, 0, 0);
    // ExternalKey extractedKey = gardener.extractLookupKey(ind);
    // assertNotNull(extractedKey);
    // assertEquals("bar/foo/baz", extractedKey.toString());
    // }
    //
    // public void testLookupConstraint() {
    // StringToMeasurementFactGardener gardener =
    // new StringToMeasurementFactGardener(null, null, 0, 0);
    // assertEquals(StringProperty.class, gardener.getLookupConstraint().getClassConstraint());
    // }
    //
    // public void testMainConstraint() {
    // StringToMeasurementFactGardener gardener =
    // new StringToMeasurementFactGardener(null, null, 0, 0);
    // assertEquals(StringFact.class, gardener.getMainConstraint().getClassConstraint());
    // }
    //
    // public void testStart() throws FatalException {
    // List<ModelEntity> units = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new UnitSynonym(new StandardMeasurementUnit(new MeasurementQuantity("length",
    // "namespace").getKey(), "foot-unit", "namespace").getKey(), "foot",
    // Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, "namespace"));
    // add(new UnitSynonym(new StandardMeasurementUnit(new MeasurementQuantity("length",
    // "namespace").getKey(), "meter-unit", "namespace").getKey(), "meter",
    // Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, "namespace"));
    // }
    // };
    //
    // ListRecordReader<ModelEntity> reader = new ListRecordReader<ModelEntity>(units);
    //
    // Map<ReaderConstraint, RecordReader<ModelEntity>> map =
    // new HashMap<ReaderConstraint, RecordReader<ModelEntity>>();
    // map.put(new ReaderConstraint(UnitSynonym.class), reader);
    //
    // RecordReaderFactory<ModelEntity> factory = new MapRecordReaderFactory<ModelEntity>(map);
    //
    // StringToMeasurementFactGardener gardener =
    // new StringToMeasurementFactGardener(factory, null, 0, 0);
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
    // List<ModelEntity> affine = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new AffineMeasurementUnit(
    // Double.valueOf(3.2808), //
    // new StandardMeasurementUnit(//
    // new MeasurementQuantity("length", "foo").getKey(), "meter", "foo").getKey(),
    // Double.valueOf(0.0), "foo"));
    // }
    // };
    // List<ModelEntity> standard = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new StandardMeasurementUnit(new MeasurementQuantity("name", "ns").getKey(),
    // "unit-name", "foo"));
    // }
    // };
    //
    // List<ModelEntity> units = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new UnitSynonym(new StandardMeasurementUnit(new MeasurementQuantity("length",
    // "namespace").getKey(), "foot-unit", "namespace").getKey(), "foot",
    // Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, "namespace"));
    // add(new UnitSynonym(new StandardMeasurementUnit(new MeasurementQuantity("length",
    // "namespace").getKey(), "meter-unit", "namespace").getKey(), "meter",
    // Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, "namespace"));
    // }
    // };
    // List<ModelEntity> facts = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new StringFact(new Individual("sumthin", "foo").getKey(), //
    // new StringProperty( //
    // new com.joelsgarage.model.Class("class-name", "foo")
    // .getKey(), "how high", "foo").getKey(), "10", "foo"));
    // add(new StringFact(new Individual("sumthin", "foo").getKey(), //
    // new StringProperty( //
    // new com.joelsgarage.model.Class("class-name", "foo")
    // .getKey(), "how wide", "foo").getKey(), "5", "foo"));
    // add(new StringFact(new Individual("sumthin", "foo").getKey(), //
    // new StringProperty( //
    // new com.joelsgarage.model.Class("class-name", "foo")
    // .getKey(), "other", "foo").getKey(), "foo-value", "foo"));
    // }
    // };
    // List<ModelEntity> properties = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // add(new StringProperty(new com.joelsgarage.model.Class("sumthin",
    // "foo").getKey(), "height (meter)", "foo"));
    // add(new StringProperty(new com.joelsgarage.model.Class("sumthin",
    // "foo").getKey(), "length (foot)", "foo"));
    // add(new StringProperty(new com.joelsgarage.model.Class("sumthin",
    // "foo").getKey(), "length (foo)", "foo"));
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
    // StringToMeasurementFactGardener gardener =
    // new StringToMeasurementFactGardener(factory, writer, 0, 0);
    //
    // Logger.getLogger(StringToMeasurementFactGardenerTest.class).info("Just before run()");
    // try {
    // gardener.run();
    // } catch (FatalException e) {
    // e.printStackTrace();
    // fail();
    // }
    //
    // List<ModelEntity> output = writer.getList();
    // assertNotNull(output);
    // assertEquals(5, output.size());
    // assertEquals("user/internal-agent/StringToMeasurementFactConverter", output.get(0).getKey()
    // .toString());
    // assertEquals(
    // "quantity_property/foo/length for quantity measurement_quantity/ns/name in domain
    // class/foo/sumthin",
    // output.get(1).getKey().toString());
    // assertEquals(
    // "derived_provenance/foo/quantity_property/foo/length for quantity
    // measurement_quantity/ns/name in domain class/foo/sumthin derived from string_property/foo/how
    // wide",
    // output.get(2).getKey().toString());
    // assertEquals(
    // "quantity_fact/foo/subject=individual/foo/sumthin&property=quantity_property/foo/length+for+quantity+measurement_quantity%2Fns%2Fname+in+domain+class%2Ffoo%2Fsumthin&value=5.0",
    // output.get(3).getKey().toString());
    // assertEquals(
    // "derived_provenance/foo/quantity_fact/foo/subject=individual/foo/sumthin&property=quantity_property/foo/length+for+quantity+measurement_quantity%2Fns%2Fname+in+domain+class%2Ffoo%2Fsumthin&value=5.0
    // derived from string_fact/foo/more than 4",
    // output.get(4).getKey().toString());
    //
    // }
}
