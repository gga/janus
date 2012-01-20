Feature: Verify the contract for a JSON service
  In order to drive a versioned, evolable API for a JSON service
  As a consuming developer
  I want to verify that the service meets a contract

  @wip
  Scenario: Verify a single contract for my correct service
    Given a web service at "/service" that returns JSON:
    """
    {
      "id": 10,
      "features": ["a", "b"]
    }
    """
    And a contract "simple JSON service":
    """
    (service
      (contract "simple JSON service"
        (definition
          (method :get)
          (header "Content-Type" "application/json")

          (clauses
            (should-have :path "$.id" :of-type :number)
            (should-have :path "$..features[*]" :matching #"[a-z]")))))
    """
    When I run janus with the contract "simple JSON service"
    Then the output should contain:
    """
    1 service passed
    0 services failed
    2 clauses passed
    0 clauses failed
    """
