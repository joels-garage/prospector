/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

/**
 * A very simple calculator action.
 * 
 * @author Tim Fennell
 */
public class CalculatorActionBean implements ActionBean {
	private ActionBeanContext context;
	@Validate(required = true)
	private double numberOne;
	@Validate(required = true)
	private double numberTwo;
	private double result;

	@DefaultHandler
	public Resolution addition() {
		this.result = getNumberOne() + getNumberTwo();
		return new ForwardResolution("/quickstart/index.jsp"); //$NON-NLS-1$
	}

	public Resolution division() {
		this.result = this.numberOne / this.numberTwo;
		return new ForwardResolution("/quickstart/index.jsp"); //$NON-NLS-1$
	}

	@ValidationMethod(on = "division")
	public void avoidDivideByZero(ValidationErrors errors) {
		if (this.numberTwo == 0) {
			// errors.add("numberTwo", new SimpleError("Dividing by zero is not allowed."));
			// //$NON-NLS-1$//$NON-NLS-2$
			errors
				.add(
					"numberTwo", new LocalizableError("joelsgarage.validation.divideByZero", Double.valueOf(this.numberTwo))); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	//
	public ActionBeanContext getContext() {
		return this.context;
	}

	public void setContext(ActionBeanContext context) {
		this.context = context;
	}

	public double getNumberOne() {
		return this.numberOne;
	}

	public void setNumberOne(double numberOne) {
		this.numberOne = numberOne;
	}

	public double getNumberTwo() {
		return this.numberTwo;
	}

	public void setNumberTwo(double numberTwo) {
		this.numberTwo = numberTwo;
	}

	public double getResult() {
		return this.result;
	}

	public void setResult(double result) {
		this.result = result;
	}
}