package com.joelsgarage.logic;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class ImpliedPreferenceTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // private static final int PAGE_SIZE = 1000;
    // private AlternativeStore store;
    // private Stakeholder e;
    // private Individual i;
    // private Individual j;
    //
    // @Override
    // public void setUp() {
    // this.store = new StaticAlternativeStore();
    // this.e = new Stakeholder();
    // // this.e.setId(Long.valueOf(41));
    // this.e.setKey(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41"));
    // this.i = new Individual();
    // // this.i.setId(Long.valueOf(25));
    // this.i.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // this.j = new Individual();
    // // this.j.setId(Long.valueOf(23));
    // this.j.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "23"));
    // }
    //
    // protected void assertKeyFields(ExternalKey test, String namespace, String type, String key) {
    // assertEquals(namespace, test.getNamespace());
    // assertEquals(type, test.getType());
    // assertEquals(key, test.getKey());
    // }
    //
    // /**
    // * Individual 21 is a good one.
    // *
    // * The situation here is as follows:
    // *
    // * <pre>
    // * rIK = 0.1 (i.e. K &gt; I strongly)
    // * rKJ = 0.7 (i.e. K &gt; J moderate)
    // * so the conclusion should be
    // * rIJ = 0.3 (i.e. J &gt; I moderate)
    // * </pre>
    // */
    //
    // public void testGoodK() {
    // final Individual goodK = new Individual();
    // // goodK.setId(Long.valueOf(21));
    // goodK.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    // final ImpliedPreference iP =
    // ImpliedPreference.newIfValid(this.store, this.e, this.i, this.j, goodK, PAGE_SIZE);
    // assertNotNull(iP);
    // final IndividualPreference rIJ = iP.getRIJ();
    // assertNotNull(rIJ);
    // assertEquals(0.3, rIJ.getValue().doubleValue(), 0.001);
    // assertKeyFields(rIJ.getPrimaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "25");
    // assertKeyFields(rIJ.getSecondaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "23");
    // assertKeyFields(rIJ.getStakeholderKey(), "", ExternalKey.STAKEHOLDER_TYPE, "41");
    // }
    //
    // /**
    // * if rIJ = 0.3 (see above), then rJI = 0.7.
    // */
    //
    // public void testReverse() {
    // final Individual goodK = new Individual();
    // // goodK.setId(Long.valueOf(21));
    // goodK.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    // // note reversal of j and i
    // final ImpliedPreference iP =
    // ImpliedPreference.newIfValid(this.store, this.e, this.j, this.i, goodK, PAGE_SIZE);
    // assertNotNull(iP);
    // final IndividualPreference rIJ = iP.getRIJ();
    // assertNotNull(rIJ);
    // assertEquals(0.7, rIJ.getValue().doubleValue(), 0.001);
    // assertKeyFields(rIJ.getPrimaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "23");
    // assertKeyFields(rIJ.getSecondaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "25");
    // assertKeyFields(rIJ.getStakeholderKey(), "", ExternalKey.STAKEHOLDER_TYPE, "41");
    // }
    //
    // /**
    // * Individual 22 has no preferences in common with 25 or 23.
    // */
    // public void testInvalidK() {
    // final Individual badK = new Individual();
    //        // badK.setId(Long.valueOf(22));
    //        badK.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "22"));
    //        final ImpliedPreference iP =
    //            ImpliedPreference.newIfValid(this.store, this.e, this.i, this.j, badK, PAGE_SIZE);
    //        assertNull(iP);
    //    }
}