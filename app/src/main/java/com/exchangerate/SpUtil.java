package com.exchangerate;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by lizhiyun on 2018/4/15.
 */

public class SpUtil {
    private static volatile SpUtil sInstance;
    private SharedPreferences mSharedPreferences = null;
    private Application mApplication;
    static final String Tag_Rates = "rates";

    private SpUtil(Application application) {
        this.mApplication = application;
        mSharedPreferences = application.getSharedPreferences("rates",
                Context.MODE_PRIVATE);
    }
    public static void init(Application application) {
        if (sInstance == null) {
            synchronized (SpUtil.class) {
                if (sInstance == null) {
                    sInstance = new SpUtil(application);
                }
            }
        }
    }
    public static SpUtil getInstance() {
        return sInstance;
    }

    public void saveRates(ExchangeRateBean exchangeRateBean){
        if (exchangeRateBean == null) return;
        String content = new Gson().toJson(exchangeRateBean,ExchangeRateBean.class);
        mSharedPreferences.edit().putString(Tag_Rates,content).commit();
    }
    public ExchangeRateBean getRates(){
        ExchangeRateBean exchangeRateBean = null;
        try {
            String content = mSharedPreferences.getString(Tag_Rates,"");
            exchangeRateBean = new Gson().fromJson(content,ExchangeRateBean.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return exchangeRateBean;
    }
}
