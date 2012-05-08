/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

/**
 * @author joel
 * 
 */
public class DisplayLink {
	private String url;
	/** TODO: i18n */
	private String text;

	@SuppressWarnings("nls")
	public DisplayLink() {
		this("", "");
	}

	public DisplayLink(String url, String text) {
		setUrl(url);
		setText(text);
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
