package com.goldenpie.devs.pincodeview

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

internal class SinglePinView : LinearLayout {
    lateinit var outerPinView: AppCompatImageView
        private set
    lateinit var innerPinView: AppCompatImageView
        private set

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    @Suppress("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private fun initView() {
        val inflater: LayoutInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        with(inflater.inflate(R.layout.single_pin_view_layout, this)) {
            outerPinView = findViewById<View>(R.id.outer_pin_view) as AppCompatImageView
            innerPinView = findViewById<View>(R.id.inner_pin_view) as AppCompatImageView
        }
    }

    fun setColors(innerColor: Int, outerColor: Int) {
        outerPinView.setColorFilter(outerColor)
        innerPinView.setColorFilter(innerColor)
    }
}