package com.joelsgarage.logic;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class AlternativeUtilityTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // private static final int PAGE_SIZE = 1000;
    // AlternativeStore store;
    // Stakeholder e;
    // Individual i;
    // AlternativeUtility r;
    // Map<ExternalKey, PreferenceDerivedIndividualPropertyUtilityFactory> factoryMap;
    //
    // @Override
    // public void setUp() {
    // this.store = new StaticAlternativeStore();
    // this.e = new Stakeholder();
    // this.e.setKey(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41"));
    // this.e.setDecisionKey(new ExternalKey("", ExternalKey.DECISION_TYPE, "1"));
    // this.i = new Individual();
    // this.factoryMap =
    // new HashMap<ExternalKey, PreferenceDerivedIndividualPropertyUtilityFactory>();
    // }
    //
    // protected void assertKeyFields(ExternalKey test, String namespace, String type, String key) {
    // assertEquals(namespace, test.getNamespace());
    // assertEquals(type, test.getType());
    // assertEquals(key, test.getKey());
    // }
    //
    // public void testDirect() {
    // this.i.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    //
    // IndividualUtility extra = new IndividualUtility();
    // Logger.getLogger(AlternativeUtilityTest.class).info("constructing."); //$NON-NLS-1$
    // this.r =
    // new AlternativeUtility(this.store, this.factoryMap, extra, this.e, this.i, PAGE_SIZE);
    //
    // final List<IndividualUtility> direct = this.r.getDirect();
    // assertEquals(1, direct.size());
    // final IndividualUtility iU = direct.get(0);
    // assertNotNull(iU);
    // assertKeyFields(iU.getStakeholderKey(), "", ExternalKey.STAKEHOLDER_TYPE, "41");
    // assertKeyFields(iU.getIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "21");
    // // see StaticAlternativeStore:274
    // assertEquals(0.6, iU.getValue().doubleValue(), 0.001);
    // assertEquals("foo", iU.getName());
    // }
    //
    // public void testUtilityDerived() {
    // // this.i.setId(Long.valueOf(21));
    // this.i.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    // // this.j.setId(Long.valueOf(23));
    // // this.j.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "23"));
    // IndividualUtility extra =
    // new IndividualUtility(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41"),
    // new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"), Double.valueOf(0.6));
    // this.r =
    // new AlternativeUtility(this.store, this.factoryMap, extra, this.e, this.i, PAGE_SIZE);
    //
    // final List<IndividualUtility> preferenceDerived = this.r.getPreferenceDerived();
    // assertEquals(1, preferenceDerived.size());
    // final IndividualUtility iUD = preferenceDerived.get(0);
    // assertNotNull(iUD);
    // assertKeyFields(iUD.getStakeholderKey(), "", ExternalKey.STAKEHOLDER_TYPE, "41");
    // assertKeyFields(iUD.getIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "21");
    // assertEquals(0.6, iUD.getValue().doubleValue(), 0.001);
    // }
    //
    // public void testPropertyDerived() {
    // // this.i.setId(Long.valueOf(21));
    // this.i.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    // // this.j.setId(Long.valueOf(23));
    // // this.j.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "23"));
    // IndividualUtility extra =
    // new IndividualUtility(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41"),
    // new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"), Double.valueOf(0.6));
    //
    // // This is the utility of individual 21.
    // // 21 is the individual of a property utility of 0.7 for the property "a".
    // // there's a fact that says 21 is a value for property "a" of 21. heh.
    // this.r =
    // new AlternativeUtility(this.store, this.factoryMap, extra, this.e, this.i, PAGE_SIZE);
    //
    // List<PropertyDerivedUtilitySummary> pDList = this.r.getPropertyDerived();
    // assertEquals(1, pDList.size());
    // final PropertyDerivedUtilitySummary pD = pDList.get(0);
    // assertNotNull(pD);
    // IndividualUtility rIP = pD.getR();
    // assertNotNull(rIP);
    // Double value = rIP.getValue();
    // assertNotNull(value);
    // assertEquals(0.7, value.doubleValue(), 0.001);
    // }
}