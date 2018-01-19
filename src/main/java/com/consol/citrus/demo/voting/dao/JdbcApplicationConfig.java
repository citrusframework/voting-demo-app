/*
 * Copyright 2006-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.demo.voting.dao;

import org.apache.commons.dbcp.BasicDataSource;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerAcl;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author Christoph Deppisch
 */
@Configuration
@EnableConfigurationProperties(JdbcConfigurationProperties.class)
public class JdbcApplicationConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(prefix = "voting.persistence", value = "server", havingValue = "enabled", matchIfMissing = true)
    public Server database() {
        Server database = new Server();
        try {
            database.setProperties(HsqlProperties.delimitedArgPairsToProps("server.database.0=file:target/testdb;" +
                    "server.dbname.0=testdb;" +
                    "server.remote_open=true;" +
                    "server.port=18080;" +
                    "hsqldb.reconfig_logging=false", "=", ";", null));
        } catch (IOException | ServerAcl.AclFormatException e) {
            throw new BeanCreationException("Failed to create embedded database storage", e);
        }
        return database;
    }

    @Bean
    @ConditionalOnProperty(prefix = "voting.persistence", value = "type", havingValue = "in_memory", matchIfMissing = true)
    public VotingListDao votingListInMemoryDao() {
        return new InMemoryVotingListDao();
    }

    @Bean
    @ConditionalOnProperty(prefix = "voting.persistence", value = "type", havingValue = "jdbc")
    public VotingListDao votingListJdbcDao() {
        return new JdbcVotingListDao();
    }

    @Bean
    @ConditionalOnProperty(prefix = "voting.persistence", value = "type", havingValue = "jdbc")
    @DependsOn("database")
    public DataSource dataSource(JdbcConfigurationProperties configurationProperties) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(configurationProperties.getDriverClassName());
        dataSource.setUrl(configurationProperties.getUrl());
        dataSource.setUsername(configurationProperties.getUsername());
        dataSource.setPassword(configurationProperties.getPassword());

        return dataSource;
    }
}
