package com.github.mrmitew.kommand.sample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mrmitew.kommand.Command
import com.github.mrmitew.kommand.CommandInvokerImpl
import com.github.mrmitew.kommand.NothingToRedoException
import com.github.mrmitew.kommand.NothingToUndoException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext

object ViewWrapper {
    const val initialColor = Color.WHITE
    var currentColor: Int = initialColor
}

// Since we can't get the background color info back from the View, we have to keep track of it ourselves
// So, just for the sake of this demo, we'll use Kotlin's property extensions to mimic as if there was
// a getter for the background color property
private var View.backgroundColor: Int
    get() = ViewWrapper.currentColor
    set(value) {
        setBackgroundColor(value)
        ViewWrapper.currentColor = value
    }

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start

/**
 * Set a click listener on a view with an action block that can suspend
 */
fun View.setOnSuspendClickListener(context: CoroutineContext = UI,
                                   action: suspend (View) -> Unit) =
        setOnClickListener { view ->
            launch(context) {
                action(view)
            }
        }

/**
 * Set a click listener on a view and back it up by an actor that allows
 * execution of a single action block at a time. Subsequent requests will be discarded until action returns.
 */
fun View.setOnRendezvousClickListener(action: suspend (View) -> Unit) {
    val eventActor = actor<View>(UI, capacity = 0) {
        for (event in channel) action(event)
    }

    setOnClickListener {
        eventActor.offer(it)
    }
}

class MainActivity : AppCompatActivity() {
    private val random = Random()
    private val cmdInvoker = CommandInvokerImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vg_root.backgroundColor = ViewWrapper.initialColor

        btn_change_background.setOnRendezvousClickListener {
            showBusy(true)
            val newColor = getRandomColor()
            cmdInvoker.execute(ChangeBackgroundColor(receiver = vg_root,
                    newColor = newColor,
                    oldColor = vg_root.backgroundColor))
            showBusy(false)
        }

        btn_undo.setOnRendezvousClickListener {
            showBusy(true)
            try {
                cmdInvoker.undo()
            } catch (e: NothingToUndoException) {
                Toast.makeText(this@MainActivity, "Nothing to undo", Toast.LENGTH_SHORT).show()
            }
            showBusy(false)
        }

        btn_cancel.setOnRendezvousClickListener {
            showBusy(true)
            try {
                cmdInvoker.cancel()
            } catch (e: NothingToUndoException) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Nothing to cancel", Toast.LENGTH_SHORT).show()
            }
            showBusy(false)
        }

        btn_redo.setOnRendezvousClickListener {
            showBusy(true)
            try {
                cmdInvoker.redo()
            } catch (e: NothingToRedoException) {
                Toast.makeText(this@MainActivity, "Nothing to redo", Toast.LENGTH_SHORT).show()
            }
            showBusy(false)
        }
    }

    private fun showBusy(isBusy: Boolean) {
        progressBar.visibility = if (isBusy) View.VISIBLE else View.GONE
    }

    private fun getRandomColor() =
            Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))

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
    }
}
