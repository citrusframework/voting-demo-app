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

package com.consol.citrus.demo.voting.predefined;

import com.consol.citrus.cucumber.message.MessageCreator;
import com.consol.citrus.http.message.HttpMessage;
import com.consol.citrus.jms.message.JmsMessage;
import com.consol.citrus.mail.model.*;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import org.springframework.http.HttpMethod;
import org.springframework.xml.transform.StringResult;

/**
 * @author Christoph Deppisch
 */
public class VotingMessageCreator {

    @MessageCreator("createVoting")
    public Message createVoting() {
        return new HttpMessage("{" +
                "\"id\": \"${id}\"," +
                "\"title\": \"${title}\"," +
                "\"report\": ${report}," +
                "\"options\": [" +
                    "{ \"name\": \"yes\" }," +
                    "{ \"name\": \"no\" }" +
                "]}")
                .path("/voting")
                .method(HttpMethod.POST)
                .contentType("application/json");
    }

    @MessageCreator("closeVoting")
    public Message closeVoting() {
        return new HttpMessage()
                .path("/voting/${id}/close")
                .method(HttpMethod.PUT)
                .contentType("application/json");
    }

    @MessageCreator("reportMessage")
    public Message reportJmsMessage() {
        return new JmsMessage("{ " +
                "\"id\": \"${id}\", " +
                "\"title\": \"${title}\", " +
                "\"options\": [ " +
                    "{ \"name\": \"yes\", \"votes\": 0 }," +
                    "{ \"name\": \"no\", \"votes\": 0 }" +
                "], " +
                "\"closed\": true, " +
                "\"report\": true }");
    }

    @MessageCreator("mailReport")
    public Message mailReport() {
        MailRequest mailRequest = new MailRequest();
        mailRequest.setFrom("voting@example.org");
        mailRequest.setCc("");
        mailRequest.setBcc("");
        mailRequest.setTo("participants@example.org");
        mailRequest.setSubject("Voting results");

        mailRequest.setBody(new BodyPart("Dear participants,\n" +
            "\n" +
            "the voting '${title}' came to an end.\n" +
            "\n" +
            "The top answer is 'yes'!\n" +
            "\n" +
            "Have a nice day!\n" +
            "Your Voting-App Team", "text/plain; charset=us-ascii"));

        StringResult marshalled = new StringResult();
        new MailMarshaller().marshal(mailRequest, marshalled);
        return new DefaultMessage(marshalled.toString());
    }
}
