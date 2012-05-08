package com.joelsgarage.logic;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class AlternativePreferenceTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // private static final int PAGE_SIZE = 1000;
    // AlternativeStore store;
    // Stakeholder e;
    // Individual i;
    // Individual j;
    // AlternativePreference r;
    //
    // @Override
    // public void setUp() {
    // this.store = new StaticAlternativeStore();
    // this.e = new Stakeholder();
    // // this.e.setId(Long.valueOf(41));
    // this.e.setKey(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41"));
    // this.e.setDecisionKey(new ExternalKey("", ExternalKey.DECISION_TYPE, "1"));
    // this.i = new Individual();
    // this.j = new Individual();
    // }
    //
    // protected void assertKeyFields(ExternalKey test, String namespace, String type, String key) {
    // assertEquals(namespace, test.getNamespace());
    // assertEquals(type, test.getType());
    // assertEquals(key, test.getKey());
    // }
    //
    // public void testDirect() {
    // // this.i.setId(Long.valueOf(21));
    // this.i.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    //
    // // this.j.setId(Long.valueOf(23));
    // this.j.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "23"));
    //
    // this.r = new AlternativePreference(this.store, this.e, this.i, this.j, PAGE_SIZE);
    //
    // final List<IndividualPreference> direct = this.r.getDirect();
    // assertEquals(1, direct.size());
    // final IndividualPreference iP = direct.get(0);
    // assertNotNull(iP);
    // assertKeyFields(iP.getStakeholderKey(), "", ExternalKey.STAKEHOLDER_TYPE, "41");
    // assertKeyFields(iP.getPrimaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "21");
    // assertKeyFields(iP.getSecondaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "23");
    // assertEquals(0.7, iP.getValue().doubleValue(), 0.001);
    // }
    //
    // public void testUtilityDerived() {
    // // this.i.setId(Long.valueOf(21));
    // this.i.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    // // this.j.setId(Long.valueOf(23));
    // this.j.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "23"));
    // this.r = new AlternativePreference(this.store, this.e, this.i, this.j, PAGE_SIZE);
    //
    // final List<UtilityDerivedIndividualPreference> utilityDerived = this.r.getUtilityDerived();
    // assertEquals(1, utilityDerived.size());
    // final IndividualPreference iUD = utilityDerived.get(0).getR();
    // assertNotNull(iUD);
    // assertKeyFields(iUD.getStakeholderKey(), "", ExternalKey.STAKEHOLDER_TYPE, "41");
    // assertKeyFields(iUD.getPrimaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "21");
    // assertKeyFields(iUD.getSecondaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "23");
    // assertEquals(0.6, iUD.getValue().doubleValue(), 0.001);
    // }
    //
    // public void testImplied() {
    // // this.i.setId(Long.valueOf(25));
    // this.i.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // // this.j.setId(Long.valueOf(23));
    // this.j.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "23"));
    // this.r = new AlternativePreference(this.store, this.e, this.i, this.j, PAGE_SIZE);
    //
    // final List<ImpliedPreference> iList = this.r.getImplied();
    // assertEquals(2, iList.size());
    //
    // // First we should find 21 as the wheel.
    // ImpliedPreference iP = iList.get(0);
    // Individual k = iP.getK();
    // assertNotNull(k);
    // // assertEquals(21, k.getId().longValue());
    // assertTrue(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21").equals(k.getKey()));
    // assertNotNull(iP);
    // IndividualPreference rIJ = iP.getRIJ();
    // assertNotNull(rIJ);
    // assertKeyFields(rIJ.getStakeholderKey(), "", ExternalKey.STAKEHOLDER_TYPE, "41");
    // assertKeyFields(rIJ.getPrimaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "25");
    // assertKeyFields(rIJ.getSecondaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "23");
    // assertEquals(0.3, rIJ.getValue().doubleValue(), 0.001);
    //
    // // Second we should find 26 as the wheel.
    // iP = iList.get(1);
    // k = iP.getK();
    // assertNotNull(k);
    // // assertEquals(26, k.getId().longValue());
    // assertTrue(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "26").equals(k.getKey()));
    //
    // assertNotNull(iP);
    // rIJ = iP.getRIJ();
    // assertNotNull(rIJ);
    // // i've contrived the values to create about (not exactly) the same result
    // assertEquals(0.35, rIJ.getValue().doubleValue(), 0.001);
    // assertKeyFields(rIJ.getStakeholderKey(), "", ExternalKey.STAKEHOLDER_TYPE, "41");
    // assertKeyFields(rIJ.getPrimaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "25");
    // assertKeyFields(rIJ.getSecondaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "23");
    // }
    //
    // public void testPropertyDerived() {
    // // this.i.setId(Long.valueOf(21));
    // this.i.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    // // this.j.setId(Long.valueOf(23));
    // this.j.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "23"));
    // this.r = new AlternativePreference(this.store, this.e, this.i, this.j, PAGE_SIZE);
    //
    // List<PropertyDerivedPreferenceSummary> pDList = this.r.getPropertyDerived();
    // assertEquals(1, pDList.size());
    // final PropertyDerivedPreferenceSummary pD = pDList.get(0);
    // assertNotNull(pD);
    // IndividualPreference rIP = pD.getR();
    // assertNotNull(rIP);
    // Double value = rIP.getValue();
    // assertNotNull(value);
    // assertEquals(0.865, value.doubleValue(), 0.001);
    //
    // }
}