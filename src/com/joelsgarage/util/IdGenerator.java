/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.joelsgarage.model.ModelEntity;

/**
 * Grr.
 * 
 * Hibernate seems unable to handle large primary keys, because it can't generate the appropriate
 * prefix length specification for MySql (note this prefix length thing is kind of a hack anyway).
 * See the code org.hibernate.mapping.PrimaryKey:18 for the problem.
 * 
 * And honestly it's not *that* good an idea to have really long primary keys. I could try to keep
 * them short, but shortness and uniqueness are opposing constraints, and it's nice for the key to
 * be legible as well as unique.
 * 
 * So, this class produces a short, opaque, String primary key.
 * 
 * It's the Base64 serialization of the SHA-1 hash of the toString serialization of the key of the
 * entity.
 * 
 * @author joel
 * 
 */
@Deprecated
public class IdGenerator implements IdentifierGenerator {

	public IdGenerator() {
		super();
	}

	@Override
	public String generate(SessionImplementor session, Object object) throws HibernateException {
		if (!(object instanceof ModelEntity)) {
			throw new HibernateException("bad object type in IdGenerator"); //$NON-NLS-1$
		}
		ModelEntity entity = (ModelEntity) object;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1"); //$NON-NLS-1$
		} catch (NoSuchAlgorithmException e) {
			throw new HibernateException(e);
		}
		byte[] hash = new byte[40];
		hash = md.digest(entity.makeKey().toString().getBytes());
		String key = Base64.encodeBytes(hash);
		return key;
	}
}
