package com.example.gisulee.lossdog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.data.entity.AlarmItem;
import com.example.gisulee.lossdog.R;

import java.util.ArrayList;

public class AlarmListRecyclerAdapter extends RecyclerView.Adapter<AlarmListRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AlarmItem> arrayList = null;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);

        void onSwitchClick(View view, boolean b, int pos);
    }

    public void setOnItemClickListener(AlarmListRecyclerAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView txtAlarmName;
        TextView txtDate;
        TextView txtLossProductName;
        TextView txtLossPlace;
        SwitchCompat switchAlarm;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            txtAlarmName = (TextView) view.findViewById(R.id.txtAlarmName);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            txtLossProductName = (TextView) view.findViewById(R.id.txtLossProductName);
            txtLossPlace = (TextView) view.findViewById(R.id.txtLossPlace);
            switchAlarm = view.findViewById(R.id.switch_alarm);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
/*
            switchAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    boolean b = (view.findViewById(R.id.switch_alarm).isSelected());
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onSwitchClick(view,b, pos); ;
                        }
                    }

                }
            });*/

            switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            if (compoundButton.isPressed())
                                mListener.onSwitchClick(view, b, pos);
                            ;
                        }
                    }
                }
            });


        }

        public View getView() {
            return view;
        }

    }

    AlarmListRecyclerAdapter(Context context, ArrayList<AlarmItem> list1) {
        this.context = context;
        this.arrayList = list1;
    }

    @Override
    public AlarmListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alarm_list_item, parent, false);
        AlarmListRecyclerAdapter.ViewHolder vh = new AlarmListRecyclerAdapter.ViewHolder(view);

        return vh;
    }


    @Override
    public void onBindViewHolder(AlarmListRecyclerAdapter.ViewHolder holder, int position) {
        boolean enabled = arrayList.get(position).getAlarmOn();

        holder.txtAlarmName.setEnabled(enabled);
        holder.txtDate.setEnabled(enabled);
        holder.txtLossProductName.setEnabled(enabled);
        holder.txtLossPlace.setEnabled(enabled);

        /*holder.txtAlarmName.setAlpha(enabled ? 1: 0.5f);
        holder.txtDate.setAlpha(enabled ? 1: 0.5f);
        holder.txtLossProductName.setAlpha(enabled ? 1: 0.5f);
        holder.txtLossPlace.setAlpha(enabled ? 1: 0.5f);*/
        int color = (enabled ? context.getResources().getColor(R.color.textColorAccent) : context.getResources().getColor(R.color.middle_gray));
        holder.txtAlarmName.setTextColor(color);
        holder.txtDate.setTextColor(color);
        holder.txtLossProductName.setTextColor(color);
        holder.txtLossPlace.setTextColor(color);

        holder.txtAlarmName.setText(arrayList.get(position).getName());
        holder.txtDate.setText(arrayList.get(position).getDate());
        holder.txtLossProductName.setText(arrayList.get(position).getProductText());
        holder.txtLossPlace.setText(arrayList.get(position).getPlaceText());
        holder.switchAlarm.setChecked(arrayList.get(position).getAlarmOn());
    }

}