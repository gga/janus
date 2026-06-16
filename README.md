# Janus: Consumer-driven Contracts and Mocks

While on a recent mobile project with a client, it was obvious that
consumer-driven contracts would have been very valuable for the
service implementors. The other thing I noticed was that the service
clients had no way to verify that the out-of-process mocks they were
building and testing against actually had any relationship to the data
that was returned by the services.

When talking to the service team, they were really keen on the idea of
using consumer-driven contracts. They desperately wanted more client
developer involvement, but just couldn't get it. However, I've
previously had a lot of trouble with getting client teams to actually
write the contracts. It is more work for them, for a promise of
eventually easier integration.

This is where Janus comes in. The idea was to write the contracts in
such a way that as well as using the contract to verify the behaviour
of the service, use that very same contract to provide mocks to the
consuming applications. Executing the contract in one mode would test
the service; executing it in another mode would create an
out-of-process mock server. As soon as the client team writes the
contracts, they would get immediate value out of it.

## Getting Started

### Prerequisites

These tools must be installed once on your machine before working with Janus.

**Java (OpenJDK)**

```bash
brew install openjdk
```

After installation, add OpenJDK to your PATH (Homebrew prints this hint too):

```bash
echo 'export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

Verify:

```bash
java -version
```

**Leiningen** (Clojure build tool)

```bash
brew install leiningen
```

Verify:

```bash
lein version
```

**rbenv** (Ruby version manager)

```bash
brew install rbenv
```

Add to your shell (if not already present):

```bash
echo 'eval "$(rbenv init -)"' >> ~/.zshrc
source ~/.zshrc
```

**rbenv-gemset** (per-project gem isolation)

```bash
brew install rbenv-gemset
```

### Project Ruby Setup

From the project root, create and activate the project's gemset:

```bash
rbenv gemset create 3.3.0 janus
```

The `.ruby-version` and `.rbenv-gemsets` files in this repo pin the Ruby
version and gemset automatically whenever you `cd` into the directory.

Install the Ruby dependencies:

```bash
gem install bundler
bundle install
```

### Building the JAR

Compile the Clojure source into a self-contained JAR:

```bash
rake build
# or directly:
lein uberjar
```

This produces `janus-0.0.2-standalone.jar` in the project root.

### Running Tests

**Clojure unit tests** (Midje):

```bash
rake test
# or directly:
lein midje
```

**Cucumber acceptance tests** (requires the JAR to be built first):

```bash
rake features
```

**Everything** (unit tests + acceptance tests):

```bash
rake verify
```

### Packaging a Release

```bash
rake package
```

Produces `janus-0.0.2.tar.gz` containing the standalone JAR.

## Using Janus

Janus currently only verifies services against contracts. To verify a service
against a contract, create a contract like so:

```clojure
(service
 "Service name"

 (contract "contract name"
           (method <one of :get, :post, :put or :delete>)
           (url "full, absolute URL to the service")
           (header "header name" "header value")
           (body <:xml, :json or :string>
                 <Clojure data structure that will be serialized as above>)

           (should-have :path "json path" :matching <regex>)
           (should-have :path "json path" :of-type <:string, :array, :object or :number>)
           (should-have :path "json path" :equal-to <value>)))
```

Save that in a file with the suffix `.jns` and then run it with janus.

    java -jar target/janus-0.0.2-standalone.jar --verify file.jns

A janus contract file is a Clojure data structure, and it is not
interpreted as code. This means you can't include Clojure code in
there. If you need any code, then create a directory called `support`
in the same directory as you run janus, any file with a `.clj` suffix
in that directory will be loaded and executed.

You can use that file to start and stop any services that you may
need.

### Json Path

janus will automatically deserialize JSON responses received from
services into Clojure data structures. Hence, to verify the structure
of the document, Json Path is used to identify parts of the
document. It's actually a pretty powerful language, though not quite
as powerful as XML.

For more info, have a look at the
[Json Path](http://github.com/gga/json-path) implementation that janus
uses.

## Limitations

* janus only support Json response documents.
* No ability to mock services based on a contract.
