/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.sourceforge.stripes.action.Resolution;

import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.fetchers.MapRecordFetcher;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.User;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class LoginActionBeanTest extends TestCase {
	// This used to work and no longer does, producing this error:
	//
	// Something is trying to access the current Stripes configuration but the current request was
	// never routed through the StripesFilter! As a result the appropriate Configuration object
	// cannot be located. Please take a look at the exact URL in your browser's address bar and
	// ensure that any requests to that URL will be filtered through the StripesFilter according to
	// the filter mappings in your web.xml.
	//
	// TODO: fix it
	

	// public void testSuccessfulLogin() throws Exception {
	// Map<String, ModelEntity> fetchMap = new HashMap<String, ModelEntity>();
	// User user = new User();
	// user.setName("shaggy");
	// user.setPassword("password");
	// user.setRealName("rogers");
	// fetchMap.put("shaggy", user);
	// RecordFetcher<String, ModelEntity> fetcher = new MapRecordFetcher<String, ModelEntity>(
	// fetchMap) {
	// @Override
	// protected String extractForeignKey(String fieldName, ModelEntity instance) {
	// // this method is not used by this test.
	// return null;
	// }
	// };
	// ShowAbstractActionBeanContext ctx = new MyMockActionBeanContext();
	// LoginActionBean bean = new LoginActionBean(fetcher);
	// bean.setContext(ctx);
	// bean.setUsername("shaggy");
	// bean.setPassword("password");
	// Resolution resolution = bean.login();
	//
	// assertNotNull(resolution);
	//
	// assertNotNull(ctx.getUser());
	// assertEquals("shaggy", ctx.getUser().getName()); //$NON-NLS-1$
	// assertEquals("rogers", ctx.getUser().getRealName()); //$NON-NLS-1$
	//	}
	

	@SuppressWarnings("nls")
	public void testFailedLogin() throws Exception {
		ShowAbstractActionBeanContext ctx = new MyMockActionBeanContext();
		Map<String, ModelEntity> fetchMap = new HashMap<String, ModelEntity>();
		User user = new User();
		user.setName("shaggy");
		user.setPassword("password");
		user.setRealName("rogers");
		fetchMap.put("shaggy", user);
		RecordFetcher<String, ModelEntity> fetcher = new MapRecordFetcher<String, ModelEntity>(
			fetchMap) {
			@Override
			protected String extractForeignKey(String fieldName, ModelEntity instance) {
				// this method is not used by this test.
				return null;
			}
		};

		LoginActionBean bean = new LoginActionBean(fetcher);
		bean.setContext(ctx);
		bean.setUsername("shaggy");
		bean.setPassword("scooby");

		Resolution resolution = bean.login();

		assertNotNull(resolution);

		assertNull(ctx.getUser());
		assertNotNull(ctx.getValidationErrors());
		assertNotNull(ctx.getValidationErrors().get("password"));
		assertEquals(1, ctx.getValidationErrors().get("password").size());
		assertEquals("password", ctx.getValidationErrors().get("password").get(0).getFieldName());
	}
}