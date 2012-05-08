/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * A simple stopwatch, with no relation to any particular UI.
 */
public class StopWatch {
	/** The fragments of the elapsed time. */
	private int hr, min, sec, fract;
	/** The fragments of the display time. */
	private int dhr, dmin, dsec, dfract;
	/** The stopwatch "split" (display freeze). */
	private boolean split;
	/** The Timer to keep time. */
	private Timer timer;
	/** The display decorations. */
	private static final String DELIM = ":"; //$NON-NLS-1$
	private static final String DOT = "."; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String ZERO = "0"; //$NON-NLS-1$

	private Logger logger;

	public StopWatch() {
		setLogger(Logger.getLogger(StopWatch.class));
		getLogger().info("foo"); //$NON-NLS-1$
	}

	protected void logError(final Exception exception) {
		if (getLogger().isEnabledFor(Level.ERROR)) {
			getLogger().error(exception.getMessage(), exception);
		}
	}

	public void reset() {
		this.hr = this.min = this.sec = this.fract = this.dhr = this.dmin = this.dsec = this.dfract = 0;
		this.split = false;
	}

	public void running() {
		this.split = false;
		if (this.timer == null) {
			this.timer = new Timer(true);
			this.timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					increment();
				}
			}, 100, 100);
		}
	}

	public void paused() {
		this.split = true;
	}

	public void stopped() {
		if (this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		} else {
			getLogger().error("shouldn't be null here"); //$NON-NLS-1$
		}
	}

	public String getDisplay() {
		String padhr = this.dhr > 9 ? EMPTY : ZERO;
		String padmin = this.dmin > 9 ? EMPTY : ZERO;
		String padsec = this.dsec > 9 ? EMPTY : ZERO;
		return new StringBuffer().append(padhr).append(this.dhr).append(DELIM).append(padmin)
			.append(this.dmin).append(DELIM).append(padsec).append(this.dsec).append(DOT).append(
				this.dfract).toString();
	}

	protected void increment() {
		if (this.fract < 9) {
			this.fract++;
		} else {
			this.fract = 0;
			if (this.sec < 59) {
				this.sec++;
			} else {
				this.sec = 0;
				if (this.min < 59) {
					this.min++;
				} else {
					this.min = 0;
					if (this.hr < 99) {
						this.hr++;
					} else {
						this.hr = 0; // wrap
					}
				}
			}
		}
		if (!this.split) {
			this.dhr = this.hr;
			this.dmin = this.min;
			this.dsec = this.sec;
			this.dfract = this.fract;
		}
	}

	public Logger getLogger() {
		return this.logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
