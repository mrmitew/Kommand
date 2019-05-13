package com.github.mrmitew.kommand.sample.util

import android.graphics.Color
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Created by Stefan Mitev on 13-5-19.
 */

/**
 * Since we can't get the background color info back from the View, we have to keep track of it ourselves
 * So, just for the sake of this demo, we'll use Kotlin's property extensions to mimic as if there was
 * a getter for the background color property
 */
internal var View.backgroundColor: Int
    get() = ViewWrapper.currentColor
    set(value) {
        setBackgroundColor(value)
        ViewWrapper.currentColor = value
    }

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

object ViewWrapper {
    const val initialColor = Color.WHITE
    var currentColor: Int = initialColor
}