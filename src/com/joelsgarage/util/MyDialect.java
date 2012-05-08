/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.sql.Types;

import org.hibernate.dialect.MySQL5Dialect;

/**
 * fix for Hibernate to allow byte[] PK.
 * 
 * @author joel
 */
public class MyDialect extends MySQL5Dialect {
    public MyDialect() {
        super();
        registerColumnType(Types.VARBINARY, 255, "varbinary($l)"); //$NON-NLS-1$
    }
}
