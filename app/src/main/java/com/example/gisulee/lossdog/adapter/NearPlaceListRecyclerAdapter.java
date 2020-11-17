package com.example.gisulee.lossdog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.view.fragment.Backup_NearFinderFragment;
import com.example.gisulee.lossdog.R;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class NearPlaceListRecyclerAdapter extends RecyclerView.Adapter<NearPlaceListRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Marker> markerList = null;
    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @Override
    public int getItemCount() {
        return markerList.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPlaceName;
        TextView txtAddress;
        TextView txtTel;

        ViewHolder(View view) {
            super(view) ;
            txtPlaceName = (TextView) view.findViewById(R.id.txtTitle);
            txtAddress = (TextView) view.findViewById(R.id.txtAddress);
            txtTel = (TextView) view.findViewById(R.id.txtTel);
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

        }
    }

    public NearPlaceListRecyclerAdapter(Context context, List<Marker> markers) {
        this.context = context;
        this.markerList = markers;
    }

    @Override
    public NearPlaceListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.near_place_list_item, parent, false) ;
        NearPlaceListRecyclerAdapter.ViewHolder vh = new NearPlaceListRecyclerAdapter.ViewHolder(view) ;

        return vh ;
    }


    @Override
    public void onBindViewHolder(NearPlaceListRecyclerAdapter.ViewHolder holder, int position) {
        holder.txtPlaceName.setText(markerList.get(position).getTitle());
        holder.txtAddress.setText(markerList.get(position).getSnippet());
        holder.txtTel.setText(Backup_NearFinderFragment.getPlaceTel(
                markerList.get(position).getTitle(),
                markerList.get(position).getId()));

    }

}