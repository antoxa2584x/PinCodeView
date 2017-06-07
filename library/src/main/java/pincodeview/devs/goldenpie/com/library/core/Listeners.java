package pincodeview.devs.goldenpie.com.library.core;

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