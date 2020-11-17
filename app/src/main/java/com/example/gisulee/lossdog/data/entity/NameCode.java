package com.example.gisulee.lossdog.data.entity;

import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;

public class NameCode implements Serializable {

    public final String TAG = "NameCode";
    public final static String BUS = "BUS";
    public final static String SUBWAY = "SUBWAY";
    public final static String TAXI = "TAXI";
    public final static String KORAIL = "KORAIL";

    public String instanceCode;
    public String name;
    public String code;


    public NameCode(){}
    public NameCode(String name, String code) {
        this.name = name;
        this.code = code;
    }
    @Override
    public Object clone() {
        Object obj = null;
        try {
            obj = super.clone();
        } catch (Exception e) {
        }
        return obj;
    }

    public String convertProCategory(NameCode categoryNameCode){
        if(this.instanceCode == null) {
            Log.d("tq", "convertProCategory: null");
            categoryNameCode.instanceCode = "";
            return null;
        }

        if(this.instanceCode.equals(NameCodePoliceAPI.instance)){
            return new NameCodePoliceAPI().convertProCategory(categoryNameCode);
        }else if( this.instanceCode.equals(NameCodeBusanSubway.instance)){
            return new NameCodeBusanSubway().convertProCategory(categoryNameCode);
        }else if( this.instanceCode.equals(NameCodeSeoul.instance)){
            return new NameCodeSeoul().convertProCategory(categoryNameCode);
        }

        return "TLqkf";
    };

    public Bundle createRequestBundle(AppSettings settings, NameCode areaCategoryCode, NameCode prdCategoryNameCode){
        if(this.instanceCode == null) {
            Log.d(TAG, "convertProCategory: null");
            return null;
        }

        if(this.instanceCode.equals(NameCodePoliceAPI.instance)){
            return new NameCodePoliceAPI().createRequestBundle(settings,areaCategoryCode,prdCategoryNameCode);
        }else if( this.instanceCode.equals(NameCodeBusanSubway.instance)){
            return new NameCodeBusanSubway().createRequestBundle(settings,areaCategoryCode,prdCategoryNameCode);
        }else if( this.instanceCode.equals(NameCodeSeoul.instance)){
            return new NameCodeSeoul().createRequestBundle(settings,areaCategoryCode,prdCategoryNameCode);
        }

        return null;
    }

}
