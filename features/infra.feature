Feature: Infrastructure and support
  In order to effectively integrate Janus into my project
  As a developer
  I want to be able to execute custom code before starting my conformance run

  Scenario: janus should execute *.clj files in the contracts/support directory
    Given a directory named "contracts/support"
    And   a file named "contracts/support/env.clj" with:
    """
    (println "hello from env")
    """
    When  I run janus
    Then  the output from janus should contain "hello from env"

  Scenario: janus should allow code to be executed after all work has been done
    Given a directory named "contracts/support"
    And   a file named "contracts/support/env.clj" with:
    """
    (at-exit #(println "exiting"))
    """
    When  I run janus
    Then  the output from janus should contain "exiting"
