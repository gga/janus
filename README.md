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
