[![](https://jitpack.io/v/antoxa2584x/PinCodeView.svg)](https://jitpack.io/#antoxa2584x/PinCodeView)

PinCodeView
=====================
 `PinCodeView` is Android UI library for replace `EditText`'s PIN input with pretty customizable view.

## Demo
![](images/preview.jpg)  

 See demo app for usage example.
 
## How To Use
### In Xml
```xml
<com.goldenpie.devs.pincodeview.PinCodeView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="24dp"
    app:pcv_pin_error_color="@color/accent"
    app:pcv_pin_inner_color="@color/colorPrimaryDark"
    app:pcv_pin_length="4"
    app:pcv_pin_outer_color="@color/colorPrimary"
    app:pcv_pin_type="unlock" />
```

### In Your Code
```java
PinCodeView pinCodeView = new PinCodeView(this);
pinCodeView.setLockType(LOCK_TYPE.ENTER_PIN);
pinCodeView.setPinEnteredListener(this);
pinCodeView.setPinReEnterListener(this);
pinCodeView.setPinMismatchListener(this);
pinCodeView.setInnerCircleColor(color);
pinCodeView.setOuterCircleColor(color);
pinCodeView.setErrorColor(color);
pinCodeView.setLockType(isChecked ? LOCK_TYPE.ENTER_PIN : LOCK_TYPE.UNLOCK);
pinCodeView.setPinLenght(lenght);
```

## License
```
MIT License

Copyright (c) 2017 antoxa2584x

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
