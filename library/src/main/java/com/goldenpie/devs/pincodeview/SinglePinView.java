package com.goldenpie.devs.pincodeview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by antoxa2584 on 07.06.17.
 */

class SinglePinView extends LinearLayout {
    private CircleView outerPinView;
    private CircleView innerPinView;

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

        outerPinView = (CircleView) mView.findViewById(R.id.outer_pin_view);
        innerPinView = (CircleView) mView.findViewById(R.id.inner_pin_view);
    }

    public void setColors(int innerColor, int outerColor) {
        outerPinView.setCircleColor(outerColor);
        innerPinView.setCircleColor(innerColor);
    }

    public CircleView getInnerPinView() {
        return innerPinView;
    }
}
