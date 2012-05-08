/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.NameUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents a fact whose value is an opaque string.
 * 
 * The distinction between this and an IndividualFact is that the string doesn't represent an
 * individual. Accordingly it may be more appropriate for facts whose references are not understood.
 * For example, the fact "colors = red/blue" could be a string fact with value "red/blue", and then
 * some gardener later transforms this into two individual facts, "color=red" and "color=blue".
 * 
 * Another distinction is that there is currently no way to represent a string preference.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.STRING_FACT_TYPE)
public final class StringFact extends Fact {
    public static final String VALUE = "value"; //$NON-NLS-1$
    private String value = new String();

    protected StringFact() {
        super();
    }

    public StringFact(ExternalKey subjectKey, ExternalKey propertyKey, String value,
        String namespace) throws FatalException {
        super(subjectKey, propertyKey, namespace);
        setValue(value);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getValue());
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setValue(input.get(VALUE));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(VALUE, getValue());
    }

    // // TODO: implement the subclass?
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
    // result += " value: " + String.valueOf(getValue());
    // return result;
    //    }

    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.SUBJECT + NameUtil.EQUALS + getSubjectKey().escapedString() //
    // + NameUtil.AND //
    // + NameUtil.PROPERTY + NameUtil.EQUALS + getPropertyKey().escapedString() //
    // + NameUtil.AND //
    // + NameUtil.VALUE + NameUtil.EQUALS + NameUtil.encode(getValue());
    // }

    /** The value of this fact, e.g. for the fact "color=red", the value is "red". */
    @VisibleField(NameUtil.VALUE)
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /** Covariant */
    @VisibleJoin(value = StringProperty.class, name = NameUtil.PROPERTY)
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    @Override
    public ExternalKey getPropertyKey() {
        return super.getPropertyKey();
    }
}
