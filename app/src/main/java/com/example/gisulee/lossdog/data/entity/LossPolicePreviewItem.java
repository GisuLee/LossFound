package com.example.gisulee.lossdog.data.entity;

import android.os.Bundle;
import android.util.Log;

import com.example.gisulee.lossdog.data.remote.AsyncTaskGetPoliceDetailItem;
import com.example.gisulee.lossdog.view.activity.LossDetailViewActivity;

import java.io.Serializable;

public class LossPolicePreviewItem extends LossPreviewItem implements Serializable, Comparable<LossPreviewItem> {

    public static final String TAG = "LossPreiviewItem";
    public static final String KEY_WRITE_PREVIEW_LIST = "WRITE_PREVIEW_LIST";

    @Override
    public int compareTo(LossPreviewItem lossPolicePreviewItem) {
        if (this.imageURL.equals(lossPolicePreviewItem.imageURL)) {
            return 0;
        } else {
            if (this.imageURL.equals("https://www.lost112.go.kr/lostnfs/images/sub/img02_no_img.gif"))
                return 1;
            else if (lossPolicePreviewItem.imageURL.equals("https://www.lost112.go.kr/lostnfs/images/sub/img02_no_img.gif"))
                return -1;
            else
                return 0;
        }
        // return pickDate.compareTo(lossPolicePreviewItem.pickDate);
    }

    @Override
    public void setUI(LossDetailViewActivity activity, Bundle readData) {

        super.setUI(activity, readData);


        new AsyncTaskGetPoliceDetailItem().setOnTaskWorkStateListener(new AsyncTaskGetPoliceDetailItem().new OnTaskWorkStateListener() {
            @Override
            public void onTaskPost(LossPoliceDetailItem item) {
                if (item == null) {
                    Log.d(TAG, "onTaskPost: onTaskPost load Error");
                    return;
                }
                activity.isLoading = true;
                activity.mLossDetailItem = item;
                activity.setUI(activity.mLossDetailItem);
            }
        }).execute(readData);

       /* activity.new GetXMLTask().setOnTaskWorkStateListener(activity.new OnTaskWorkStateListener() {
            @Override
            void onTaskPost(LossPoliceDetailItem item) {
                if (item == null) {
                    Log.d(TAG, "onTaskPost: onTaskPost load Error");
                    return;
                }
                activity.isLoading = true;
                activity.mLossDetailItem = item;
                activity.setUI(activity.mLossDetailItem);
            }
        }).execute(readData);*/
    }


}
