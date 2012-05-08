/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

/**
 * See Lee 2007 (https://nscnt12.nsc.gov.tw/APPLYFORM/WRITINGS/U120353377/20080220221959.pdf). To
 * estimate a missing preference involving two individuals, we find a third individual (the "wheel")
 * that we do have preferences for, between the two desired individuals.
 * 
 * Note the notation is different here: I and J are desired, K is the wheel.
 * 
 * @author joel
 * 
 */
public class ImplicationOperator {
	/**
	 * This is a separate class so I can make little lists of them, and access them by name.
	 * 
	 * @author joel
	 * 
	 */
	public static class Inputs {
		/*
		 * The possible preferences we use as input. Null if unavailable.
		 */
		private Double rIK;
		private Double rKJ;
		private Double rJK;
		private Double rKI;

		public Inputs() {
			this(null, null, null, null);
		}

		public Inputs(final Double rIK, final Double rKJ, final Double rJK, final Double rKI) {
			setRIK(rIK);
			setRKJ(rKJ);
			setRJK(rJK);
			setRKI(rKI);
		}

		@Override
		@SuppressWarnings("nls")
		public String toString() {
			String result = new String();
			if (getRIK() != null) {
				result += "rIK: " + getRIK().toString();
			} else {
				result += "rIK null";
			}
			if (getRKJ() != null) {
				result += " rKJ: " + getRKJ().toString();
			} else {
				result += " rKJ null";
			}
			if (getRJK() != null) {
				result += " rJK: " + getRJK().toString();
			} else {
				result += " rJK null";
			}
			if (getRKI() != null) {
				result += " rKI: " + getRKI().toString();
			} else {
				result += " rKI null";
			}
			return result;
		}

		protected Double getRIK() {
			return this.rIK;
		}

		protected void setRIK(Double rik) {
			this.rIK = rik;
		}

		protected Double getRKJ() {
			return this.rKJ;
		}

		protected void setRKJ(Double rkj) {
			this.rKJ = rkj;
		}

		protected Double getRJK() {
			return this.rJK;
		}

		protected void setRJK(Double rjk) {
			this.rJK = rjk;
		}

		protected Double getRKI() {
			return this.rKI;
		}

		protected void setRKI(Double rki) {
			this.rKI = rki;
		}
	}

	private Inputs inputs;
	/*
	 * All these values are null if they can't be calculated.
	 */
	/**
	 * rIJ1 = rIK + rKJ - 0.5
	 */
	private Double rIJ1;
	/**
	 * rIJ2 = rKJ - rKI + 0.5
	 */
	private Double rIJ2;
	/**
	 * rIJ3 = rIK - rJK + 0.5
	 */
	private Double rIJ3;
	/**
	 * rIJ4 = 1.5 - rKI - rJK
	 */
	private Double rIJ4;
	/**
	 * The final calculated value, if a value can be calculated.
	 */
	private Double value;

	/**
	 * The number of contributing terms.
	 */
	private int F;

	/**
	 * OK to pass null for any of these if they don't exist.
	 * 
	 * @param rIK
	 * @param rKJ
	 * @param rJK
	 * @param rKI
	 */
	// public ImplicationOperator(final Double rIK, final Double rKJ, final Double rJK,
	// final Double rKI) {
	public ImplicationOperator(final Inputs inputs) {
		// setRIK(rIK);
		// setRKJ(rKJ);
		// setRJK(rJK);
		// setRKI(rKI);
		setInputs(inputs);
		setValue(Double.valueOf(0.0));
		setF(0);
		calculateValue();
	}

	@SuppressWarnings("nls")
	private void calculateValue() {
		Inputs i = getInputs();
		if ((i.getRIK() != null) && (i.getRKJ() != null)) {
			setRIJ1(Double.valueOf(i.getRIK().doubleValue() + i.getRKJ().doubleValue() - 0.5));
			setValue(Double.valueOf(getValue().doubleValue() + getRIJ1().doubleValue()));
			// System.out.println("RIJ1, now value: " + getValue().toString());
			setF(getF() + 1);
		}
		if ((i.getRKJ() != null) && (i.getRKI() != null)) {
			setRIJ2(Double.valueOf(i.getRKJ().doubleValue() - i.getRKI().doubleValue() + 0.5));
			setValue(Double.valueOf(getValue().doubleValue() + getRIJ2().doubleValue()));
			// System.out.println("RIJ2, now value: " + getValue().toString());
			setF(getF() + 1);
		}
		if ((i.getRIK() != null) && (i.getRJK() != null)) {
			setRIJ3(Double.valueOf(i.getRIK().doubleValue() - i.getRJK().doubleValue() + 0.5));
			setValue(Double.valueOf(getValue().doubleValue() + getRIJ3().doubleValue()));
			// System.out.println("RIJ3, now value: " + getValue().toString());

			setF(getF() + 1);
		}
		if ((i.getRKI() != null) && (i.getRJK() != null)) {
			setRIJ4(Double.valueOf(1.5 - i.getRKI().doubleValue() - i.getRJK().doubleValue()));
			setValue(Double.valueOf(getValue().doubleValue() + getRIJ4().doubleValue()));
			// System.out.println("RIJ4, now value: " + getValue().toString());

			setF(getF() + 1);
		}
		if (getF() > 0) {
			setValue(Double.valueOf(getValue().doubleValue() / this.F));
			// System.out.println("final value: " + getValue().toString());
			// TODO: add something about consistency
		} else {
			setValue(null);
		}
	}

	protected Inputs getInputs() {
		return this.inputs;
	}

	protected void setInputs(Inputs inputs) {
		this.inputs = inputs;
	}

	public Double getValue() {
		return this.value;
	}

	protected Double getRIJ1() {
		return this.rIJ1;
	}

	protected void setRIJ1(Double rij1) {
		this.rIJ1 = rij1;
	}

	protected Double getRIJ2() {
		return this.rIJ2;
	}

	protected void setRIJ2(Double rij2) {
		this.rIJ2 = rij2;
	}

	protected Double getRIJ3() {
		return this.rIJ3;
	}

	protected void setRIJ3(Double rij3) {
		this.rIJ3 = rij3;
	}

	protected Double getRIJ4() {
		return this.rIJ4;
	}

	protected void setRIJ4(Double rij4) {
		this.rIJ4 = rij4;
	}

	protected int getF() {
		return this.F;
	}

	protected void setF(int f) {
		this.F = f;
	}

	protected void setValue(Double value) {
		this.value = value;
	}

}
