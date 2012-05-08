/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * The new new version of "preference," a Rating represents a stakeholder's desires with respect to
 * some aspect of a decision.
 * <p>
 * There is only one kind of rating: the various subclasses of Preference are represented by the
 * query field, specifying a subgraph.
 * <p>
 * Ratings are absolute, ranging from 0 to 1 inclusive. Someday I'll do relative ratings (i.e. "i
 * strongly prefer x over y").
 * <p>
 * See http://sites.google.com/a/joelsgarage.com/wik/Home/design/multi-person-decision-making
 * 
 * @author joel
 */
@VisibleType(ExternalKey.RATING_TYPE)
public class Rating extends ModelEntity {
    public static final String STAKEHOLDER = "stakeholder"; //$NON-NLS-1$
    /**
     * The user-decision combination, which implies Class.
     */
    private ExternalKey stakeholderKey = new ExternalKey();
    private String query = new String();

    protected Rating() {
        super();
    }

    public Rating(final ExternalKey stakeholderKey, String query, String namespace) {
        super(namespace);
        setStakeholderKey(stakeholderKey);
        setQuery(query);
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getStakeholderKey());
        u.update(getQuery());
    }

    //
    //

    @VisibleJoin(value = Stakeholder.class, name = STAKEHOLDER)
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getStakeholderKey() {
        return this.stakeholderKey;
    }

    public void setStakeholderKey(final ExternalKey stakeholderKey) {
        this.stakeholderKey = stakeholderKey;
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
