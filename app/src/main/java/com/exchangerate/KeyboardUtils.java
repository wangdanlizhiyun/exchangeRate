package com.exchangerate;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import lzy.com.life_library.utils.LifeUtil;

/**
 * Created by lizhiyun on 2018/4/13.
 */

public class KeyboardUtils {
    public static void hideSoftInput(final View view) {
        InputMethodManager imm =
                (InputMethodManager) LifeUtil.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
