Feature: Command line interface
  In order to discover usage
  As a developer
  I want to be able to run janus from the command line

  Scenario: janus should exit when started without args
    When I run janus
    Then the exit status should be 0
