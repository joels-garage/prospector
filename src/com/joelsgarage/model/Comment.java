/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * A comment is just a free-text statement made by some user at some time, about some entity.
 * 
 * Most comments, I think, will be about individuals, and associated with preferences; they're like
 * "reviews", i.e. preference justifications.
 * 
 * If you restrict the comments on a single individual to a single stakeholder cohort, and sort by
 * time, then you get a conversation about it.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.COMMENT_TYPE)
public final class Comment extends ModelEntity {
    /**
     * The text of the comment
     */
    private String text = new String();
    /**
     * The entity to which this comment refers.
     */
    private ExternalKey referentKey = new ExternalKey();

    /**
     * ISO 8601 (extended) localtime of the first time this entity was written. Comments should
     * really be immutable, but if for some reason they're changed, I want to retain the original
     * creation date, otherwise the sequence of comments is lost.
     * 
     * TODO: consider pulling this up.
     * 
     * TODO: this is not actually populated, and won't be until I pull it up. :-)
     */
    private String createDate = new String();

    protected Comment() {
        super();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
    }

    // @Override
    // public DisplayModelEntity newDisplayEntity() {
    // DisplayClass d = new DisplayClass(true);
    // d.setInstance(this);
    // return d;
    // }

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
    // + NameUtil.CREATOR + NameUtil.EQUALS + getCreatorKey().escapedString() //
    // + NameUtil.AND //
    // + NameUtil.CREATE_DATE + NameUtil.EQUALS + getCreateDate();
    // }

    @VisibleField("text")
    @WizardField(type = WizardField.Type.OPTIONAL, position = 2)
    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @VisibleJoin(value = ModelEntity.class, name = "referent")
    @WizardField(type = WizardField.Type.OPTIONAL, position = 3)
    public ExternalKey getReferentKey() {
        return this.referentKey;
    }

    public void setReferentKey(ExternalKey referentKey) {
        this.referentKey = referentKey;
    }

    // DETERMINED
    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
