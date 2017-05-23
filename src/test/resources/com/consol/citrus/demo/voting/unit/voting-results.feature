Feature: Show voting results

  User should be able to vote for an option.
  Voting results are displayed to the user

  Background:
    Given I create new voting "Do you like cucumbers?"
    And voting options are "yes:no"

  Scenario: Initial vote results
    Then votes of option "yes" should be 0
    And votes of option "no" should be 0

  Scenario: Get vote results
    When I vote for "yes"
    Then votes of option "yes" should be 1
    And votes of option "no" should be 0

  Scenario: Get top vote result
    When I vote for "yes" 3 times
    And I vote for "no" 5 times
    Then top vote should be "no"
