package com.exchangerate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by lizhiyun on 2018/4/13.
 */

public class ExchangeRateBean {
    private String base;
    private LinkedHashMap<String, Float> rates;
    private List<Item> listRates;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public LinkedHashMap<String, Float> getRates() {
        return rates;
    }

    public void setListRates(List<Item> listRates) {
        this.listRates = listRates;
    }

    public void setRates(LinkedHashMap<String, Float> rates) {
        this.rates = rates;
    }

    private static final String USD = "USD";

    public void addBase() {
        if (!rates.containsKey(base)) {
            rates.put(base, 1.0f);
        }

    }

    public void baseUSD() {
        float usd = rates.get(USD);
        if (usd == 1) return;
        for (String key : rates.keySet()
                ) {
            rates.put(key, rates.get(key) / usd);
        }
        base = USD;
    }



    public static class Item {
        private String key;
        private float rate;
        private float showValue;

        public Item( String key, float rate) {
            this.key = key;
            this.rate = rate;
            this.showValue = rate;
        }


        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public float getRate() {
            return rate;
        }

        public void setRate(float rate) {
            this.rate = rate;
        }

        public float getShowValue() {
            return showValue;
        }

        public void setShowValue(float showValue) {
            this.showValue = showValue;
        }

        public String getShowValueString() {
            BigDecimal bigDecimal = new BigDecimal(getShowValue());

            return "" + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        }
    }


    public void fillList(List<Item> list) {
        if (listRates == null) {
            listRates = new ArrayList<>();
        }
        list.clear();
        if (listRates.isEmpty()){
            for (String key : rates.keySet()
                    ) {
                Item item = new Item(key, rates.get(key));
                list.add(item);
                listRates.add(item);
            }
        }else {
            for (Item item:listRates) {
                list.add(item);
            }
        }

    }


}
