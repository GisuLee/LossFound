package com.example.gisulee.lossdog.data.remote;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.example.gisulee.lossdog.view.listenerinterface.OnTaskWorkStateListener;
import com.example.gisulee.lossdog.data.entity.NameCode;
import com.example.gisulee.lossdog.data.entity.NameCodePoliceAPI;
import com.example.gisulee.lossdog.data.entity.Request;

import java.util.ArrayList;

public class AsyncTaskGetSubAreaCategory extends AsyncTask<String, Void, ArrayList<NameCode>> {

    private static final String TAG = "tqtq";
    private OnTaskWorkStateListener onTaskWorkStateListener;
    private ArrayList<NameCode> arrayList;
    private Resources resources;
    private String resultCode = Request.KEY_REQUEST_SUCCESS_CODE;
    public AsyncTaskGetSubAreaCategory(ArrayList<NameCode> arrayList) {
        this.arrayList = arrayList;
    }

    public AsyncTaskGetSubAreaCategory setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener){
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @Override
    protected ArrayList<NameCode> doInBackground(String... strings) {

        if(onTaskWorkStateListener!= null)
            onTaskWorkStateListener.onTaskExecute();

        String mainAreaCode = strings[0];
        ArrayList<NameCode> subAreaList = new ArrayList();
        subAreaList.add(new NameCodePoliceAPI("전체",""));

        for(int i=0; i<arrayList.size(); i++){
            String areaCode = arrayList.get(i).code;
            if(mainAreaCode.equals(areaCode)) continue;
            if(areaCode.startsWith(mainAreaCode.substring(0,3))){
                subAreaList.add(new NameCodePoliceAPI(arrayList.get(i).name,areaCode));
            }
        }

        return subAreaList;
    }

    @Override
    protected void onPostExecute(ArrayList<NameCode> a) {
        super.onPostExecute(a);
        if(onTaskWorkStateListener!= null)
            onTaskWorkStateListener.onTaskPost(resultCode, arrayList.size());
    }
}
