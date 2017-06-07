package com.goldenpie.devs.pincodeview.core;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by antoxa2584 on 07.06.17.
 */

public class Utils {
    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static void showDelayedKeyboard(Context context, View view) {
        showDelayedKeyboard(context, view, 100);
    }

    public static void showDelayedKeyboard(final Context context, final View view, final int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }, delay);
    }
}
