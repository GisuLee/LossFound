package com.example.gisulee.lossdog.common;

import android.graphics.Bitmap;

import com.example.gisulee.lossdog.data.entity.LossPoliceDetailItem;

import java.util.ArrayList;

public class DataManager {

    private Bitmap bitmap;
    private ArrayList<Object> lossPreviewItems;
    private ArrayList<LossPoliceDetailItem> lossDetailItems = new ArrayList();

    private DataManager(){
    }

    private static class ClassHolder {
        public static final DataManager Instance = new DataManager();
    }

    public static DataManager getInstance(){
        return ClassHolder.Instance;
    }

    public ArrayList<LossPoliceDetailItem> getLossDetailItems() {
        return lossDetailItems;
    }

    public ArrayList<Object> getLossPreviewItems(){
        return lossPreviewItems;
    }

    public void clearLossPreviewItems(){
        lossPreviewItems.clear();
    }

    public void clearLossDetailItems(){
        lossDetailItems.clear();
    }

    public void setLossPreviewItems(ArrayList<Object> list){
        this.lossPreviewItems = list;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public DataManager setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        return this;
    }

    public DataManager setLossDetailItems(ArrayList<LossPoliceDetailItem> lossDetailItems) {
        this.lossDetailItems = lossDetailItems;
        return this;
    }
}
