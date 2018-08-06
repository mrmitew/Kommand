package com.github.mrmitew.kommand

interface Command {
    suspend fun execute(): Any
    suspend fun undo(): Any

    interface Invoker {
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

fun Command.Invoker.isRedoAvailable(): Boolean = redoCount() > 0
fun Command.Invoker.isUndoAvailable(): Boolean = undoCount() > 0