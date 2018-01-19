/*
 * Copyright 2006-2017 the original author or authors.
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

import com.consol.citrus.demo.voting.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
@Service
@ConditionalOnProperty(prefix = "voting.authentication", value = "type", havingValue = "remote")
public class UserServiceRemote extends UserServiceLocal {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(UserServiceRemote.class);

    @Value("${user.service.base.url:http://localhost:8088/services/rest/v1}")
    private String baseUrl;

    /** User service client */
    private RestTemplate userServiceClient = new RestTemplate();

    @Override
    public String login(User user) {
        log.info("User login: " + user.getName());

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("name", user.getName());
        uriVariables.put("password", user.getPassword());

        String token = userServiceClient.getForEntity(baseUrl + "/user/login?username={name}&password={password}", String.class, uriVariables).getBody();

        userTokens.put(System.currentTimeMillis(), token);

        return token;
    }

    /**
     * Gets the baseUrl.
     *
     * @return
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the baseUrl.
     *
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
