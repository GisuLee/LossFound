package com.example.gisulee.lossdog.data.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class AlarmItem  implements Serializable {

    public static final String KEY_WRITE_ALARM_LIST = "ALARM_LIST";
    public static final String KEY_WRITE_ALARM_PREVIEW_LIST = "ALARM_PREVIEW_LIST";


    private ArrayList<NameCode> prdCategoryList;
    private ArrayList<NameCode> prdAreaList;
    private Boolean isAlarmOn;
    private String name;
    private String date;
    private String productText;
    private String placeText;
    private String productFeature;
    private String lastUpdate;
    private int receivedCount = 0;

    public AlarmItem(){

    }

    /* 뷰 홀더용 생성자*/
    public AlarmItem(String nameText, String date, String productText, String placeText) {
        this.name = nameText;
        this.date = date;
        this.productText = productText;
        this.placeText = placeText;
    }

    /* 데이터 전달용 생성자 */
    public AlarmItem(Boolean isAlarmOn, String name, String date, String productText, String placeText, String productFeature, ArrayList<NameCode> prdCategoryList, ArrayList<NameCode> prdAreaList) {
        this.isAlarmOn = isAlarmOn;
        this.name = name;
        this.date = date;
        this.productText = productText;
        this.placeText = placeText;
        this.productFeature = productFeature;
        this.prdCategoryList = prdCategoryList;
        this.prdAreaList = prdAreaList;
    }

    public ArrayList<NameCode> getPrdCategoryList() {
        return prdCategoryList;
    }

    public AlarmItem setPrdCategoryList(ArrayList<NameCode> prdCategoryList) {
        this.prdCategoryList = prdCategoryList;
        return this;
    }

    public ArrayList<NameCode> getPrdAreaList() {
        return prdAreaList;
    }

    public AlarmItem setPrdAreaList(ArrayList<NameCode> prdAreaList) {
        this.prdAreaList = prdAreaList;
        return this;
    }

    public Boolean getAlarmOn() {
        return isAlarmOn;
    }

    public AlarmItem setAlarmOn(Boolean alarmOn) {
        isAlarmOn = alarmOn;
        return this;
    }

    public String getName() {
        return name;
    }

    public AlarmItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getDate() {
        return date;
    }

    public AlarmItem setDate(String date) {
        this.date = date;
        return this;
    }

    public String getProductText() {
        return productText;
    }

    public AlarmItem setProductText(String productText) {
        this.productText = productText;
        return this;
    }

    public String getPlaceText() {
        return placeText;
    }

    public AlarmItem setPlaceText(String placeText) {
        this.placeText = placeText;
        return this;
    }

    public String getProductFeature() {
        return productFeature;
    }

    public AlarmItem setProductFeature(String productFeature) {
        this.productFeature = productFeature;
        return this;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public AlarmItem setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public int getReceivedCount() {
        return receivedCount;
    }

    public AlarmItem setReceivedCount(int receivedCount) {
        this.receivedCount = receivedCount;
        return this;
    }
}
