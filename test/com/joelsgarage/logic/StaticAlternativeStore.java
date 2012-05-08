/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.readers.ListRecordReader;
import com.joelsgarage.model.AnnotatedAlternative;
import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualFact;
import com.joelsgarage.model.IndividualPreference;
import com.joelsgarage.model.IndividualProperty;
import com.joelsgarage.model.IndividualPropertyPreference;
import com.joelsgarage.model.IndividualPropertyUtility;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.MaximizerQuantityPropertyUtility;
import com.joelsgarage.model.MeasurementQuantity;
import com.joelsgarage.model.MeasurementUnit;
import com.joelsgarage.model.MinimizerQuantityPropertyUtility;
import com.joelsgarage.model.OptimizerQuantityPropertyUtility;
import com.joelsgarage.model.Preference;
import com.joelsgarage.model.PropertyWeight;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.model.QuantityProperty;
import com.joelsgarage.model.RankedAlternativeCollection;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.model.StandardMeasurementUnit;
import com.joelsgarage.model.User;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.TestData;

/**
 * Supplies all the data needed for ranking. Everything is hot. Good for testing.
 * 
 * @author joel
 * 
 */
public class StaticAlternativeStore extends AlternativeStoreImpl {
    private static final String DECISION_DESCRIPTION = "decision-description"; //$NON-NLS-1$
    private static final String CLASS_NAME = "class-name"; //$NON-NLS-1$
    private static final String USER_EMAIL = "foo@foo.com"; //$NON-NLS-1$
    private static final String BLANK = ""; //$NON-NLS-1$

    private final List<RankedAlternativeCollection> rList;

    private final List<Stakeholder> sList;

    // private final List<IndividualProperty> pList;

    private final List<IndividualFact> fList;
    private final List<QuantityFact> qFList;

    private final List<Individual> iList;

    private final List<PropertyWeight> wList;

    // INDIVIDUAL PREFERENCES
    private final List<IndividualPreference> iPList;
    private final List<IndividualUtility> iUList;

    // PROPERTY PREFERENCES
    private final List<IndividualPropertyPreference> iPPList;
    private final List<IndividualPropertyUtility> iPUList;
    private final List<MaximizerQuantityPropertyUtility> maxQPUList;
    private final List<MinimizerQuantityPropertyUtility> minQPUList;
    private final List<OptimizerQuantityPropertyUtility> optMPUList;
    private final List<OptimizerQuantityPropertyUtility> optQPUList;

    private final List<MeasurementQuantity> measurementQuantityList;
    private final List<MeasurementUnit> measurementUnitList;

    private TestData testData;

    @SuppressWarnings("nls")
    public StaticAlternativeStore() throws FatalException {
        // ID space:
        // 0 invalid
        // 1-9 Decision
        // 11-19 Class
        // 21-30 Individual
        // 31-39 IndividualPreference
        // 41-49 Stakeholder
        // 51-59 IndividualProperty
        // 61-69 User
        // 71-79 IndividualUtility
        // 81-89 IndividualFact
        // 91-99 IndividualPropertyPreference
        // 101-110 PropertyWeight

        this.testData = new TestData();

        this.rList = new ArrayList<RankedAlternativeCollection>();

        RankedAlternativeCollection r = new RankedAlternativeCollection();

        Decision d = this.testData.decisionOne;
        r.setDecision(d);

        r.setAlternatives(new ArrayList<AnnotatedAlternative>());
        this.iList = new ArrayList<Individual>();

        Individual i = this.testData.individual21;
        this.iList.add(i);
        AnnotatedAlternative a = new AnnotatedAlternative();
        a.setIndividual(i);
        a.setScore(new Double(0.7));
        r.getAlternatives().add(a);

        i = this.testData.individual23;
        this.iList.add(i);
        a = new AnnotatedAlternative();
        a.setIndividual(i);
        a.setScore(new Double(0.3));
        r.getAlternatives().add(a);

        i = this.testData.individual25;
        this.iList.add(i);
        a = new AnnotatedAlternative();
        a.setIndividual(i);
        a.setScore(new Double(0.3));
        r.getAlternatives().add(a);

        i = this.testData.individual26;
        this.iList.add(i);
        a = new AnnotatedAlternative();
        a.setIndividual(i);
        a.setScore(new Double(0.3));
        r.getAlternatives().add(a);

        // these are individuals mentioned in facts about 25 and 26
        i = this.testData.individual27;
        this.iList.add(i);
        a = new AnnotatedAlternative();
        a.setIndividual(i);
        a.setScore(new Double(0.3));
        r.getAlternatives().add(a);

        i = this.testData.individual28;
        this.iList.add(i);

        a = new AnnotatedAlternative();
        a.setIndividual(i);
        a.setScore(new Double(0.3));
        r.getAlternatives().add(a);

        // These individuals have no relevant preferences except for property preferences.

        i = this.testData.individual29;
        this.iList.add(i);

        a = new AnnotatedAlternative();
        a.setIndividual(i);
        a.setScore(new Double(0.3));
        r.getAlternatives().add(a);

        i = this.testData.individual30;
        this.iList.add(i);

        a = new AnnotatedAlternative();
        a.setIndividual(i);
        a.setScore(new Double(0.3));
        r.getAlternatives().add(a);

        this.iList.add(new Individual("quant1", BLANK));
        this.iList.add(new Individual("quant2", BLANK));
        this.iList.add(new Individual("measurement1", BLANK));
        this.iList.add(new Individual("measurement2", BLANK));

        this.rList.add(r);

        r = new RankedAlternativeCollection();
        d = this.testData.decisionTwo;
        r.setDecision(d);

        r.setAlternatives(new ArrayList<AnnotatedAlternative>());

        i = this.testData.individual22;
        a = new AnnotatedAlternative();
        a.setIndividual(i);
        a.setScore(new Double(0.2));
        r.getAlternatives().add(a);

        i = this.testData.individual24;
        a = new AnnotatedAlternative();
        a.setIndividual(i);
        a.setScore(new Double(0.8));
        r.getAlternatives().add(a);

        this.rList.add(r);

        this.iPList = new ArrayList<IndividualPreference>();

        // i like 21 better than 23, preferencewise
        this.iPList.add(makeIndividualPreference(BLANK, this.testData.decisionOne,
            this.testData.user, this.testData.individual21, this.testData.individual23, Double
                .valueOf(0.7)));

        // i like 25 better than 21
        // this is to test 25 > 21 > 23 implication, deriving 25 > 23
        this.iPList.add(makeIndividualPreference(BLANK, this.testData.decisionOne,
            this.testData.user, this.testData.individual25, this.testData.individual21, Double
                .valueOf(0.1)));

        // i like 23 better than 26
        // this is to test 25 > 26 > 23 implication
        this.iPList.add(makeIndividualPreference(BLANK, this.testData.decisionOne,
            this.testData.user, this.testData.individual23, this.testData.individual26, Double
                .valueOf(0.65)));
        this.iPList.add(makeIndividualPreference(BLANK, this.testData.decisionOne,
            this.testData.user, this.testData.individual25, this.testData.individual26, Double
                .valueOf(0.5)));

        // i like 21 better than 23, utilitywise

        this.iUList = new ArrayList<IndividualUtility>();

        this.iUList.add(makeIndividualUtility(BLANK, CLASS_NAME, DECISION_DESCRIPTION, USER_EMAIL,
            "21", Double.valueOf(0.6)));
        this.iUList.add(makeIndividualUtility(BLANK, CLASS_NAME, DECISION_DESCRIPTION, USER_EMAIL,
            "23", Double.valueOf(0.4)));
        this.iUList.add(makeIndividualUtility(BLANK, CLASS_NAME, DECISION_DESCRIPTION, USER_EMAIL,
            "27", Double.valueOf(0.6)));
        this.iUList.add(makeIndividualUtility(BLANK, CLASS_NAME, DECISION_DESCRIPTION, USER_EMAIL,
            "28", Double.valueOf(0.4)));

        //
        // Stakeholder
        //

        this.sList = new ArrayList<Stakeholder>();
        this.sList.add(this.testData.stakeholderS);
        this.sList.add(this.testData.stakeholder);
        this.sList.add(this.testData.stakeholder41);

        //
        // Individual Property
        //

        // this is not actually used for anything.
        //
        // this.pList = new ArrayList<IndividualProperty>();
        // final IndividualProperty p = new IndividualProperty();
        // p.setId(new Long(51));
        // p = new IndividualProperty();
        // p.setId(new Long(52));

        //
        // Individual Property Preference
        //
        {
            this.iPPList = new ArrayList<IndividualPropertyPreference>();

            // 28 > 27
            this.iPPList.add(makeIndividualPropertyPreference(BLANK, CLASS_NAME,
                DECISION_DESCRIPTION, USER_EMAIL, "51-prop-domain", "51-prop-name", "a-prop-range",
                "28", "27", Double.valueOf(0.8)));

            // 24 > 28
            // this is to produce 24 -> 28 -> 27 so compare 29 to 30 another way.
            this.iPPList.add(makeIndividualPropertyPreference(BLANK, CLASS_NAME,
                DECISION_DESCRIPTION, USER_EMAIL, "a-prop-domain", "a-prop-name", "a-prop-range",
                "24", "28", Double.valueOf(0.6)));

            // 22 > 24, with weight
            this.iPPList.add(makeIndividualPropertyPreference(BLANK, CLASS_NAME,
                DECISION_DESCRIPTION, USER_EMAIL, "52-prop-domain", "52-prop-name", "a-prop-range",
                "22", "24", Double.valueOf(0.7)));
        }
        // Prefer individual 22 to 23 for property "a", expressed in utility terms
        {
            this.iPUList = new ArrayList<IndividualPropertyUtility>();

            this.iPUList.add(makeIndividualPropertyUtility(BLANK, CLASS_NAME, DECISION_DESCRIPTION,
                USER_EMAIL, "a-prop-domain", "a-prop-name", "a-prop-range", "22", Double
                    .valueOf(0.7)));

            this.iPUList.add(makeIndividualPropertyUtility(BLANK, CLASS_NAME, DECISION_DESCRIPTION,
                USER_EMAIL, "a-prop-domain", "a-prop-name", "a-prop-range", "23", Double
                    .valueOf(0.4)));

            // indifferent between 21 and 24
            this.iPUList.add(makeIndividualPropertyUtility(BLANK, CLASS_NAME, DECISION_DESCRIPTION,
                USER_EMAIL, "a-prop-domain", "a-prop-name", "a-prop-range", "21", Double
                    .valueOf(0.7)));
            this.iPUList.add(makeIndividualPropertyUtility(BLANK, CLASS_NAME, DECISION_DESCRIPTION,
                USER_EMAIL, "a-prop-domain", "a-prop-name", "a-prop-range", "24", Double
                    .valueOf(0.7)));
        }
        // Prefer maximum values for property "b"
        {
            this.maxQPUList = new ArrayList<MaximizerQuantityPropertyUtility>();
            this.maxQPUList.add(makeMaximizerQuantityPropertyUtility(BLANK, CLASS_NAME,
                DECISION_DESCRIPTION, USER_EMAIL, "b-prop-domain", "b-prop-name", Double
                    .valueOf(0.8), Double.valueOf(0.2), null, null));
        }
        // Prefer minimum values for property "c"
        {
            this.minQPUList = new ArrayList<MinimizerQuantityPropertyUtility>();
            this.minQPUList.add(makeMinimizerQuantityPropertyUtility(BLANK, CLASS_NAME,
                DECISION_DESCRIPTION, USER_EMAIL, "c-prop-domain", "c-prop-name", Double
                    .valueOf(0.8), Double.valueOf(0.2), null, null));
        }
        // Prefer optimal value of 50 for property "d"
        {
            this.optQPUList = new ArrayList<OptimizerQuantityPropertyUtility>();
            this.optQPUList.add(makeOptimizerQuantityPropertyUtility(BLANK, CLASS_NAME,
                DECISION_DESCRIPTION, USER_EMAIL, "d-prop-domain", "d-prop-name", Double
                    .valueOf(0.8), Double.valueOf(0.2), Double.valueOf(50), null, null));
        }
        // prefer optimal value of 20 feet for property "e"
        {
            this.optMPUList = new ArrayList<OptimizerQuantityPropertyUtility>();
            this.optMPUList.add(makeOptimizerQuantityPropertyUtility(BLANK, CLASS_NAME,
                DECISION_DESCRIPTION, USER_EMAIL, "e-prop-domain", "e-prop-name", Double
                    .valueOf(0.8), Double.valueOf(0.2), Double.valueOf(20), "length", "foot"));
        }

        //
        // PropertyWeight
        //

        this.wList = new ArrayList<PropertyWeight>();
        // no weight at all
        this.wList.add(makePropertyWeight(BLANK, CLASS_NAME, DECISION_DESCRIPTION, USER_EMAIL,
            "51-prop-domain", "51-prop-name", "51-prop-range", Double.valueOf(0.5)));
        // a lot of weight
        this.wList.add(makePropertyWeight(BLANK, CLASS_NAME, DECISION_DESCRIPTION, USER_EMAIL,
            "52-prop-domain", "52-prop-name", "52-prop-range", Double.valueOf(0.8)));

        this.fList = makeIndividualFactList();
        this.qFList = makeQuantityFactList();
        this.measurementQuantityList = makeMeasurementQuantityList();
        this.measurementUnitList = makeMeasurementUnitList();
    }

    protected static PropertyWeight makePropertyWeight(String namespace, String className,
        String decisionDescription, String userName, String propertyDomain, String propertyName,
        String propertyRange, Double value) throws FatalException {
        return new PropertyWeight(
        //
            new Stakeholder(//
                new Decision(//
                    new com.joelsgarage.model.Class(className, namespace).makeKey(), //
                    decisionDescription, // 
                    new User(userName, namespace).makeKey(), //
                    namespace).makeKey(),//
                new User(userName, namespace).makeKey(), //
                namespace).makeKey(), new IndividualProperty(new com.joelsgarage.model.Class(
                propertyDomain, namespace).makeKey(), //
                propertyName, //
                new com.joelsgarage.model.Class(propertyRange, namespace).makeKey(), namespace)
                .makeKey(), //
            value, namespace);
    }

    @SuppressWarnings("nls")
    protected List<QuantityFact> makeQuantityFactList() throws FatalException {
        List<QuantityFact> list = new ArrayList<QuantityFact>();

        list.add(makeQuantityFact(BLANK, "quant1", "b-prop-domain", "b-prop-name", null, Double
            .valueOf(1.0)));
        list.add(makeQuantityFact(BLANK, "quant2", "b-prop-domain", "b-prop-name", null, Double
            .valueOf(4.0)));
        list.add(makeQuantityFact(BLANK, "quant3", "c-prop-domain", "c-prop-name", null, Double
            .valueOf(4.0)));
        list.add(makeQuantityFact(BLANK, "quant4", "c-prop-domain", "c-prop-name", null, Double
            .valueOf(4.0)));
        list.add(makeQuantityFact(BLANK, "quant5", "d-prop-domain", "d-prop-name", null, Double
            .valueOf(1.0)));
        list.add(makeQuantityFact(BLANK, "quant6", "d-prop-domain", "d-prop-name", null, Double
            .valueOf(48.0)));
        list.add(makeQuantityFact(BLANK, "measurement1", "e-prop-domain", "e-prop-name",
            this.testData.foot, Double.valueOf(18.0)));
        list.add(makeQuantityFact(BLANK, "measurement2", "e-prop-domain", "e-prop-name",
            this.testData.foot, Double.valueOf(4.0)));

        return list;
    }

    protected static IndividualPreference makeIndividualPreference(String namespace,
        Decision decision, User user, Individual primaryIndividual, Individual secondaryIndividual,
        Double value) throws FatalException {
        return new IndividualPreference(
        //
            new Stakeholder(
            //
                decision.makeKey(), //
                user.makeKey(), //
                namespace).makeKey(), //

            primaryIndividual.makeKey(), //
            secondaryIndividual.makeKey(), //
            value, //
            namespace);
    }

    protected static IndividualUtility makeIndividualUtility(String namespace, String className,
        String decisionDescription, String userName, String individualName, Double value)
        throws FatalException {
        return new IndividualUtility(
        //
            new Stakeholder(
            //
                new Decision(new com.joelsgarage.model.Class(className, namespace).makeKey(), //
                    decisionDescription, //
                    new User(userName, namespace).makeKey(),//
                    namespace).makeKey(), //
                new User(userName, namespace).makeKey(), //
                namespace).makeKey(), //
            new Individual(individualName, namespace).makeKey(), //
            value, //
            namespace);
    }

    protected static IndividualPropertyUtility makeIndividualPropertyUtility(String namespace,
        String className, String decisionDescription, String userName, String domainClassName,
        String propertyName, String rangeClassName, String individualName, Double value)
        throws FatalException {
        return new IndividualPropertyUtility(
        //
            new Stakeholder(
            //
                new Decision(new com.joelsgarage.model.Class(className, namespace).makeKey(), //
                    decisionDescription, //
                    new User(userName, namespace).makeKey(),//
                    namespace).makeKey(), //
                new User(userName, namespace).makeKey(), //
                namespace).makeKey(), //
            new IndividualProperty(new com.joelsgarage.model.Class(domainClassName, namespace)
                .makeKey(), propertyName,
                new com.joelsgarage.model.Class(rangeClassName, namespace).makeKey(), namespace)
                .makeKey(), //
            new Individual(individualName, namespace).makeKey(), //
            value, //
            namespace);
    }

    protected static MaximizerQuantityPropertyUtility makeMaximizerQuantityPropertyUtility(
        String namespace, String className, String decisionDescription, String userName,
        String domainClassName, String propertyName, Double preferredUtility, Double minUtility,
        String measurementQuantityName, String unitName) throws FatalException {
        MeasurementQuantity measurementQuantity =
            new MeasurementQuantity(measurementQuantityName, namespace);

        return new MaximizerQuantityPropertyUtility(
        //
            new Stakeholder(
            //
                new Decision(new com.joelsgarage.model.Class(className, namespace).makeKey(), //
                    decisionDescription, //
                    new User(userName, namespace).makeKey(),//
                    namespace).makeKey(), //
                new User(userName, namespace).makeKey(), //
                namespace).makeKey(), //
            new QuantityProperty(
                //
                new com.joelsgarage.model.Class(domainClassName, namespace).makeKey(),
                propertyName, //
                measurementQuantity.makeKey(), //
                new StandardMeasurementUnit(measurementQuantity.makeKey(), unitName, namespace)
                    .makeKey(), namespace).makeKey(), //
            preferredUtility, minUtility, //
            namespace);
    }

    protected static MinimizerQuantityPropertyUtility makeMinimizerQuantityPropertyUtility(
        String namespace, String className, String decisionDescription, String userName,
        String domainClassName, String propertyName, Double preferredUtility, Double minUtility,
        String measurementQuantityName, String unitName) throws FatalException {
        MeasurementQuantity measurementQuantity =
            new MeasurementQuantity(measurementQuantityName, namespace);

        return new MinimizerQuantityPropertyUtility(
        //
            new Stakeholder(
            //
                new Decision(new com.joelsgarage.model.Class(className, namespace).makeKey(), //
                    decisionDescription, //
                    new User(userName, namespace).makeKey(),//
                    namespace).makeKey(), //
                new User(userName, namespace).makeKey(), //
                namespace).makeKey(), //
            new QuantityProperty(
                //
                new com.joelsgarage.model.Class(domainClassName, namespace).makeKey(),
                propertyName, //
                measurementQuantity.makeKey(), //
                new StandardMeasurementUnit(measurementQuantity.makeKey(), unitName, namespace)
                    .makeKey(), namespace).makeKey(), //
            preferredUtility, minUtility, //
            namespace);
    }

    protected static OptimizerQuantityPropertyUtility makeOptimizerQuantityPropertyUtility(
        String namespace, String className, String decisionDescription, String userName,
        String domainClassName, String propertyName, Double preferredUtility, Double minUtility,
        Double factValue, String measurementQuantityName, String unitName) throws FatalException {
        MeasurementQuantity measurementQuantity =
            new MeasurementQuantity(measurementQuantityName, namespace);

        MeasurementUnit measurementUnit =
            new StandardMeasurementUnit(measurementQuantity.makeKey(), unitName, namespace);

        return new OptimizerQuantityPropertyUtility(
        //
            new Stakeholder(
            //
                new Decision(new com.joelsgarage.model.Class(className, namespace).makeKey(), //
                    decisionDescription, //
                    new User(userName, namespace).makeKey(),//
                    namespace).makeKey(), //
                new User(userName, namespace).makeKey(), //
                namespace).makeKey(), //
            new QuantityProperty(
                //
                new com.joelsgarage.model.Class(domainClassName, namespace).makeKey(),
                propertyName, //
                measurementQuantity.makeKey(), //
                measurementUnit.makeKey(), //
                namespace).makeKey(), //
            preferredUtility, //
            minUtility, //
            factValue,//
            measurementUnit.makeKey(), //
            namespace);
    }

    protected static IndividualPropertyPreference makeIndividualPropertyPreference(
        String namespace, String className, String decisionDescription, String userName,
        String domainClassName, String propertyName, String rangeClassName,
        String primaryIndividualName, String secondaryIndividualName, Double value)
        throws FatalException {
        return new IndividualPropertyPreference(
        //
            new Stakeholder(
            //
                new Decision(new com.joelsgarage.model.Class(className, namespace).makeKey(), //
                    decisionDescription, //
                    new User(userName, namespace).makeKey(),//
                    namespace).makeKey(), //
                new User(userName, namespace).makeKey(), //
                namespace).makeKey(), //
            new IndividualProperty(new com.joelsgarage.model.Class(domainClassName, namespace)
                .makeKey(), propertyName,
                new com.joelsgarage.model.Class(rangeClassName, namespace).makeKey(), namespace)
                .makeKey(), //
            new Individual(primaryIndividualName, namespace).makeKey(), //
            new Individual(secondaryIndividualName, namespace).makeKey(), //
            value, //
            namespace);
    }

    public static QuantityFact makeQuantityFact(String namespace, String subjectName,
        String propertyDomainClassName, String propertyName, MeasurementUnit measurementUnit,
        Double value) throws FatalException {
        ExternalKey measurementUnitKey = null;
        if (measurementUnit != null)
            measurementUnitKey = measurementUnit.makeKey();

        return new QuantityFact( //
            new Individual(subjectName, namespace).makeKey(), //
            new QuantityProperty(
                //
                new com.joelsgarage.model.Class(propertyDomainClassName, namespace).makeKey(),
                propertyName, null, null, namespace).makeKey(), //
            value, measurementUnitKey, namespace);
    }

    protected static IndividualFact makeIndividualFact(String namespace, String subjectName,
        String propertyDomainClassName, String propertyName, String objectName)
        throws FatalException {
        // null is ok below because it's not part of the key.
        IndividualProperty individualProperty =
            new IndividualProperty(new com.joelsgarage.model.Class(propertyDomainClassName,
                namespace).makeKey(), propertyName, null, namespace);
        return new IndividualFact(new Individual(subjectName, namespace).makeKey(),
            individualProperty.makeKey(), new Individual(objectName, namespace).makeKey(),
            namespace);
    }

    @SuppressWarnings("nls")
    protected static List<IndividualFact> makeIndividualFactList() throws FatalException {
        List<IndividualFact> fList = new ArrayList<IndividualFact>();

        fList.add(makeIndividualFact(BLANK, "25", "51-prop-domain", "51-prop-name", "27"));

        // since 28 is preferred, then 26 should be too.
        fList.add(makeIndividualFact(BLANK, "26", "51-prop-domain", "51-prop-name", "28"));
        fList.add(makeIndividualFact(BLANK, "29", "51-prop-domain", "51-prop-name", "27"));

        // since 28 is preferred, then 30 should be too.
        fList.add(makeIndividualFact(BLANK, "30", "51-prop-domain", "51-prop-name", "28"));
        fList.add(makeIndividualFact(BLANK, "21", "52-prop-domain", "52-prop-name", "22"));

        // since 22 is preferred, then 21 should be too.
        fList.add(makeIndividualFact(BLANK, "23", "52-prop-domain", "52-prop-name", "24"));

        // for implied property preference
        // this is to produce 24 -> 28 -> 27 so compare 29 to 30 another way.
        fList.add(makeIndividualFact(BLANK, "30", "51-prop-domain", "51-prop-name", "24"));
        fList.add(makeIndividualFact(BLANK, "30", "51-prop-domain", "51-prop-name", "23"));

        // 21 a:22
        // 30 a:23
        fList.add(makeIndividualFact(BLANK, "a1", "a-prop-domain", "a-prop-name", "22"));
        fList.add(makeIndividualFact(BLANK, "a2", "a-prop-domain", "a-prop-name", "23"));

        // indifferent between 23 and 21
        // 23 a:22
        // 21 a:24
        fList.add(makeIndividualFact(BLANK, "a3", "a-prop-domain", "a-prop-name", "22"));
        fList.add(makeIndividualFact(BLANK, "a4", "a-prop-domain", "a-prop-name", "24"));

        // individual 21 is both the subject and object here; we should create a property derived
        // utility for it.
        fList.add(makeIndividualFact(BLANK, "21", "a-prop-domain", "a-prop-name", "21"));

        // individual 25 has a:21
        fList.add(makeIndividualFact(BLANK, "25", "a-prop-domain", "a-prop-name", "21"));

        // individual 26 has a:21
        fList.add(makeIndividualFact(BLANK, "26", "a-prop-domain", "a-prop-name", "21"));

        return fList;
    }

    // I think this list may not be necessary to reify.
    @SuppressWarnings("nls")
    protected List<MeasurementQuantity> makeMeasurementQuantityList() {
        List<MeasurementQuantity> results = new ArrayList<MeasurementQuantity>();
        MeasurementQuantity length = this.testData.length;
        results.add(length);
        return results;
    }

    @SuppressWarnings("nls")
    protected List<MeasurementUnit> makeMeasurementUnitList() {
        List<MeasurementUnit> results = new ArrayList<MeasurementUnit>();
        results.add(this.testData.meter);
        results.add(this.testData.foot);
        return results;
    }

    @Override
    public MeasurementUnit getMeasurementUnit(ExternalKey measurementUnitKey) {
        for (MeasurementUnit unit : this.measurementUnitList) {
            if (unit.makeKey().equals(measurementUnitKey))
                return unit;
        }
        return null;
    }

    @Override
    public List<MeasurementUnit> getMeasurementUnitList(int pageSize) {
        return this.measurementUnitList;
    }

    @Override
    public MeasurementQuantity getMeasurementQuantity(ExternalKey measurementQuantityKey) {
        for (MeasurementQuantity quant : this.measurementQuantityList) {
            if (quant.makeKey().equals(measurementQuantityKey))
                return quant;
        }
        return null;
    }

    @Override
    public List<Decision> getDecisions(int pageSize) {
        final List<Decision> result = new ArrayList<Decision>();
        for (final RankedAlternativeCollection r : this.rList) {
            result.add(r.getDecision());
        }
        return result;
    }

    @Override
    public List<Individual> getAlternatives(final Decision d, int pageSize) {
        return getAlternatives(d.makeKey(), pageSize);
    }

    @Override
    public Individual getIndividual(ExternalKey individualKey) {
        for (Individual i : this.iList) {
            if (i.makeKey().equals(individualKey)) {
                return i;
            }
        }
        return null;
    }

    @Override
    public List<Individual> getAlternatives(final ExternalKey decisionKey, int pageSize) {
        Logger.getLogger(StaticAlternativeStore.class).info(
            "getAlternatives for decision " + decisionKey.toString()); //$NON-NLS-1$
        for (final RankedAlternativeCollection r : this.rList) {
            Logger.getLogger(StaticAlternativeStore.class).info(
                "comparing to decision " + r.getDecision().toString()); //$NON-NLS-1$
            if (decisionKey.equals(r.getDecision().makeKey())) {
                // Found it; copy the individuals out.
                Logger.getLogger(StaticAlternativeStore.class).info("got a decision"); //$NON-NLS-1$
                final List<AnnotatedAlternative> aList = r.getAlternatives();
                final List<Individual> result = new ArrayList<Individual>();
                for (final AnnotatedAlternative a : aList) {
                    Logger.getLogger(StaticAlternativeStore.class).info(
                        "got individual " + a.getIndividual().makeKey().toString()); //$NON-NLS-1$
                    result.add(a.getIndividual());
                }
                return result;
            }
        }
        // Didn't find it.
        return null;

    }

    // This should be in SQL.
    @Override
    public List<IndividualPreference> getIndividualPreferences(final Decision d,
        final Individual i, int pageSize) {
        Logger.getLogger(StaticAlternativeStore.class).info(
            "getIndividualPreferences for decision " + d.toString() + " i " + i.toString()); //$NON-NLS-1$

        final List<IndividualPreference> result = new ArrayList<IndividualPreference>();
        for (final IndividualPreference iP : this.iPList) {
            if (iP.getPrimaryIndividualKey().equals(i.makeKey())
                || iP.getSecondaryIndividualKey().equals(i.makeKey())) {
                // This Preference is relevant to this individual; now check
                // if any of the relevant Stakeholders are the owners.
                for (final Stakeholder s : this.sList) {
                    if (s.getDecisionKey().equals(d.makeKey())
                        && iP.getStakeholderKey().equals(s.makeKey())) {
                        result.add(iP);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<IndividualPreference> getAllIndividualPreferences(ExternalKey stakeholderKey,
        int pageSize) {
        final List<IndividualPreference> result = new ArrayList<IndividualPreference>();
        for (final IndividualPreference iP : this.iPList) {
            if (iP.getStakeholderKey().equals(stakeholderKey))
                result.add(iP);
        }
        return result;
    }

    @Override
    public List<Stakeholder> getStakeholders(final Decision d, int pageSize) {
        Logger.getLogger(StaticAlternativeStore.class).info(
            "looking for s for decision " + d.makeKey().toString()); //$NON-NLS-1$
        final List<Stakeholder> result = new ArrayList<Stakeholder>();
        for (final Stakeholder s : this.sList) {
            Logger.getLogger(StaticAlternativeStore.class).info(
                "looking at s: " + s.makeKey().toString()); //$NON-NLS-1$
            if (s.getDecisionKey().equals(d.makeKey())) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public List<IndividualPreference> getIndividualPreferences(final ExternalKey eKey,
        final ExternalKey iKey, final ExternalKey jKey, int pageSize) {
        Logger
            .getLogger(StaticAlternativeStore.class)
            .info(
                "getIndividualPreferences for e " + eKey.toString() + " i " + iKey.toString() + " j " + jKey.toString()); //$NON-NLS-1$
        final List<IndividualPreference> result = new ArrayList<IndividualPreference>();
        for (final IndividualPreference r : this.iPList) {
            if (r.getPrimaryIndividualKey().equals(iKey)
                && r.getSecondaryIndividualKey().equals(jKey) && r.getStakeholderKey().equals(eKey)) {
                result.add(r);
            }
        }
        return result;
    }

    @Override
    public List<IndividualUtility> getIndividualUtilities(final Stakeholder e, final Individual i,
        int pageSize) {
        final List<IndividualUtility> result = new ArrayList<IndividualUtility>();
        for (final IndividualUtility u : this.iUList) {
            Logger.getLogger(AlternativePreference.class).info(
                "getIndividualUtilities: looking at " + u.toString()); //$NON-NLS-1$

            if (u.getIndividualKey().equals(i.makeKey())
                && u.getStakeholderKey().equals(e.makeKey())) {

                Logger.getLogger(StaticAlternativeStore.class).info(
                    "getIndividualUtilities: found " + u.toString()); //$NON-NLS-1$

                result.add(u);
            }
        }
        return result;
    }

    @Override
    public List<IndividualFact> getIndividualFacts(final ExternalKey subjectKey, int pageSize) {
        final List<IndividualFact> result = new ArrayList<IndividualFact>();
        for (final IndividualFact f : this.fList) {
            if (f.getSubjectKey().equals(subjectKey)) {
                result.add(f);
            }
        }
        return result;
    }

    @Override
    public List<QuantityFact> getQuantityFacts(final ExternalKey subjectKey, int pageSize) {
        final List<QuantityFact> result = new ArrayList<QuantityFact>();
        for (final QuantityFact f : this.qFList) {
            if (f.getSubjectKey().equals(subjectKey)) {
                result.add(f);
            }
        }
        return result;
    }

    @Override
    public List<IndividualPropertyPreference> getIndividualPropertyPreferences(
        final ExternalKey stakeholderKey, int pageSize) {
        Logger.getLogger(StaticAlternativeStore.class).info("getIndividualPropertyPreferences"); //$NON-NLS-1$
        final List<IndividualPropertyPreference> result =
            new ArrayList<IndividualPropertyPreference>();
        filterStakeholder(this.iPPList, stakeholderKey, result);
        return result;
    }

    @Override
    public List<IndividualPropertyPreference> getIndividualPropertyPreferencesForProperty(
        ExternalKey stakeholderKey, ExternalKey propertyKey, int pageSize) {
        final List<IndividualPropertyPreference> result =
            new ArrayList<IndividualPropertyPreference>();
        for (final IndividualPropertyPreference iPP : this.iPPList) {
            if (iPP.getPropertyKey().equals(propertyKey)
                && iPP.getStakeholderKey().equals(stakeholderKey))
                result.add(iPP);
        }
        return result;
    }

    @Override
    public List<PropertyWeight> getPropertyWeights(final ExternalKey stakeholderKey, int pageSize) {
        final List<PropertyWeight> result = new ArrayList<PropertyWeight>();
        for (final PropertyWeight w : this.wList) {
            if (w.getStakeholderKey().equals(stakeholderKey)) {
                result.add(w);
            }
        }
        return result;
    }

    @Override
    public List<PropertyWeight> getPropertyWeightsForProperty(ExternalKey stakeholderKey,
        ExternalKey propertyKey, int pageSize) {
        final List<PropertyWeight> result = new ArrayList<PropertyWeight>();
        for (final PropertyWeight w : this.wList) {
            if (w.getStakeholderKey().equals(stakeholderKey)
                && w.getPropertyKey().equals(propertyKey)) {
                result.add(w);
            }
        }
        return result;
    }

    @Override
    public List<IndividualPropertyUtility> getIndividualPropertyUtilityList(
        ExternalKey stakeholderKey, int pageSize) {
        Logger.getLogger(StaticAlternativeStore.class).info("getIndividualPropertyUtilityList"); //$NON-NLS-1$
        final List<IndividualPropertyUtility> result = new ArrayList<IndividualPropertyUtility>();
        filterStakeholder(this.iPUList, stakeholderKey, result);
        return result;
    }

    @Override
    public List<MaximizerQuantityPropertyUtility> getMaximizerQuantityPropertyUtilityList(
        ExternalKey stakeholderKey, int pageSize) {
        final List<MaximizerQuantityPropertyUtility> result =
            new ArrayList<MaximizerQuantityPropertyUtility>();
        filterStakeholder(this.maxQPUList, stakeholderKey, result);
        return result;
    }

    @Override
    public List<MinimizerQuantityPropertyUtility> getMinimizerQuantityPropertyUtilityList(
        ExternalKey stakeholderKey, int pageSize) {
        final List<MinimizerQuantityPropertyUtility> result =
            new ArrayList<MinimizerQuantityPropertyUtility>();
        filterStakeholder(this.minQPUList, stakeholderKey, result);
        return result;
    }

    @Override
    public List<OptimizerQuantityPropertyUtility> getOptimizerQuantityPropertyUtilityList(
        ExternalKey stakeholderKey, int pageSize) {
        Logger.getLogger(StaticAlternativeStore.class).info(
            "getOptimizerQuantityPropertyUtilityList"); //$NON-NLS-1$
        final List<OptimizerQuantityPropertyUtility> qResult =
            new ArrayList<OptimizerQuantityPropertyUtility>();
        filterStakeholder(this.optQPUList, stakeholderKey, qResult);
        final List<OptimizerQuantityPropertyUtility> mResult =
            new ArrayList<OptimizerQuantityPropertyUtility>();
        filterStakeholder(this.optMPUList, stakeholderKey, mResult);
        for (OptimizerQuantityPropertyUtility util : mResult) {
            qResult.add(util);
        }
        return qResult;
    }

    /**
     * Copy members of the input list into the output if their stakeholder key matches key.
     * 
     * @param <T>
     * @param input
     * @param key
     * @param output
     */
    protected <T extends Preference> void filterStakeholder(List<T> input, ExternalKey key,
        List<T> output) {
        for (final T t : input) {
            Logger.getLogger(StaticAlternativeStore.class).info("Looking at " + t.toString()); //$NON-NLS-1$
            if (t.getStakeholderKey().equals(key)) {
                Logger.getLogger(StaticAlternativeStore.class).info("found " + t.toString()); //$NON-NLS-1$
                output.add(t);
            }
        }
    }

    @Override
    public void visitQuantityFactsForProperty(ExternalKey propertyKey,
        ProcessNode<QuantityFact, ?> processNode) {
        List<QuantityFact> filteredList = new ArrayList<QuantityFact>();
        for (QuantityFact fact : this.qFList) {
            if (fact.getPropertyKey().equals(propertyKey))
                filteredList.add(fact);
        }
        ListRecordReader<QuantityFact> reader = new ListRecordReader<QuantityFact>(filteredList);
        processNode.setReader(reader);
        processNode.run();
    }

}
