/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation of entity fields that should be rendered in the create and edit views. Annotate both
 * lookup and primitive getters.
 * 
 * An entity field may be:
 * <ul>
 * <li> required, like "classKey" in ClassMember
 * <li> optional, like "expressedUnitKey" in QuantityFact.
 * <li> determined, like "creatorKey" (the currently-logged-in-user) or timestamp ("now"), or key
 * </ul>
 * 
 * Unnannotated members are ignored by the wizard.
 * 
 * Members must also be annotated with VisibleField for the wizard to notice them. Maybe consider
 * combining this annotation with VisibleField.
 * 
 * Entity fields have an ordering within the wizard. For now just use an ordinal number.
 * 
 * The same field may be required in some subclasses (e.g. "User.name") but determined in others
 * (e.g. "ClassMember.name"). Use subclass overriding getters for those cases.
 * 
 * What to do with determinism? In all cases the function is pretty simple (e.g. a concatenation of
 * other fields), so maybe just indicate it here. Or maybe it's a property of the entity itself? The
 * key and name derivations for, say, DerivedProvenance are different than that for User.
 * 
 * I already have NameUtil, which derives names, and that seems like a good way to do it.
 * 
 * On the other hand, since I don't much care anymore about making the model entities "data only" I
 * could just put the derivation logic in them.
 * 
 * For now just handle deterministic fields outside of this annotation; thus determined and
 * unannotated are the same.
 * 
 * @author joel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WizardField {
	public enum Type {
		/** Must have a nonblank value, like like "classKey" in ClassMember */
		REQUIRED,
		/** May be null, like "expressedUnitKey" in QuantityFact */
		OPTIONAL,
		// /** System provides the value, like "creatorKey" or "timestamp" */
		// DETERMINED
	}

	/** The validation of this field */
	Type type();

	/**
	 * Where the field should appear in the wizard flow. Primitive fields may be grouped together
	 * with the same position; foreign key fields may not.
	 */
	int position();
}
