/*******************************************************************************
 * (c) Copyright 2017 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package io.cloudslang.content.database.services;

import io.cloudslang.content.database.services.entities.SQLInputs;
import io.cloudslang.content.database.utils.Constants;
import io.cloudslang.content.database.utils.InputsProcessor;
import io.cloudslang.content.database.utils.OOResultSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by vranau on 12/11/2014.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionService.class, SQLQueryService.class})
public class SQLQueryServiceTest {

    private static final int SQL_QUERY_TIMEOUT = 10;
    private static final String SQL_QUERY = "select * from dbTable";
    private static final java.lang.Integer COLUMN_COUNT = 3;
    private static final String DEFAUL_LABEL = "defaulLabel";
    private SQLQueryService sqlQueryService = new SQLQueryService();
    private SQLInputs sqlInputs;

    @Mock
    private ConnectionService connectionServiceMock;
    @Mock
    private Connection connectionMock;

    @Mock
    private Statement statementMock;
    @Rule
    private ExpectedException expectedEx = ExpectedException.none();

    @Mock
    private ResultSet resultSetMock;
    @Mock
    private ResultSetMetaData resultSetMetadataMock;

    @Before
    public void setUp() throws Exception {
        sqlInputs = new SQLInputs();
        InputsProcessor.init(sqlInputs);
        PowerMockito.whenNew(ConnectionService.class).withNoArguments().thenReturn(connectionServiceMock);
        when(connectionServiceMock.setUpConnection(sqlInputs)).thenReturn(connectionMock);
        when(connectionMock.createStatement(Matchers.any(Integer.class), Matchers.any(Integer.class))).thenReturn(statementMock);
        when(statementMock.executeQuery(SQL_QUERY)).thenReturn(resultSetMock);
        when(resultSetMock.getMetaData()).thenReturn(resultSetMetadataMock);
        when(resultSetMetadataMock.getColumnCount()).thenReturn(COLUMN_COUNT);
        when(resultSetMetadataMock.getColumnLabel(Matchers.any(Integer.class))).thenReturn(DEFAUL_LABEL);
    }

    @Test
    public void testExecuteSqlQuery() throws Exception {
        sqlInputs.setDbType(Constants.ORACLE_DB_TYPE);
        sqlInputs.setDbPort("30");
        sqlInputs.setDbServer("localhost");
        sqlInputs.setDbName("/dbName");
        sqlInputs.setSqlCommand(SQL_QUERY);
        sqlInputs.setTimeout(SQL_QUERY_TIMEOUT);

        sqlQueryService.executeSqlQuery(sqlInputs);

        assertEquals("defaulLabel,defaulLabel,defaulLabel", sqlInputs.getStrColumns());
        verify(connectionServiceMock, Mockito.times(1)).closeConnection(connectionMock);
        verify(connectionMock, Mockito.times(1)).setReadOnly(true);
        verify(statementMock, Mockito.times(1)).setQueryTimeout(SQL_QUERY_TIMEOUT);
        verify(statementMock, Mockito.times(1)).executeQuery(SQL_QUERY);
    }

    @Test
    public void testExecuteSqlQueryPSQLLocal() throws Exception {
        sqlInputs.setDbType(Constants.POSTGRES_DB_TYPE);
        sqlInputs.setDbPort("5432");
        sqlInputs.setDbServer("localhost");
        sqlInputs.setDbName("/dbName");
        sqlInputs.setSqlCommand(SQL_QUERY);
        sqlInputs.setResultSetType(OOResultSet.TYPE_SCROLL_INSENSITIVE);
        sqlInputs.setResultSetConcurrency(OOResultSet.CONCUR_READ_ONLY);
        sqlInputs.setTimeout(SQL_QUERY_TIMEOUT);

        sqlQueryService.executeSqlQuery(sqlInputs);

        assertEquals("defaulLabel,defaulLabel,defaulLabel", sqlInputs.getStrColumns());
        verify(connectionServiceMock, Mockito.times(1)).closeConnection(connectionMock);
        verify(connectionMock, Mockito.times(1)).setReadOnly(true);
        verify(statementMock, Mockito.times(1)).setQueryTimeout(SQL_QUERY_TIMEOUT);
        verify(statementMock, Mockito.times(1)).executeQuery(SQL_QUERY);
    }

    @Test
    public void testExecuteSqlQueryNoCommand() throws Exception {
        expectedEx.expect(Exception.class);
        expectedEx.expectMessage("command input is empty.");
        sqlQueryService.executeSqlQuery(sqlInputs);
    }
}
