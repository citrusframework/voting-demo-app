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

package com.consol.citrus.demo.voting.unit;

import com.consol.citrus.demo.voting.dao.InMemoryVotingListDao;
import com.consol.citrus.demo.voting.model.VoteOption;
import com.consol.citrus.demo.voting.model.Voting;
import com.consol.citrus.demo.voting.service.VotingService;
import com.consol.citrus.demo.voting.service.VotingServiceImpl;
import cucumber.api.java.After;
import cucumber.api.java.en.*;
import org.junit.Assert;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
public class VotingSteps {

    private VotingService votingService = new VotingServiceImpl(new InMemoryVotingListDao(), Collections.emptyList());
    private UUID votingId;

    private Stack<Exception> exceptions = new Stack<>();

    @After
    public void checkErrors() {
        Assert.assertEquals("Found unhandled errors", 0, exceptions.size());
    }

    @Given("^New default voting$")
    public void votingDefault() {
        createVotingWithTitle("Do you like testing?");
        votingOptions("yes:no");
    }

    @Given("^voting options are \"([^\"]*)\"$")
    public void votingOptions(String optionList) {
        String[] options = optionList.split(":");
        List<VoteOption> voteOptions = new ArrayList<>();
        for (String option : options) {
            voteOptions.add(new VoteOption(option));
        }

        votingService.get(votingId.toString()).setOptions(voteOptions);
    }

    @When("^(?:I|user) creates? new voting$")
    public void createVotingDefault() {
        votingDefault();
    }

    @When("^(?:I|user) creates? new voting \"([^\"]*)\"$")
    public void createVotingWithTitle(String title) {
        votingId = UUID.randomUUID();
        Voting voting = new Voting(votingId, title);
        votingService.add(voting);
    }

    @When("^(?:I|user) votes? for \"([^\"]*)\"$")
    public void voteFor(String option) {
        try {
            votingService.vote(votingId.toString(), option);
        } catch (Exception e) {
            exceptions.push(e);
        }
    }

    @When("^(?:I|user) votes? for \"([^\"]*)\" (\\d+) times$")
    public void voteForTimes(String option, int times) {
        for (int i = 1; i <= times; i++) {
            voteFor(option);
        }
    }

    @When("^voting is closed$")
    public void votingIsClosed() {
        votingService.get(votingId.toString()).setClosed(true);
    }

    @Then("^votes of option \"([^\"]*)\" should be (\\d+)$")
    public void votesOfOptionShouldBe(String option, int count) {
        Assert.assertEquals(count, votingService.get(votingId.toString()).getOption(option).getVotes());
    }

    @Then("^top vote should be \"([^\"]*)\"$")
    public void topVoteShouldBe(String option) {
        Assert.assertEquals(option, votingService.getTopVote(votingService.get(votingId.toString())).getName());
    }

    @Then("^voting should have (\\d+) options$")
    public void votingShouldHaveOptions(int optionCount) {
        Assert.assertEquals(optionCount, votingService.get(votingId.toString()).getOptions().size());
    }

    @Then("^voting should have option \"([^\"]*)\"$")
    public void votingShouldHaveOption(String option) {
        Assert.assertNotNull(votingService.get(votingId.toString()).getOption(option));
    }

    @Then("^voting title should be \"([^\"]*)\"$")
    public void votingTitleShouldBe(String title) {
        Assert.assertEquals(title, votingService.get(votingId.toString()).getTitle());
    }

    @Then("^(?:I|user) should get the error \"([^\"]*)\"$")
    public void shouldGetError(String errorMessage) {
        Assert.assertEquals(errorMessage, this.exceptions.pop().getMessage());
    }
}
