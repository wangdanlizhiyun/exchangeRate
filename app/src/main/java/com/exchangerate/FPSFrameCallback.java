package com.exchangerate;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Choreographer;

/**
 * Created by lizhiyun on 2017/11/20.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class FPSFrameCallback implements Choreographer.FrameCallback {
    private long startSampleTimeInNs = 0;
    private long needToCollectFrameTimeCost = 17l;
    private long cost = 0l;
    private HandlerThread mCollectHandlerThread;
    Handler mCollectHandler;

    public FPSFrameCallback() {
        mCollectHandlerThread = new HandlerThread("collectCostMessage");
        mCollectHandlerThread.start();
        mCollectHandler = new Handler(mCollectHandlerThread.getLooper());
    }

    Runnable mCollectRunnable = new Runnable() {
        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
            for (StackTraceElement s : stackTrace) {
                sb.append(s.toString() + "\n");
            }
            Log.v("test", "耗时记录"+sb.toString());
        }
    };
    @Override
    public void doFrame(long frameTimeNanos) {
        if (startSampleTimeInNs == 0){
            startSampleTimeInNs = frameTimeNanos;
        }

        cost = (frameTimeNanos - startSampleTimeInNs)/1000000;
        if (cost > needToCollectFrameTimeCost){

            if (cost > 200){
                mCollectHandler.post(mCollectRunnable);
            }
        }
        startSampleTimeInNs = frameTimeNanos;
        Choreographer.getInstance().postFrameCallback(this);
    }
}
