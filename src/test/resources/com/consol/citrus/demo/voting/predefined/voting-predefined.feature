Feature: Voting Http REST API

  Background:
    Given message creator com.consol.citrus.demo.voting.predefined.VotingMessageCreator
    Given variables
      | id     | citrus:randomUUID()  |
      | title  | Do you like Mondays? |
      | report | true                 |
    When <votingClient> sends message <userLogin>
    Then <votingClient> should receive PLAINTEXT message <userToken>

  Scenario: Close voting with report
    When <votingClient> sends message <createVoting>
    And  <votingClient> sends message <closeVoting>
    Then <reportingEndpoint> should receive JSON message <reportMessage>
    And  <mailServer> should receive message <mailReport>