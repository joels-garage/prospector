/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.lang.reflect.Method;

import com.joelsgarage.model.ModelEntity;

/**
 * Represents a foreign key relationship.
 * 
 * @author joel
 * 
 */
public class ForeignKey {
	private Class<? extends ModelEntity> clas;
	private Method method;
	private String label;

	public ForeignKey(Class<? extends ModelEntity> foreign, Method method, String label) {
		setClas(foreign);
		setMethod(method);
		setLabel(label);
	}

	@Override
	public String toString() {
		String result = getClas().getName();
		result += ":"; //$NON-NLS-1$
		result += getMethod().getName();
		result += ":"; //$NON-NLS-1$
		result += getLabel();
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getClas() == null) ? 0 : getClas().hashCode());
		result = prime * result + ((getLabel() == null) ? 0 : getLabel().hashCode());
		result = prime * result + ((getMethod() == null) ? 0 : getMethod().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ForeignKey other = (ForeignKey) obj;
		if (getClas() == null) {
			if (other.getClas() != null)
				return false;
		} else if (!getClas().equals(other.getClas()))
			return false;
		if (getLabel() == null) {
			if (other.getLabel() != null)
				return false;
		} else if (!getLabel().equals(other.getLabel()))
			return false;
		if (getMethod() == null) {
			if (other.getMethod() != null)
				return false;
		} else if (!getMethod().equals(other.getMethod()))
			return false;
		return true;
	}

	//

	public Class<? extends ModelEntity> getClas() {
		return this.clas;
	}

	public void setClas(Class<? extends ModelEntity> clas) {
		this.clas = clas;
	}

	public Method getMethod() {
		return this.method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
