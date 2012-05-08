/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;

import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.fetchers.MapRecordFetcher;
import com.joelsgarage.dataprocessing.writers.ListRecordWriter;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.User;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class RegisterActionBeanTest extends TestCase {
	/** Demonstrate validation of a username not present in the DB */
	public void testSuccessfulValidation() throws Exception {
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

		ListRecordWriter<ModelEntity> writer = new ListRecordWriter<ModelEntity>();

		ShowAbstractActionBeanContext ctx = new MyMockActionBeanContext();
		RegisterActionBean bean = new RegisterActionBean(fetcher, writer);
		bean.setContext(ctx);
		User newUser = new User();

		newUser.setName("not shaggy");
		newUser.setPassword("another password");
		newUser.setRealName("my real name");
		newUser.setEmailAddress("my emailAddress");
		bean.setUser(newUser);

		ValidationErrors errors = new ValidationErrors();
		bean.validate(errors);
		assertEquals(0, errors.size());
	}

	/** Demonstrate registration, i.e. writing the specified user to the output and context */
	public void testSuccessfulRegistration() throws Exception {
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

		ListRecordWriter<ModelEntity> writer = new ListRecordWriter<ModelEntity>();

		ShowAbstractActionBeanContext ctx = new MyMockActionBeanContext();
		RegisterActionBean bean = new RegisterActionBean(fetcher, writer);
		bean.setContext(ctx);
		User newUser = new User();

		newUser.setName("not shaggy");
		newUser.setPassword("another password");
		newUser.setRealName("my real name");
		newUser.setEmailAddress("my emailAddress");
		bean.setUser(newUser);
		Resolution resolution = bean.register();

		assertNotNull(resolution);

		assertNotNull(ctx.getUser());
		assertEquals("not shaggy", ctx.getUser().getName()); //$NON-NLS-1$
		assertEquals("my real name", ctx.getUser().getRealName()); //$NON-NLS-1$

		List<ModelEntity> writerOutput = writer.getList();
		assertNotNull(writerOutput);
		assertEquals(1, writerOutput.size());
		assertEquals("not shaggy", writerOutput.get(0).getName());
		assertTrue(writerOutput.get(0) instanceof User);
	}

	/** Demonstrate failed validation using a user name that already exists in the database */
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

		ListRecordWriter<ModelEntity> writer = new ListRecordWriter<ModelEntity>();

		RegisterActionBean bean = new RegisterActionBean(fetcher, writer);
		bean.setContext(ctx);
		User newUser = new User();

		newUser.setName("shaggy");
		newUser.setPassword("another password");
		newUser.setRealName("my real name");
		newUser.setEmailAddress("my emailAddress");
		bean.setUser(newUser);
		
		ValidationErrors errors = new ValidationErrors();
		bean.validate(errors);
		assertEquals(1, errors.size());
		assertEquals("user.name", errors.keySet().iterator().next());
		List<ValidationError> errorList = errors.get("user.name");
		assertNotNull(errorList);
		assertEquals(1, errorList.size());
		assertEquals("user.name", errorList.get(0).getFieldName());
	}
}