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

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Christoph Deppisch
 */
public class InMemoryVotingListDao implements VotingListDao {

    /** In memory storage */
    private List<Voting> storage = new ArrayList<>();

    @Override
    public void save(Voting entry) {
        storage.add(entry);
    }

    @Override
    public List<Voting> list() {
        return storage.stream().collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        storage = storage.stream()
                         .filter(current -> !current.getId().equals(id))
                         .collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }

    @Override
    public void update(Voting entry) {
        Optional<Voting> found = storage.stream()
                .filter(current -> current.getId().equals(entry.getId()))
                .findFirst();

        if (!found.isPresent()) {
            throw new RuntimeException(String.format("Unable to find entry with uuid '%s'", entry.getId()));
        }

        found.get().setTitle(entry.getTitle());
        found.get().setOptions(entry.getOptions());
        found.get().setClosed(entry.isClosed());
        found.get().setReport(entry.isReport());
    }

    @Override
    public Voting findById(String votingId) {
        return storage.stream()
                .filter(voting -> voting.getId().equals(votingId))
                .findFirst()
                .orElseThrow(() -> new DataAccessException("Unable to find entry for id: " + votingId));
    }

    @Override
    public boolean findId(String id) {
        return storage.stream()
                .anyMatch(voting -> voting.getId().equals(id));
    }
}
