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

package org.apache.james.mailrepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.james.filesystem.api.mock.MockFileSystem;
import org.apache.james.mailrepository.api.MailRepository;
import org.apache.james.mailrepository.file.FileMailRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class FileMailRepositoryTest {

    public abstract class GenericFileMailRepositoryTest implements MailRepositoryContract {
        private FileMailRepository mailRepository;
        private MockFileSystem filesystem;

        @BeforeEach
        void init() throws Exception {
            filesystem = new MockFileSystem();
            mailRepository = new FileMailRepository();
            mailRepository.setFileSystem(filesystem);
            mailRepository.configure(getConfiguration());
            mailRepository.init();
        }

        protected BaseHierarchicalConfiguration getConfiguration() {
            BaseHierarchicalConfiguration configuration = new BaseHierarchicalConfiguration();
            configuration.addProperty("[@destinationURL]", "file://target/var/mailRepository");
            return withConfigurationOptions(configuration);
        }

        protected abstract BaseHierarchicalConfiguration withConfigurationOptions(BaseHierarchicalConfiguration configuration);

        @AfterEach
        void tearDown() {
            filesystem.clear();
        }

        @Override
        public MailRepository retrieveRepository() {
            return mailRepository;
        }
    }

    @Nested
    @DisplayName("Default configuration")
    public class DefaultFileMailRepositoryTest extends GenericFileMailRepositoryTest {

        @Override
        protected BaseHierarchicalConfiguration withConfigurationOptions(BaseHierarchicalConfiguration configuration) {
            configuration.addProperty("[@FIFO]", "false");
            configuration.addProperty("[@CACHEKEYS]", "true");
            return configuration;
        }
    }

    @Nested
    @DisplayName("Default configuration")
    public class AccessTest {
        private FileMailRepository mailRepository;
        private MockFileSystem filesystem;

        @BeforeEach
        void init() throws Exception {
            filesystem = new MockFileSystem();
            mailRepository = new FileMailRepository();
            mailRepository.setFileSystem(filesystem);
        }

        protected BaseHierarchicalConfiguration getConfiguration() {
            BaseHierarchicalConfiguration configuration = new BaseHierarchicalConfiguration();
            configuration.addProperty("[@destinationURL]", "file://../../trying/a/location/of/james/root");
            return withConfigurationOptions(configuration);
        }

        protected BaseHierarchicalConfiguration withConfigurationOptions(BaseHierarchicalConfiguration configuration) {
            configuration.addProperty("[@FIFO]", "false");
            configuration.addProperty("[@CACHEKEYS]", "true");
            return configuration;
        }

        @AfterEach
        void tearDown() {
            filesystem.clear();
        }

        @Test
        void repositoriesOutsideOfJamesRootShouldBeRejected() {
            assertThatThrownBy(() -> {
                mailRepository.configure(getConfiguration());
                mailRepository.init();
            }).isInstanceOf(org.apache.commons.configuration2.ex.ConfigurationException.class)
                .hasCauseInstanceOf(IOException.class)
                .<Throwable>extracting(Throwable::getCause)
                .satisfies(e -> e.getMessage().startsWith("file://../../trying/a/location/of/james/root jail break outside of "));
        }
    }

    @Nested
    @DisplayName("No cache configuration")
    public class NoCacheFileMailRepositoryTest extends GenericFileMailRepositoryTest {

        @Override
        protected BaseHierarchicalConfiguration withConfigurationOptions(BaseHierarchicalConfiguration configuration) {
            configuration.addProperty("[@FIFO]", "false");
            configuration.addProperty("[@CACHEKEYS]", "false");
            return configuration;
        }
    }

    @Nested
    @DisplayName("Fifo configuration")
    public class FifoFileMailRepositoryTest extends GenericFileMailRepositoryTest {

        @Override
        protected BaseHierarchicalConfiguration withConfigurationOptions(BaseHierarchicalConfiguration configuration) {
            configuration.addProperty("[@FIFO]", "true");
            configuration.addProperty("[@CACHEKEYS]", "true");
            return configuration;
        }
    }

    @Nested
    @DisplayName("Fifo no cache configuration")
    public class FifoNoCacheFileMailRepositoryTest extends GenericFileMailRepositoryTest {

        @Override
        protected BaseHierarchicalConfiguration withConfigurationOptions(BaseHierarchicalConfiguration configuration) {
            configuration.addProperty("[@FIFO]", "true");
            configuration.addProperty("[@CACHEKEYS]", "false");
            return configuration;
        }
    }

}
