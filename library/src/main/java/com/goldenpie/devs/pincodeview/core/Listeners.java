package com.goldenpie.devs.pincodeview.core;

public class Listeners {
    public interface PinEnteredListener {
        void onPinEntered(String pinCode);
    }

    public interface PinMismatchListener {
        void onPinMismatch();
    }

    public interface PinReEnterListener {
        void onPinReEnterStarted();
    }
}