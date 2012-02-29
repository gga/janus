require 'cucumber'
require 'cucumber/rake/task'

VERSION = "0.0.1"
JAR = "janus-#{VERSION}-standalone.jar"

CLJ_SRC = FileList['**/*.clj']

file JAR => CLJ_SRC do
  sh "lein uberjar"
end

desc "Build janus into something runnable"
task :build => JAR

desc "Run all tests"
task :test do
  sh "lein midje"
end

Cucumber::Rake::Task.new(:features => JAR) do |t|
  t.cucumber_opts = "features --format pretty --tags ~@wip"
end
