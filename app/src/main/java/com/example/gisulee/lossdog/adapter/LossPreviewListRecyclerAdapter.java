package com.example.gisulee.lossdog.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.data.entity.LossPolicePreviewItem;
import com.example.gisulee.lossdog.data.entity.LossPreviewItem;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.adapter.viewholder.UnifiedNativeAdViewHolder;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LossPreviewListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_VIEW_TYPE_BASIC = 10;
    private final int ITEM_VIEW_TYPE_FOOTER = 11;

    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    // The unified native ad view type.
    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

    private Context context;
    private ArrayList<Object> arrayList = null;
    private OnItemClickListener mListener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ProgressHolder extends RecyclerView.ViewHolder{
        LinearLayout container;

        public ProgressHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName;
        TextView txtProductCategory;
        TextView txtDepPlace;
        TextView txtDate;
        ImageView imgProduct;

        ViewHolder(View view) {
            super(view);
            txtProductName = (TextView) view.findViewById(R.id.txtProductFeature);
            txtProductCategory = (TextView) view.findViewById(R.id.txtProductCategory);
            txtDepPlace = (TextView) view.findViewById(R.id.txtDepPlace);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            imgProduct = (ImageView) view.findViewById(R.id.lostImageView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });

        }
    }

    public LossPreviewListRecyclerAdapter(Context context, ArrayList<Object> list1) {
        this.context = context;
        this.arrayList = list1;
        this.registerAdapterDataObserver(new Observer());
    }

    LossPreviewListRecyclerAdapter(Context context, ArrayList<Object> list1, boolean isRegObserver) {
        this.context = context;
        this.arrayList = list1;
        if(isRegObserver)
            this.registerAdapterDataObserver(new Observer());
    }

    @Override
    public int getItemViewType(int position) {
        if( arrayList.get(position) == null) {
            return ITEM_VIEW_TYPE_FOOTER;
        }
        Object recyclerViewItem = arrayList.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return MENU_ITEM_VIEW_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RecyclerView.ViewHolder vh;

        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = inflater.inflate(R.layout.ad_unified, parent, false);
                vh = new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
                break;

            case ITEM_VIEW_TYPE_FOOTER:
                View progessView = inflater.inflate(R.layout.progress_layout, parent, false);
                vh =  new ViewHolder(progessView);
                break;

            case MENU_ITEM_VIEW_TYPE:
                // Fall through.


            default:
                View menuItemLayoutView = inflater.inflate(R.layout.preview_list_item, parent, false);
                vh = new ViewHolder(menuItemLayoutView);
                break;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewholder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_VIEW_TYPE_FOOTER:
                break;
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) arrayList.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) viewholder).getAdView());
                break;
            case MENU_ITEM_VIEW_TYPE:
                // fall through

            default:
                ViewHolder holder = (ViewHolder) viewholder;
                LossPreviewItem lossItem = (LossPreviewItem) arrayList.get(position);

                if (lossItem.fdSbjt == null) {
                    holder.txtProductName.setVisibility(View.GONE);
                } else {
                    holder.txtProductName.setVisibility(View.VISIBLE);
                }

                if (lossItem.productCategory == null) {
                    holder.txtProductCategory.setVisibility(View.GONE);
                } else {
                    holder.txtProductCategory.setVisibility(View.VISIBLE);
                }

                holder.txtProductName.setText(lossItem.fdSbjt);
                holder.txtProductCategory.setText(lossItem.productCategory);
                holder.txtDepPlace.setText(lossItem.depPlace);
                holder.txtDate.setText(lossItem.pickDate);
                if (lossItem.imageURL.equals("https://www.lost112.go.kr/lostnfs/images/sub/img02_no_img.gif")) {
                    ViewGroup.LayoutParams layoutParams = holder.imgProduct.getLayoutParams();
                    layoutParams.width = layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, context.getResources().getDisplayMetrics());
                    holder.imgProduct.setLayoutParams(layoutParams);
                    holder.imgProduct.requestLayout();
                    Picasso.with(context).load(R.drawable.img_noimg).into(holder.imgProduct);
                } else {
                    ViewGroup.LayoutParams layoutParams = holder.imgProduct.getLayoutParams();
                    layoutParams.width = layoutParams.MATCH_PARENT;
                    layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, context.getResources().getDisplayMetrics());
                    holder.imgProduct.setLayoutParams(layoutParams);
                    holder.imgProduct.requestLayout();
                    Picasso.with(context).load(lossItem.imageURL).into(holder.imgProduct);
                }
        }

    }


    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
       /* ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());*/

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        //NativeAd.Image icon = nativeAd.getIcon();

        // 미디어(이미지)뷰 ScaleType 설정
        adView.getMediaView().setImageScaleType(ImageView.ScaleType.FIT_XY);
/*

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }*/

 /*       if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }*/

      /*  if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }*/

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    public void add(Object item, int position) {
        arrayList.add(position, item);
        notifyItemInserted(position);
    }

    class Observer extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            if (arrayList != null)
                Collections.sort(arrayList, new Comparator<Object>() {
                    @Override
                    public int compare(Object o, Object t1) {

                        if (o instanceof LossPolicePreviewItem && t1 instanceof LossPolicePreviewItem) {

                            LossPreviewItem c1 = (LossPreviewItem) o;
                            LossPreviewItem c2 = (LossPreviewItem) t1;
                            if (c1.imageURL.equals(c2.imageURL)) {
                                return 0;
                            } else {
                                if (c1.imageURL.equals("https://www.lost112.go.kr/lostnfs/images/sub/img02_no_img.gif"))
                                    return 1;
                                else if (c2.imageURL.equals("https://www.lost112.go.kr/lostnfs/images/sub/img02_no_img.gif"))
                                    return -1;
                                else
                                    return 0;
                            }

                        }else
                            return 0;
                    }
                });
            super.onChanged();
        }
    }
}