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
 * Represents the belonging of an individual to a class.
 * <p>
 * At the moment, this is the only way to associate an individual with a class.
 * <p>
 * In a sense, a ClassMember is just a class-valued Fact, but it's a special one. If it turns out
 * that ClassMember should be represented as, say, ClassFact instead, I can change it.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.CLASS_MEMBER_TYPE)
public final class ClassMember extends ModelEntity {
    public static final String INDIVIDUAL = "individual"; //$NON-NLS-1$
    public static final String CLASS = "class"; //$NON-NLS-1$

    /** The individual whose membership is herein described */
    private ExternalKey individualKey = new ExternalKey();
    /** The class to which this individual belongs */
    private ExternalKey classKey = new ExternalKey();

    protected ClassMember() {
        super();
    }

    /**
     * Hash incorporates individual and class, and type and namespace.
     * 
     * @param individualKey
     * @param classKey
     * @param namespace
     * @return
     * @throws FatalException
     */
    public ClassMember(ExternalKey individualKey, ExternalKey classKey, String namespace)
        throws FatalException {
        setNamespace(namespace);
        setIndividualKey(individualKey);
        setClassKey(classKey);
        populateKey();
    }

    // @Override
    // public DisplayModelEntity newDisplayEntity() {
    // DisplayClassMember d = new DisplayClassMember(true);
    // d.setInstance(this);
    // return d;
    // }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setIndividualKey(new ExternalKey(input.get(INDIVIDUAL)));
        setClassKey(new ExternalKey(input.get(CLASS)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(INDIVIDUAL, String.valueOf(getIndividualKey()));
        output.put(CLASS, String.valueOf(getClassKey()));
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getIndividualKey());
        u.update(getClassKey());
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " individual: " + String.valueOf(getIndividualKey()) + //
    // " class: " + String.valueOf(getClassKey());
    // return result;
    //    }

    // This is from model.xsl:
    //
    // <xsl:text>individual=</xsl:text>
    // <xsl:value-of select="$individual_namespace" />
    // <xsl:text>/</xsl:text>
    // <xsl:value-of select="$individualKeyKey" />
    // <xsl:text>&amp;class=</xsl:text>
    // <xsl:value-of select="$class_namespace" />
    // <xsl:text>/</xsl:text>
    // <xsl:value-of select="$classKeyKey" />

    // Old code:
    //
    // return INDIVIDUAL + EQUALS + encode(individualKey.getNamespace()) //
    // + SLASH + encode(individualKey.getKey()) //
    // + AND + CLASS + EQUALS + encode(classKey.getNamespace()) //
    // + SLASH + encode(classKey.getKey());

    // /** Assumes that IndividualKey and ClassKey are both already escaped */
    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.INDIVIDUAL + NameUtil.EQUALS + getIndividualKey() //
    // + NameUtil.AND //
    // + NameUtil.CLASS + NameUtil.EQUALS + getClassKey();
    // }

    @VisibleJoin(value = Individual.class, name = INDIVIDUAL)
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getIndividualKey() {
        return this.individualKey;
    }

    public void setIndividualKey(ExternalKey individualKey) {
        this.individualKey = individualKey;
    }

    @VisibleJoin(value = Class.class, name = CLASS)
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getClassKey() {
        return this.classKey;
    }

    public void setClassKey(ExternalKey classKey) {
        this.classKey = classKey;
    }
}
