package com.exchangerate;

/**
 * Created by lizhiyun on 2017/5/12.
 */

public class AntiShakeUtil {
    private static long lastClickTime;
    public static final int mIntDefaultTime = 300;
    // 是否-快速点击
    public static boolean isFastDoubleClick(long maxTime) {
        if (maxTime == 0)
            maxTime = mIntDefaultTime;
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < maxTime)
            return true;
        else {
            lastClickTime = time;
            return false;
        }
    }
}
