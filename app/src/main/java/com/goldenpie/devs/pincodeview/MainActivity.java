package com.goldenpie.devs.pincodeview;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import pincodeview.devs.goldenpie.com.library.core.LOCK_TYPE;
import pincodeview.devs.goldenpie.com.library.core.Listeners;
import pincodeview.devs.goldenpie.com.library.view.PinCodeView;

public class MainActivity extends AppCompatActivity implements Listeners.PinEnteredListener,
        Listeners.PinReEnterListener, Listeners.PinMismatchListener, View.OnClickListener {

    private PinCodeView pinCodeView;
    private SwitchCompat switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        pinCodeView.setPinEnteredListener(this);
        pinCodeView.setPinReEnterListener(this);
        pinCodeView.setPinMismatchListener(this);
    }

    private void initView() {
        pinCodeView = (PinCodeView) findViewById(R.id.ci);
        AppCompatButton innerColor = (AppCompatButton) findViewById(R.id.inner_color);
        AppCompatButton outerColor = (AppCompatButton) findViewById(R.id.outer_color);
        AppCompatButton errorColor = (AppCompatButton) findViewById(R.id.error_color);
        SeekBar seekBar = (SeekBar) findViewById(R.id.sek_bar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pinCodeView.setPinCount(progress);
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
        switchCompat = (SwitchCompat) findViewById(R.id.switch_compat);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pinCodeView.setLockType(isChecked ? LOCK_TYPE.ENTER_PIN : LOCK_TYPE.UNLOCK);
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
                        cp2.dismiss();
                    }
                });
                break;
        }
    }
}
