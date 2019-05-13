package com.github.mrmitew.kommand.sample.command

import android.view.View
import com.github.mrmitew.kommand.Command
import com.github.mrmitew.kommand.sample.util.backgroundColor
import kotlinx.coroutines.experimental.delay
import java.util.*

class ChangeBackgroundColor(private val receiver: View,
                            private val newColor: Int,
                            private val oldColor: Int) : Command {
    override suspend fun execute() {
        // Simulate some expensive work by delaying the actual execution of the command
        delay((1000..2000).random())
        receiver.backgroundColor = newColor
    }

    override suspend fun undo() {
        // Simulate some expensive work by delaying the actual execution of the command
        delay((1000..2000).random())
        receiver.backgroundColor = oldColor
    }

    private fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start
}