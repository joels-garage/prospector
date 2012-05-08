/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.util.HashMap;
import java.util.Map;

import com.joelsgarage.model.AffineMeasurementUnit;
import com.joelsgarage.model.ClassMember;
import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.MeasurementQuantity;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.model.StandardMeasurementUnit;
import com.joelsgarage.model.StringFact;
import com.joelsgarage.model.StringProperty;
import com.joelsgarage.model.Subclass;
import com.joelsgarage.model.User;
import com.joelsgarage.model.WordSense;

/**
 * entities for tests
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class TestData {
    public static final String BLANK_NAMESPACE = ""; //$NON-NLS-1$
    private static final String USER_EMAIL = "foo@foo.com"; //$NON-NLS-1$
    private static final String USER_NAMESPACE = "usernamespace"; //$NON-NLS-1$
    private static final String EN_US = "en_US"; //$NON-NLS-1$
    private static final String BLANK = ""; //$NON-NLS-1$

    public final Map<ExternalKey, ModelEntity> entities = new HashMap<ExternalKey, ModelEntity>();

    public final User user;
    public final com.joelsgarage.model.Class clazz;
    public final Decision decision;
    public final Stakeholder stakeholder;
    public final Individual individual;
    public final MeasurementQuantity measurementQuantity;

    public final Individual individual21;
    public final Individual individual22;
    public final Individual individual23;
    public final Individual individual24;
    public final Individual individual25;
    public final Individual individual26;
    public final Individual individual27;
    public final Individual individual28;
    public final Individual individual29;
    public final Individual individual30;

    public final Stakeholder stakeholder41;

    public final StringProperty stringProperty1;

    public final MeasurementQuantity length;
    public final MeasurementQuantity area;

    public final StandardMeasurementUnit meter;
    public final StandardMeasurementUnit squareMeter;

    public final AffineMeasurementUnit foot;

    public final WordSense wordSense1;
    public final WordSense wordSense2;
    public final WordSense wordSense3;

    public final com.joelsgarage.model.Class parentClass;
    public final com.joelsgarage.model.Class childClass;
    public final Subclass subclass;

    //
    // stuff for the json query processor, from my MQL examples.
    //

    public final Individual harrisonFord;
    public final Individual tomHanks;
    public final Individual jodieFoster;

    public final com.joelsgarage.model.Class actor;

    public final ClassMember harrisonFordActor;
    public final ClassMember tomHanksActor;
    public final ClassMember jodieFosterActor;

    public final StringProperty stringProperty;
    public final StringFact harrisonFordStringFact;

    public final Stakeholder stakeholderS;

    public final Decision decisionOne;
    public final Decision decisionTwo;

    public TestData() throws FatalException {
        //
        // stuff for the json query processor.
        //
        this.actor = new com.joelsgarage.model.Class("actor", BLANK_NAMESPACE);
        put(this.actor);

        this.harrisonFord = new Individual("harrisonFord", BLANK_NAMESPACE);
        put(this.harrisonFord);
        this.tomHanks = new Individual("tomHanks", BLANK_NAMESPACE);
        put(this.tomHanks);
        this.jodieFoster = new Individual("jodieFoster", BLANK_NAMESPACE);
        put(this.jodieFoster);

        this.harrisonFordActor =
            new ClassMember(this.harrisonFord.makeKey(), this.actor.makeKey(), BLANK_NAMESPACE);
        put(this.harrisonFordActor);

        this.tomHanksActor =
            new ClassMember(this.tomHanks.makeKey(), this.actor.makeKey(), BLANK_NAMESPACE);
        put(this.tomHanksActor);

        this.jodieFosterActor =
            new ClassMember(this.jodieFoster.makeKey(), this.actor.makeKey(), BLANK_NAMESPACE);
        put(this.jodieFosterActor);

        this.stringProperty =
            new StringProperty(this.actor.makeKey(), "some name", BLANK_NAMESPACE);
        put(this.stringProperty);

        this.harrisonFordStringFact =
            new StringFact(this.harrisonFord.makeKey(), this.stringProperty.makeKey(),
                "some value", BLANK_NAMESPACE);
        put(this.harrisonFordStringFact);

        //
        //
        //

        this.user = new User(USER_EMAIL, USER_NAMESPACE);
        put(this.user);

        this.clazz = new com.joelsgarage.model.Class("decisionclassname", "decisionclassnamespace");
        put(this.clazz);

        this.decision =
            new Decision(this.clazz.makeKey(), "decisiondescription", this.user.makeKey(),
                "decisionamespace");
        put(this.decision);
        this.decisionOne =
            new Decision(new com.joelsgarage.model.Class("11", BLANK).makeKey(), "decision one", //
                this.user.makeKey(), BLANK);
        put(this.decisionOne);
        this.decisionTwo =
            new Decision(new com.joelsgarage.model.Class("12", BLANK).makeKey(), "decision two",
                this.user.makeKey(), BLANK);
        put(this.decisionTwo);

        this.stakeholder =
            new Stakeholder(this.decision.makeKey(), this.user.makeKey(), "stakeholdernamespace");
        put(this.stakeholder);

        this.individual = new Individual("individualname", "individualnamespace");
        put(this.individual);

        this.measurementQuantity =
            new MeasurementQuantity("measurementquantityname", "measurementquantitynamespace");
        put(this.measurementQuantity);

        this.individual21 = new Individual("21", BLANK_NAMESPACE);
        put(this.individual21);
        this.individual22 = new Individual("22", BLANK_NAMESPACE);
        put(this.individual22);
        this.individual23 = new Individual("23", BLANK_NAMESPACE);
        put(this.individual23);
        this.individual24 = new Individual("24", BLANK_NAMESPACE);
        put(this.individual24);
        this.individual25 = new Individual("25", BLANK_NAMESPACE);
        put(this.individual25);
        this.individual26 = new Individual("26", BLANK_NAMESPACE);
        put(this.individual26);
        this.individual27 = new Individual("27", BLANK_NAMESPACE);
        put(this.individual27);
        this.individual28 = new Individual("28", BLANK_NAMESPACE);
        put(this.individual28);
        this.individual29 = new Individual("29", BLANK_NAMESPACE);
        put(this.individual29);
        this.individual30 = new Individual("30", BLANK_NAMESPACE);
        put(this.individual30);

        this.stakeholder41 =
            new Stakeholder(this.decisionOne.makeKey(), this.user.makeKey(), BLANK_NAMESPACE);
        put(this.stakeholder41);

        this.length = new MeasurementQuantity("length", BLANK_NAMESPACE);
        put(this.length);
        this.area = new MeasurementQuantity("area", BLANK_NAMESPACE);
        put(this.area);
        this.meter = new StandardMeasurementUnit(this.length.makeKey(), "meter", BLANK_NAMESPACE);
        put(this.meter);

        this.squareMeter =
            new StandardMeasurementUnit(this.area.makeKey(), "square meter", BLANK_NAMESPACE);
        put(this.squareMeter);

        // note that the unit name, "foot" does not appear here. :-)
        this.foot =
            new AffineMeasurementUnit(Double.valueOf(3.2808), this.meter.makeKey(), Double
                .valueOf(0.0), BLANK_NAMESPACE);
        put(this.foot);

        this.wordSense1 =
            new WordSense(EN_US, "individual 21", true, this.individual21.makeKey(),
                BLANK_NAMESPACE);
        put(this.wordSense1);

        this.stringProperty1 =
            new StringProperty(this.clazz.makeKey(), "aproperty", BLANK_NAMESPACE);
        put(this.stringProperty1);

        this.wordSense2 =
            new WordSense(EN_US, "A Property", true, this.stringProperty1.makeKey(),
                BLANK_NAMESPACE);
        put(this.wordSense2);

        this.wordSense3 =
            new WordSense(EN_US, "Square Meter", true, this.squareMeter.makeKey(), BLANK_NAMESPACE);
        put(this.wordSense3);

        this.parentClass =
            new com.joelsgarage.model.Class("parentClassName", "parentClassNamespace");
        put(this.parentClass);

        this.childClass = new com.joelsgarage.model.Class("childClassName", "childClassNamespace");
        put(this.childClass);

        this.subclass =
            new Subclass(this.childClass.makeKey(), this.parentClass.makeKey(), "subclassNamespace");
        put(this.subclass);

        this.stakeholderS = new Stakeholder(this.decisionOne.makeKey(), this.user.makeKey(), BLANK);
        put(this.stakeholderS);
    }

    protected void put(ModelEntity e) {
        this.entities.put(e.makeKey(), e);
    }
}
