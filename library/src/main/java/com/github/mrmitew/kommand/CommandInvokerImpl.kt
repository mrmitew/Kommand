package com.github.mrmitew.kommand

import java.util.*

open class CommandException : RuntimeException()
class NothingToUndoException : CommandException()
class NothingToRedoException : CommandException()

// TODO: Add capacity support
class CommandInvokerImpl : Command.Invoker {
    private val undoStack: Stack<Command> = Stack()
    private val redoStack: Stack<Command> = Stack()

    override fun undoCount(): Int = undoStack.size
    override fun redoCount(): Int = redoStack.size

    override fun undoPeek(): Command = undoStack.peek()
    override fun redoPeek(): Command = redoStack.peek()

    override suspend fun execute(cmd: Command): Any {
        val result = cmd.execute()
        undoStack.push(cmd)
        redoStack.clear()
        return result
    }

    override suspend fun undo(): Any {
        if (isUndoAvailable()) {
            val cmd = undoStack.pop()
            val result = cmd.undo()
            redoStack.push(cmd)
            return result
        }

        throw NothingToUndoException()
    }

    override suspend fun redo(): Any {
        if (isRedoAvailable()) {
            val cmd = redoStack.pop()
            val result = cmd.execute()
            undoStack.add(cmd)
            return result
        }

        throw NothingToRedoException()
    }

    override suspend fun cancel(): Any {
        while (undoStack.size > 1) {
            undoStack.pop()
        }

        while (isRedoAvailable()) {
            redoStack.pop()
        }

        return undo()
    }
}