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

package com.consol.citrus.demo.voting.service;

import com.consol.citrus.demo.voting.dao.VotingListDao;
import com.consol.citrus.demo.voting.model.VoteOption;
import com.consol.citrus.demo.voting.model.Voting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Service
public class VotingServiceImpl implements VotingService {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(VotingServiceImpl.class);

    private final VotingListDao votingListDao;

    private final List<ReportingService> reportingServices;

    @Autowired
    public VotingServiceImpl(VotingListDao votingListDao, List<ReportingService> reportingServices) {
        this.votingListDao = votingListDao;
        this.reportingServices = reportingServices;
    }

    @Override
    public List<Voting> getVotings() {
        return votingListDao.list();
    }

    @Override
    public void add(Voting voting) {
        votingListDao.save(voting);
    }

    @Override
    public void vote(String votingId, String option) {
        checkVoting(votingId);

        Voting voting = votingListDao.findById(votingId);
        if (voting.isClosed()) {
            throw new RuntimeException("Failed to add vote - voting is closed!");
        }

        for (VoteOption voteOption : voting.getOptions()) {
            if (voteOption.getName().equals(option)) {
                voteOption.increment();
            }
        }
    }

    @Override
    public Voting get(String votingId) {
        checkVoting(votingId);
        return votingListDao.findById(votingId);
    }

    @Override
    public void remove(String votingId) {
        checkVoting(votingId);
        votingListDao.delete(votingId);
    }

    @Override
    public VoteOption getTopVote(Voting voting) {
        VoteOption topVote = null;

        for (VoteOption voteOption : voting.getOptions()) {
            if (topVote == null ||
                    voteOption.getVotes() > topVote.getVotes()) {
                topVote = voteOption;
            }
        }

        return topVote;
    }

    @Override
    public void close(Voting voting) {
        voting.setClosed(true);

        log.info("Close voting: " + voting.getId());

        if (voting.isReport()) {
            reportingServices.forEach(service -> {
                try {
                    service.report(voting, getTopVote(voting));
                } catch (Exception e) {
                    log.error("Failed to send report", e);
                }
            });
        }
    }

    /**
     * Checks that voting id is known to the system.
     * @param votingId
     * @throws RuntimeException
     */
    private void checkVoting(String votingId) throws RuntimeException {
        if (!votingListDao.findId(votingId)) {
            throw new RuntimeException("No such voting for id: " + votingId);
        }
    }
}
