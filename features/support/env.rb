require 'aruba/cucumber'
require 'harper/client'

class JanusWorld

  attr_reader :harper

  def initialize
    @harper = HarperClient.new
  end

  def contract_name(human_name)
    human_name.downcase.gsub(" ", "_")
  end
end

World do
  JanusWorld.new
end

Before do
  @aruba_timeout_seconds = 5
end

After do
  harper.stop if harper.started?
end
