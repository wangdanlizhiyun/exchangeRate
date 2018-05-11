package com.exchangerate.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import java.lang.reflect.Method;


/**
 * Created by lizhiyun on 2018/4/14.
 */

public class VirtualKeyboardEditTextView extends android.support.v7.widget.AppCompatEditText {
    MyTextWatcher myTextWatcher;
    TextChangeListener textChangeListener;
    public VirtualKeyboardEditTextView(Context context) {
        super(context);
        init();
    }

    public VirtualKeyboardEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VirtualKeyboardEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            this.setInputType(InputType.TYPE_NULL);
        } else {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        myTextWatcher = new MyTextWatcher();
        addTextChangedListener(myTextWatcher);

    }

    public void setTextChangeListener(TextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    public void setPosition(int position) {
        myTextWatcher.setPosition(position);
    }
    class MyTextWatcher implements TextWatcher {
        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (System.currentTimeMillis()-lastInitTextTime<50){
                return;
            }
            handler.removeCallbacksAndMessages(null);
            handler.sendMessageDelayed(handler.obtainMessage(),1000);

        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }
    public void initTextView(CharSequence text){
        lastInitTextTime = System.currentTimeMillis();
        setText(text);
        setSelection(getText().length());
    }

    long lastInitTextTime;

    public static interface TextChangeListener{
        void afterTextChanged();
    }
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (textChangeListener != null){
                textChangeListener.afterTextChanged();
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}
