package com.joelsgarage.logic;

import junit.framework.TestCase;

public class ImplicationOperatorTest extends TestCase {
	private Double rIK;
	private Double rKJ;
	private Double rJK;
	private Double rKI;

	public void testAll() {
		this.rIK = Double.valueOf(0.1);
		this.rKJ = Double.valueOf(0.2);
		this.rJK = Double.valueOf(0.3);
		this.rKI = Double.valueOf(0.4);

		ImplicationOperator.Inputs inputs = new ImplicationOperator.Inputs(this.rIK, this.rKJ,
			this.rJK, this.rKI);
		ImplicationOperator op = new ImplicationOperator(inputs);
		Double result = op.getValue();
		assertNotNull(result);
		assertEquals(0.3, result.doubleValue(), 0.001);
	}
}