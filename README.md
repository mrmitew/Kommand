# Kommand
_As described in Wikipedia, the command pattern .._
> _is a behavioral design pattern in which an object is used to encapsulate all information needed to perform an action or trigger an event at a later time._

In a nutshell, the Kommand library implements the [command pattern](https://en.wikipedia.org/wiki/Command_pattern) in Kotlin, using Kotlin's [coroutines](https://kotlinlang.org/docs/reference/coroutines.html) to execute suspending command requests.

## What problems does the Command pattern solve
- decoupling of the invoker of a request from a particular request
- configuring an object that invokes a request with a request

## What solutions does the Command patern describe
- defines a separate command object that encapsulate a request
- invoker class delegates a request to a command object instead of implementing a particular request directly

This allows us to have a class that is not coupled to a particular request and is independent of how the request is carried out. The execution of a command, on the other hand, is done in a suspending function that may return immediately or suspend until the request is completes once it is triggered.

# Class diagram of Kommand
(TODO)

# Usage
```kotlin
private val cmdInvoker = CommandInvokerImpl()

...

    showBusy(true)
    cmdInvoker.execute(ChangeBackgroundColor(vg_root, newColor = getRandomColor(), oldColor = vg_root.backgroundColor))
    showBusy(false)
    
...

    showBusy(true)
    try {
        cmdInvoker.undo()
    } catch (e: NothingToUndoException) {
        Toast.makeText(this, "Nothing to undo", Toast.LENGTH_SHORT).show()
    }
    showBusy(false)
    
...

class ChangeBackgroundColor(private val view: View,
                            private val newColor: Int,
                            private val oldColor: Int) : Command {
    override suspend fun execute() {
        // Simulate some expensive work by delaying the actual execution of the command
        delay((1000..2000).random())
        view.backgroundColor = newColor
    }

    override suspend fun undo() {
        // Simulate some expensive work by delaying the actual execution of the command
        delay((1000..2000).random())
        view.backgroundColor = oldColor
    }
}
```
For a complete demonstration, please refer to the sample app and the unit tests in the library project.

# Demonstration
* See the unit tests in the library project
* See the sample app

# Note
Please note that the `execute()` and `undo()` return `Any`, according to the `Command` interface. Therefore, you can return the output of the request/command back to the caller. However, you would need to type cast from `Any` to your known object.

Example:
```kotlin
val result = cmdInvoker.execute(MyCommand(arg1,arg2..))
doSomethingWith(result as? MyOutputObject)
```

# To do
* Documentation
* Class diagram
