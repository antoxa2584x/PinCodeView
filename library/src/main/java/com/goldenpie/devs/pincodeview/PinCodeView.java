package com.goldenpie.devs.pincodeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import com.goldenpie.devs.pincodeview.core.Utils;
import com.goldenpie.devs.pincodeview.core.LOCK_TYPE;
import com.goldenpie.devs.pincodeview.core.Listeners;

public class PinCodeView extends LinearLayout implements View.OnClickListener {

    @ColorInt
    private int innerCircleColor;
    @ColorInt
    private int outerCircleColor;
    @ColorInt
    private int errorColor;
    private int pinCount;

    private LinearLayout pinLayout;
    private AppCompatEditText invisibleEditText;
    private LOCK_TYPE lockType;
    private String pass;

    private Listeners.PinEnteredListener pinEnteredListener;
    private Listeners.PinMismatchListener pinMismatchListener;
    private Listeners.PinReEnterListener pinReEnterListener;

    private ArrayList<SinglePinView> pins = new ArrayList<>();

    public PinCodeView(Context context) {
        super(context);
        init(null);
    }

    public PinCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PinCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PinCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void initView() {

        LayoutInflater inflater;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View mView = inflater.inflate(R.layout.pin_code_view_layout, this);

        pinLayout = (LinearLayout) mView.findViewById(R.id.pin_layout);
        invisibleEditText = (AppCompatEditText) mView.findViewById(R.id.invisible_edittext);
    }

    private void parseAttributes(AttributeSet attrs) {
        innerCircleColor = Color.WHITE;
        outerCircleColor = ContextCompat.getColor(getContext(), R.color.primary_dark);
        errorColor = ContextCompat.getColor(getContext(), R.color.accent);
        pinCount = 4;
        lockType = LOCK_TYPE.UNLOCK;

        if (attrs == null)
            return;

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PinCodeView);

        innerCircleColor = a.getColor(R.styleable.PinCodeView_pcv_pin_inner_color, innerCircleColor);
        outerCircleColor = a.getColor(R.styleable.PinCodeView_pcv_pin_outer_color, outerCircleColor);
        errorColor = a.getColor(R.styleable.PinCodeView_pcv_pin_error_color, errorColor);
        pinCount = a.getInteger(R.styleable.PinCodeView_pcv_pin_length, pinCount);

        switch (a.getInt(R.styleable.PinCodeView_pcv_pin_type, 0)) {
            case 0:
                lockType = LOCK_TYPE.UNLOCK;
                break;
            case 1:
                lockType = LOCK_TYPE.ENTER_PIN;
                break;
        }

        a.recycle();
    }

    private void init(AttributeSet attrs) {
        initView();
        parseAttributes(attrs);

        setUpView();
        setUpPins();
    }

    private void setUpPins() {
        pins.clear();
        pinLayout.removeAllViews();

        if (pinCount <= 0)
            return;

        for (int i = 0; i < pinCount; i++) {
            SinglePinView singlePinView = new SinglePinView(getContext());
            singlePinView.setColors(innerCircleColor, outerCircleColor);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            int margin = (int) Utils.pxFromDp(getContext(), 2);
            singlePinView.setGravity(Gravity.CENTER_HORIZONTAL);

            lp.setMargins(margin, margin, margin, margin);
            singlePinView.setLayoutParams(lp);

            pins.add(singlePinView);
            pinLayout.addView(singlePinView);
        }

        invisibleEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(pinCount)});
    }

    private void setUpView() {
        invisibleEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Utils.showDelayedKeyboard(getContext(), v);
                }
            }
        });

        invisibleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pinCount <= 0)
                    return;

                for (SinglePinView pin : pins) {
                    pin.getInnerPinView().setAlpha(0.3f);
                }

                for (int i = 0; i < s.length(); i++) {
                    pins.get(i).getInnerPinView().setAlpha(1);
                }

                if (lockType == LOCK_TYPE.ENTER_PIN && !TextUtils.isEmpty(pass) && s.length() == 0) {
                    if (pinReEnterListener != null)
                        pinReEnterListener.onPinReEnterStarted();
                }

                if (s.length() == pinCount) {
                    if (lockType == LOCK_TYPE.UNLOCK) {
                        pass = s.toString();

                        if (pinEnteredListener != null)
                            pinEnteredListener.onPinEntered(pass);
                        return;
                    }

                    if (TextUtils.isEmpty(pass)) {
                        pass = s.toString();
                        invisibleEditText.getText().clear();
                    } else {
                        if (!pass.equals(s.toString())) {

                            reset();

                            if (pinMismatchListener != null)
                                pinMismatchListener.onPinMismatch();
                            return;
                        }

                        if (pinEnteredListener != null)
                            pinEnteredListener.onPinEntered(pass);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pinLayout.setOnClickListener(this);
    }

    /**
     * Reset pin code view with error style
     */
    public void reset() {
        for (SinglePinView pin : pins) {
            pin.getInnerPinView().setCircleColor(errorColor);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (SinglePinView pin : pins) {
                    pin.getInnerPinView().setCircleColor(innerCircleColor);
                }
                invisibleEditText.getText().clear();
            }
        }, 500);
    }

    /**
     * Clear pin code view input
     */
    public void clear() {
        pass = null;
        invisibleEditText.getText().clear();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.pin_layout) {
            invisibleEditText.clearFocus();
            invisibleEditText.requestFocus();
        }
    }

    /**
     * Set type of view logic
     * Could be one of
     * - LOCK_TYPE.UNLOCK if you need just confirm pin
     * - LOCK_TYPE.ENTER_PIN if you want to save pin
     *
     * @param lockType
     */
    public void setLockType(LOCK_TYPE lockType) {
        this.lockType = lockType;
        this.pass = null;
        clear();
    }

    public void setInnerCircleColor(@ColorInt int innerCircleColor) {
        this.innerCircleColor = innerCircleColor;
        setUpPins();
    }

    public void setOuterCircleColor(@ColorInt int outerCircleColor) {
        this.outerCircleColor = outerCircleColor;
        setUpPins();
    }

    public void setErrorColor(@ColorInt int errorColor) {
        this.errorColor = errorColor;
    }

    public void setPinLenght(int pinCount) {
        this.pinCount = pinCount;
        setUpPins();
    }

    public void setPinEnteredListener(Listeners.PinEnteredListener pinEnteredListener) {
        this.pinEnteredListener = pinEnteredListener;
    }

    public void setPinMismatchListener(Listeners.PinMismatchListener pinMismatchListener) {
        this.pinMismatchListener = pinMismatchListener;
    }

    public void setPinReEnterListener(Listeners.PinReEnterListener pinReEnterListener) {
        this.pinReEnterListener = pinReEnterListener;
    }
}