Feature: Close voting

  User should be able to close voting. No further votes are accepted then.

  Scenario: Closed voting should not accept votes
    Given New default voting
    When voting is closed
    And I vote for "yes"
    Then I should get the error "Failed to add vote - voting is closed!"
    And votes of option "yes" should be 0
    And votes of option "no" should be 0