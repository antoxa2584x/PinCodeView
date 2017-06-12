package com.goldenpie.devs.pincodeview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by antoxa2584 on 07.06.17.
 */

class SinglePinView extends LinearLayout {
    private AppCompatImageView outerPinView;
    private AppCompatImageView innerPinView;

    public SinglePinView(Context context) {
        super(context);
        initView();
    }

    public SinglePinView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SinglePinView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SinglePinView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        LayoutInflater inflater;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View mView = inflater.inflate(R.layout.single_pin_view_layout, this);

        outerPinView = (AppCompatImageView) mView.findViewById(R.id.outer_pin_view);
        innerPinView = (AppCompatImageView) mView.findViewById(R.id.inner_pin_view);

        outerPinView.setImageResource(R.drawable.circle);
        innerPinView.setImageResource(R.drawable.circle);
    }

    public void setColors(int innerColor, int outerColor) {
        outerPinView.setColorFilter(outerColor);
        innerPinView.setColorFilter(innerColor);
    }

    public AppCompatImageView getOuterPinView() {
        return outerPinView;
    }

    public AppCompatImageView getInnerPinView() {
        return innerPinView;
    }
}
