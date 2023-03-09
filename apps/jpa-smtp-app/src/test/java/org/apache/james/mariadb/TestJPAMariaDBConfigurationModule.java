/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mariadb;

import javax.inject.Singleton;

import org.apache.james.modules.data.JPAConfiguration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TestJPAMariaDBConfigurationModule extends AbstractModule {

    private static final String MARIA_USERNAME = "test";
    private static final String MARIA_PASSWORD = "test";
    private static final String JDBC_EMBEDDED_DRIVER = org.mariadb.jdbc.Driver.class.getName();
    private final String mariaDBUrl;

    public TestJPAMariaDBConfigurationModule(String mariaDBUrl) {
        this.mariaDBUrl = mariaDBUrl;
    }

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    JPAConfiguration provideConfiguration() {
        return JPAConfiguration.builder()
                .driverName(JDBC_EMBEDDED_DRIVER)
                .driverURL(mariaDBUrl)
                .username(MARIA_USERNAME)
                .password(MARIA_PASSWORD)
                .build();
    }
}
