package com.goldenpie.devs.pincodeview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.LinearLayout
import com.goldenpie.devs.pincodeview.core.Listeners
import com.goldenpie.devs.pincodeview.core.LockType
import com.goldenpie.devs.pincodeview.core.pxFromDp
import com.goldenpie.devs.pincodeview.core.showDelayedKeyboard
import java.util.*

open class PinCodeView : LinearLayout, View.OnClickListener {

    private lateinit var pinLayout: LinearLayout
    private lateinit var invisibleEditText: AppCompatEditText

    private lateinit var lockType: LockType

    @ColorInt
    private var innerCircleColor = 0
    @ColorInt
    private var outerCircleColor = 0
    @ColorInt
    private var errorColor = 0

    private var pinCount = 0
    private var innerAlpha = 0f

    private var innerDrawable: Drawable? = null
    private var outerDrawable: Drawable? = null
    private var tintInner = true
    private var tintOuter = true

    private var pass: String? = null

    private var pinEnteredListener: Listeners.PinEnteredListener? = null
    private var pinMismatchListener: Listeners.PinMismatchListener? = null
    private var pinReEnterListener: Listeners.PinReEnterListener? = null

    private val pins = ArrayList<SinglePinView>()

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun initView() {
        val inflater: LayoutInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        with(inflater.inflate(R.layout.pin_code_view_layout, this)) {
            pinLayout = findViewById<View>(R.id.pin_layout) as LinearLayout
            invisibleEditText = findViewById<View>(R.id.invisible_edittext) as AppCompatEditText
        }
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        innerCircleColor = Color.WHITE
        outerCircleColor = ContextCompat.getColor(context, R.color.primary_dark)
        errorColor = ContextCompat.getColor(context, R.color.accent)
        pinCount = 4
        lockType = LockType.UNLOCK
        innerAlpha = 0.3f

        if (attrs == null)
            return

        with(context.obtainStyledAttributes(attrs, R.styleable.PinCodeView)) {
            innerCircleColor = getColor(R.styleable.PinCodeView_pcv_pin_inner_color, innerCircleColor)
            outerCircleColor = getColor(R.styleable.PinCodeView_pcv_pin_outer_color, outerCircleColor)
            errorColor = getColor(R.styleable.PinCodeView_pcv_pin_error_color, errorColor)
            pinCount = getInteger(R.styleable.PinCodeView_pcv_pin_length, pinCount)
            innerAlpha = getFloat(R.styleable.PinCodeView_pcv_pin_inner_alpha, innerAlpha)
            tintInner = getBoolean(R.styleable.PinCodeView_pcv_pin_tint_inner, tintInner)
            tintOuter = getBoolean(R.styleable.PinCodeView_pcv_pin_tint_outer, tintOuter)

            innerDrawable = getDrawable(R.styleable.PinCodeView_pcv_pin_inner_drawable)
            innerDrawable ?: run { innerDrawable = ContextCompat.getDrawable(context, R.drawable.circle) }

            outerDrawable = getDrawable(R.styleable.PinCodeView_pcv_pin_outer_drawable)
            outerDrawable ?: run { outerDrawable = ContextCompat.getDrawable(context, R.drawable.circle) }

            when (getInt(R.styleable.PinCodeView_pcv_pin_type, 0)) {
                0 -> lockType = LockType.UNLOCK
                1 -> lockType = LockType.ENTER_PIN
            }

            recycle()
        }
    }

    private fun init(attrs: AttributeSet?) {
        initView()
        parseAttributes(attrs)

        setUpView()
        setUpPins()
    }

    private fun setUpPins() {
        pins.clear()
        pinLayout.removeAllViews()

        if (pinCount <= 0)
            return

        for (i in 0 until pinCount) {
            val singlePinView = SinglePinView(context).apply {
                setColors(innerCircleColor, outerCircleColor)

                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                        .apply {
                            weight = 1f
                            gravity = Gravity.CENTER_HORIZONTAL

                            with(pxFromDp(2f).toInt()) {
                                setMargins(this, this, this, this)
                            }
                        }

                innerPinView.setImageDrawable(innerDrawable)
                outerPinView.setImageDrawable(outerDrawable)

                if (!tintInner)
                    innerPinView.clearColorFilter()

                if (!tintOuter)
                    outerPinView.clearColorFilter()

                innerPinView.alpha = innerAlpha
            }

            with(singlePinView) {
                pins.add(this)
                pinLayout.addView(this)
            }
        }

        invisibleEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(pinCount))
    }

    private fun setUpView() {
        invisibleEditText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showDelayedKeyboard(v)
            }
        }

        invisibleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (pinCount <= 0)
                    return

                pins.asSequence()
                        .map { it.innerPinView }
                        .onEach { alpha = innerAlpha }

                (0 until s.length).forEach { pins[it].innerPinView.alpha = 1f }


                if (lockType == LockType.ENTER_PIN && !pass.isNullOrEmpty() && s.isEmpty()) {
                    pinReEnterListener?.onPinReEnterStarted()
                }

                if (s.length == pinCount) {
                    pass = s.toString()

                    if (lockType == LockType.UNLOCK) {
                        pinEnteredListener?.onPinEntered(pass)
                        return
                    }

                    if (pass.isNullOrEmpty()) {
                        invisibleEditText.text!!.clear()
                    } else {
                        if (pass != s.toString()) {
                            reset()
                            pinMismatchListener?.onPinMismatch()

                            return
                        }

                        pinEnteredListener?.onPinEntered(pass)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        pinLayout.setOnClickListener(this)
    }

    /**
     * Reset pin code view with error style
     */
    fun reset() {
        pins.onEach { it.innerPinView.setColorFilter(errorColor) }

        Handler().postDelayed({
            pins.asSequence()
                    .map { it.innerPinView }
                    .onEach {
                        if (tintInner)
                            it.setColorFilter(innerCircleColor)
                        else
                            it.clearColorFilter()
                    }

            invisibleEditText.text!!.clear()
        }, 500)
    }

    /**
     * Clear pin code view input
     */
    fun clear() {
        pass = null
        invisibleEditText.text!!.clear()
    }

    override fun onClick(v: View) {
        when {
            v.id == R.id.pin_layout ->
                with(invisibleEditText) {
                    clearFocus()
                    requestFocus()
                }
        }
    }

    /**
     * Set type of view logic
     * Could be one of
     * - LockType.UNLOCK if you need just confirm pin
     * - LockType.ENTER_PIN if you want to save pin
     *
     * @param lockType
     */
    fun setLockType(lockType: LockType): PinCodeView {
        this.lockType = lockType
        this.pass = null
        clear()
        return this
    }

    fun setInnerCircleColor(@ColorInt innerCircleColor: Int): PinCodeView {
        this.innerCircleColor = innerCircleColor
        setUpPins()
        return this
    }

    fun setOuterCircleColor(@ColorInt outerCircleColor: Int): PinCodeView {
        this.outerCircleColor = outerCircleColor
        setUpPins()
        return this
    }

    fun setErrorColor(@ColorInt errorColor: Int): PinCodeView {
        this.errorColor = errorColor
        return this
    }

    fun setPinLength(pinCount: Int): PinCodeView {
        this.pinCount = pinCount
        setUpPins()
        return this
    }

    fun setInnerDrawable(innerDrawable: Drawable): PinCodeView {
        this.innerDrawable = innerDrawable
        setUpPins()
        return this
    }

    fun setOuterDrawable(outerDrawable: Drawable): PinCodeView {
        this.outerDrawable = outerDrawable
        setUpPins()
        return this
    }

    fun setInnerAlpha(innerAlpha: Float): PinCodeView {
        this.innerAlpha = innerAlpha
        setUpPins()
        return this
    }

    fun setTintInner(tintInner: Boolean): PinCodeView {
        this.tintInner = tintInner
        setUpPins()
        return this
    }

    fun setTintOuter(tintOuter: Boolean): PinCodeView {
        this.tintOuter = tintOuter
        setUpPins()
        return this
    }

    fun setPinEnteredListener(pinEnteredListener: Listeners.PinEnteredListener): PinCodeView {
        this.pinEnteredListener = pinEnteredListener
        return this
    }

    fun setPinMismatchListener(pinMismatchListener: Listeners.PinMismatchListener): PinCodeView {
        this.pinMismatchListener = pinMismatchListener
        return this
    }

    fun setPinReEnterListener(pinReEnterListener: Listeners.PinReEnterListener): PinCodeView {
        this.pinReEnterListener = pinReEnterListener
        return this
    }
}