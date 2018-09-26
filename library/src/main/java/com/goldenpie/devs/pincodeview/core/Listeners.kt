package com.goldenpie.devs.pincodeview.core

open class Listeners {
    interface PinEnteredListener {
        fun onPinEntered(pinCode: String?)
    }

    interface PinMismatchListener {
        fun onPinMismatch()
    }

    interface PinReEnterListener {
        fun onPinReEnterStarted()
    }
}