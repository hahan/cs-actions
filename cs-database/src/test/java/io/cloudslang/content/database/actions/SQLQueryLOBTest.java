/*
 * (c) Copyright 2017 Hewlett-Packard Enterprise Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
*/
package io.cloudslang.content.database.actions;

import com.hp.oo.sdk.content.plugin.GlobalSessionObject;
import io.cloudslang.content.database.constants.DBResponseNames;
import io.cloudslang.content.database.constants.DBReturnCodes;
import io.cloudslang.content.database.services.SQLQueryLobService;
import io.cloudslang.content.database.services.SQLScriptService;
import io.cloudslang.content.database.utils.SQLInputs;
import io.cloudslang.content.database.utils.SQLInputsUtils;
import io.cloudslang.content.database.utils.SQLSessionResource;
import io.cloudslang.content.database.utils.SQLUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.*;

import static io.cloudslang.content.constants.BooleanValues.FALSE;
import static io.cloudslang.content.constants.OutputNames.RETURN_CODE;
import static io.cloudslang.content.constants.OutputNames.RETURN_RESULT;
import static io.cloudslang.content.constants.ReturnCodes.FAILURE;
import static io.cloudslang.content.constants.ReturnCodes.SUCCESS;
import static io.cloudslang.content.database.constants.DBDefaultValues.AUTH_SQL;
import static io.cloudslang.content.database.constants.DBOtherValues.CONCUR_READ_ONLY;
import static io.cloudslang.content.database.constants.DBOtherValues.MSSQL_DB_TYPE;
import static io.cloudslang.content.database.constants.DBOtherValues.TYPE_FORWARD_ONLY;
import static io.cloudslang.content.database.constants.DBReturnCodes.NO_MORE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by victor on 13.02.2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SQLQueryLOB.class, SQLInputsUtils.class, SQLQueryLobService.class})
public class SQLQueryLOBTest {

    @Spy
    private final SQLQueryLOB sqlQueryLOB = new SQLQueryLOB();

    @Test
    public void execute() throws Exception {
        final Map<String, String> resultMap = new SQLQueryLOB().execute(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, null);
        assertThat(resultMap.get(RETURN_CODE), is(FAILURE));
        assertThat(resultMap.get(RETURN_RESULT), is("dbServerName can't be empty\nusername input is empty.\npassword input is empty.\ndatabase input is empty.\ntrustStore or trustStorePassword is mandatory if trustAllRoots is false\ncommand input is empty."));
    }

    @Test
    public void executeSuccessNoMore() throws Exception {
        final String aKey = "akey";
        final GlobalSessionObject<Map<String, Object>> globalSessionObject = new GlobalSessionObject<>();
        final Map<String, Object> stringMap = new HashMap<>();
        stringMap.put(aKey, aKey);
        globalSessionObject.setResource(new SQLSessionResource(stringMap));

        mockStatic(SQLInputsUtils.class);
        when(SQLInputsUtils.getSqlKey(any(SQLInputs.class))).thenReturn(aKey);


        when(SQLInputsUtils.getOrDefaultGlobalSessionObj(any(GlobalSessionObject.class))).thenReturn(globalSessionObject);
        final Map<String, String> resultMap = sqlQueryLOB.execute("1", MSSQL_DB_TYPE, "username", "Password", "someInstance", "123", "db",
                AUTH_SQL, EMPTY, EMPTY, "something", "true", EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, TYPE_FORWARD_ONLY, CONCUR_READ_ONLY, globalSessionObject);

        verifyStatic();
        assertThat(resultMap.get(RETURN_CODE), is(NO_MORE));
        assertThat(resultMap.get(RETURN_RESULT), is(DBResponseNames.NO_MORE));
    }

    @Test
    public void executeSuccessHasMore() throws Exception {
        final String aKey = "akey";
        final GlobalSessionObject<Map<String, Object>> globalSessionObject = new GlobalSessionObject<>();
        final Map<String, Object> stringMap = new HashMap<>();
        final List<String> aList = new ArrayList<>();
        aList.add("a");
        stringMap.put(aKey, aList);
        globalSessionObject.setResource(new SQLSessionResource(stringMap));

        mockStatic(SQLInputsUtils.class);
        when(SQLInputsUtils.getSqlKey(any(SQLInputs.class))).thenReturn(aKey);


        when(SQLInputsUtils.getOrDefaultGlobalSessionObj(any(GlobalSessionObject.class))).thenReturn(globalSessionObject);
        final Map<String, String> resultMap = sqlQueryLOB.execute("1", MSSQL_DB_TYPE, "username", "Password", "someInstance", "123", "db",
                AUTH_SQL, EMPTY, EMPTY, "something", "true", EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, TYPE_FORWARD_ONLY, CONCUR_READ_ONLY, globalSessionObject);

        verifyStatic();
        assertThat(resultMap.get(RETURN_CODE), is(SUCCESS));
        assertThat(resultMap.get(RETURN_RESULT), is("a"));
    }

    @Test
    public void executeSuccessNoGlobalSessionFailure() throws Exception {
        mockStatic(SQLQueryLobService.class);

        when(SQLQueryLobService.executeSqlQueryLob(any(SQLInputs.class))).thenReturn(true);

        final Map<String, String> resultMap = sqlQueryLOB.execute("1", MSSQL_DB_TYPE, "username", "Password", "someInstance", "123", "db",
                AUTH_SQL, EMPTY, EMPTY, "something", "true", EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, TYPE_FORWARD_ONLY, CONCUR_READ_ONLY, null);

        verifyStatic();
        assertThat(resultMap.get(RETURN_CODE), is(DBReturnCodes.NO_MORE));
    }
}