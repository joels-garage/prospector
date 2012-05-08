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
public class StringToMeasurementFactConverterTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // // This just logs what it finds.
    // public static class FakeStringToMeasurementFactConverter extends
    // StringToMeasurementFactConverter {
    // public List<StringFact> inputFacts;
    // public List<StringProperty> inputProperties;
    // public List<String> newPropertyNames;
    // public List<String> numbers;
    // public List<ExternalKey> measurementUnitKeys;
    //
    // public FakeStringToMeasurementFactConverter(List<StringFact> f, List<StringProperty> p,
    // List<String> pn, List<String> n, List<ExternalKey> k,
    // Map<ExternalKey, ModelEntity> affineMeasurementUnitMap,
    // Map<ExternalKey, ModelEntity> measurementQuantityMap,
    // Map<ExternalKey, ModelEntity> standardMeasurementUnitMap,
    // Map<ExternalKey, ModelEntity> unitSynonymMap) {
    // super(affineMeasurementUnitMap, measurementQuantityMap, standardMeasurementUnitMap,
    // unitSynonymMap);
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
    // // public void testExtractForeignKey() {
    // // StringFact fact = new StringFact();
    // // fact.setPropertyKey(new ExternalKey("foo", "bar", "baz"));
    // // StringToMeasurementFactGardener gardener = new StringToMeasurementFactGardener(null, null,
    // // 0, 0);
    // // ExternalKey extractedKey = gardener.extractForeignKey(fact);
    // // assertEquals("bar/foo/baz", extractedKey.toString());
    // // }
    // //
    // // public void testExtractForeignKeyFail() {
    // // Individual ind = new Individual();
    // // ind.setKey(new ExternalKey("foo", "bar", "baz"));
    // // StringToMeasurementFactGardener gardener = new StringToMeasurementFactGardener(null, null,
    // // 0, 0);
    // // ExternalKey extractedKey = gardener.extractForeignKey(ind);
    // // assertNull(extractedKey);
    // // }
    // //
    // // public void testExtractPrimaryKey() {
    // // StringProperty prop = new StringProperty();
    // // prop.setKey(new ExternalKey("foo", "bar", "baz"));
    // // StringToMeasurementFactGardener gardener = new StringToMeasurementFactGardener(null, null,
    // // 0, 0);
    // // ExternalKey extractedKey = gardener.extractLookupKey(prop);
    // // assertNotNull(extractedKey);
    // // assertEquals("bar/foo/baz", extractedKey.toString());
    // // }
    //
    // // public void testExtractPrimaryKeyOtherType() {
    // // Individual ind = new Individual();
    // // ind.setKey(new ExternalKey("foo", "bar", "baz"));
    // // StringToMeasurementFactGardener gardener = new StringToMeasurementFactGardener(null, null,
    // // 0, 0);
    // // ExternalKey extractedKey = gardener.extractLookupKey(ind);
    // // assertNotNull(extractedKey);
    // // assertEquals("bar/foo/baz", extractedKey.toString());
    // // }
    // //
    // // public void testLookupConstraint() {
    // // StringToMeasurementFactGardener gardener = new StringToMeasurementFactGardener(null, null,
    // // 0, 0);
    // // assertEquals(StringProperty.class, gardener.getLookupConstraint().getClassConstraint());
    // // }
    // //
    // // public void testMainConstraint() {
    // // StringToMeasurementFactGardener gardener = new StringToMeasurementFactGardener(null, null,
    // // 0, 0);
    // // assertEquals(StringFact.class, gardener.getMainConstraint().getClassConstraint());
    // // }
    //
    // public void testMakeSerializedMatchString() {
    // StringFact fact = new StringFact();
    // StringProperty property = new StringProperty();
    // fact.setValue("foo/value");
    // property.setName("bar&name");
    // String serial = StringToMeasurementFactConverter.makeSerializedMatchString(fact, property);
    // assertEquals("bar%26name&foo%2Fvalue", serial);
    // }
    //
    // public void testStart() {
    // List<ModelEntity> units = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // UnitSynonym u = new UnitSynonym();
    // u.setKey(new ExternalKey("foo", "unit_synonym", "foot unit"));
    // u.setValue("foot");
    // u.setMeasurementUnitKey(new ExternalKey("foo", "bar", "baz"));
    // add(u);
    // u = new UnitSynonym();
    // u.setKey(new ExternalKey("foo", "unit_synonym", "meter unit"));
    // u.setValue("meter");
    // u.setMeasurementUnitKey(new ExternalKey("squeeze", "the", "charmin"));
    // add(u);
    // }
    // };
    //
    // //
    // // ListRecordReader<ModelEntity> reader = new ListRecordReader<ModelEntity>(units);
    //
    // // Map<ReaderConstraint, RecordReader<ModelEntity>> map =
    // // new HashMap<ReaderConstraint, RecordReader<ModelEntity>>();
    // // map.put(new ReaderConstraint(UnitSynonym.class), reader);
    // //
    // // RecordReaderFactory<ModelEntity> factory = new MapRecordReaderFactory<ModelEntity>(map);
    //
    // Map<ExternalKey, ModelEntity> unitsMap = new HashMap<ExternalKey, ModelEntity>();
    // unitsMap.put(units.get(0).getKey(), units.get(0));
    // unitsMap.put(units.get(1).getKey(), units.get(1));
    //
    // StringToMeasurementFactConverter converter =
    // new StringToMeasurementFactConverter(null, null, null, unitsMap);
    //
    // try {
    // converter.start();
    // } catch (FatalException e) {
    // e.printStackTrace();
    // fail();
    // }
    // List<QuantityPattern> pList = converter.getPatterns();
    // assertNotNull(pList);
    // assertEquals(2, pList.size());
    // assertEquals(
    // "^([^&()]*)%28\\+*meter\\+*%29\\+*&\\+*([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)$",
    // pList.get(0).getPattern().pattern());
    // assertEquals("the/squeeze/charmin", pList.get(0).getMeasurementUnitKey().toString());
    // assertEquals(
    // "^([^&()]*)%28\\+*foot\\+*%29\\+*&\\+*([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)$",
    // pList.get(1).getPattern().pattern());
    // assertEquals("bar/foo/baz", pList.get(1).getMeasurementUnitKey().toString());
    // }
    //
    // /** Verify matching the constructed strings and patterns */
    // public void testMatching() {
    // List<ModelEntity> units = new ArrayList<ModelEntity>() {
    // private static final long serialVersionUID = 1L;
    // {
    // UnitSynonym u = new UnitSynonym();
    // u.setKey(new ExternalKey("foo", "unit_synonym", "foot unit"));
    // u.setValue("foot");
    // u.setMeasurementUnitKey(new ExternalKey("foo", "bar", "baz"));
    // add(u);
    // u = new UnitSynonym();
    // u.setKey(new ExternalKey("foo", "unit_synonym", "meter unit"));
    // u.setValue("meter");
    // u.setMeasurementUnitKey(new ExternalKey("squeeze", "the", "charmin"));
    // add(u);
    // }
    // };
    // List<StringFact> facts = new ArrayList<StringFact>() {
    // private static final long serialVersionUID = 1L;
    // {
    // StringFact fact = new StringFact();
    // fact.setKey(new ExternalKey("foo", "string_fact", "more than 9"));
    // fact.setPropertyKey(new ExternalKey("foo", "string_property", "how high"));
    // fact.setValue("10");
    // add(fact);
    // fact = new StringFact();
    // fact.setKey(new ExternalKey("foo", "string_fact", "more than 4"));
    // fact.setPropertyKey(new ExternalKey("foo", "string_property", "how wide"));
    // fact.setValue("5");
    // add(fact);
    // fact = new StringFact();
    // fact.setKey(new ExternalKey("foo", "string_fact", "foo"));
    // fact.setPropertyKey(new ExternalKey("foo", "string_property", "other"));
    // fact.setValue("foo");
    // add(fact);
    // }
    // };
    // List<StringProperty> properties = new ArrayList<StringProperty>() {
    // private static final long serialVersionUID = 1L;
    // {
    // StringProperty property = new StringProperty();
    // property.setKey(new ExternalKey("foo", "string_property", "how high"));
    // property.setName("height (meter)");
    // add(property);
    // property = new StringProperty();
    // property.setKey(new ExternalKey("foo", "string_property", "how wide"));
    // property.setName("length (foot)");
    // add(property);
    // property = new StringProperty();
    // property.setKey(new ExternalKey("foo", "string_property", "other"));
    // property.setName("length (foo)");
    // add(property);
    // }
    // };
    //
    // Map<ExternalKey, ModelEntity> unitMap = new HashMap<ExternalKey, ModelEntity>();
    // // Map<ExternalKey, ModelEntity> factMap = new HashMap<ExternalKey, ModelEntity>();
    // // Map<ExternalKey, ModelEntity> propertyMap = new HashMap<ExternalKey, ModelEntity>();
    // for (ModelEntity e : units) {
    // unitMap.put(e.getKey(), e);
    // }
    // // for (ModelEntity e : facts) {
    // // factMap.put(e.getKey(), e);
    // // }
    // // for (ModelEntity e : properties) {
    // // propertyMap.put(e.getKey(), e);
    // // }
    //
    // // ListRecordReader<ModelEntity> unitReader = new ListRecordReader<ModelEntity>(units);
    // // ListRecordReader<ModelEntity> factReader = new ListRecordReader<ModelEntity>(facts);
    // // ListRecordReader<ModelEntity> propertyReader = new
    // // ListRecordReader<ModelEntity>(properties);
    //
    // // // This is incomplete but we should fail gracefully.
    // // MapRecordReaderFactory<ModelEntity> factory = new MapRecordReaderFactory<ModelEntity>();
    // //
    // // factory.putReader(new ReaderConstraint(UnitSynonym.class), unitReader);
    // // factory.putReader(new ReaderConstraint(StringFact.class), factReader);
    // // factory.putReader(new ReaderConstraint(StringProperty.class), propertyReader);
    //
    // List<StringFact> inputFacts = new ArrayList<StringFact>();
    // List<StringProperty> inputProperties = new ArrayList<StringProperty>();
    // List<String> newPropertyNames = new ArrayList<String>();
    // List<String> numbers = new ArrayList<String>();
    // List<ExternalKey> measurementUnitKeys = new ArrayList<ExternalKey>();
    //
    // // ListRecordWriter<ModelEntity> writer = new ListRecordWriter<ModelEntity>();
    //
    // StringToMeasurementFactConverter converter =
    // new FakeStringToMeasurementFactConverter(inputFacts, inputProperties, newPropertyNames,
    // numbers, measurementUnitKeys, null, null, null, unitMap);
    // try {
    // converter.start();
    // } catch (FatalException e1) {
    // e1.printStackTrace();
    // fail();
    // }
    //
    // // try {
    // converter.convert(facts.get(0), properties.get(0));
    // converter.convert(facts.get(1), properties.get(1));
    // converter.convert(facts.get(2), properties.get(2));
    //
    // // } catch (FatalException e) {
    // // e.printStackTrace();
    // // fail();
    // // }
    //
    // assertEquals(2, inputFacts.size());
    // assertEquals(2, inputProperties.size());
    // assertEquals(2, newPropertyNames.size());
    // assertEquals(2, numbers.size());
    // assertEquals(2, measurementUnitKeys.size());
    // assertEquals("height", newPropertyNames.get(0));
    // assertEquals("length", newPropertyNames.get(1));
    // assertEquals("10", numbers.get(0));
    // assertEquals("5", numbers.get(1));
    // assertEquals("the/squeeze/charmin", measurementUnitKeys.get(0).toString());
    // assertEquals("bar/foo/baz", measurementUnitKeys.get(1).toString());
    // }
    //
    // // public void testEndToEnd() {
    // // List<ModelEntity> affine = new ArrayList<ModelEntity>() {
    // // private static final long serialVersionUID = 1L;
    // // {
    // // AffineMeasurementUnit u = new AffineMeasurementUnit();
    // // u.setKey(new ExternalKey("foo", ExternalKey.AFFINE_MEASUREMENT_UNIT_TYPE, "baz"));
    // // u.setStandardMeasurementUnitKey(new ExternalKey("squeeze",
    // // ExternalKey.AFFINE_MEASUREMENT_UNIT_TYPE, "charmin"));
    // // add(u);
    // // }
    // // };
    // // List<ModelEntity> standard = new ArrayList<ModelEntity>() {
    // // private static final long serialVersionUID = 1L;
    // // {
    // // StandardMeasurementUnit u = new StandardMeasurementUnit();
    // // u.setKey(new ExternalKey("squeeze", ExternalKey.AFFINE_MEASUREMENT_UNIT_TYPE,
    // // "charmin"));
    // // u.setMeasurementQuantityKey(new ExternalKey("ns",
    // // ExternalKey.MEASUREMENT_QUANTITY_TYPE, "name"));
    // // add(u);
    // //
    // // }
    // // };
    // //
    // // List<ModelEntity> units = new ArrayList<ModelEntity>() {
    // // private static final long serialVersionUID = 1L;
    // // {
    // // UnitSynonym u = new UnitSynonym();
    // // u.setKey(new ExternalKey("foo", "unit_synonym", "foot unit"));
    // // u.setValue("foot");
    // // u.setMeasurementUnitKey(new ExternalKey("foo",
    // // ExternalKey.AFFINE_MEASUREMENT_UNIT_TYPE, "baz"));
    // // add(u);
    // // u = new UnitSynonym();
    // // u.setKey(new ExternalKey("foo", "unit_synonym", "meter unit"));
    // // u.setValue("meter");
    // // u.setMeasurementUnitKey(new ExternalKey("squeeze",
    // // ExternalKey.STANDARD_MEASUREMENT_UNIT_TYPE, "charmin"));
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
    // // ListRecordReader<ModelEntity> affineReader = new ListRecordReader<ModelEntity>(affine);
    // // ListRecordReader<ModelEntity> standardReader = new
    // ListRecordReader<ModelEntity>(standard);
    // // ListRecordReader<ModelEntity> unitReader = new ListRecordReader<ModelEntity>(units);
    // // ListRecordReader<ModelEntity> factReader = new ListRecordReader<ModelEntity>(facts);
    // // ListRecordReader<ModelEntity> propertyReader = new
    // ListRecordReader<ModelEntity>(properties);
    // //
    // // // This is incomplete but we should fail gracefully.
    // // MapRecordReaderFactory<ModelEntity> factory = new MapRecordReaderFactory<ModelEntity>();
    // //
    // // factory.putReader(new ReaderConstraint(AffineMeasurementUnit.class), affineReader);
    // // factory.putReader(new ReaderConstraint(StandardMeasurementUnit.class), standardReader);
    // // factory.putReader(new ReaderConstraint(UnitSynonym.class), unitReader);
    // // factory.putReader(new ReaderConstraint(StringFact.class), factReader);
    // // factory.putReader(new ReaderConstraint(StringProperty.class), propertyReader);
    // //
    // // ListRecordWriter<ModelEntity> writer = new ListRecordWriter<ModelEntity>();
    // //
    // // StringToMeasurementFactGardener gardener = new StringToMeasurementFactGardener(factory,
    // // writer, 0, 0);
    // //
    // // Logger.getLogger(StringToMeasurementFactGardenerTest.class).info("Just before run()");
    // // try {
    // // gardener.run();
    // // } catch (FatalException e) {
    // // e.printStackTrace();
    // // fail();
    // // }
    // //
    // // List<ModelEntity> output = writer.getList();
    // // assertNotNull(output);
    // // assertEquals(4, output.size());
    // // assertEquals(
    // // "quantity_property/foo/length for quantity measurement_quantity/ns/name in domain //",
    // // output.get(0).getKey().toString());
    // // assertEquals(
    // // "derived_provenance/foo/quantity_property/foo/length for quantity
    // // measurement_quantity/ns/name in domain // derived from string_property/foo/how wide",
    // // output.get(1).getKey().toString());
    // // assertEquals(
    // //
    // "quantity_fact/foo/subject=%2F%2F&property=length+for+quantity+measurement_quantity%2Fns%2Fname+in+domain+%2F%2F&value=5",
    // // output.get(2).getKey().toString());
    // // assertEquals(
    // //
    // "derived_provenance/foo/quantity_fact/foo/subject=%2F%2F&property=length+for+quantity+measurement_quantity%2Fns%2Fname+in+domain+%2F%2F&value=5
    // // derived from string_fact/foo/more than 4",
    // // output.get(3).getKey().toString());
    // // }
}
