package com.exchangerate.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.exchangerate.R;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lizhiyun on 2018/4/14.
 */

public class VirtualKeyboardView extends PercentRelativeLayout {
    @BindViews({R.id.num1, R.id.num2, R.id.num3, R.id.num4, R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9, R.id.num0, R.id.point})
    List<TextView> nums;

    EditText mFocusTextView = null;

    public void setFocusTextView(EditText focusTextView) {
        this.mFocusTextView = focusTextView;
        if (mFocusTextView != null) {
            setFocusableInTouchMode(true);
            if (getVisibility() != View.VISIBLE) {
                setVisibility(View.VISIBLE);
            }
        }
    }

    public VirtualKeyboardView(Context context) {
        super(context);
        init();
    }

    public VirtualKeyboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VirtualKeyboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_virtual_keyboard, this, true);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.close)
    void close(View view) {
        setVisibility(View.GONE);
    }

    @OnClick({R.id.num1, R.id.num2, R.id.num3, R.id.num4, R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9, R.id.num0, R.id.point})
    void clickNums(TextView textView) {
        if (mFocusTextView == null) return;
        int selectionStart = mFocusTextView.getSelectionStart();
        int selectionEnd = mFocusTextView.getSelectionEnd();
        String content = mFocusTextView.getText().toString().trim();
        content = content.substring(0, selectionStart) + textView.getText().toString() + content.substring(selectionEnd);
        if (content.contains(".") && textView.getId() == R.id.point) {
            return;
        }
        mFocusTextView.setText(content);
        mFocusTextView.setSelection(selectionStart + textView.getText().toString().length());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(
                (int) (View.MeasureSpec.getSize(widthMeasureSpec) * 2 / 7.0f),
                View.MeasureSpec.getMode(widthMeasureSpec)));
    }

    public void monitorEditViewFouse(final EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    setFocusTextView(editText);
                }else {
                    setFocusTextView(null);
                }
            }
        });
    }
}
