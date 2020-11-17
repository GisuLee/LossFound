package com.example.gisulee.lossdog.common;

import android.content.Context;
import android.util.Log;

import com.example.gisulee.lossdog.adapter.viewholder.IntegerHolder;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.adapter.LossPreviewListRecyclerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.android.gms.ads.formats.NativeAdOptions.ADCHOICES_BOTTOM_RIGHT;

public class AdsManager {

    // The number of native ads to load.
    public static boolean ADVERTIZE_MODE = false;
    public static final int NUMBER_OF_ADS = 5;
    public static final int OFFSET = 20;
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private final String TAG = "AdsManager";

    // The AdLoader used to load ads.
    private AdLoader adLoader;

    // List of native ads that have been successfully loaded.
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    private Context mContext;

    private AdsManager() {
    }

    private static class ClassHolder {
        public static final AdsManager Instance = new AdsManager();
    }

    public void init(Context context) {
        mContext = context;
    }

    public static AdsManager getInstance() {
        return AdsManager.ClassHolder.Instance;
    }


    private void insertAdsInMenuItems(LossPreviewListRecyclerAdapter adapter) {
        if (mNativeAds.size() <= 0) {
            return;
        }

        int offset = (adapter.getItemCount() / mNativeAds.size()) + 1;
        int index = offset;
        for (UnifiedNativeAd ad : mNativeAds) {
            Log.d(TAG, "insertAdsInMenuItems: index : " + index);
            if(index > adapter.getItemCount()){
                adapter.add(ad, adapter.getItemCount());
                return;
            }
            adapter.add(ad,index);
            index = index + offset;
        }

    }


    private int insertAdsInMenuItems(LossPreviewListRecyclerAdapter adapter, int curIndex, int numOfAds) {
        if (mNativeAds.size() <= 0) {
            return curIndex;
        }

        int offset = OFFSET;
        int index = curIndex + offset;

        for (int i = mNativeAds.size() - 1; i >= mNativeAds.size() - numOfAds; i--) {
            Log.d(TAG, "insertAdsInMenuItems: index : " + index + "i=" + i);
            if(index > adapter.getItemCount()) {
                index = adapter.getItemCount();
                i = mNativeAds.size() - numOfAds -1;
            }
            UnifiedNativeAd ad = mNativeAds.get(i);
            adapter.add(ad,index);
            index = index + offset;

        }
        return index - offset;
    }

    public void stopExecutor(){
        executor.shutdownNow();
    }

    public void insertLoadNativeAds(LossPreviewListRecyclerAdapter adapter) {

        if(executor.isShutdown())
            executor = Executors.newSingleThreadExecutor();

        executor.submit(new Runnable() {
            @Override
            public void run() {
                int cnt = 0;
                cnt = adapter.getItemCount() / 5;
                if(cnt == 0) cnt = 1;
                if(cnt > 5) cnt = 5;

                mNativeAds.clear();
                Log.d(TAG, "insertLoadNativeAds: ");
                AdLoader.Builder builder = new AdLoader.Builder(mContext, mContext.getString(R.string.banner_ad_unit_id_for_test));
                adLoader = builder.forUnifiedNativeAd(
                        new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                // A native ad loaded successfully, check if the ad loader has finished loading
                                // and if so, insert the ads into the list.
                                mNativeAds.add(unifiedNativeAd);
                                if (!adLoader.isLoading()) {
                                    insertAdsInMenuItems(adapter);
                                }
                            }
                        }).withAdListener(
                        new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                // A native ad failed to load, check if the ad loader has finished loading
                                // and if so, insert the ads into the list.
                                Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                        + " load another.");
                                if (!adLoader.isLoading()) {
                                    insertAdsInMenuItems(adapter);
                                }
                            }
                        })
                        .withNativeAdOptions(new NativeAdOptions.Builder()
                                .setAdChoicesPlacement(ADCHOICES_BOTTOM_RIGHT)
                                .build())
                        .build();

                // Load the Native ads.
                adLoader.loadAds(new AdRequest.Builder().build(), cnt);
            }
        });
    }


    public void insertLoadNativeAds(LossPreviewListRecyclerAdapter adapter, int curIndex, int numOfAds) {

        if(executor.isShutdown())
            executor = Executors.newSingleThreadExecutor();

        executor.submit(new Runnable() {
            @Override
            public void run() {
                mNativeAds.clear();
                int cnt = 0;
                cnt = adapter.getItemCount() / numOfAds;
                if(cnt == 0) cnt = 1;
                if(cnt > 5) cnt = 5;

                Log.d(TAG, "insertLoadNativeAds: ");
                AdLoader.Builder builder = new AdLoader.Builder(mContext, mContext.getString(R.string.banner_ad_unit_id_for_test));
                adLoader = builder.forUnifiedNativeAd(
                        new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                // A native ad loaded successfully, check if the ad loader has finished loading
                                // and if so, insert the ads into the list.
                                mNativeAds.add(unifiedNativeAd);
                                if (!adLoader.isLoading()) {
                                    insertAdsInMenuItems(adapter, curIndex, numOfAds);
                                }

                            }
                        }).withAdListener(
                        new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                // A native ad failed to load, check if the ad loader has finished loading
                                // and if so, insert the ads into the list.
                                Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                        + " load another.");
                                if (!adLoader.isLoading()) {
                                    insertAdsInMenuItems(adapter, curIndex, numOfAds);
                                }
                            }
                        })
                        .withNativeAdOptions(new NativeAdOptions.Builder()
                                .setAdChoicesPlacement(ADCHOICES_BOTTOM_RIGHT)
                                .build())
                        .build();

                // Load the Native ads.
                adLoader.loadAds(new AdRequest.Builder().build(), cnt);
            }
        });
    }

    public void adsLoad(LossPreviewListRecyclerAdapter adapter, IntegerHolder mLargestLastPostion, int numOfAds) {

        if (AdsManager.ADVERTIZE_MODE) {

            AdsManager adsManager = AdsManager.getInstance();

            if (adapter.getItemCount() >= numOfAds * AdsManager.OFFSET) {
                adsManager.insertLoadNativeAds(adapter, mLargestLastPostion.value, numOfAds);
                mLargestLastPostion.value =  mLargestLastPostion.value + (numOfAds * AdsManager.OFFSET);
            } else {
                adsManager.insertLoadNativeAds(adapter);
                mLargestLastPostion.value =  mLargestLastPostion.value + (numOfAds * AdsManager.OFFSET);
            }
        }
    }
}
