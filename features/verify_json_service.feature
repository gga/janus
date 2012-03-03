Feature: Verify the contract for a JSON service
  In order to drive a versioned, evolable API for a JSON service
  As a consuming developer
  I want to verify that the service meets a contract

  Background:
    Given I have a mock HTTP service

  Scenario: Verify a single contract for a service
    Given a contract "simple JSON resource":
    """
    (service "simple JSON service"
      (contract "GET document"
        (method :get)
        (url "http://localhost:4568/service")
        (header "Content-Type" "application/json")

        (should-have :path "$.id" :of-type :number)
        (should-have :path "$.features[*]" :matching #"[a-z]")))
    """
    When   a resource at "/service" is represented with the JSON:
    """
    {
      "id": 10,
      "features": ["a", "b"]
    }
    """
    And  I run janus with the contract "simple JSON resource"
    Then the output from janus should contain:
    """
    1 service (0 failed)
    """
    When   a resource at "/service" is represented with the JSON:
    """
    {
      "id": 10,
      "features": [10, "b"]
    }
    """
    And  I run janus with the contract "simple JSON resource"
    Then the output from janus should contain:
    """
    1 service (1 failed)
    simple JSON service:
     GET document:
         Expected "10" to match regex [a-z], at path $.features[*]
    """

  @wip
  Scenario: Verify a POST contract for my correct service
    Given a contract "POST JSON service":
    """
    (service
      "Shopping: One-way"

      (contract "POST valid search"
           (method :post)
           (url "http://localhost:4568/search")
           (header "Content-Type" "application/json")
           (body :json
                 {:origin "ORD"
                  :destination "ATL"
                  :paxCount 1
                  :date "2012-03-21"})

           (should-have :path "$.origin" :matching #"^[A-Z]{3,3}$")
           (should-have :path "$.destination" :matching #"^[A-Z]{3,3}$")
           (should-have :path "$.departDate" :matching #"^[0-9]{4,4}-[0-9]{2,2}-[0-9]{2,2}$")
           (should-have :path "$.itineraries" :of-type :number)))
    """
    When   a resource is created at "/search" in JSON:
    """
    {
      "origin": "ATL",
      "destination": "ORD",
      "departDate": "2012-03-21",
      "itineraries": []
    }
    """
    And  I run janus with the contract "POST JSON service"
    Then the output from janus should contain:
    """
    1 service (1 failed)
    """
