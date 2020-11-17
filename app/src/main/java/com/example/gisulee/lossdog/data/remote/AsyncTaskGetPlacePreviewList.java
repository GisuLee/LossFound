package com.example.gisulee.lossdog.data.remote;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.gisulee.lossdog.view.listenerinterface.OnTaskWorkStateListener;
import com.example.gisulee.lossdog.data.entity.LossPolicePreviewItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class AsyncTaskGetPlacePreviewList extends AsyncTask<Bundle, Integer, Integer> {

    final private String TAG = "AsyncTaskGetPlaceList";
    private OnTaskWorkStateListener onTaskWorkStateListener;
    private ArrayList<Object> arrayList;
    private String resultCode;

    public AsyncTaskGetPlacePreviewList(ArrayList<Object> arrayList) {
        this.arrayList = arrayList;
    }

    public AsyncTaskGetPlacePreviewList setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener){
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @Override
    protected Integer doInBackground(Bundle... bundles) {

        if(onTaskWorkStateListener!= null)
            onTaskWorkStateListener.onTaskExecute();

        int totalCount = 0 ;
        String prdName = bundles[0].getString("PrdName");
        String depPlace = bundles[0].getString("DepPlace");
        String pageNum = bundles[0].getString("Page");
        String rows = bundles[0].getString("Rows");
        String queryUrl="http://apis.data.go.kr/1320000/LosfundInfoInqireService/getLosfundInfoAccTpNmCstdyPlace?serviceKey=HUWDE%2FafItJjY%2BE0GvLNrVBH%2FYYibs33wrOk3XtnbqLO7aGXVnhNlaQ1%2FENAgTsobYTI5gmur%2B0tzhJOvPjzAA%3D%3D&" +
                "PRDT_NM=" + prdName +
                "&DEP_PLACE=" + depPlace +
                "&pageNo=" + pageNum +
                "&numOfRows=" + rows;

        Log.i(TAG, "doInBackground: prdName-" + prdName);
        Log.i(TAG, "doInBackground: depPlace-" + depPlace);
        Log.i(TAG, "doInBackground: pageNum-" + pageNum);
        Log.i(TAG, "doInBackground: rows-" + pageNum);
        Log.i(TAG, "doInBackground: URL-" + queryUrl);

        LossPolicePreviewItem item = null;
        String tag;

        try {
            URL url= new URL(queryUrl);
            InputStream inputStream= url.openStream();

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser= factory.newPullParser();
            xmlPullParser.setInput( new InputStreamReader(inputStream, "UTF-8") );

            int eventType= xmlPullParser.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT ){
                if(isCancelled()) break;
                switch( eventType ){
                    case XmlPullParser.START_TAG:

                        tag= xmlPullParser.getName();//테그 이름 얻어오기
                        if(tag.equals("resultCode")){
                            resultCode = xmlPullParser.nextText();
                        }
                        else if(tag.equals("item")) // 첫번째 검색결과
                            item = new LossPolicePreviewItem();
                        else if(tag.equals("atcId")){
                            item.id = xmlPullParser.nextText();
                        }
                        else if(tag.equals("depPlace")){
                            item.depPlace = xmlPullParser.nextText();
                        }
                        else if(tag.equals("fdFilePathImg")){
                            item.imageURL = xmlPullParser.nextText();
                        }
                        else if(tag.equals("fdPrdtNm")){
                            item.productName = xmlPullParser.nextText();
                        }
                        else if(tag.equals("fdSbjt")){
                            item.fdSbjt = xmlPullParser.nextText();
                        }
                        else if(tag.equals("fdSn")){
                            item.sequenceNumber = xmlPullParser.nextText();
                        }
                        else if(tag.equals("fdYmd")){
                            item.pickDate = xmlPullParser.nextText();
                        }
                        else if(tag.equals("prdtClNm")){
                            item.productCategory = xmlPullParser.nextText();
                        }
                        else if(tag.equals("totalCount")){
                            totalCount = Integer.parseInt(xmlPullParser.nextText());
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xmlPullParser.getName(); //테그 이름 얻어오기

                        if(tag.equals("item"))
                            arrayList.add(item);
                        break;
                }

                eventType= xmlPullParser.next();
            }

        } catch (Exception e) {

        }


        return totalCount;

    }

    @Override
    protected void onPostExecute(Integer totalCount) {
        super.onPostExecute(totalCount);
        if(onTaskWorkStateListener!= null)
            onTaskWorkStateListener.onTaskPost(resultCode,totalCount);
    }
}
