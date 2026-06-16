require 'aruba/cucumber'
require 'harper/client'
require 'harper'
require 'rack'
require 'rack/server'

# The harper CLI launcher uses fork+Open3 which is broken in Ruby 3.3.
# Start the WEBrick server directly in a Thread instead.
class HarperClient
  HARPER_PORT = 4568

  def start
    @server_thread = Thread.new do
      server = Rack::Server.new(app: Harper::App, Port: HARPER_PORT, server: 'webrick')
      Harper::App.server(server.server)
      server.start
    end

    # Block until the server is accepting TCP connections (max 5s)
    50.times do
      TCPSocket.new('localhost', HARPER_PORT).close
      @started = true
      return
    rescue Errno::ECONNREFUSED
      sleep 0.1
    end

    raise "Harper failed to start on port #{HARPER_PORT} within 5 seconds"
  end
end

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
  aruba.config.exit_timeout = 5
end

After do
  harper.stop if harper.started?
end
