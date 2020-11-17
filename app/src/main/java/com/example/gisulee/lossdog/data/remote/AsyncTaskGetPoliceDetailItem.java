package com.example.gisulee.lossdog.data.remote;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.gisulee.lossdog.data.entity.LossPoliceDetailItem;
import com.example.gisulee.lossdog.data.entity.Request;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class AsyncTaskGetPoliceDetailItem extends AsyncTask<Bundle, Integer, LossPoliceDetailItem> {

    private OnTaskWorkStateListener onTaskWorkStateListener;

    public AsyncTaskGetPoliceDetailItem setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener) {
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @Override
    protected LossPoliceDetailItem doInBackground(Bundle... bundles) {

        String id = bundles[0].getString(Request.PARAM_ID);
        String sequenceNumber = bundles[0].getString(Request.PARAM_SEQUENCE);
        String queryUrl = "http://apis.data.go.kr/1320000/LosfundInfoInqireService/getLosfundDetailInfo?serviceKey=HUWDE%2FafItJjY%2BE0GvLNrVBH%2FYYibs33wrOk3XtnbqLO7aGXVnhNlaQ1%2FENAgTsobYTI5gmur%2B0tzhJOvPjzAA%3D%3D&ATC_ID=" + id + "&FD_SN=" + sequenceNumber;
        LossPoliceDetailItem item = null;
        try {
            URL url = new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream inputStream = url.openStream(); //url위치로 입력스트림 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new InputStreamReader(inputStream, "UTF-8")); //inputstream 으로부터 xml 입력받기3333
            String tag;


            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:

                        tag = xmlPullParser.getName();//테그 이름 얻어오기

                        if (tag.equals("item")) // 첫번째 검색결과
                            item = new LossPoliceDetailItem();
                        else if (tag.equals("atcId")) {
                            item.id = xmlPullParser.nextText();
                            Log.i("TAG", item.id);
                        } else if (tag.equals("depPlace")) {
                            item.depPlace = xmlPullParser.nextText();
                            Log.i("TAG", item.depPlace);
                        } else if (tag.equals("fdFilePathImg")) {
                            item.imageURL = xmlPullParser.nextText();
                            Log.i("TAG", item.depPlace);
                        } else if (tag.equals("fdHor")) {
                            item.pickTime = xmlPullParser.nextText();
                            Log.i("TAG", item.pickTime);
                        } else if (tag.equals("fdPlace")) {
                            item.pickPlace = xmlPullParser.nextText();
                            Log.i("TAG", item.pickPlace);
                        } else if (tag.equals("fdPrdtNm")) {
                            item.productName = xmlPullParser.nextText();
                            Log.i("TAG", item.productName);
                        } else if (tag.equals("fdSn")) {
                            item.sequenceNumber = xmlPullParser.nextText();
                            Log.i("TAG", item.depPlace);
                        } else if (tag.equals("fdYmd")) {
                            item.pickDate = xmlPullParser.nextText();
                            Log.i("TAG", item.pickDate);
                        } else if (tag.equals("prdtClNm")) {
                            item.productCategory = xmlPullParser.nextText();
                            Log.i("TAG", item.productCategory);
                        } else if (tag.equals("tel")) {
                            item.depPlaceTel = xmlPullParser.nextText();
                            Log.i("TAG", item.depPlaceTel);
                        } else if (tag.equals("uniq")) {
                            item.subject = xmlPullParser.nextText();
                            Log.i("TAG", item.subject);
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xmlPullParser.getName(); //테그 이름 얻어오기

                }
                eventType = xmlPullParser.next();
            }

        } catch (Exception e) {

        }

        return item;
    }

    @Override
    protected void onPostExecute(LossPoliceDetailItem lossDetailItem) {
        super.onPostExecute(lossDetailItem);
        onTaskWorkStateListener.onTaskPost(lossDetailItem);
    }

    abstract public class OnTaskWorkStateListener {
        public abstract void onTaskPost(LossPoliceDetailItem item);
    }

}
