package com.example.gisulee.lossdog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.data.entity.NotificationItem;
import com.example.gisulee.lossdog.R;

import java.util.ArrayList;

public class NotificationItemRecyclerAdapter extends RecyclerView.Adapter<NotificationItemRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NotificationItem> list = null;
    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout background;
        TextView txtTitle;
        TextView txtContent;
        TextView txtDate;
        TextView txtNew;
        ImageView imgInfoIcon;

        ViewHolder(View view) {
            super(view) ;
            background = view.findViewById(R.id.notification_background);
            txtTitle = view.findViewById(R.id.txtTitle);
            txtContent = view.findViewById(R.id.txtContent);
            txtDate = view.findViewById(R.id.txtDate);
            txtNew = view.findViewById(R.id.img_new);
            imgInfoIcon = view.findViewById(R.id.info_icon);

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

    public NotificationItemRecyclerAdapter(Context context, ArrayList<NotificationItem> notificationItems) {
        this.context = context;
        this.list = notificationItems;
    }

    @Override
    public NotificationItemRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.notification_item, parent, false) ;
        NotificationItemRecyclerAdapter.ViewHolder vh = new NotificationItemRecyclerAdapter.ViewHolder(view) ;

        return vh ;
    }


    @Override
    public void onBindViewHolder(NotificationItemRecyclerAdapter.ViewHolder holder, int position) {
        holder.txtTitle.setText(list.get(position).getTitle());
        holder.txtContent.setText(list.get(position).getContent());
        holder.txtDate.setText(list.get(position).getStrTime());
        if(list.get(position).isReading()){
            holder.background.setBackgroundResource(R.color.frameBackgroud);
            holder.txtNew.setVisibility(View.GONE);
        }else{
            holder.background.setBackgroundResource(R.color.ligh_blue);
            holder.txtNew.setVisibility(View.VISIBLE);
        }

        if(list.get(position).getType() == NotificationItem.TYPE.ALARM){
            holder.imgInfoIcon.setImageDrawable(context.getDrawable(R.drawable.icon_chat));
        }else if(list.get(position).getType() == NotificationItem.TYPE.DETAIL_ITEM){
            holder.imgInfoIcon.setImageDrawable(context.getDrawable(R.drawable.icon_file_down));
        }else{
            holder.imgInfoIcon.setImageDrawable(context.getDrawable(R.drawable.icon_chat));
        }
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(NotificationItem item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<NotificationItem> getData(){
        return list;
    }
}