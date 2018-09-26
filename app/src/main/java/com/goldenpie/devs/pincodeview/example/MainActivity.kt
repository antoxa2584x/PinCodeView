package com.goldenpie.devs.pincodeview.example

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.goldenpie.devs.pincodeview.core.Listeners
import com.goldenpie.devs.pincodeview.core.LockType
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), Listeners.PinEnteredListener, Listeners.PinReEnterListener, Listeners.PinMismatchListener, View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        listOf(pinCodeView, drawablePinCodeView).onEach {
            it.setLockType(LockType.ENTER_PIN)
            it.setPinEnteredListener(this)
            it.setPinReEnterListener(this)
            it.setPinMismatchListener(this)
        }
    }

    private fun initView() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                listOf(pinCodeView, drawablePinCodeView).onEach {
                    it.setPinLength(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        alphaSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                listOf(pinCodeView, drawablePinCodeView).onEach {
                    it.setInnerAlpha(progress.div(10f))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        listOf(innerColor, outerColor, errorColor).onEach {
            it.setOnClickListener(this)
        }

        switchCompat.setOnCheckedChangeListener { _, isChecked ->
            listOf(pinCodeView, drawablePinCodeView).onEach {
                it.setLockType(if (isChecked) LockType.ENTER_PIN else LockType.UNLOCK)
            }
        }

        innerTintSwitch.setOnCheckedChangeListener { _, isChecked ->
            listOf(pinCodeView, drawablePinCodeView).onEach {
                it.setTintInner(isChecked)
            }
        }

        outerTintSwitch.setOnCheckedChangeListener { _, isChecked ->
            listOf(pinCodeView, drawablePinCodeView).onEach {
                it.setTintOuter(isChecked)
            }
        }
    }

    override fun onPinEntered(pinCode: String?) {
        toast(pinCode)
    }

    override fun onPinReEnterStarted() {
        toast("Pin re-enter started")
    }

    override fun onPinMismatch() {
        toast("Pin mismatch")
    }

    override fun onClick(v: View) {
        val cp = ColorPicker(this@MainActivity)
                .apply {
                    show()
                }

        when (v) {
            innerColor -> {
                cp.setCallback { color ->
                    listOf(pinCodeView, drawablePinCodeView).onEach {
                        it.setInnerCircleColor(color)
                    }
                    cp.dismiss()
                }
            }
            outerColor -> {
                cp.setCallback { color ->
                    listOf(pinCodeView, drawablePinCodeView).onEach {
                        it.setOuterCircleColor(color)
                    }
                    cp.dismiss()
                }
            }
            errorColor -> {
                cp.setCallback { color ->
                    listOf(pinCodeView, drawablePinCodeView).onEach {
                        it.setErrorColor(color)
                    }
                    cp.dismiss()
                }
            }
        }
    }

    private fun Activity.toast(string: String?) =
            Toast.makeText(this, string, Toast.LENGTH_SHORT).show()

}
