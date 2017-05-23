Feature: Voting Http REST API

  Background:
    Given message creator com.consol.citrus.demo.voting.predefined.VotingMessageCreator
    Given variables
      | id     | citrus:randomUUID()  |
      | title  | Do you like Mondays? |
      | report | true                 |

  Scenario: Close voting with report
    When <votingClient> sends message <createVoting>
    And  <votingClient> sends message <closeVoting>
    Then <reportingEndpoint> should receive JSON message <reportMessage>
    And  <mailServer> should receive message <mailReport>