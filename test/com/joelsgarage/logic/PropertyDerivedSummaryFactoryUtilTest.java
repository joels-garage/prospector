package com.joelsgarage.logic;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class PropertyDerivedSummaryFactoryUtilTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // private static final int PAGE_SIZE = 1000;
    // private AlternativeStore store;
    // private Stakeholder e;
    // private PropertyDerivedSummaryFactoryUtil factoryUtil;
    //
    // @Override
    // public void setUp() {
    // setStore(new StaticAlternativeStore());
    // setE(new Stakeholder());
    // getE().setKey(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41"));
    // setFactoryUtil(new PropertyDerivedSummaryFactoryUtil(getStore(), PAGE_SIZE));
    // }
    //
    // /**
    // * Verify max and min
    // */
    // public void testGetRangeBoundsQuantity() {
    // System.out.println("b");
    // RangeBounds result =
    // getFactoryUtil().getRangeBoundsForProperty(
    // new ExternalKey("", ExternalKey.PROPERTY_TYPE, "b"));
    // assertEquals(1.0, result.getMin(), 0.001);
    // assertEquals(4.0, result.getMax(), 0.001);
    //
    // System.out.println("c");
    // result =
    // getFactoryUtil().getRangeBoundsForProperty(
    // new ExternalKey("", ExternalKey.PROPERTY_TYPE, "c"));
    // assertEquals(1.0, result.getMin(), 0.001);
    // assertEquals(4.0, result.getMax(), 0.001);
    //
    // System.out.println("d");
    // result =
    // getFactoryUtil().getRangeBoundsForProperty(
    // new ExternalKey("", ExternalKey.QUANTITY_PROPERTY_TYPE, "d"));
    // assertEquals(1.0, result.getMin(), 0.001);
    // assertEquals(48.0, result.getMax(), 0.001);
    // }
    //
    // /**
    // * Verify max, min, and scaling with mixed units
    // */
    // public void testGetRangeBoundsMeasurement() {
    // System.out.println("e");
    // RangeBounds result =
    // getFactoryUtil().getRangeBoundsForProperty(
    // new ExternalKey("", ExternalKey.QUANTITY_PROPERTY_TYPE, "e"));
    // assertEquals(1.219, result.getMin(), 0.001);
    // assertEquals(5.486, result.getMax(), 0.001);
    // }
    //
    // /**
    // * Verify that the correct weight is returned.
    // */
    // public void testGetWeightFound() {
    // IndividualPropertyPreference iPP = new IndividualPropertyPreference();
    // iPP.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "52"));
    // double weight = getFactoryUtil().getWeight(this.e, iPP);
    // assertEquals(0.8, weight, 0.001);
    // }
    //
    // /**
    // * Verify that the default weight of 0.5 is returned if no weight is found for a property.
    // */
    // public void testGetWeightDefault() {
    // IndividualPropertyPreference iPP = new IndividualPropertyPreference();
    // iPP.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "nonexistent key"));
    // double weight = getFactoryUtil().getWeight(this.e, iPP);
    // assertEquals(0.5, weight, 0.001);
    // }
    //
    // //
    // // Accessors
    // //
    //
    // public AlternativeStore getStore() {
    // return this.store;
    // }
    //
    // public void setStore(AlternativeStore store) {
    // this.store = store;
    // }
    //
    // public Stakeholder getE() {
    // return this.e;
    //    }
    //
    //    public void setE(Stakeholder e) {
    //        this.e = e;
    //    }
    //
    //    public PropertyDerivedSummaryFactoryUtil getFactoryUtil() {
    //        return this.factoryUtil;
    //    }
    //
    //    public void setFactoryUtil(PropertyDerivedSummaryFactoryUtil factoryUtil) {
    //        this.factoryUtil = factoryUtil;
    //    }
}