/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.access.provider.file.shiro;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import junit.framework.Assert;

import org.apache.access.core.Database;
import org.apache.access.core.Privilege;
import org.apache.access.core.Server;
import org.apache.access.core.Subject;
import org.apache.access.core.Table;
import org.apache.access.provider.file.ResourceAuthorizationProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


import com.google.common.base.Objects;

@RunWith(Parameterized.class)
public class TestResourceAuthorizationProviderDatabaseTables {

  private static final Subject SUB_ADMIN = new Subject("admin");
  private static final Subject SUB_MANAGER = new Subject("manager");
  private static final Subject SUB_ANALYST = new Subject("analyst");
  private static final Subject SUB_JUNIOR_ANALYST = new Subject("junior_analyst");

  private static final Server SVR_SERVER1 = new Server("server1");
  private static final Server SVR_ALL = new Server("*");

  private static final Database DB_CUSTOMERS = new Database("customers");
  private static final Database DB_ANALYST = new Database("analyst");
  private static final Database DB_JR_ANALYST = new Database("junior_analyst");

  private static final Table TBL_PURCHASES = new Table("purchases");


  private static final EnumSet<Privilege> ALL = EnumSet.of(Privilege.ALL);
  private static final EnumSet<Privilege> SELECT = EnumSet.of(Privilege.SELECT);
  private static final EnumSet<Privilege> INSERT = EnumSet.of(Privilege.INSERT);


  private final ResourceAuthorizationProvider authzProvider;
  private final Subject subject;
  private final Server server;
  private final Database database;
  private final Table table;
  private final EnumSet<Privilege> privileges;
  private final boolean expected;

  public TestResourceAuthorizationProviderDatabaseTables(
      ResourceAuthorizationProvider authzProvider, Subject subject,
      Server server, Database database, Table table,
      EnumSet<Privilege> privileges, boolean expected) {
    this.authzProvider = authzProvider;
    this.subject = subject;
    this.server = server;
    this.database = database;
    this.table = table;
    this.privileges = privileges;
    this.expected = expected;
  }

  @Test
  public void testResourceAuthorizationProvider() throws Exception {
    Objects.ToStringHelper helper = Objects.toStringHelper("TestParameters");
    helper.add("Subject", subject).add("Server", server).add("DB", database)
    .add("Table", table).add("Privileges", privileges);
    Assert.assertEquals(helper.toString(), expected,
        authzProvider.hasAccess(subject, server, database, table, privileges));
  }

  @Parameters
  public static Collection<Object[]> run() {
    ResourceAuthorizationProvider authzProvider =
        new ResourceAuthorizationProvider("classpath:test-authz-provider.ini");
    return Arrays.asList(new Object[][] {
        /*
         * Admin
         */
        {authzProvider, SUB_ADMIN, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, ALL, true},
        {authzProvider, SUB_ADMIN, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, SELECT, true},
        {authzProvider, SUB_ADMIN, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, INSERT, true},
        {authzProvider, SUB_ADMIN, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, ALL, true},
        {authzProvider, SUB_ADMIN, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, SELECT, true},
        {authzProvider, SUB_ADMIN, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, INSERT, true},
        /*
         * Manager
         */
        {authzProvider, SUB_MANAGER, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, ALL, false},
        {authzProvider, SUB_MANAGER, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, SELECT, true},
        {authzProvider, SUB_MANAGER, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, INSERT, false},
        {authzProvider, SUB_MANAGER, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, ALL, false},
        {authzProvider, SUB_MANAGER, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, SELECT, false},
        {authzProvider, SUB_MANAGER, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, INSERT, false},
        /*
         * Analyst
         */
        {authzProvider, SUB_ANALYST, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, ALL, false},
        {authzProvider, SUB_ANALYST, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, SELECT, true},
        {authzProvider, SUB_ANALYST, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, INSERT, false},
        {authzProvider, SUB_ANALYST, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, ALL, false},
        {authzProvider, SUB_ANALYST, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, SELECT, false},
        {authzProvider, SUB_ANALYST, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, INSERT, false},
        // analyst sandbox
        {authzProvider, SUB_ANALYST, SVR_SERVER1, DB_ANALYST, TBL_PURCHASES, ALL, true},
        {authzProvider, SUB_ANALYST, SVR_SERVER1, DB_ANALYST, TBL_PURCHASES, SELECT, true},
        {authzProvider, SUB_ANALYST, SVR_SERVER1, DB_ANALYST, TBL_PURCHASES, INSERT, true},
        {authzProvider, SUB_ANALYST, SVR_ALL, DB_ANALYST, TBL_PURCHASES, ALL, true},
        {authzProvider, SUB_ANALYST, SVR_ALL, DB_ANALYST, TBL_PURCHASES, SELECT, true},
        {authzProvider, SUB_ANALYST, SVR_ALL, DB_ANALYST, TBL_PURCHASES, INSERT, true},
        // jr analyst sandbox
        {authzProvider, SUB_ANALYST, SVR_SERVER1, DB_JR_ANALYST, TBL_PURCHASES, ALL, false},
        {authzProvider, SUB_ANALYST, SVR_SERVER1, DB_JR_ANALYST, TBL_PURCHASES, SELECT, true},
        {authzProvider, SUB_ANALYST, SVR_SERVER1, DB_JR_ANALYST, TBL_PURCHASES, INSERT, false},
        {authzProvider, SUB_ANALYST, SVR_ALL, DB_JR_ANALYST, TBL_PURCHASES, ALL, false},
        {authzProvider, SUB_ANALYST, SVR_ALL, DB_JR_ANALYST, TBL_PURCHASES, SELECT, false},
        {authzProvider, SUB_ANALYST, SVR_ALL, DB_JR_ANALYST, TBL_PURCHASES, INSERT, false},
        /*
         * Junior Analyst
         */
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, ALL, false},
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, SELECT, false},
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_SERVER1, DB_CUSTOMERS, TBL_PURCHASES, INSERT, false},
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, ALL, false},
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, SELECT, false},
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_ALL, DB_CUSTOMERS, TBL_PURCHASES, INSERT, false},
        // jr analyst sandbox
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_SERVER1, DB_JR_ANALYST, TBL_PURCHASES, ALL, true},
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_SERVER1, DB_JR_ANALYST, TBL_PURCHASES, SELECT, true},
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_SERVER1, DB_JR_ANALYST, TBL_PURCHASES, INSERT, true},
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_ALL, DB_JR_ANALYST, TBL_PURCHASES, ALL, true},
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_ALL, DB_JR_ANALYST, TBL_PURCHASES, SELECT, true},
        {authzProvider, SUB_JUNIOR_ANALYST, SVR_ALL, DB_JR_ANALYST, TBL_PURCHASES, INSERT, true},
    });
  }
}