package com.joelsgarage.logic;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class PropertyDerivedUtilitySummaryFactoryTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // private static final int PAGE_SIZE = 1000;
    // private AlternativeStore store;
    // private Stakeholder e;
    // private Individual i;
    // private PropertyDerivedUtilitySummaryFactory factory;
    // private Map<ExternalKey, PreferenceDerivedIndividualPropertyUtilityFactory> factoryMap;
    //
    // @Override
    // public void setUp() {
    // setStore(new StaticAlternativeStore());
    // setE(new Stakeholder());
    // getE().setKey(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41"));
    // setI(new Individual());
    // setFactory(new PropertyDerivedUtilitySummaryFactory(getStore(), PAGE_SIZE));
    // setFactoryMap(new HashMap<ExternalKey, PreferenceDerivedIndividualPropertyUtilityFactory>());
    // }
    //
    // // ALL THESE TESTS ARE TESTING THE SAME DAMNED THING.
    // //
    // // SO IT'S NOT THAT HELPFUL TO HAVE SO MANY.
    // //
    // // TODO(joel): fix it.
    //
    // public void testWithNoWeight() {
    // // Individual 25 has a property, "a", whose value is 21, utility of 0.7
    // Logger.getLogger(PropertyDerivedUtilitySummaryFactoryTest.class).info("testWithNoWeight");
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"));
    // PropertyDerivedUtilitySummary pD = getFactory().newIfValid(getFactoryMap(), getE(), getI());
    // assertNotNull(pD);
    // IndividualUtility r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.7, value.doubleValue(), 0.001);
    // }
    //
    // public void testReverse() {
    // // utility of individual 21 in property 'a' is 0.7;
    // // individual '26' has 21 as value for property 'a', so it's just that value.
    // Logger.getLogger(PropertyDerivedUtilitySummaryFactoryTest.class).info("testReverse");
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "26"));
    // PropertyDerivedUtilitySummary pD = getFactory().newIfValid(getFactoryMap(), getE(), getI());
    // assertNotNull(pD);
    // IndividualUtility r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.7, value.doubleValue(), 0.001);
    // }
    //
    // public void testWithWeight() {
    // // utility of individual 21 in property 'a' is 0.7;
    // // individual '21' has 21 as value for property 'a', so it's just that value.
    // Logger.getLogger(PropertyDerivedUtilitySummaryFactoryTest.class).info("testWithWeight");
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    // PropertyDerivedUtilitySummary pD = getFactory().newIfValid(getFactoryMap(), getE(), getI());
    // assertNotNull(pD);
    // IndividualUtility r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.7, value.doubleValue(), 0.001);
    // }
    //
    // public void testIndividualUtility() {
    // // utility of individual 22 in property 'a' is 0.7;
    // // individual 'a1' has 22 as value for property 'a', so it's just that value.
    // Logger.getLogger(PropertyDerivedUtilitySummaryFactoryTest.class).info(
    // "testIndividualUtility");
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "a1"));
    // PropertyDerivedUtilitySummary pD = getFactory().newIfValid(getFactoryMap(), getE(), getI());
    // assertNotNull(pD);
    // IndividualUtility r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.7, value.doubleValue(), 0.001);
    // }
    //
    // public void testIndividualUtilityIndifferent() {
    // // utility of individual 22 in property 'a' is 0.7;
    // // individual 'a3' has 22 as value for property 'a', so it's just that value.
    // Logger.getLogger(PropertyDerivedUtilitySummaryFactoryTest.class).info(
    // "testIndividualUtilityIndifferent");
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "a3"));
    // PropertyDerivedUtilitySummary pD = getFactory().newIfValid(getFactoryMap(), getE(), getI());
    // assertNotNull(pD);
    // IndividualUtility r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.7, value.doubleValue(), 0.001);
    // }
    //
    // /**
    // * Verify quant2 favored over quant1
    // */
    // public void testMaxQuant() {
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "quant1"));
    // PropertyDerivedUtilitySummary pD = getFactory().newIfValid(getFactoryMap(), getE(), getI());
    // assertNotNull(pD);
    // IndividualUtility r = pD.getR();
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
    // PropertyDerivedUtilitySummary pD = getFactory().newIfValid(getFactoryMap(), getE(), getI());
    // assertNotNull(pD);
    // IndividualUtility r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.8, value.doubleValue(), 0.001);
    // }
    //
    // /**
    // * Verify utility of quant5 (which has value 1 for property 'd'), is as specified by the
    // * property utility. this 0.2 for the extrema, 0.8 for the optimum of 50. So since the fact
    // * value of 1 is the extremum (the other value is 48), it gets 0.2.
    // */
    // public void testOptQuant() {
    // Logger.getLogger(PropertyDerivedUtilitySummaryFactoryTest.class).info("testOptQuant");
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "quant5"));
    // PropertyDerivedUtilitySummary pD = getFactory().newIfValid(getFactoryMap(), getE(), getI());
    // assertNotNull(pD);
    // IndividualUtility r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.2, value.doubleValue(), 0.001);
    // }
    //
    // /**
    // * Measurement1 is an individual with 18 feet as the value for property 'e', a quantity
    // * property. The optimal value for "e" is 20 feet, with a utility of 0.8, and 0.2 for the
    // * extrema. The other fact for "e" is 4 feet, so, linear interpolation between 20(0.8) and
    // * 4(0.2) gives 0.725
    // */
    // public void testOptMeasure() {
    // Logger.getLogger(PropertyDerivedUtilitySummaryFactoryTest.class).info("testOptMeasure");
    //
    // getI().setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "measurement1"));
    // System.out.println("BARRRRR3");
    // PropertyDerivedUtilitySummary pD = getFactory().newIfValid(getFactoryMap(), getE(), getI());
    // System.out.println("BARRRRR4");
    // assertNotNull(pD);
    // IndividualUtility r = pD.getR();
    // assertNotNull(r);
    // Double value = r.getValue();
    // assertNotNull(value);
    // assertEquals(0.725, value.doubleValue(), 0.001);
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
    // }
    //
    // public void setE(Stakeholder e) {
    // this.e = e;
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
    //    public PropertyDerivedUtilitySummaryFactory getFactory() {
    //        return this.factory;
    //    }
    //
    //    public void setFactory(PropertyDerivedUtilitySummaryFactory factory) {
    //        this.factory = factory;
    //    }
    //
    //    public Map<ExternalKey, PreferenceDerivedIndividualPropertyUtilityFactory> getFactoryMap() {
    //        return this.factoryMap;
    //    }
    //
    //    public void setFactoryMap(
    //        Map<ExternalKey, PreferenceDerivedIndividualPropertyUtilityFactory> factoryMap) {
    //        this.factoryMap = factoryMap;
    //    }
}