package com.example.gisulee.lossdog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gisulee.lossdog.data.entity.LossPoliceDetailItem;
import com.example.gisulee.lossdog.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LossDetailViewListAdapter extends BaseAdapter {
    private LossDetailViewHolder viewHolder;
    private LayoutInflater inflater = null;
    private ArrayList<LossPoliceDetailItem> items = null;
    private int listCount = 0;

    public LossDetailViewListAdapter(ArrayList<LossPoliceDetailItem> data) {
        items = data;
        listCount = items.size();
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        if(converView == null){
            final Context context = parent.getContext();
            if(inflater == null){
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            converView = inflater.inflate(R.layout.loss_detail_view_item, parent, false);

            viewHolder = new LossDetailViewHolder();
            viewHolder.txtProductName = (TextView) converView.findViewById(R.id.txtProductFeature);
            viewHolder.txtProductCategory = (TextView) converView.findViewById(R.id.txtProductCategory);
            viewHolder.txtDepPlace = (TextView) converView.findViewById(R.id.txtDepPlace);
            viewHolder.txtPickPlace = (TextView) converView.findViewById(R.id.txtPickPlace);
            viewHolder.txtSubject = (TextView) converView.findViewById(R.id.txtSubject);
            viewHolder.lossImageView = (ImageView) converView.findViewById(R.id.lostImageView);
            converView.setTag(viewHolder);
        }else {
            viewHolder = (LossDetailViewHolder) converView.getTag();
        }
        String strPickPlace = items.get(position).pickPlace + " (" + items.get(position).pickDate + " " + items.get(position).pickTime + "시 경)";
        viewHolder.txtProductName.setText(items.get(position).productName);
        viewHolder.txtProductCategory.setText(items.get(position).productCategory);
        viewHolder.txtDepPlace.setText(items.get(position).depPlace);
        viewHolder.txtPickPlace.setText(strPickPlace);
        viewHolder.txtSubject.setText(items.get(position).id);
        Picasso.with(parent.getContext()).load(items.get(position).imageURL).into(viewHolder.lossImageView);
        return converView;
    }

}

class LossDetailViewHolder {
    ImageView lossImageView;
    TextView txtSubject;
    TextView txtProductName;
    TextView txtProductCategory;
    TextView txtPickPlace;
    TextView txtDepPlace;
    TextView txtDepPlaceTel;
}