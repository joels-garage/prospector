package com.joelsgarage.logic;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class PropertyDerivedPreferenceSummaryFactoryTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // private static final int PAGE_SIZE = 1000;
    // private AlternativeStore store;
    // private Stakeholder e;
    // private Individual i;
    // private Individual j;
    // private PropertyDerivedPreferenceSummaryFactory factory;
    //
    // @Override
    // public void setUp() {
    // setStore(new StaticAlternativeStore());
    // setE(new Stakeholder());
    // getE().setKey(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41"));
    // setI(new Individual());
    // setJ(new Individual());
    // setFactory(new PropertyDerivedPreferenceSummaryFactory(getStore(), PAGE_SIZE));
    // }
    //
    // public void testWithNoWeight() {
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // getJ().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "26"));
    // PropertyDerivedPreferenceSummary pD = getFactory().newIfValid(getE(), getI(), getJ());
    // assertNotNull(pD);
    // IndividualPreference r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.2, value.doubleValue(), 0.001);
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void testFindDirectIPPForward() {
    // final List<IndividualPropertyPreference> iPPList =
    // getStore().getIndividualPropertyPreferences(getE(), PAGE_SIZE);
    //
    // List<String> reasons = new ArrayList<String>();
    //
    // IndividualFact iF = new IndividualFact();
    // iF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "81"));
    // iF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "51"));
    // iF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // iF.setObjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "28"));
    //
    // IndividualFact jF = new IndividualFact();
    // jF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "82"));
    // jF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "51"));
    // jF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // jF.setObjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "27"));
    //
    // List<PropertyDerivedIndividualPreference> rPList =
    // new ArrayList<PropertyDerivedIndividualPreference>();
    //
    // boolean foundOne =
    // PropertyDerivedPreferenceSummaryFactory.findDirectIPP(iPPList, iF, jF, rPList, reasons);
    //
    // assertTrue(foundOne);
    //
    // assertEquals(1, rPList.size());
    // PropertyDerivedIndividualPreference pp = rPList.get(0);
    // assertTrue(pp instanceof PropertyPreferenceDerivedIndividualPreference);
    // PropertyPreferenceDerivedIndividualPreference<IndividualPropertyPreference> ipp =
    // (PropertyPreferenceDerivedIndividualPreference<IndividualPropertyPreference>) pp;
    //
    // assertEquals(0.8, ipp.getValue().doubleValue(), 0.001);
    // assertEquals(jF.getPropertyKey().toString(), ipp.getPropertyKey().toString());
    // assertEquals(1, reasons.size());
    // assertEquals("Direct id: individual_property_preference//91 value: 0.80", reasons.get(0));
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void testFindDirectIPPReverse() {
    // final List<IndividualPropertyPreference> iPPList =
    // getStore().getIndividualPropertyPreferences(getE(), PAGE_SIZE);
    //
    // List<String> reasons = new ArrayList<String>();
    //
    // IndividualFact iF = new IndividualFact();
    // iF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "81"));
    // iF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "51"));
    // iF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // iF.setObjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "27"));
    //
    // IndividualFact jF = new IndividualFact();
    // jF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "82"));
    // jF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "51"));
    // jF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // jF.setObjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "28"));
    //
    // List<PropertyDerivedIndividualPreference> rPList =
    // new ArrayList<PropertyDerivedIndividualPreference>();
    //
    // boolean foundOne =
    // PropertyDerivedPreferenceSummaryFactory.findDirectIPP(iPPList, iF, jF, rPList, reasons);
    //
    // assertTrue(foundOne);
    //
    // assertEquals(1, rPList.size());
    // PropertyDerivedIndividualPreference pp = rPList.get(0);
    // assertTrue(pp instanceof PropertyPreferenceDerivedIndividualPreference);
    // PropertyPreferenceDerivedIndividualPreference<IndividualPropertyPreference> ipp =
    // (PropertyPreferenceDerivedIndividualPreference<IndividualPropertyPreference>) pp;
    //
    // assertEquals(0.2, ipp.getValue().doubleValue(), 0.001);
    // assertEquals(jF.getPropertyKey().toString(), ipp.getPropertyKey().toString());
    // assertEquals(1, reasons.size());
    // assertEquals("Reverse id: individual_property_preference//91 value: 0.20", reasons.get(0));
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void testFindDirectIPU() {
    // final List<IndividualPropertyUtility> iPUList =
    // this.store.getIndividualPropertyUtilityList(this.e, PAGE_SIZE);
    // List<String> reasons = new ArrayList<String>();
    //
    // IndividualFact iF = new IndividualFact();
    // iF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "81"));
    // iF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "a"));
    // iF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // iF.setObjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "22"));
    //
    // IndividualFact jF = new IndividualFact();
    // jF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "82"));
    // jF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "a"));
    // jF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // jF.setObjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "23"));
    //
    // List<PropertyDerivedIndividualPreference> rPList =
    // new ArrayList<PropertyDerivedIndividualPreference>();
    //
    // boolean foundOne =
    // PropertyDerivedPreferenceSummaryFactory.findDirectIPU(iPUList, iF, jF, rPList, reasons);
    //
    // assertTrue(foundOne);
    //
    // assertEquals(1, rPList.size());
    // assertEquals(1, reasons.size());
    //
    // PropertyDerivedIndividualPreference pp = rPList.get(0);
    //
    // assertTrue(pp instanceof IndividualPropertyUtilityDerivedIndividualPreference);
    // IndividualPropertyUtilityDerivedIndividualPreference<IndividualPropertyUtility> ipp = //
    // (IndividualPropertyUtilityDerivedIndividualPreference<IndividualPropertyUtility>) pp;
    //
    // assertEquals(0.6364, ipp.getValue().doubleValue(), 0.001);
    // assertEquals(jF.getPropertyKey().toString(), ipp.getPropertyKey().toString());
    // assertEquals("Direct i: individual_property_utility//a value: 0.70 "
    // + "j: individual_property_utility//b value: 0.40", reasons.get(0));
    //
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void testFindDirectIPUIndifferent() {
    // final List<IndividualPropertyUtility> iPUList =
    // getStore().getIndividualPropertyUtilityList(getE(), PAGE_SIZE);
    // List<String> reasons = new ArrayList<String>();
    //
    // IndividualFact iF = new IndividualFact();
    // iF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "81"));
    // iF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "a"));
    // iF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // iF.setObjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    //
    // IndividualFact jF = new IndividualFact();
    // jF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "82"));
    // jF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "a"));
    // jF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // jF.setObjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "24"));
    //
    // List<PropertyDerivedIndividualPreference> rPList =
    // new ArrayList<PropertyDerivedIndividualPreference>();
    //
    // boolean foundOne =
    // PropertyDerivedPreferenceSummaryFactory.findDirectIPU(iPUList, iF, jF, rPList, reasons);
    //
    // assertTrue(foundOne);
    //
    // assertEquals(1, rPList.size());
    // PropertyPreference pp = rPList.get(0);
    //
    // assertTrue(pp instanceof IndividualPropertyUtilityDerivedIndividualPreference);
    // IndividualPropertyUtilityDerivedIndividualPreference<IndividualPropertyUtility> ipp = //
    // (IndividualPropertyUtilityDerivedIndividualPreference<IndividualPropertyUtility>) pp;
    //
    // assertEquals(0.5, ipp.getValue().doubleValue(), 0.001);
    // assertEquals(jF.getPropertyKey().toString(), ipp.getPropertyKey().toString());
    // assertEquals(1, reasons.size());
    // assertEquals("Direct i: individual_property_utility//c value: 0.70 "
    // + "j: individual_property_utility//d value: 0.70", reasons.get(0));
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void testFindDirectMaxQPU() {
    // final List<MaximizerQuantityPropertyUtility> mQPUList =
    // getStore().getMaximizerQuantityPropertyUtilityList(getE(), PAGE_SIZE);
    // List<String> reasons = new ArrayList<String>();
    //
    // QuantityFact iF = new QuantityFact();
    // iF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "81"));
    // iF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "b"));
    // iF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // iF.setValue(Double.valueOf(1.0));
    //
    // QuantityFact jF = new QuantityFact();
    // jF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "82"));
    // jF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "b"));
    // jF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // jF.setValue(Double.valueOf(4.0));
    //
    // List<PropertyDerivedIndividualPreference> rPList =
    // new ArrayList<PropertyDerivedIndividualPreference>();
    //
    // boolean foundOne =
    // getFactory().findDirectMaxQPU(mQPUList, Double.valueOf(0.0), Double.valueOf(5.0),
    // iF.getValue(), jF.getValue(), iF.getPropertyKey(), rPList, reasons);
    //
    // assertTrue(foundOne);
    //
    // assertEquals(1, rPList.size());
    // PropertyPreference pp = rPList.get(0);
    // assertTrue(pp instanceof QuantityPropertyUtilityDerivedIndividualPreference);
    // QuantityPropertyUtilityDerivedIndividualPreference<MaximizerQuantityPropertyUtility> ipp = //
    // (QuantityPropertyUtilityDerivedIndividualPreference<MaximizerQuantityPropertyUtility>) pp;
    //
    // assertEquals(0.32, ipp.getValue().doubleValue(), 0.001);
    // assertEquals(jF.getPropertyKey().toString(), ipp.getPropertyKey().toString());
    // assertEquals(1, reasons.size());
    // assertEquals("Max Quant: maximizer_quantity_property_utility//a value: 0.32", reasons
    // .get(0));
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void testFindDirectMinQPU() {
    // Logger.getLogger(PropertyDerivedPreferenceSummaryFactoryTest.class).info(
    // "testFindDirectMinQPU");
    // final List<MinimizerQuantityPropertyUtility> mQPUList =
    // getStore().getMinimizerQuantityPropertyUtilityList(getE(), PAGE_SIZE);
    // List<String> reasons = new ArrayList<String>();
    //
    // QuantityFact iF = new QuantityFact();
    // iF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "81"));
    // iF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "c"));
    // iF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // iF.setValue(Double.valueOf(1.0));
    //
    // QuantityFact jF = new QuantityFact();
    // jF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "82"));
    // jF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "c"));
    // jF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // jF.setValue(Double.valueOf(4.0));
    //
    // List<PropertyDerivedIndividualPreference> rPList =
    // new ArrayList<PropertyDerivedIndividualPreference>();
    //
    // boolean foundOne =
    // getFactory().findDirectMinQPU(mQPUList, Double.valueOf(0.0), Double.valueOf(5.0),
    // iF.getValue(), jF.getValue(), iF.getPropertyKey(), rPList, reasons);
    //
    // assertTrue(foundOne);
    //
    // assertEquals(1, rPList.size());
    // PropertyPreference pp = rPList.get(0);
    // assertTrue(pp instanceof QuantityPropertyUtilityDerivedIndividualPreference);
    // QuantityPropertyUtilityDerivedIndividualPreference<MinimizerQuantityPropertyUtility> ipp = //
    // (QuantityPropertyUtilityDerivedIndividualPreference<MinimizerQuantityPropertyUtility>) pp;
    //
    // assertEquals(0.68, ipp.getValue().doubleValue(), 0.001);
    // assertEquals(jF.getPropertyKey().toString(), ipp.getPropertyKey().toString());
    // assertEquals(1, reasons.size());
    // assertEquals("Min Quant: minimizer_quantity_property_utility//a value: 0.68", reasons
    // .get(0));
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void testFindDirectOptQPU() {
    // Logger.getLogger(PropertyDerivedPreferenceSummaryFactoryTest.class).info(
    // "testFindDirectOptQPU");
    // final List<OptimizerQuantityPropertyUtility> oQPUList =
    // this.store.getOptimizerQuantityPropertyUtilityList(this.e, PAGE_SIZE);
    // List<String> reasons = new ArrayList<String>();
    //
    // QuantityFact iF = new QuantityFact();
    // iF.setKey(new ExternalKey("", ExternalKey.QUANTITY_FACT_TYPE, "81"));
    // iF.setPropertyKey(new ExternalKey("", ExternalKey.QUANTITY_PROPERTY_TYPE, "d"));
    // iF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // iF.setValue(Double.valueOf(40.0));
    //
    // QuantityFact jF = new QuantityFact();
    // jF.setKey(new ExternalKey("", ExternalKey.QUANTITY_FACT_TYPE, "82"));
    // jF.setPropertyKey(new ExternalKey("", ExternalKey.QUANTITY_PROPERTY_TYPE, "d"));
    // jF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // jF.setValue(Double.valueOf(2.0));
    //
    // List<PropertyDerivedIndividualPreference> rPList =
    // new ArrayList<PropertyDerivedIndividualPreference>();
    //
    // boolean foundOne = getFactory().findDirectOptQPU(oQPUList, //
    // Double.valueOf(0.0), //
    // Double.valueOf(100.0), //
    // iF.getValue(), //
    // jF.getValue(), //
    // iF.getPropertyKey(), //
    // rPList, //
    // reasons);
    //
    // assertTrue(foundOne);
    //
    // assertEquals(1, rPList.size());
    // PropertyPreference pp = rPList.get(0);
    // assertTrue(pp instanceof QuantityPropertyUtilityDerivedIndividualPreference);
    // QuantityPropertyUtilityDerivedIndividualPreference<OptimizerQuantityPropertyUtility> ipp = //
    // (QuantityPropertyUtilityDerivedIndividualPreference<OptimizerQuantityPropertyUtility>) pp;
    //
    // assertEquals(0.7522, ipp.getValue().doubleValue(), 0.001);
    // assertEquals(jF.getPropertyKey().toString(), ipp.getPropertyKey().toString());
    // assertEquals(1, reasons.size());
    // assertEquals("Opt Quant: optimizer_quantity_property_utility//a value: 0.75", reasons
    // .get(0));
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void testFindDirectOptMPU() {
    // Logger.getLogger(PropertyDerivedPreferenceSummaryFactoryTest.class).info(
    // "testFindDirectOptMPU");
    //
    // final List<OptimizerQuantityPropertyUtility> oMPUList =
    // getStore().getOptimizerQuantityPropertyUtilityList(getE(), PAGE_SIZE);
    //
    // List<OptimizerQuantityPropertyUtility> oQPUList =
    // new ArrayList<OptimizerQuantityPropertyUtility>();
    //
    // for (OptimizerQuantityPropertyUtility u : oMPUList) {
    // oQPUList.add(u);
    // }
    //
    // List<String> reasons = new ArrayList<String>();
    //
    // QuantityFact iF = new QuantityFact();
    // iF.setKey(new ExternalKey("", ExternalKey.QUANTITY_FACT_TYPE, "81"));
    // iF.setPropertyKey(new ExternalKey("", ExternalKey.QUANTITY_PROPERTY_TYPE, "e"));
    // iF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // iF.setValue(Double.valueOf(18.0));
    // iF.setExpressedUnitKey(new ExternalKey("", ExternalKey.MEASUREMENT_UNIT_TYPE, "foot"));
    //
    // QuantityFact jF = new QuantityFact();
    // jF.setKey(new ExternalKey("", ExternalKey.QUANTITY_FACT_TYPE, "82"));
    // jF.setPropertyKey(new ExternalKey("", ExternalKey.QUANTITY_PROPERTY_TYPE, "e"));
    // jF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // jF.setValue(Double.valueOf(4.0));
    // jF.setExpressedUnitKey(new ExternalKey("", ExternalKey.MEASUREMENT_UNIT_TYPE, "foot"));
    //
    // List<PropertyDerivedIndividualPreference> rPList =
    // new ArrayList<PropertyDerivedIndividualPreference>();
    //
    // System.out.println("BARRRRR1");
    // // Note converted units below
    // boolean foundOne =
    // getFactory().findDirectOptQPU(oQPUList, Double.valueOf(0.0), Double.valueOf(12.192),
    // Double.valueOf(5.4864), Double.valueOf(1.2192), iF.getPropertyKey(), rPList,
    // reasons);
    // System.out.println("BARRRRR2");
    //
    // assertTrue(foundOne);
    //
    // assertEquals(1, rPList.size());
    // PropertyPreference pp = rPList.get(0);
    // assertTrue(pp instanceof QuantityPropertyUtilityDerivedIndividualPreference);
    // QuantityPropertyUtilityDerivedIndividualPreference<OptimizerQuantityPropertyUtility> ipp = //
    // (QuantityPropertyUtilityDerivedIndividualPreference<OptimizerQuantityPropertyUtility>) pp;
    //
    // assertEquals(0.6981, ipp.getValue().doubleValue(), 0.001);
    // assertEquals(jF.getPropertyKey().toString(), ipp.getPropertyKey().toString());
    // assertEquals(1, reasons.size());
    // assertEquals("Opt Quant: optimizer_measurement_property_utility//a value: 0.70", reasons
    // .get(0));
    // }
    //
    // //
    // //
    //
    // @SuppressWarnings("unchecked")
    // public void testFindImpliedIPP() {
    // final List<IndividualPropertyPreference> iPPList =
    // this.store.getIndividualPropertyPreferences(this.e, PAGE_SIZE);
    //
    // List<String> reasons = new ArrayList<String>();
    //
    // IndividualFact iF = new IndividualFact();
    // iF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "81"));
    // iF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "51"));
    // iF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // iF.setObjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "24"));
    //
    // IndividualFact jF = new IndividualFact();
    // jF.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_FACT_TYPE, "82"));
    // jF.setPropertyKey(new ExternalKey("", ExternalKey.PROPERTY_TYPE, "51"));
    // jF.setSubjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // jF.setObjectKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "27"));
    //
    // List<PropertyDerivedIndividualPreference> rPList =
    // new ArrayList<PropertyDerivedIndividualPreference>();
    //
    // PropertyDerivedPreferenceSummaryFactory.findImpliedIPP(iPPList, iF, jF, rPList, reasons);
    //
    // assertEquals(1, rPList.size());
    // PropertyDerivedIndividualPreference pp = rPList.get(0);
    // assertTrue(pp instanceof ImpliedPropertyPreferenceDerivedIndividualPreference);
    // ImpliedPropertyPreferenceDerivedIndividualPreference ipp =
    // (ImpliedPropertyPreferenceDerivedIndividualPreference) pp;
    //
    // assertEquals(0.9, ipp.getValue().doubleValue(), 0.001);
    // assertEquals(jF.getPropertyKey().toString(), ipp.getPropertyKey().toString());
    // assertEquals(2, ipp.getPropertyPreferenceList().size());
    // List<IndividualPropertyPreference> ippFoundList = ipp.getPropertyPreferenceList();
    // assertEquals("individual_property_preference//93", ippFoundList.get(0).getKey().toString());
    // assertEquals("individual_property_preference//91", ippFoundList.get(1).getKey().toString());
    // assertEquals("individual//24", ippFoundList.get(0).getPrimaryIndividualKey().toString());
    // assertEquals("individual//28", ippFoundList.get(0).getSecondaryIndividualKey().toString());
    // assertEquals("individual//28", ippFoundList.get(1).getPrimaryIndividualKey().toString());
    // assertEquals("individual//27", ippFoundList.get(1).getSecondaryIndividualKey().toString());
    //
    // assertEquals(3, reasons.size());
    // assertEquals("Implied using k individual//28 rIK: 0.60 rKJ: 0.80", reasons.get(0));
    // assertEquals("Implied using k individual//28 value: 0.90", reasons.get(1));
    // assertEquals("setvalue: 0.90", reasons.get(2));
    // }
    //
    // public void testReverse() {
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "26"));
    // getJ().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // PropertyDerivedPreferenceSummary pD = getFactory().newIfValid(getE(), getI(), getJ());
    // assertNotNull(pD);
    // IndividualPreference r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.8, value.doubleValue(), 0.001);
    // }
    //
    // public void testWithWeight() {
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    // getJ().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "23"));
    // PropertyDerivedPreferenceSummary pD = getFactory().newIfValid(getE(), getI(), getJ());
    // assertNotNull(pD);
    // IndividualPreference r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.865, value.doubleValue(), 0.001);
    // }
    //
    // public void testIndividualUtility() {
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "a1"));
    // getJ().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "a2"));
    // PropertyDerivedPreferenceSummary pD = getFactory().newIfValid(getE(), getI(), getJ());
    // assertNotNull(pD);
    // IndividualPreference r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.6364, value.doubleValue(), 0.001);
    // }
    //
    // public void testIndividualUtilityIndifferent() {
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "a3"));
    // getJ().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "a4"));
    // PropertyDerivedPreferenceSummary pD = getFactory().newIfValid(getE(), getI(), getJ());
    // assertNotNull(pD);
    // IndividualPreference r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.5, value.doubleValue(), 0.001);
    // }
    //
    // /**
    // * Verify quant2 favored over quant1
    // */
    // public void testMaxQuant() {
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "quant1"));
    // getJ().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "quant2"));
    // PropertyDerivedPreferenceSummary pD = getFactory().newIfValid(getE(), getI(), getJ());
    // assertNotNull(pD);
    // IndividualPreference r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.2, value.doubleValue(), 0.001);
    // }
    //
    // /**
    // * Verify quant3 favored over quant4
    // */
    // public void testMinQuant() {
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "quant3"));
    // getJ().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "quant4"));
    // PropertyDerivedPreferenceSummary pD = getFactory().newIfValid(getE(), getI(), getJ());
    // assertNotNull(pD);
    // IndividualPreference r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.8, value.doubleValue(), 0.001);
    // }
    //
    // /**
    // * Verify quant6 closer to optimum than quant5
    // */
    // public void testOptQuant() {
    // Logger.getLogger(PropertyDerivedPreferenceSummaryFactoryTest.class).info("testOptQuant");
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "quant5"));
    // getJ().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "quant6"));
    //        PropertyDerivedPreferenceSummary pD = getFactory().newIfValid(getE(), getI(), getJ());
    //        assertNotNull(pD);
    //        IndividualPreference r = pD.getR();
    //        assertNotNull(r);
    //        Double value = r.getValue();
    //        assertNotNull(value);
    //        assertEquals(0.2050, value.doubleValue(), 0.001);
    //    }
    //
    //    /** Verify measurement1 closer to optimum than measurement2 */
    //    public void testOptMeasure() {
    //        Logger.getLogger(PropertyDerivedPreferenceSummaryFactoryTest.class).info("testOptMeasure");
    //        getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "measurement1"));
    //        getJ().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "measurement2"));
    //        System.out.println("BARRRRR3");
    //        PropertyDerivedPreferenceSummary pD = getFactory().newIfValid(getE(), getI(), getJ());
    //        System.out.println("BARRRRR4");
    //        assertNotNull(pD);
    //        IndividualPreference r = pD.getR();
    //        assertNotNull(r);
    //        Double value = r.getValue();
    //        assertNotNull(value);
    //        assertEquals(0.7838, value.doubleValue(), 0.001);
    //    }
    //
    //    //
    //    // Accessors
    //    //
    //
    //    public AlternativeStore getStore() {
    //        return this.store;
    //    }
    //
    //    public void setStore(AlternativeStore store) {
    //        this.store = store;
    //    }
    //
    //    public Stakeholder getE() {
    //        return this.e;
    //    }
    //
    //    public void setE(Stakeholder e) {
    //        this.e = e;
    //    }
    //
    //    public Individual getI() {
    //        return this.i;
    //    }
    //
    //    public void setI(Individual i) {
    //        this.i = i;
    //    }
    //
    //    public Individual getJ() {
    //        return this.j;
    //    }
    //
    //    public void setJ(Individual j) {
    //        this.j = j;
    //    }
    //
    //    public PropertyDerivedPreferenceSummaryFactory getFactory() {
    //        return this.factory;
    //    }
    //
    //    public void setFactory(PropertyDerivedPreferenceSummaryFactory factory) {
    //        this.factory = factory;
    //    }
}