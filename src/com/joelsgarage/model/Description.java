/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * 
 * @author joel
 */
@VisibleType(ExternalKey.DESCRIPTION_TYPE)
public final class Description extends ModelEntity {
    public static final String TEXT = "text"; //$NON-NLS-1$
    public static final String REFERENT = "referent"; //$NON-NLS-1$
    /** The text */
    private String text = new String();
    /** The entity to which this refers. */
    private ExternalKey referentKey = new ExternalKey();

    protected Description() {
        super();
    }

    /**
     * key is referent and text
     * 
     * @throws FatalException
     */
    public Description(ExternalKey referentKey, String text, String namespace)
        throws FatalException {
        super(namespace);
        setReferentKey(referentKey);
        setText(text);
        populateKey();
    }

    // @Override
    // public DisplayModelEntity newDisplayEntity() {
    // DisplayClass d = new DisplayClass(true);
    // d.setInstance(this);
    // return d;
    // }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setText(input.get(TEXT));
        setReferentKey(new ExternalKey(input.get(REFERENT)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(TEXT, getText());
        output.put(REFERENT, String.valueOf(getReferentKey()));
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getText());
        u.update(getReferentKey());
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " text: " + String.valueOf(getText());
    // return result;
    //    }

    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.REFERENT + NameUtil.EQUALS + getReferentKey().escapedString() //
    // + NameUtil.AND //
    // + NameUtil.CREATOR + NameUtil.EQUALS + getCreatorKey().escapedString();
    // }

    @VisibleField(TEXT)
    @WizardField(type = WizardField.Type.OPTIONAL, position = 2)
    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @VisibleJoin(value = ModelEntity.class, name = REFERENT)
    @WizardField(type = WizardField.Type.OPTIONAL, position = 3)
    public ExternalKey getReferentKey() {
        return this.referentKey;
    }

    public void setReferentKey(ExternalKey referentKey) {
        this.referentKey = referentKey;
    }
}
