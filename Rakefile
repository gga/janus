require 'cucumber'
require 'cucumber/rake/task'

task :test do
  sh "lein midje"
end

task :build do
  sh "lein uberjar"
end

Cucumber::Rake::Task.new(:features => :build) do |t|
  t.cucumber_opts = "features --format pretty"
end
