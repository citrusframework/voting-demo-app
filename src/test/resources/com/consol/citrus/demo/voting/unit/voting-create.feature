Feature: Create voting

  User should be able to create new votings. The user should be able to
  add custom vote options. The default vote options are “yes” and “no”.

  Scenario: Default voting options
    When I create new voting
    Then voting title should be "Do you like testing?"
    Then voting should have 2 options
    And voting should have option "yes"
    And voting should have option "no"

  Scenario: Custom voting options
    When I create new voting "What is your favorite color?"
    And voting options are "green:red:blue"
    Then voting title should be "What is your favorite color?"
    And voting should have 3 options
    And voting should have option "green"
    And voting should have option "red"
    And voting should have option "blue"