# Kommand
_As described in Wikipedia, the command pattern .._
> _is a behavioral design pattern in which an object is used to encapsulate all information needed to perform an action or trigger an event at a later time._

In a nutshell, the Kommand library implements the [command pattern](https://en.wikipedia.org/wiki/Command_pattern) in Kotlin, using Kotlin's [coroutines](https://kotlinlang.org/docs/reference/coroutines.html) to execute suspending command requests.

## What problems does Command pattern solves
- decoupling of the invoker of a request from a particular request
- configuring an object that invokes a request with a request

## What solution does the Command patern describes
- defines a separate command object that encapsulate a request
- invoker class delegates a request to a command object instead of implementing a particular request directly

This allows us to have a class that is not coupled to a particular request and is independent of how the request is carried out. The execution of a command, on the other hand, is done in a suspending function that may return immediately or suspend until the request is completes once it is triggered.

# Class diagram of Kommand
(TODO)

# Demonstration
* See the unit tests in the library project
* ~See the sample app~ (TODO)

# To do
* Documentation
* Class diagram
* Sample app
