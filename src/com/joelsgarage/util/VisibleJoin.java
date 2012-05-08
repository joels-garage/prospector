/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.joelsgarage.model.ModelEntity;

/**
 * Annotation indicating which joins are allowed, for what types, and using what visible term.
 * 
 * Annotate the "getter" on the foreign class.
 * 
 * For example, join Individual to Fact on Subject as follows:
 * 
 * <code>
 * class Fact {
 * @VisibleJoin(value = Individual.class, name = "subject")
 * public ExternalKey getSubjectKey();
 * }
 * </code>
 * 
 * @author joel
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VisibleJoin {
	/** The the type to join to */
	Class<? extends ModelEntity> value();

	/** The user-visible name (i.e. in the URL) */
	String name();
}
