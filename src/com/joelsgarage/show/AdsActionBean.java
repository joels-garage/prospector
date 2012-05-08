/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.stripes.action.Before;

/**
 * @author joel
 * 
 */
public class AdsActionBean extends ShowActionBean {
	// TODO: something with the bold type.
	@SuppressWarnings("nls")
	private List<Ad> ads = new ArrayList<Ad>() {
		private static final long serialVersionUID = 1L;
		{
			add(new Ad("/adfoo", "TEDCO Sheet Metal", "Prototype to Production",
				"Quick Turnaround", "www.tedcosheetmetal.com", "6736 Preston Avenue, Ste. A, 94551"));
			add(new Ad("/adfoo", "Cold Rolled <b>Steel</b>, Online",
				" Bar, Rod, Sheet &amp; Plate, 5000 items",
				"&quot;Cut to Size&quot;, &quot;No Minimum Order&quot;", "SpeedyMetals.com", null));
			add(new Ad("/adfoo", "California <b>Steel</b> Products",
				"Contact Us Today For Quality Rolled",
				"<b>Steel</b> Sheets, Products &amp; Solutions.", "www.Rolled<b>Steel</b>.com",
				"San Francisco-Oakland-San Jose, CA"));
			add(new Ad("/adfoo", "<b>Steel</b>", "Top class quality tool", "Tools made in Germany",
				"Solida-Werk.de/Tools", null));
			add(new Ad("/adfoo", "<b>Steel</b> Service Centers",
				"List of <b>steel</b> service centers", "with information, links, and RFQs.",
				"www.IndustrialQuickSearch.com", null));
			add(new Ad("/adfoo", "<b>Steel</b>", "Hex, Shaft, Flat, Sq., Tubes &amp; More",
				"Bar <b>Steel</b> Metric Line Items!", "MetricMetal.com", null));
			add(new Ad("/adfoo", "Harrison &amp; Bonini Inc", "Bolts, nuts, threaded fasteners",
				"All grades and materials", "www.hbbolt.com", "1122 Harrison St, San Francisco, CA"));

		}
	};

	/** Randomize the order for every request */
	@Before
	public void shuffle() {
		Collections.shuffle(getAds());
	}

	public List<Ad> getAds() {
		return this.ads;
	}

	public void setAds(List<Ad> ads) {
		this.ads = ads;
	}
}
