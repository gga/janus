Given /^I have a mock HTTP service$/ do
  harper.start
end

Given /^a web service at "([^"]*)" that returns JSON:$/ do |url, mock_content|
  harper.mock({
                "url" => url,
                "method" => "GET",
                "content-type" => "application/json",
                "body" => mock_content
              })
end
