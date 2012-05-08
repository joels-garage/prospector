/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents a fact whose value is an individual.
 * 
 * May also be useful for facts we don't understand. Just create an individual for the object.
 * 
 * This may result in a proliferation of meaningless individuals, I suppose, but at least we can
 * indicate preference about them.
 * 
 * TODO: make sure the propertyId of this fact is an IndividualProperty.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.INDIVIDUAL_FACT_TYPE)
public final class IndividualFact extends Fact {
    public static final String OBJECT = "object"; //$NON-NLS-1$
    /**
     * The object is the individual that describes the subject, e.g. in "Hillary Clinton is a
     * democrat," the object is "democrat" (an individual member of the class, "political party").
     */
    private ExternalKey objectKey = new ExternalKey();

    protected IndividualFact() {
        super();
    }

    public IndividualFact(ExternalKey subjectKey, ExternalKey propertyKey, ExternalKey objectKey,
        String namespace) throws FatalException {
        super(subjectKey, propertyKey, namespace);
        setObjectKey(objectKey);
        populateKey();
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setObjectKey(new ExternalKey(input.get(OBJECT)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(OBJECT, String.valueOf(getObjectKey()));
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getObjectKey());
    }

    // @Override
    // public DisplayModelEntity newDisplayEntity() {
    // DisplayIndividualFact d = new DisplayIndividualFact(true);
    // d.setInstance(this);
    // return d;
    // }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " object : " + String.valueOf(getObjectKey());
    // return result;
    //    }

    // /** Assumes that the keys are already escaped */
    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.SUBJECT + NameUtil.EQUALS + getSubjectKey().toString() //
    // + NameUtil.AND //
    // + NameUtil.PROPERTY + NameUtil.EQUALS + getPropertyKey().toString() //
    // + NameUtil.AND //
    // + NameUtil.OBJECT + NameUtil.EQUALS + getObjectKey().toString();
    // }

    @VisibleJoin(value = Individual.class, name = OBJECT)
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public ExternalKey getObjectKey() {
        return this.objectKey;
    }

    public void setObjectKey(ExternalKey objectKey) {
        this.objectKey = objectKey;
    }

    /** Covariant */
    @VisibleJoin(value = IndividualProperty.class, name = "property")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    @Override
    public ExternalKey getPropertyKey() {
        return super.getPropertyKey();
    }
}
