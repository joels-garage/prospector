package com.joelsgarage.logic;

import junit.framework.TestCase;

public class UtilityDerivedPreferenceTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // // private AlternativeStore store;
    // private Stakeholder e;
    // private IndividualUtility uI;
    // private IndividualUtility uJ;
    //
    // @SuppressWarnings("nls")
    // @Override
    // public void setUp() {
    // // this.store = new StaticAlternativeStore();
    // this.e = new Stakeholder();
    // // this.e.setId(Long.valueOf(41));
    // this.e.setKey(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41"));
    // this.uI = new IndividualUtility();
    // this.uI.setIndividualKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    // this.uJ = new IndividualUtility();
    // this.uJ.setIndividualKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "23"));
    // }
    //
    // /**
    // * f(0.8, 0.5) = 0.615 (mild preference)
    // */
    // public void testPreference() {
    // this.uI.setValue(Double.valueOf(0.8));
    // this.uJ.setValue(Double.valueOf(0.5));
    // final UtilityDerivedIndividualPreference uD =
    // new UtilityDerivedIndividualPreference(null, this.e, this.uI, this.uJ);
    // assertNotNull(uD);
    // assertNotNull(uD.getR());
    // assertNotNull(uD.getR().getValue());
    // assertEquals(0.615, uD.getR().getValue().doubleValue(), 0.001);
    // }
    //
    // /**
    // * equal values produce indifference
    // */
    // public void testIndifference() {
    // this.uI.setValue(Double.valueOf(0.2));
    // this.uJ.setValue(Double.valueOf(0.2));
    // final UtilityDerivedIndividualPreference uD =
    // new UtilityDerivedIndividualPreference(null, this.e, this.uI, this.uJ);
    // assertNotNull(uD);
    // assertNotNull(uD.getR());
    // assertNotNull(uD.getR().getValue());
    // assertEquals(0.5, uD.getR().getValue().doubleValue(), 0.001);
    // }
    //
    // /**
    // * indifference, special case to avoid division by zero
    // */
    // public void testZero() {
    // this.uI.setValue(Double.valueOf(0.0));
    // this.uJ.setValue(Double.valueOf(0.0));
    // final UtilityDerivedIndividualPreference uD =
    // new UtilityDerivedIndividualPreference(null, this.e, this.uI, this.uJ);
    // assertNotNull(uD);
    // assertNull(uD.getR());
    // // assertNotNull(uD.getR().getValue());
    // // assertEquals(0.5, uD.getR().getValue().doubleValue(), 0.001);
    // }
    //
    // /**
    // * illegal values return zero
    // */
    // public void testIllegalValues() {
    // this.uI.setValue(Double.valueOf(-1));
    // this.uJ.setValue(Double.valueOf(0.0));
    // final UtilityDerivedIndividualPreference uD =
    // new UtilityDerivedIndividualPreference(null, this.e, this.uI, this.uJ);
    //        assertNotNull(uD);
    //        assertNull(uD.getR());
    //        // assertNotNull(uD.getR().getValue());
    //        // assertEquals(0.0, uD.getR().getValue().doubleValue(), 0.001);
    //    }
}