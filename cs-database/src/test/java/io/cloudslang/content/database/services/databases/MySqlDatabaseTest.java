/*******************************************************************************
 * (c) Copyright 2017 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package io.cloudslang.content.database.services.databases;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.SQLException;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by vranau on 12/10/2014.
 */
public class MySqlDatabaseTest {

    public static final String DB_NAME = "/dbName";
    public static final String DB_SERVER = "dbServer";
    public static final String DB_PORT = "30";
    public static final String DB_SERVER_IPV6_LITERAL = "2001-0db8-85a3-0042-1000-8a2e-0370-7334.ipv6-literal.net";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    private ArrayList<String> dbUrls = null;

    @Before
    public void setUp() {
        dbUrls = new ArrayList<>();
    }

    @Test
    public void testSetUpNoDbName() throws ClassNotFoundException, SQLException {
        MySqlDatabase mySqlDatabase = new MySqlDatabase();
        mySqlDatabase.setUp("", DB_SERVER, DB_PORT, dbUrls);
        assertEquals("jdbc:mysql://dbServer:30", dbUrls.get(0));
        assertEquals(1, dbUrls.size());
    }

    @Test
    public void testSetUpNoServerName() throws ClassNotFoundException, SQLException {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("host   not valid");
        MySqlDatabase mySqlDatabase = new MySqlDatabase();
        mySqlDatabase.setUp(DB_NAME, null, DB_PORT, dbUrls);
    }

    @Test
    public void testSetUpNoDbPort() throws ClassNotFoundException, SQLException {
        expectedEx.expect(SQLException.class);
        expectedEx.expectMessage("No port provided!");
        MySqlDatabase mySqlDatabase = new MySqlDatabase();
        mySqlDatabase.setUp(DB_NAME, DB_SERVER, null, dbUrls);
    }

    @Test
    public void testSetUpAll() throws ClassNotFoundException, SQLException {
        MySqlDatabase mySqlDatabase = new MySqlDatabase();
        mySqlDatabase.setUp(DB_NAME, DB_SERVER, DB_PORT, dbUrls);
        assertEquals("jdbc:mysql://dbServer:30/dbName", dbUrls.get(0));
        assertEquals(1, dbUrls.size());
    }

    @Test
    public void testSetUpAllIPV6LIteral() throws ClassNotFoundException, SQLException {
        MySqlDatabase mySqlDatabase = new MySqlDatabase();
        mySqlDatabase.setUp(DB_NAME, DB_SERVER_IPV6_LITERAL, DB_PORT, dbUrls);
        assertEquals("jdbc:mysql://2001-0db8-85a3-0042-1000-8a2e-0370-7334.ipv6-literal.net:30/dbName", dbUrls.get(0));
        assertEquals(1, dbUrls.size());
    }
}
