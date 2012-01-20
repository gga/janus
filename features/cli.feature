Feature: Command line interface
  In order to discover usage
  As a developer
  I want to be able to run janus from the command line

  Scenario: janus should exit when started without args
    When I run janus
    Then the exit status should be 0

  @announce
  Scenario: Running janus to verify a service
    When I run janus with args "--verify contract.jns"
    Then the exit status should be 0
    And the output from janus should contain "Could not find 'contract.jns'"
    
    Given a file named "bad-contract.jns" with:
    """
    (Invalid contract text
    """
    When I run janus with args "--verify bad-contract.jns"
    Then the exit status should be 0
    And the output from janus should contain "Invalid contract in 'bad-contract.jns'"

    Given a file named "good-contract.jns" with:
    """
    (service
      (contract "good contract syntax"
        (definition)))
    """
    When I run janus with args "--verify good-contract.jns"
    Then the exit status should be 0
