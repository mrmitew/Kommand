package com.github.mrmitew.kommand.sample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mrmitew.kommand.CommandInvokerImpl
import com.github.mrmitew.kommand.NothingToRedoException
import com.github.mrmitew.kommand.NothingToUndoException
import com.github.mrmitew.kommand.sample.command.ChangeBackgroundColor
import com.github.mrmitew.kommand.sample.util.ViewWrapper
import com.github.mrmitew.kommand.sample.util.backgroundColor
import com.github.mrmitew.kommand.sample.util.setOnRendezvousClickListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

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

}