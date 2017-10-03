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
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Christoph Deppisch
 */
@Service
@Profile("!remote-user-auth")
public class UserServiceLocal implements UserService {

    private static final long EVICT_EXPIRED_INTERVAL = 1000 * 60;
    private static final long TOKEN_EXPIRED_AFTER = 1000 * 60 * 10;

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(UserServiceLocal.class);

    /** Local storage of user tokens */
    protected Map<Long, String> userTokens = new ConcurrentHashMap<>();

    @Override
    public String login(User user) {
        log.info("User login: " + user.getName());
        String token = UUID.randomUUID().toString();

        userTokens.put(System.currentTimeMillis(), token);

        return token;
    }

    @Override
    public void logout(String token) {
        userTokens.remove(token);
    }

    @Override
    public boolean verify(String token) {
        if (userTokens.values().contains(token)) {
            log.info("User token verified OK!");
            return true;
        } else {
            log.info("User token verification failed! " + token);
            return false;
        }
    }

    @Scheduled(fixedRate=EVICT_EXPIRED_INTERVAL)
    public void evictExpiredTokens() {
        userTokens.entrySet()
                  .removeIf(entry -> System.currentTimeMillis() - entry.getKey() > TOKEN_EXPIRED_AFTER);
    }
}
