/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents something about an individual. A fact instance fills the slots described by a
 * property. See subclasses for details.
 * <p>
 * Detailed examples
 * <p>
 * Measurement Fact: My particular volvo v70 has 83270 miles
 * <ul>
 * <li>individual = my particular volvo v70 (a Long id reference)
 * <li>property = measurement property: "mileage" (a Long id reference)
 * <li>value = 134010075 meters (a float)
 * </ul>
 * <p>
 * Quantity Fact: A particular campsite has space for 2 cars:
 * <ul>
 * <li>individual = campsite-x (a Long id reference)
 * <li>property = quantity property: "number of cars" (a Long id reference)
 * <li>value = 2 (a float)
 * </ul>
 * <p>
 * Individual Fact
 * <ul>
 * <li>individual = hillary clinton (a Long id reference)
 * <li>property = individual property: "party affiliation" (a Long id reference)
 * <li>value = democratic party (a Long id reference)
 * </ul>
 * <p>
 * Other
 * <p>
 * "toyota camry is mentioned here" (maybe?).
 * <p>
 * the canonical Canon S200 has 3x optical zoom
 * <p>
 * 
 * 
 * @author joel
 */
@VisibleType(ExternalKey.FACT_TYPE)
public abstract class Fact extends ModelEntity {
    public static final String SUBJECT = "subject"; //$NON-NLS-1$
    public static final String PROPERTY = "property"; //$NON-NLS-1$
    /**
     * The externalKey of the individual described by this Fact, e.g. in "Hillary Clinton is a
     * Democrat," the subject is "Hillary Clinton." The key would be some individual, e.g.:
     * 
     * <pre>
     * www.foo.com#individual/somebody
     * </pre>
     */
    private ExternalKey subjectKey = new ExternalKey();

    /**
     * The property being described, e.g. "Party Affiliation." The key is some property, e.g.
     * 
     * <pre>
     * www.foo.com#property/bar
     * </pre>
     */
    private ExternalKey propertyKey = new ExternalKey();

    protected Fact() {
        super();
    }

    public Fact(ExternalKey subjectKey, ExternalKey propertyKey, String namespace) {
        super(namespace);
        setSubjectKey(subjectKey);
        setPropertyKey(propertyKey);
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setSubjectKey(new ExternalKey(input.get(SUBJECT)));
        setPropertyKey(new ExternalKey(input.get(PROPERTY)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(SUBJECT, String.valueOf(getSubjectKey()));
        output.put(PROPERTY, String.valueOf(getPropertyKey()));
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getSubjectKey());
        u.update(getPropertyKey());
    }

    // @Override
    // public DisplayModelEntity newDisplayEntity() {
    // DisplayFact d = new DisplayFact(true);
    // d.setInstance(this);
    // return d;
    // }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " subject : " + String.valueOf(getSubjectKey()) + //
    // " property: " + String.valueOf(getPropertyKey());
    // return result;
    //    }

    @VisibleJoin(value = Individual.class, name = SUBJECT)
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getSubjectKey() {
        return this.subjectKey;
    }

    public void setSubjectKey(ExternalKey subjectKey) {
        this.subjectKey = subjectKey;
    }

    /** Covariant */
    @VisibleJoin(value = Property.class, name = PROPERTY)
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getPropertyKey() {
        return this.propertyKey;
    }

    public void setPropertyKey(ExternalKey propertyKey) {
        this.propertyKey = propertyKey;
    }
}
