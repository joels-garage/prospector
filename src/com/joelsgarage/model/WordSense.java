/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleType;

/**
 * WordSense is a user-visible pretty label for an entity.
 * 
 * This is like "name" but multi valued.
 * 
 * Also because "name" is sometimes used in keys (e.g. for foreign names), this is the only
 * "name"-like thing available. Bleah.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.WORD_SENSE_TYPE)
public final class WordSense extends ModelEntity {
    public static final String LANG = "lang"; //$NON-NLS-1$
    public static final String WORD = "word"; //$NON-NLS-1$
    public static final String CANONICAL = "canonical"; //$NON-NLS-1$
    public static final String REFERENT = "referent"; //$NON-NLS-1$

    /** RFC 4646 string. Or maybe just RFC 3066 :-) */
    private String lang = new String();
    /** text of the sense, could be multiple tokens, e.g. "Roman Catholic". */
    private String word = new String();
    /** use this sense as a label for the whole "synset" i.e. class or whatever */
    private boolean canonical = false;
    /**
     * the synset, i.e. class, described. Can be any ModelEntity.
     */
    private ExternalKey referentKey = new ExternalKey();

    protected WordSense() {
        // no
    }

    public WordSense(String lan, String word, boolean canonical, ExternalKey referentKey,
        String namespace) throws FatalException {
        super(namespace);
        setLang(lan);
        setWord(word);
        setCanonical(canonical);
        setReferentKey(referentKey);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getLang());
        u.update(getWord());
        u.update(Boolean.valueOf(isCanonical()));
        u.update(getReferentKey());
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setLang(input.get(LANG));
        setWord(input.get(WORD));
        setCanonical(Boolean.valueOf(input.get(CANONICAL)).booleanValue());
        setReferentKey(new ExternalKey(input.get(REFERENT)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(LANG, getLang());
        output.put(WORD, getWord());
        output.put(CANONICAL, String.valueOf(isCanonical()));
        output.put(REFERENT, String.valueOf(getReferentKey()));
    }

    // /** name is made of subject, object, and author */
    // @Override
    // public void setDefaultName() {
    // setName(NameUtil.SUBJECT + NameUtil.EQUALS + getSubjectKey().escapedString() //
    // + NameUtil.AND //
    // + NameUtil.OBJECT + NameUtil.EQUALS + getObjectKey().escapedString() //
    // + NameUtil.AND //
    // + NameUtil.CREATOR + NameUtil.EQUALS + getCreatorKey().escapedString());
    // }

    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.REFERENT + NameUtil.EQUALS + getReferentKey().escapedString()//
    // + NameUtil.AND //
    // + NameUtil.VALUE + NameUtil.EQUALS + NameUtil.encode(getWord());
    // }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " lang: " + String.valueOf(getLang());
    // result += " word: " + String.valueOf(getWord());
    // result += " canonical: " + String.valueOf(isCanonical());
    //        return result;
    //    }

    //
    //

    public String getLang() {
        return this.lang;
    }

    public void setLang(String lan) {
        this.lang = lan;
    }

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isCanonical() {
        return this.canonical;
    }

    public void setCanonical(boolean canonical) {
        this.canonical = canonical;
    }

    public ExternalKey getReferentKey() {
        return this.referentKey;
    }

    public void setReferentKey(ExternalKey referentKey) {
        this.referentKey = referentKey;
    }
}
