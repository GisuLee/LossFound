package com.example.gisulee.lossdog.adapter.viewholder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.R;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

public class UnifiedNativeAdViewHolder  extends RecyclerView.ViewHolder {

    private UnifiedNativeAdView adView;

    public UnifiedNativeAdView getAdView() {
        return adView;
    }

    public UnifiedNativeAdViewHolder(View view) {
        super(view);
        adView = (UnifiedNativeAdView) view.findViewById(R.id.ad_view);

        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));        //2. 이미지

        // Register the view used for each individual asset.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));              //1. 광고제목
        adView.setBodyView(adView.findViewById(R.id.ad_body));                      //3. 본문
        //adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));    //5. 클릭 유도 문안
        //adView.setIconView(adView.findViewById(R.id.ad_icon));                      //4. 아이콘
        //adView.setPriceView(adView.findViewById(R.id.ad_price));
        //adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        //adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

    }
}