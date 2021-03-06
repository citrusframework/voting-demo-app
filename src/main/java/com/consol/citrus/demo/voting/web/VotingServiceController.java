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

package com.consol.citrus.demo.voting.web;

import com.consol.citrus.demo.voting.model.*;
import com.consol.citrus.demo.voting.service.UserService;
import com.consol.citrus.demo.voting.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/rest/services")
public class VotingServiceController {

    private final UserService userService;
    private final VotingService votingService;

    @Autowired
    public VotingServiceController(UserService userService, VotingService votingService) {
        this.userService = userService;
        this.votingService = votingService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestBody User user) {
        return userService.login(user);
    }

    @RequestMapping(value = "/logout/{token}", method = RequestMethod.GET)
    public ResponseEntity logout(@PathVariable("token") String token) {
        userService.logout(token);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/voting", method = RequestMethod.GET)
    @ResponseBody
    public List<Voting> list() {
        return votingService.getVotings();
    }

    @RequestMapping(value = "/voting", method = RequestMethod.POST)
    @ResponseBody
    public Voting add(@RequestBody Voting voting) {
        votingService.add(voting);
        return voting;
    }

    @RequestMapping(value = "/voting/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Voting getVoting(@PathVariable("id") String votingId) {
        return votingService.get(votingId);
    }

    @RequestMapping(value = "/voting/{id}/{option}", method = RequestMethod.PUT)
    public ResponseEntity vote(@PathVariable("id") String votingId, @PathVariable("option") String option) {
        votingService.vote(votingId, option);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/voting/{id}/top", method = RequestMethod.GET)
    @ResponseBody
    public VoteOption vote(@PathVariable("id") String votingId) {
        return votingService.getTopVote(votingService.get(votingId));
    }

    @RequestMapping(value = "/voting/{id}/close", method = RequestMethod.PUT)
    public ResponseEntity close(@PathVariable("id") String votingId) {
        votingService.close(votingService.get(votingId));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/voting/{id}", method = RequestMethod.DELETE)
    public ResponseEntity remove(@PathVariable("id") String votingId) {
        votingService.remove(votingId);
        return ResponseEntity.ok().build();
    }
}
