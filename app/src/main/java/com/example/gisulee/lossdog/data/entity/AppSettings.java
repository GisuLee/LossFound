package com.example.gisulee.lossdog.data.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class AppSettings   implements Serializable {
    public static final String KEY_APP_SETTINGS = "APP_SETTINGS";
    private ArrayList<NameCode> selectedAreaList;
    private ArrayList<NameCode> selectedPrdList;
    private String beginDate;
    private String endDate;

    public AppSettings(){

    }

    public AppSettings(AppSettings temp) {
        this.selectedAreaList = temp.selectedAreaList;
        this.selectedPrdList = temp.selectedPrdList;
    }

    public ArrayList<NameCode> getSelectedAreaList() {
        return selectedAreaList;
    }

    public AppSettings setSelectedAreaList(ArrayList<NameCode> selectedAreaList) {
        this.selectedAreaList = selectedAreaList;
        return this;
    }

    public ArrayList<NameCode> getSelectedPrdList() {
        return selectedPrdList;
    }

    public AppSettings setSelectedPrdList(ArrayList<NameCode> selectedPrdList) {
        this.selectedPrdList = selectedPrdList;
        return this;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public AppSettings setBeginDate(String beginDate) {
        this.beginDate = beginDate;
        return this;
    }

    public String getEndDate() {
        return endDate;
    }

    public AppSettings setEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }
}
