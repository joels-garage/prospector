/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class RangeBoundsProcessNodeTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // private static final int PAGE_SIZE = 1000;
    // private AlternativeStore store;
    // private QuantityFactUtil util;
    //
    // private RecordReader<QuantityFact> reader;
    // private SingleRecordWriter<RangeBounds> writer;
    // private ProcessNode<QuantityFact, RangeBounds> processNode;
    // private List<QuantityFact> input = new ArrayList<QuantityFact>();
    //
    // @Override
    // protected void setUp() {
    // setStore(new StaticAlternativeStore());
    // setUtil(new QuantityFactUtil(getStore().getMeasurementUnitList(PAGE_SIZE)));
    // }
    //
    // /**
    // * Verify max and min
    // */
    // public void testGetRangeBoundsQuantity() {
    // QuantityFact fact = new QuantityFact();
    // fact.setValue(Double.valueOf(1.0));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getInput().add(fact);
    //
    // fact = new QuantityFact();
    // fact.setValue(Double.valueOf(-1.0));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getInput().add(fact);
    //
    // fact = new QuantityFact();
    // fact.setValue(Double.valueOf(2.0));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getInput().add(fact);
    //
    // setReader(new ListRecordReader<QuantityFact>(getInput()));
    // setWriter(new SingleRecordWriter<RangeBounds>());
    // setProcessNode(new RangeBoundsProcessNode(getReader(), getWriter(), 0, 0, getUtil()));
    // getProcessNode().run();
    //
    // assertEquals(-1.0, getWriter().getRecord().getMin(), 0.001);
    // assertEquals(2.0, getWriter().getRecord().getMax(), 0.001);
    // }
    //
    // /**
    // * Verify max, min, and scaling with mixed units
    // */
    // public void testGetRangeBoundsMeasurement() {
    // QuantityFact fact = new QuantityFact();
    // fact.setValue(Double.valueOf(3.0));
    // fact.setExpressedUnitKey(new ExternalKey("", ExternalKey.MEASUREMENT_UNIT_TYPE, "foot"));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getInput().add(fact);
    //
    // fact = new QuantityFact();
    // fact.setValue(Double.valueOf(-1.0));
    // fact.setExpressedUnitKey(new ExternalKey("", ExternalKey.MEASUREMENT_UNIT_TYPE, "foot"));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getInput().add(fact);
    //
    // fact = new QuantityFact();
    // fact.setValue(Double.valueOf(1.0));
    // fact.setExpressedUnitKey(new ExternalKey("", ExternalKey.MEASUREMENT_UNIT_TYPE, "meter"));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getInput().add(fact);
    //
    // setReader(new ListRecordReader<QuantityFact>(getInput()));
    // setWriter(new SingleRecordWriter<RangeBounds>());
    // setProcessNode(new RangeBoundsProcessNode(getReader(), getWriter(), 0, 0, getUtil()));
    // getProcessNode().run();
    //
    // assertEquals(-0.3048, getWriter().getRecord().getMin(), 0.001);
    // assertEquals(1.0, getWriter().getRecord().getMax(), 0.001);
    // }
    //
    // /**
    // * Verify failure for mixed-type list
    // */
    // public void testGetRangeBoundsMixed() {
    // QuantityFact fact = new QuantityFact();
    // fact.setValue(Double.valueOf(3.0));
    // fact.setExpressedUnitKey(new ExternalKey("", ExternalKey.MEASUREMENT_UNIT_TYPE, "foot"));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getInput().add(fact);
    //
    // QuantityFact qf = new QuantityFact();
    // qf.setValue(Double.valueOf(-1.0));
    // qf.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getInput().add(qf);
    //
    // fact = new QuantityFact();
    // fact.setValue(Double.valueOf(1.0));
    // fact.setExpressedUnitKey(new ExternalKey("", ExternalKey.MEASUREMENT_UNIT_TYPE, "meter"));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getInput().add(fact);
    //
    // setReader(new ListRecordReader<QuantityFact>(getInput()));
    // setWriter(new SingleRecordWriter<RangeBounds>());
    // setProcessNode(new RangeBoundsProcessNode(getReader(), getWriter(), 0, 0, getUtil()));
    // getProcessNode().run();
    //
    // assertFalse(getWriter().getRecord().isValid());
    // }
    //
    // public RecordReader<QuantityFact> getReader() {
    // return this.reader;
    // }
    //
    // public void setReader(RecordReader<QuantityFact> reader) {
    // this.reader = reader;
    // }
    //
    // public SingleRecordWriter<RangeBounds> getWriter() {
    // return this.writer;
    // }
    //
    // public void setWriter(SingleRecordWriter<RangeBounds> writer) {
    // this.writer = writer;
    // }
    //
    // public ProcessNode<QuantityFact, RangeBounds> getProcessNode() {
    // return this.processNode;
    // }
    //
    // public void setProcessNode(ProcessNode<QuantityFact, RangeBounds> processNode) {
    // this.processNode = processNode;
    // }
    //
    // public List<QuantityFact> getInput() {
    //        return this.input;
    //    }
    //
    //    public void setInput(List<QuantityFact> input) {
    //        this.input = input;
    //    }
    //
    //    public AlternativeStore getStore() {
    //        return this.store;
    //    }
    //
    //    public void setStore(AlternativeStore store) {
    //        this.store = store;
    //    }
    //
    //    public QuantityFactUtil getUtil() {
    //        return this.util;
    //    }
    //
    //    public void setUtil(QuantityFactUtil util) {
    //        this.util = util;
    //    }

}
