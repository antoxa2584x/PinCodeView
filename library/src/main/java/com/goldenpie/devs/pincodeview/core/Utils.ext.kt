package com.goldenpie.devs.pincodeview.core

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.pxFromDp(dp: Float) = dp * resources.displayMetrics.density

fun View.showDelayedKeyboard(view: View) = showDelayedKeyboard(view, 100)

fun View.showDelayedKeyboard(view: View, delay: Int) {
    Handler().postDelayed({
        val imm = context.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }, delay.toLong())
}