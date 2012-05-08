/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents the expression of a unit. Some are the canonical one (e.g. "meter"); some are
 * canonical abbreviations (e.g. "m"), according to booleans herein.
 * 
 * TODO: some checking for these booleans: only one canonical unitsynonym name per measurementunit,
 * etc.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.UNIT_SYNONYM_TYPE)
public final class UnitSynonym extends ModelEntity {
    /** The MeasurementUnit expressed here, e.g. meter */
    private ExternalKey measurementUnitKey = new ExternalKey();

    /** The actual string, e.g. "m" or "meter" */
    private String value = new String();

    /** This instance is the canonical name (e.g. "foot") for this measurement unit. */
    private Boolean canonicalName = Boolean.FALSE;
    /**
     * This instance is the canonical plural name (e.g. "feet"). Noncanonical plurals are mixed in
     * with the other noncanonical synonyms. I dunno if this makes sense, but there it is anyway.
     */
    private Boolean canonicalPlural = Boolean.FALSE;

    /** This instance is the canonical abbreviation (e.g. "ft") for this measurement unit. */
    private Boolean canonicalAbbreviation = Boolean.FALSE;

    protected UnitSynonym() {
        super();
    }

    /** Key is measurementunit and value */
    public UnitSynonym(ExternalKey measurementUnitKey, String value, Boolean canonicalName,
        Boolean canonicalPlural, Boolean canonicalAbbreviation, String namespace)
        throws FatalException {
        super(namespace);
        setMeasurementUnitKey(measurementUnitKey);
        setValue(value);
        setCanonicalName(canonicalName);
        setCanonicalPlural(canonicalPlural);
        setCanonicalAbbreviation(canonicalAbbreviation);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getMeasurementUnitKey());
        u.update(getValue());
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " measurement unit : " + String.valueOf(getMeasurementUnitKey()) + //
    // " value: " + String.valueOf(getValue());
    // return result;
    //    }

    //
    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

    //

    @VisibleJoin(value = MeasurementUnit.class, name = "measurement_unit")
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getMeasurementUnitKey() {
        return this.measurementUnitKey;
    }

    public void setMeasurementUnitKey(ExternalKey measurementUnitKey) {
        this.measurementUnitKey = measurementUnitKey;
    }

    @VisibleField("value")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @VisibleField("canonical_name")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public Boolean getCanonicalName() {
        return this.canonicalName;
    }

    public void setCanonicalName(Boolean canonicalName) {
        this.canonicalName = canonicalName;
    }

    @VisibleField("canonical_abbreviation")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public Boolean getCanonicalAbbreviation() {
        return this.canonicalAbbreviation;
    }

    public void setCanonicalAbbreviation(Boolean canonicalAbbreviation) {
        this.canonicalAbbreviation = canonicalAbbreviation;
    }

    @VisibleField("canonical_plural")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public Boolean getCanonicalPlural() {
        return this.canonicalPlural;
    }

    public void setCanonicalPlural(Boolean canonicalPlural) {
        this.canonicalPlural = canonicalPlural;
    }
}
