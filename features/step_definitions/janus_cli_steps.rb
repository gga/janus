last_janus_run = ""

When /^I run janus$/ do
  last_janus_run = "java -jar ../../janus-0.0.1-standalone.jar"
  step %{I run `#{last_janus_run}`}
end

When /^I run janus with args "([^"]*)"$/ do |args|
  last_janus_run = "java -jar ../../janus-0.0.1-standalone.jar #{args}"
  step %{I run `#{last_janus_run}`}
end

Then /^the output from janus should contain "([^"]*)"$/ do |expected|
  step %{the output from "#{last_janus_run}" should contain "#{expected}"}
end
