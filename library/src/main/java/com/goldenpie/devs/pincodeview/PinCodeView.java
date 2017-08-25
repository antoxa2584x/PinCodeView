package com.goldenpie.devs.pincodeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import com.goldenpie.devs.pincodeview.core.LOCK_TYPE;
import com.goldenpie.devs.pincodeview.core.Listeners;
import com.goldenpie.devs.pincodeview.core.Utils;

import java.util.ArrayList;

public class PinCodeView extends LinearLayout implements View.OnClickListener {

    @ColorInt
    private int innerCircleColor;
    @ColorInt
    private int outerCircleColor;
    @ColorInt
    private int errorColor;

    private Drawable innerDrawable;
    private Drawable outerDrawable;
    private int pinCount;
    private float innerAlpha;
    private boolean tintInner = true;
    private boolean tintOuter = true;

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
        innerAlpha = 0.3f;

        if (attrs == null)
            return;

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PinCodeView);

        innerCircleColor = a.getColor(R.styleable.PinCodeView_pcv_pin_inner_color, innerCircleColor);
        outerCircleColor = a.getColor(R.styleable.PinCodeView_pcv_pin_outer_color, outerCircleColor);
        errorColor = a.getColor(R.styleable.PinCodeView_pcv_pin_error_color, errorColor);
        pinCount = a.getInteger(R.styleable.PinCodeView_pcv_pin_length, pinCount);
        innerAlpha = a.getFloat(R.styleable.PinCodeView_pcv_pin_inner_alpha, innerAlpha);
        tintInner = a.getBoolean(R.styleable.PinCodeView_pcv_pin_tint_inner, tintInner);
        tintOuter = a.getBoolean(R.styleable.PinCodeView_pcv_pin_tint_outer, tintOuter);

        innerDrawable = a.getDrawable(R.styleable.PinCodeView_pcv_pin_inner_drawable);

        if (innerDrawable == null)
            innerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.circle);

        outerDrawable = a.getDrawable(R.styleable.PinCodeView_pcv_pin_outer_drawable);

        if (outerDrawable == null)
            outerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.circle);

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

            singlePinView.getInnerPinView().setImageDrawable(innerDrawable);
            singlePinView.getOuterPinView().setImageDrawable(outerDrawable);

            if (!tintInner)
                singlePinView.getInnerPinView().clearColorFilter();
                singlePinView.getInnerPinView().setAlpha(innerAlpha);
            if (!tintOuter)
                singlePinView.getOuterPinView().clearColorFilter();


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
                    pin.getInnerPinView().setAlpha(innerAlpha);
                }

                for (int i = 0; i < s.length(); i++) {
                    pins.get(i).getInnerPinView().setAlpha(1f);
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
            pin.getInnerPinView().setColorFilter(errorColor);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (SinglePinView pin : pins) {
                    if (tintInner)
                        pin.getInnerPinView().setColorFilter(innerCircleColor);
                    else
                        pin.getInnerPinView().clearColorFilter();

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
    public PinCodeView setLockType(LOCK_TYPE lockType) {
        this.lockType = lockType;
        this.pass = null;
        clear();
        return this;
    }

    public PinCodeView setInnerCircleColor(@ColorInt int innerCircleColor) {
        this.innerCircleColor = innerCircleColor;
        setUpPins();
        return this;
    }

    public PinCodeView setOuterCircleColor(@ColorInt int outerCircleColor) {
        this.outerCircleColor = outerCircleColor;
        setUpPins();
        return this;
    }

    public PinCodeView setErrorColor(@ColorInt int errorColor) {
        this.errorColor = errorColor;
        return this;
    }

    public PinCodeView setPinLength(int pinCount) {
        this.pinCount = pinCount;
        setUpPins();
        return this;
    }

    public PinCodeView setInnerDrawable(Drawable innerDrawable) {
        this.innerDrawable = innerDrawable;
        setUpPins();
        return this;
    }

    public PinCodeView setOuterDrawable(Drawable outerDrawable) {
        this.outerDrawable = outerDrawable;
        setUpPins();
        return this;
    }

    public PinCodeView setInnerAlpha(float innerAlpha) {
        this.innerAlpha = innerAlpha;
        setUpPins();
        return this;
    }

    public PinCodeView setTintInner(boolean tintInner) {
        this.tintInner = tintInner;
        setUpPins();
        return this;
    }

    public PinCodeView setTintOuter(boolean tintOuter) {
        this.tintOuter = tintOuter;
        setUpPins();
        return this;
    }

    public PinCodeView setPinEnteredListener(Listeners.PinEnteredListener pinEnteredListener) {
        this.pinEnteredListener = pinEnteredListener;
        return this;
    }

    public PinCodeView setPinMismatchListener(Listeners.PinMismatchListener pinMismatchListener) {
        this.pinMismatchListener = pinMismatchListener;
        return this;
    }

    public PinCodeView setPinReEnterListener(Listeners.PinReEnterListener pinReEnterListener) {
        this.pinReEnterListener = pinReEnterListener;
        return this;
    }
}