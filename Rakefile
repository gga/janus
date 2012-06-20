require 'cucumber'
require 'cucumber/rake/task'

VERSION = "0.0.2"
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

desc "Verifies janus"
task :verify => [:test, :features]

desc "Packages Janus for release"
task :package => [:build, :verify] do
  rel_name = "janus-#{VERSION}"
  mkdir_p rel_name
  cp JAR, "#{rel_name}/janus.jar"
  rm_f "#{rel_name}.tar.gz"
  sh "tar -cvf #{rel_name}.tar #{rel_name}"
  sh "gzip #{rel_name}.tar"
end
