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

import com.consol.citrus.demo.voting.model.Voting;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @author Christoph Deppisch
 */
public class JdbcVotingListDao implements VotingListDao, InitializingBean {

    @Autowired
    private DataSource dataSource;

    @Override
    public void save(Voting entry) {
        try {
            try (Connection connection = getConnection()) {
                connection.setAutoCommit(true);
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO votings (id, title, closed, report) VALUES (?, ?, ?, ?)")) {
                    statement.setString(1, getNextId());
                    statement.setString(2, entry.getTitle());
                    statement.setBoolean(3, entry.isClosed());
                    statement.setBoolean(4, entry.isReport());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not save entry " + entry, e);
        }
    }

    private String getNextId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public List<Voting> list() {
        try {
            try (Connection connection = getConnection()) {
                try (Statement statement = connection.createStatement()) {
                    try (ResultSet resultSet = statement.executeQuery("SELECT id, title, closed, report FROM votings")) {
                        List<Voting> list = new ArrayList<>();
                        while (resultSet.next()) {
                            String id = resultSet.getString(1);
                            String title = resultSet.getString(2);
                            boolean closed = resultSet.getBoolean(3);
                            boolean report = resultSet.getBoolean(4);

                            Voting voting = new Voting(UUID.fromString(id), title);
                            voting.setClosed(closed);
                            voting.setReport(report);
                            list.add(voting);
                        }
                        return list;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not list entries", e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            try (Connection connection = getConnection()) {
                connection.setAutoCommit(true);
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM votings WHERE id = ?")) {
                    statement.setString(1, id);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete entries for id " + id, e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            try (Connection connection = getConnection()) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeQuery("DELETE FROM votings");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete entries", e);
        }
    }

    @Override
    public void update(Voting entry) {
        try {
            try (Connection connection = getConnection()) {
                connection.setAutoCommit(true);
                try (PreparedStatement statement = connection.prepareStatement("UPDATE votings SET title=?, closed=?, report=? WHERE id = ?")) {
                    statement.setString(1, entry.getTitle());
                    statement.setBoolean(2, entry.isClosed());
                    statement.setBoolean(3, entry.isReport());
                    statement.setString(4, entry.getId());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not update entry " + entry, e);
        }
    }

    @Override
    public Voting findById(String votingId) {
        try {
            try (Connection connection = getConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, title, closed, report FROM votings WHERE id = ?")) {
                    preparedStatement.setString(1, votingId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            String id = resultSet.getString(1);
                            String title = resultSet.getString(2);
                            boolean closed = resultSet.getBoolean(3);
                            boolean report = resultSet.getBoolean(4);

                            Voting voting = new Voting(UUID.fromString(id), title);
                            voting.setClosed(closed);
                            voting.setReport(report);
                            return voting;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find entry", e);
        }

        throw new DataAccessException("Unable to find entry for id: " + votingId);
    }

    @Override
    public boolean findId(String votingId) {
        try {
            try (Connection connection = getConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM votings WHERE id = ?")) {
                    preparedStatement.setString(1, votingId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find entry", e);
        }

        return false;
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    private DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            try (Connection connection = getConnection()) {
                connection.setAutoCommit(true);
                try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS votings (id VARCHAR(50), title VARCHAR(255), closed BOOLEAN, report BOOLEAN)")) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not create db tables", e);
        }

    }
}
