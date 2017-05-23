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

import com.consol.citrus.demo.voting.model.VoteOption;
import com.consol.citrus.demo.voting.model.Voting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Christoph Deppisch
 */
@Service
public class ReportingServiceImpl implements ReportingService {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(ReportingServiceImpl.class);

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    private JmsTemplate jmsTemplate;

    private String reportingDestination = "jms.voting.report";

    @Override
    public void report(Voting voting, VoteOption topVote) {
        log.info("Create reporting for voting: " + voting.getId());

        try {
            jmsTemplate.convertAndSend(reportingDestination, voting);
        } finally {
            sendMailReport(voting, topVote);
        }
    }

    /**
     * Sends mail report to participant mailing list.
     * @param voting
     */
    private void sendMailReport(Voting voting, VoteOption topVote) {
        try {
            mailService.sendMail("participants@example.org", "Voting results",
                    String.format(FileCopyUtils.copyToString(new InputStreamReader(
                            new ClassPathResource("templates/reporting-mail.txt").getInputStream())), voting.getTitle(), topVote.getName()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read reporting mail text", e);
        }
    }
}
