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

package com.consol.citrus.demo.voting.jms;

import com.consol.citrus.demo.voting.model.Voting;
import com.consol.citrus.demo.voting.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
@Profile("jms")
public class VotingJmsResource {

    @Autowired
    private VotingService votingService;

    @JmsListener(destination = "jms.voting.inbound", containerFactory = "jmsListenerContainerFactory")
    public void receiveTodo(Voting voting) {
        votingService.add(voting);
    }

    @JmsListener(destination = "jms.vote.inbound", containerFactory = "jmsListenerContainerFactory")
    public void receiveTodo(@Header("votingId") String votingId, String option) {
        votingService.vote(votingId, option);
    }
}
