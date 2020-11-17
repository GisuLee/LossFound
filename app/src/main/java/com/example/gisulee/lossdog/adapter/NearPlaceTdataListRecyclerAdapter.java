package com.example.gisulee.lossdog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.R;
import com.skt.Tmap.TMapPOIItem;

import java.util.ArrayList;

public class NearPlaceTdataListRecyclerAdapter extends RecyclerView.Adapter<NearPlaceTdataListRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TMapPOIItem> list = null;
    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
        void onButtonClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPlaceName;
        TextView txtAddress;
        TextView txtTel;
        TextView txtCount;
        TextView txtCategory;
        ImageView btnMore;

        ViewHolder(View view) {
            super(view) ;
            txtPlaceName = (TextView) view.findViewById(R.id.txtTitle);
            txtAddress = (TextView) view.findViewById(R.id.txtAddress);
            txtTel = (TextView) view.findViewById(R.id.txtTel);
            txtCount = (TextView) view.findViewById(R.id.txtCount);
            txtCategory = (TextView) view.findViewById(R.id.txtContent);
            btnMore = (ImageView) view.findViewById(R.id.info_icon);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });

            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onButtonClick(v, pos); ;
                        }
                    }
                }
            });
        }
    }

    NearPlaceTdataListRecyclerAdapter(Context context, ArrayList<TMapPOIItem> tMapPOIItems) {
        this.context = context;
        this.list = tMapPOIItems;
    }

    @Override
    public NearPlaceTdataListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.near_place_list_item, parent, false) ;
        NearPlaceTdataListRecyclerAdapter.ViewHolder vh = new NearPlaceTdataListRecyclerAdapter.ViewHolder(view) ;

        return vh ;
    }


    @Override
    public void onBindViewHolder(NearPlaceTdataListRecyclerAdapter.ViewHolder holder, int position) {
        holder.txtPlaceName.setText(list.get(position).getPOIName());
        holder.txtAddress.setText(list.get(position).address);
        holder.txtTel.setText(list.get(position).telNo);
        holder.txtCount.setText("("  + list.get(position).distance + "KM)");
        holder.txtCategory.setText(list.get(position).middleBizName + " > " + list.get(position).lowerBizName);
    }

}