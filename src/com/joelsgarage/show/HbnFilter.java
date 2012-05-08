/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.joelsgarage.util.HbnSessionUtil;

/**
 * Stripes filter that manages Hibernate sessions. Configure this filter "around" the rest of the
 * app.
 * 
 * @author joel
 * 
 */
public class HbnFilter implements Filter {
	@SuppressWarnings("unused")
	/** ignored */
	private FilterConfig filterConfig = null;

	/**
	 * Parameter is ignored.
	 * 
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig config) {
		this.filterConfig = config;
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		this.filterConfig = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
		throws IOException, ServletException {
		try {
			HbnSessionUtil.beginTransaction();

			chain.doFilter(req, resp);
		} catch (Exception e) {
			HbnSessionUtil.rollbackOnly();

			e.printStackTrace();

			req.setAttribute("e", e); //$NON-NLS-1$
			req.getRequestDispatcher(ApplicationURL.APP_EXCEPTION).forward(req, resp);
		} finally {
			HbnSessionUtil.resolveTransaction();
			HbnSessionUtil.closeSession();
		}
	}
}