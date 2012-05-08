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
public class RangeBoundsTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // private static final int PAGE_SIZE = 1000;
    // private AlternativeStore store;
    // private QuantityFactUtil util;
    // private RangeBounds rangeBounds;
    //
    // @Override
    // protected void setUp() {
    // setStore(new StaticAlternativeStore());
    // setUtil(new QuantityFactUtil(getStore().getMeasurementUnitList(PAGE_SIZE)));
    // setRangeBounds(new RangeBounds(getUtil()));
    // }
    //
    // /**
    // * Verify max and min
    // */
    // public void testGetRangeBoundsQuantity() {
    // QuantityFact fact = new QuantityFact();
    // fact.setValue(Double.valueOf(1.0));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getRangeBounds().update(fact);
    //
    // fact = new QuantityFact();
    // fact.setValue(Double.valueOf(-1.0));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getRangeBounds().update(fact);
    //
    // fact = new QuantityFact();
    // fact.setValue(Double.valueOf(2.0));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getRangeBounds().update(fact);
    //
    // assertEquals(-1.0, getRangeBounds().getMin(), 0.001);
    // assertEquals(2.0, getRangeBounds().getMax(), 0.001);
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
    // getRangeBounds().update(fact);
    //
    // fact = new QuantityFact();
    // fact.setValue(Double.valueOf(-1.0));
    // fact.setExpressedUnitKey(new ExternalKey("", ExternalKey.MEASUREMENT_UNIT_TYPE, "foot"));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getRangeBounds().update(fact);
    //
    // fact = new QuantityFact();
    // fact.setValue(Double.valueOf(1.0));
    // fact.setExpressedUnitKey(new ExternalKey("", ExternalKey.MEASUREMENT_UNIT_TYPE, "meter"));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getRangeBounds().update(fact);
    //
    // assertEquals(-0.3048, getRangeBounds().getMin(), 0.001);
    // assertEquals(1.0, getRangeBounds().getMax(), 0.001);
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
    // getRangeBounds().update(fact);
    //
    // QuantityFact qf = new QuantityFact();
    // qf.setValue(Double.valueOf(-1.0));
    // qf.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getRangeBounds().update(qf);
    //
    // fact = new QuantityFact();
    // fact.setValue(Double.valueOf(1.0));
    // fact.setExpressedUnitKey(new ExternalKey("", ExternalKey.MEASUREMENT_UNIT_TYPE, "meter"));
    // fact.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "same"));
    // getRangeBounds().update(fact);
    //
    // assertFalse(getRangeBounds().isValid());
    // }
    //
    // public QuantityFactUtil getUtil() {
    // return this.util;
    // }
    //
    // public void setUtil(QuantityFactUtil util) {
    // this.util = util;
    // }
    //
    //    public AlternativeStore getStore() {
    //        return this.store;
    //    }
    //
    //    public void setStore(AlternativeStore store) {
    //        this.store = store;
    //    }
    //
    //    public RangeBounds getRangeBounds() {
    //        return this.rangeBounds;
    //    }
    //
    //    public void setRangeBounds(RangeBounds rangeBounds) {
    //        this.rangeBounds = rangeBounds;
    //    }
}
