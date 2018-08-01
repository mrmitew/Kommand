package com.github.mrmitew.kommand

import junit.framework.Assert.assertTrue
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import java.util.*

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start

class KommandTest {
    @Test
    fun shouldUndo() {
        val invoker = CommandInvokerImpl()
        val initialColor = null
        val receiver = ColorReceiver(currentColor = initialColor)

        val changeColorToBlue = ChangeColorCommand(colors = Pair("blue", initialColor), receiver = receiver)
        val changeColorToPink = ChangeColorCommand(colors = Pair("pink", "blue"), receiver = receiver)
        val changeColorToRed = ChangeColorCommand(colors = Pair("red", "pink"), receiver = receiver)

        runBlocking {
            invoker.execute(changeColorToBlue)
            invoker.execute(changeColorToPink)
            invoker.execute(changeColorToRed)
        }

        assertTrue(receiver.currentColor == "red")
        runBlocking { invoker.undo() }
        assertTrue(receiver.currentColor == "pink")
        runBlocking { invoker.undo() }
        assertTrue(receiver.currentColor == "blue")
        runBlocking { invoker.undo() }
        assertTrue(receiver.currentColor == initialColor)
    }

    @Test
    fun shouldRedo() {
        val invoker = CommandInvokerImpl()
        val initialColor = null
        val receiver = ColorReceiver(currentColor = initialColor)

        val changeColorToBlue = ChangeColorCommand(colors = Pair("blue", initialColor), receiver = receiver)
        val changeColorToPink = ChangeColorCommand(colors = Pair("pink", "blue"), receiver = receiver)
        val changeColorToRed = ChangeColorCommand(colors = Pair("red", "pink"), receiver = receiver)

        runBlocking {
            invoker.execute(changeColorToBlue)
            invoker.execute(changeColorToPink)
            invoker.execute(changeColorToRed)
        }

        assertTrue(receiver.currentColor == "red")
        runBlocking { invoker.undo() }
        assertTrue(receiver.currentColor == "pink")
        runBlocking { invoker.redo() }
        assertTrue(receiver.currentColor == "red")
    }

    @Test
    fun shouldCancel() {
        val initialColor = null
        val receiver = ColorReceiver(currentColor = initialColor)
        val invoker = CommandInvokerImpl()

        val changeColorToBlue = ChangeColorCommand(colors = Pair("blue", initialColor), receiver = receiver)
        val changeColorToPink = ChangeColorCommand(colors = Pair("pink", "blue"), receiver = receiver)
        val changeColorToRed = ChangeColorCommand(colors = Pair("red", "pink"), receiver = receiver)

        runBlocking {
            invoker.execute(changeColorToBlue)
            invoker.execute(changeColorToPink)
            invoker.execute(changeColorToRed)
            invoker.cancel()
        }

        assertTrue(receiver.currentColor == initialColor)
    }


    data class ColorReceiver(var currentColor: String?) : Command.Receiver

    data class ChangeColorCommand(val colors: Pair<String?, String?>,
                                  private val receiver: ColorReceiver) : Command {
        override suspend fun execute(): Command {
//            delay((100..2000).random())
            receiver.currentColor = colors.first
            return this
        }

        override suspend fun undo(): Command {
//            delay((100..2000).random())
            receiver.currentColor = colors.second
            return this
        }
    }

}