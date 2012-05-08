/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents a stakeholder's desires with respect to some aspect of a decision.
 * <p>
 * There are several different kinds (subclasses) of preferences. All of them (i.e. the base class)
 * reference the StakeHolder (user+decision) involved in the preference.
 * <ul>
 * <li>IndividualPropertyPreference. Refers to an IndividualProperty, e.g. "I like the 'stainless
 * steel' value for the 'material' property" -- "stainless steel" is an individual here. This is
 * what I originally thought a preference was.
 * <li>ClassPreference. Refers to a Class, e.g. "I like democrats" refers to the class "democrat."
 * This is useful for classes which are not reducible to a single property (so "I like democrats" is
 * a bad example, since it could be better expressed as "I like the 'democrat' value for the
 * party-affiliation property").
 * <li>IndividualPreference. Refers to an Individual, e.g. "I like Hillary," refers to the
 * individual (Clinton) which is an alternative for the decision at hand.
 * <li>MeasurementPropertyPreference. Refers to a MeasurementProperty. Ultimately this is some
 * function of the measurement, like "more is better," or "x is optimum," or something.
 * </ul>
 * <p>
 * Absolute vs. Relative Preference. See background below. Preferences may be stated in terms of
 * utility or fuzzy-preference relation. An example of utility is a star rating. A fuzzy-preference
 * relation compares two alternatives, and assigns a score within [0,1] to the pair, representing
 * the degree to which the first is preferred over the second. For a value x, the reverse relation's
 * value is 1-x; thus a score of 0.5 indicates indifference. Each subclass of Preference may
 * represent either preference type (utility or preference-relation).
 * <p>
 * Examples of usage:
 * <ul>
 * <li>"love it/hate it" -- utility values close to 1 and 0, respectively, for individuals
 * (reference the individual)
 * <li>"I like this brand" -- utility value close to 1 for some IndividualProperty (i.e. reference
 * the property and the individual).
 * <li>"like X better than Y" -- fuzzy preference value close to 1 (reference both individuals)
 * <li>"4 out of 5 stars" -- utility value of 0.8 (or 0.83 if zero is possible) (reference the
 * individual)
 * <li>"I need to drive in the snow" -- utility value equal to 1 ("must-have") for a particular
 * dimension (e.g. snow/ice traction).
 * <li> I don't want to spend more than $x. -- utility *function* of price, equal to 1 less than x,
 * zero above.
 * <li> I want to make big enlargements. So this implies something about resolution, but isn't
 * really resolution per se. Should I create some sort of map between use-cases and properties? Or
 * is that not really that useful? Maybe do it later if it turns out to be useful.
 * </ul>
 * <p>
 * Different users' individual preferences are combined using some aggregate function (TBD) to
 * create a consensus ranking.
 * 
 * see http://sites.google.com/a/joelsgarage.com/wik/Home/design/multi-person-decision-making
 * 
 * @author joel
 */
@VisibleType(ExternalKey.PREFERENCE_TYPE)
public abstract class Preference extends ModelEntity {
    /**
     * The user-decision combination, which implies Class.
     */
    private ExternalKey stakeholderKey = new ExternalKey();

    protected Preference() {
        super();
    }

    /** Key includes stakeholder */
    protected Preference(final ExternalKey stakeholderKey, String namespace) {
        super(namespace);
        setStakeholderKey(stakeholderKey);
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getStakeholderKey());
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " stakeholder : " + String.valueOf(getStakeholderKey());
    // return result;
    // }

    @VisibleJoin(value = Stakeholder.class, name = "stakeholder")
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getStakeholderKey() {
        return this.stakeholderKey;
    }

    public void setStakeholderKey(final ExternalKey stakeholderKey) {
        this.stakeholderKey = stakeholderKey;
    }
}
