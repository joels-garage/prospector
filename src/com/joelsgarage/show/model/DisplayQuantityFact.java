/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.QuantityFact;
import com.joelsgarage.util.HibernateProperty;

/**
 * QuantityFact has an optional unit.
 * 
 * @author joel
 * 
 */
public class DisplayQuantityFact extends DisplayFact {
	private DisplayMeasurementUnit expressedUnit;

	/**
	 * Getting this UnitSynonym is not dereferencing. To avoid a cycle, it's a many-to-many relation
	 * on MeasurementUnit, with an extra constraint (the "canonical name" bit).
	 * 
	 * So, we need a different way to express that kind of query, in general. For now we just kind
	 * of cheat, using the Hibernate Criteria directly.
	 * 
	 * Probably better to move this goop into the DisplayUnitSynonym, at least. :-)
	 */
	private DisplayUnitSynonym canonicalNameUnitSynonym;

	public DisplayQuantityFact(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setExpressedUnit(new DisplayMeasurementUnit(false));
			setCanonicalNameUnitSynonym(new DisplayUnitSynonym(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		ExternalKey unitKey = getInstance().getExpressedUnitKey();
		if (unitKey.equals(new ExternalKey()))
			// this quantity has no unit
			return;
		if (getExpressedUnit() != null) {
			getExpressedUnit().fetch(getInstance().getExpressedUnitKey());
		}
		if (getCanonicalNameUnitSynonym() != null) {
			// Get the unit synonym with this measurement unit key
			getCanonicalNameUnitSynonym().fetch(
				org.hibernate.criterion.Property.forName(HibernateProperty.MEASUREMENT_UNIT_KEY)
					.eq(getInstance().getExpressedUnitKey()),
				// ... and get the "canonical name" unit synonym.
				org.hibernate.criterion.Property.forName(HibernateProperty.CANONICAL_NAME).eq(
					Boolean.TRUE));
			// Note this may or may not actually exist.
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return QuantityFact.class;
	}

	@Override
	public QuantityFact getInstance() {
		return (QuantityFact) super.getInstance();
	}

	//

	public DisplayMeasurementUnit getExpressedUnit() {
		return this.expressedUnit;
	}

	public void setExpressedUnit(DisplayMeasurementUnit expressedUnit) {
		this.expressedUnit = expressedUnit;
	}

	public DisplayUnitSynonym getCanonicalNameUnitSynonym() {
		return this.canonicalNameUnitSynonym;
	}

	public void setCanonicalNameUnitSynonym(DisplayUnitSynonym canonicalNameUnitSynonym) {
		this.canonicalNameUnitSynonym = canonicalNameUnitSynonym;
	}

}
