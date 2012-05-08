/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.lookup;

/**
 * @author joel
 * 
 */
public class SomeJoinType extends SomeRecordType {
	/** Points at SomeRecordType.primaryKey */
	private String foreignKey;

	public String getForeignKey() {
		return this.foreignKey;
	}

	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}

	/**
	 * @param primaryKey
	 * @param data
	 * @param foreignKey
	 */
	public SomeJoinType(String primaryKey, String data, String foreignKey) {
		super(primaryKey, data);
		this.foreignKey = foreignKey;
	}

}
