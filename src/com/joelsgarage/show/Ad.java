/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

/**
 * An ad displayed in the "show" app.
 * 
 * @author joel
 * 
 */
public class Ad {
	/** where the clickthrough goes */
	private String url;
	/** the headline, in larger type */
	private String head;
	/** first line of text */
	private String line1;
	/** second line of text */
	private String line2;
	/** the advertiser, usually the domain */
	private String advertiser;
	/** optional location attribute */
	private String location;

	public Ad(String url, String head, String line1, String line2, String advertiser,
		String location) {
		setUrl(url);
		setHead(head);
		setLine1(line1);
		setLine2(line2);
		setAdvertiser(advertiser);
		setLocation(location);
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHead() {
		return this.head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getLine1() {
		return this.line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return this.line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getAdvertiser() {
		return this.advertiser;
	}

	public void setAdvertiser(String advertiser) {
		this.advertiser = advertiser;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
