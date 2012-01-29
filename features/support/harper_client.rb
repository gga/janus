require 'open3'
require 'httparty'
require 'json'

class HarperClient
  include HTTParty
  
  base_uri "localhost:4568"

  def initialize
    @started = false
  end

  def started?
    @started
  end

  def start
    read, write = IO.pipe

    fork do
      read.close
      Open3.popen2({}, "harper 4568", {:err => [:child, :out]}) do |i, o|
        found_start = false
        while !found_start && line = o.readline
          if line =~ /WEBrick::HTTPServer#start/
            write.puts "Harper started"
            found_start = true
          end
        end
      end
    end

    write.close
    read.readline

    @started = true
  end

  def stop
    self.class.put "/h/control", :body => {:command => "quit"}.to_json
    @started = false
  end

  def mock(mock)
    mock_body = case mock
                when String
                  mock
                else
                  mock.to_json
                end
    self.class.post "/h/mocks", :body => mock_body
  end
end
