package com.example.gisulee.lossdog.data.entity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.example.gisulee.lossdog.data.remote.AsyncTaskGetBusanSubwayDetailItem;
import com.example.gisulee.lossdog.view.activity.LossDetailViewActivity;

import java.io.Serializable;

public class LossBusanSubwayPreviewItem extends LossPreviewItem implements Serializable {
    public final static String TAG = "LossBusanSubwayPreviewItem";
    @SuppressLint("LongLogTag")
    @Override

    public void setUI(LossDetailViewActivity activity, Bundle readData){

        super.setUI(activity,readData);


        new AsyncTaskGetBusanSubwayDetailItem().setOnTaskWorkStateListener(new AsyncTaskGetBusanSubwayDetailItem().new OnTaskWorkStateListener() {
            @Override
            public void onTaskPost(LossPoliceDetailItem item) {
                if (item == null) {
                    Log.d(TAG, "onTaskPost: onTaskPost load Error");
                    setUI(activity,readData);
                    return;
                }
                activity.isLoading = true;
                activity.mLossDetailItem = item;
                if(item !=null)
                    activity.setUI(activity.mLossDetailItem);
            }
        }).execute(readData);

    }
}
