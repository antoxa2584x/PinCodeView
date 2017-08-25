package com.goldenpie.devs.pincodeview.example;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.goldenpie.devs.pincodeview.PinCodeView;
import com.goldenpie.devs.pincodeview.core.LOCK_TYPE;
import com.goldenpie.devs.pincodeview.core.Listeners;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

public class MainActivity extends AppCompatActivity implements Listeners.PinEnteredListener,
        Listeners.PinReEnterListener, Listeners.PinMismatchListener, View.OnClickListener {

    private PinCodeView pinCodeView;
    private PinCodeView drawablePinCodeView;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        pinCodeView.setLockType(LOCK_TYPE.ENTER_PIN);
        pinCodeView.setPinEnteredListener(this);
        pinCodeView.setPinReEnterListener(this);
        pinCodeView.setPinMismatchListener(this);
        drawablePinCodeView.setLockType(LOCK_TYPE.ENTER_PIN);
        drawablePinCodeView.setPinEnteredListener(this);
        drawablePinCodeView.setPinReEnterListener(this);
        drawablePinCodeView.setPinMismatchListener(this);
    }

    private void initView() {
        pinCodeView = (PinCodeView) findViewById(R.id.ci);
        AppCompatButton innerColor = (AppCompatButton) findViewById(R.id.inner_color);
        AppCompatButton outerColor = (AppCompatButton) findViewById(R.id.outer_color);
        AppCompatButton errorColor = (AppCompatButton) findViewById(R.id.error_color);
        SeekBar seekBar = (SeekBar) findViewById(R.id.sek_bar);
        drawablePinCodeView = (PinCodeView) findViewById(R.id.ci_drawable);
        SeekBar alphaSekBar = (SeekBar) findViewById(R.id.alpha_sek_bar);
        SwitchCompat innerTintSwitch = (SwitchCompat) findViewById(R.id.inner_tint_switch);
        SwitchCompat outerTintSwitch = (SwitchCompat) findViewById(R.id.outer_tint_switch);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pinCodeView.setPinLength(progress);
                drawablePinCodeView.setPinLength(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        alphaSekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pinCodeView.setInnerAlpha(progress / 10f);
                drawablePinCodeView.setInnerAlpha(progress / 10f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        innerColor.setOnClickListener(this);
        outerColor.setOnClickListener(this);
        errorColor.setOnClickListener(this);
        SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.switch_compat);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pinCodeView.setLockType(isChecked ? LOCK_TYPE.ENTER_PIN : LOCK_TYPE.UNLOCK);
                drawablePinCodeView.setLockType(isChecked ? LOCK_TYPE.ENTER_PIN : LOCK_TYPE.UNLOCK);
            }
        });
        innerTintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pinCodeView.setTintInner(isChecked);
                drawablePinCodeView.setTintInner(isChecked);
            }
        });
        outerTintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pinCodeView.setTintOuter(isChecked);
                drawablePinCodeView.setTintOuter(isChecked);
            }
        });
    }

    @Override
    public void onPinEntered(String pinCode) {
        Toast.makeText(this, pinCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPinReEnterStarted() {
        Toast.makeText(this, "Pin re-enter started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPinMismatch() {
        Toast.makeText(this, "Pin mismatch", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.inner_color:
                final ColorPicker cp = new ColorPicker(MainActivity.this, 255, 255, 255);

                cp.show();

                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {
                        pinCodeView.setInnerCircleColor(color);
                        drawablePinCodeView.setInnerCircleColor(color);
                        cp.dismiss();
                    }
                });

                break;
            case R.id.outer_color:
                final ColorPicker cp1 = new ColorPicker(MainActivity.this, 255, 255, 255);

                cp1.show();

                cp1.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {
                        pinCodeView.setOuterCircleColor(color);
                        drawablePinCodeView.setOuterCircleColor(color);
                        cp1.dismiss();
                    }
                });
                break;
            case R.id.error_color:
                final ColorPicker cp2 = new ColorPicker(MainActivity.this, 255, 255, 255);

                cp2.show();

                cp2.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {
                        pinCodeView.setErrorColor(color);
                        drawablePinCodeView.setErrorColor(color);
                        cp2.dismiss();
                    }
                });
                break;
        }
    }
}
