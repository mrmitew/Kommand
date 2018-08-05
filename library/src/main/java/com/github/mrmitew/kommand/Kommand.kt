package com.github.mrmitew.kommand

import java.util.*

open class CommandException : RuntimeException()
class NothingToUndoException : CommandException()
class NothingToRedoException : CommandException()

// TODO: Add capacity support
// TODO: Add logger support

interface Command {
    suspend fun execute(): Any
    suspend fun undo(): Any

    interface Invoker {
        fun isRedoAvailable(): Boolean
        fun isUndoAvailable(): Boolean

        fun undoCount(): Int
        fun redoCount(): Int

        fun undoPeek(): Command
        fun redoPeek(): Command

        suspend fun execute(cmd: Command): Any
        suspend fun undo(): Any
        suspend fun redo(): Any
        suspend fun cancel(): Any
    }
}

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
        println("Pushed to undo stack: $cmd. Stack size: ${undoStack.size}")
        redoStack.clear()
        return result
    }

    override suspend fun undo(): Any {
        if (isUndoAvailable()) {
            val cmd = undoStack.pop()
            val result = cmd.undo()
            println("Popped from undo stack: $cmd. Stack size: ${undoStack.size}")
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

    override fun isUndoAvailable() = !undoStack.isEmpty()
    override fun isRedoAvailable() = !redoStack.isEmpty()
}