/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.gardening;

import junit.framework.TestCase;

/**
 * Test the fact value split gardener.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class FactValueSplitGardenerTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    // RecordReader<StringFact> reader;
    // ListRecordWriter<ModelEntity> writer;
    // FactValueSplitGardener gardener;
    //
    // List<StringFact> input = new ArrayList<StringFact>() {
    // private static final long serialVersionUID = 1L;
    // {
    // StringFact foo = new StringFact();
    // foo.setKey(new ExternalKey("original key namespace", "original key type",
    // "original key name"));
    // foo.setCreatorKey(new ExternalKey("creator1", "creator2", "creator3"));
    // foo.setName("some item name");
    // foo.setLastModified("foo date");
    // foo.setPropertyKey(new ExternalKey("property namespace", "property type",
    // "property name"));
    // foo.setSubjectKey(new ExternalKey("subject namespace", "subject type", "subject name"));
    // foo.setValue("foo value, bar value");
    // add(foo);
    // }
    // };
    //
    // @Override
    // protected void setUp() {
    // setReader(new ListRecordReader<StringFact>(getInput()));
    // setWriter(new ListRecordWriter<ModelEntity>());
    // setGardener(new FactValueSplitGardener(getReader(), getWriter(), 0, 0));
    // }
    //
    // /**
    // * Verifies construction, i.e. doesn't do anything.
    // */
    // public void testNothing() {
    // assertNotNull(getGardener());
    // }
    //
    // public void testSimple() {
    // // because it appears in the output
    // // String date = "my testdate";
    // // getGardener().setIso8601Date(date);
    //
    // getGardener().run();
    //
    // List<ModelEntity> output = getWriter().getList();
    // // we called output four times.
    // assertEquals(5, output.size());
    // {
    // ModelEntity foo = output.get(0);
    // assertTrue(foo.getClass().getName(), foo instanceof User);
    // User user = (User) foo;
    // assertEquals("internal-agent", user.getNamespace());
    // assertEquals("user", user.getKey().getType());
    // assertEquals("FactValueSplitter", user.getKey().getKey());
    // assertEquals(new ExternalKey("internal-agent", "user", "FactValueSplitter"), user
    // .getCreatorKey());
    // }
    // {
    // ModelEntity foo = output.get(1);
    // assertTrue(foo instanceof StringFact);
    // StringFact fact = (StringFact) foo;
    // assertEquals("original key namespace", fact.getNamespace());
    // assertEquals("string_fact", fact.getKey().getType());
    // // dunno why i did this
    // // assertEquals("original key type", fact.getKey().getType());
    //
    // assertEquals(
    // "subject=subject+type/subject+namespace/subject+name&property=property+type/property+namespace/property+name&value=foo+value",
    // fact.getKey().getKey());
    // assertEquals(new ExternalKey("internal-agent", "user", "FactValueSplitter"), fact
    // .getCreatorKey());
    // // assertEquals(date, fact.getLastModified());
    // assertEquals(new ExternalKey("subject namespace", "subject type", "subject name"), fact
    // .getSubjectKey());
    // assertEquals(new ExternalKey("property namespace", "property type", "property name"),
    // fact.getPropertyKey());
    // assertEquals("", fact.getName()); // no name for facts
    // assertEquals("foo value", fact.getValue());
    // }
    // {
    // ModelEntity foo = output.get(2);
    // assertTrue(foo instanceof DerivedProvenance);
    // DerivedProvenance pro = (DerivedProvenance) foo;
    // assertEquals("original key namespace", pro.getNamespace());
    // assertEquals("derived_provenance", pro.getKey().getType());
    // assertEquals(
    // "string_fact/original key
    // namespace/subject=subject+type/subject+namespace/subject+name&property=property+type/property+namespace/property+name&value=foo+value
    // derived from original key type/original key namespace/original key name",
    // pro.getKey().getKey());
    // assertEquals(new ExternalKey("internal-agent", "user", "FactValueSplitter"), pro
    // .getCreatorKey());
    // // assertEquals(date, pro.getLastModified());
    // assertEquals(
    // new ExternalKey(
    // "original key namespace",
    // "string_fact",
    // "subject=subject+type/subject+namespace/subject+name&property=property+type/property+namespace/property+name&value=foo+value")
    // .toString(), pro.getSubjectKey().toString());
    // assertEquals(new ExternalKey("original key namespace", "original key type",
    // "original key name"), pro.getAntecedentKey());
    // assertEquals(
    // "string_fact/original key
    // namespace/subject=subject+type/subject+namespace/subject+name&property=property+type/property+namespace/property+name&value=foo+value
    // derived from original key type/original key namespace/original key name",
    // pro.getName());
    // assertEquals("FactValueSplitter", pro.getDerivation());
    // }
    // {
    // ModelEntity foo = output.get(3);
    // assertTrue(foo instanceof StringFact);
    // StringFact fact = (StringFact) foo;
    // assertEquals("original key namespace", fact.getNamespace());
    // assertEquals("string_fact", fact.getKey().getType());
    // assertEquals(
    // "subject=subject+type/subject+namespace/subject+name&property=property+type/property+namespace/property+name&value=bar+value",
    // fact.getKey().getKey());
    // assertEquals(new ExternalKey("internal-agent", "user", "FactValueSplitter"), fact
    // .getCreatorKey());
    // // assertEquals(date, fact.getLastModified());
    // assertEquals(new ExternalKey("subject namespace", "subject type", "subject name"), fact
    // .getSubjectKey());
    // assertEquals(new ExternalKey("property namespace", "property type", "property name"),
    // fact.getPropertyKey());
    // assertEquals("", fact.getName()); // no name
    // assertEquals("bar value", fact.getValue());
    // }
    // {
    // ModelEntity foo = output.get(4);
    // assertTrue(foo instanceof DerivedProvenance);
    // DerivedProvenance pro = (DerivedProvenance) foo;
    // assertEquals("original key namespace", pro.getNamespace());
    // assertEquals("derived_provenance", pro.getKey().getType());
    // assertEquals(
    // "string_fact/original key
    // namespace/subject=subject+type/subject+namespace/subject+name&property=property+type/property+namespace/property+name&value=bar+value
    // derived from original key type/original key namespace/original key name",
    // pro.getKey().getKey());
    // assertEquals(new ExternalKey("internal-agent", "user", "FactValueSplitter"), pro
    // .getCreatorKey());
    // // assertEquals(date, pro.getLastModified());
    // assertEquals(
    // new ExternalKey(
    // "original key namespace",
    // "string_fact",
    // "subject=subject+type/subject+namespace/subject+name&property=property+type/property+namespace/property+name&value=bar+value"),
    // pro.getSubjectKey());
    // assertEquals(new ExternalKey("original key namespace", "original key type",
    // "original key name"), pro.getAntecedentKey());
    // assertEquals(
    // "string_fact/original key
    // namespace/subject=subject+type/subject+namespace/subject+name&property=property+type/property+namespace/property+name&value=bar+value
    // derived from original key type/original key namespace/original key name",
    // pro.getName());
    // assertEquals("FactValueSplitter", pro.getDerivation());
    // }
    // }
    //
    // // Accessors
    //
    // public List<StringFact> getInput() {
    // return this.input;
    // }
    //
    // public void setInput(List<StringFact> input) {
    // this.input = input;
    // }
    //
    // public RecordReader<StringFact> getReader() {
    // return this.reader;
    // }
    //
    // public void setReader(RecordReader<StringFact> reader) {
    // this.reader = reader;
    // }
    //
    // public FactValueSplitGardener getGardener() {
    // return this.gardener;
    // }
    //
    // public void setGardener(FactValueSplitGardener gardener) {
    // this.gardener = gardener;
    // }
    //
    // public ListRecordWriter<ModelEntity> getWriter() {
    // return this.writer;
    // }
    //
    // public void setWriter(ListRecordWriter<ModelEntity> writer) {
    // this.writer = writer;
    // }
}
