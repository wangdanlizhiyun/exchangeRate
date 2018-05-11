package com.exchangerate;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.view.Choreographer;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import lzy.com.life_library.utils.LifeUtil;
import okhttp3.OkHttpClient;

/**
 * Created by lizhiyun on 2018/4/13.
 */

public class MyApp extends Application {


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        LifeUtil.init(this);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();

        OkHttpUtils.initClient(okHttpClient);
        SpUtil.init(this);
        Choreographer.getInstance().postFrameCallback(new FPSFrameCallback());
    }
}
