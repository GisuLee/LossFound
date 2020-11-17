package com.example.gisulee.lossdog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gisulee.lossdog.data.entity.NameCode;
import com.example.gisulee.lossdog.R;

import java.util.ArrayList;

public class  SpinnerAdapter extends BaseAdapter {

    int selectedIndex;
    Context context;
    ArrayList<NameCode> data;
    LayoutInflater inflater;
    TextView textView;
    ImageView imgCheck;

    public SpinnerAdapter(Context context, ArrayList<NameCode> data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(data!=null) return data.size();
        else return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.spinner_normal, parent, false);
        }

        if(data!=null){
            String text = data.get(position).name;
            ((TextView)convertView.findViewById(R.id.spinnerText)).setText(text);
        }
        selectedIndex = position;
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.spinner_dropdown, parent, false);
        }

        //데이터세팅
        String text = data.get(position).name;
        textView = ((TextView)convertView.findViewById(R.id.spinnerText));
        imgCheck = (ImageView) convertView.findViewById(R.id.img_check) ;
        textView.setTextColor(context.getResources().getColor(R.color.black));
        textView.setText(text);
        imgCheck.setVisibility(View.INVISIBLE);


        if(selectedIndex == position){
            textView.setTextColor(context.getResources().getColor(R.color.blue));
            imgCheck.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
