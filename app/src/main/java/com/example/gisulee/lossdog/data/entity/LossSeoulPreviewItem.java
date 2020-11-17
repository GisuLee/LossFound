package com.example.gisulee.lossdog.data.entity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.example.gisulee.lossdog.data.remote.AsyncTaskGetSeoulDetailItem;
import com.example.gisulee.lossdog.view.activity.LossDetailViewActivity;

import java.io.Serializable;

public class LossSeoulPreviewItem extends LossPreviewItem implements Serializable {
    public final static String TAG = "LossSeoulPreviewItem";

    @SuppressLint("LongLogTag")
    @Override

    public void setUI(LossDetailViewActivity activity, Bundle readData) {

        super.setUI(activity, readData);

        new AsyncTaskGetSeoulDetailItem().setOnTaskWorkStateListener(new AsyncTaskGetSeoulDetailItem().new OnTaskWorkStateListener() {
            @Override
            public void onTaskPost(LossPoliceDetailItem item) {
                if (item == null) {
                    Log.d(TAG, "onTaskPost: onTaskPost load Error");
                    setUI(activity,readData);
                    return;
                }
                activity.isLoading = true;
                activity.mLossDetailItem = item;
                if (item != null)
                    activity.setUI(activity.mLossDetailItem);
            }
        }).execute(readData);

    }
}
