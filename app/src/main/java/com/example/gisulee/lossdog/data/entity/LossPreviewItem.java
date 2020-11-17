package com.example.gisulee.lossdog.data.entity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.CallSuper;

import com.example.gisulee.lossdog.common.DataManager;
import com.example.gisulee.lossdog.view.activity.LossDetailViewActivity;

import java.io.Serializable;

public class LossPreviewItem implements Serializable {
    public String id;
    public String sequenceNumber;
    public String productName;
    public String productCategory;
    public String depPlace;
    public String pickDate;
    public String imageURL;
    public String fdSbjt;
    public String areaSubCode;

    public Object clone() {
        Object obj = null;
        try {
            obj = super.clone();
        } catch (Exception e) {
        }
        return obj;
    }

    @CallSuper
    public void setUI(LossDetailViewActivity activity, Bundle readData){

        if (readData == null) {
            Log.d("LossPreviewItem", "onCreate: intent 전달 에러");
            return;
        }

        Bitmap image = DataManager.getInstance().getBitmap();
        if (image != null)
            activity.mImageView.setImageBitmap(image);

        activity.mContent.setVisibility(View.GONE);
    };
}
