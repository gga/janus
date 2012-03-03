Given /^I have a mock HTTP service$/ do
  harper.start
end

When /^a resource at "([^"]*)" is represented with the JSON:$/ do |url, mock_content|
  harper.mock({
                "url" => url,
                "method" => "GET",
                "content-type" => "application/json",
                "body" => mock_content
              })
end

When /^a resource is created at "([^"]*)" in JSON:$/ do |url, mock_content|
  harper.mock({
                "url" => url,
                "method" => "POST",
                "content-type" => "application/json",
                "body" => mock_content
              })
end
