package com.example.gisulee.lossdog.data.remote;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.example.gisulee.lossdog.view.listenerinterface.OnTaskWorkStateListener;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.NameCode;
import com.example.gisulee.lossdog.data.entity.NameCodePoliceAPI;
import com.example.gisulee.lossdog.data.entity.Request;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class AsyncTaskGetPrdMainCategory extends AsyncTask<Void, Void, Void>  {

    private OnTaskWorkStateListener onTaskWorkStateListener;
    private ArrayList<NameCode> arrayList;
    private Resources resources;
    private String resultCode = Request.KEY_REQUEST_SUCCESS_CODE;
    public AsyncTaskGetPrdMainCategory(Resources res ,ArrayList<NameCode> arrayList) {
        this.resources = res;
        this.arrayList = arrayList;
    }

    public AsyncTaskGetPrdMainCategory setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener){
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if(onTaskWorkStateListener!= null)
            onTaskWorkStateListener.onTaskExecute();

        NameCode item = null;
        String tag;
        String queryUrl="http://apis.data.go.kr/1320000/CmmnCdService/getThngClCd?serviceKey=HUWDE%2FafItJjY%2BE0GvLNrVBH%2FYYibs33wrOk3XtnbqLO7aGXVnhNlaQ1%2FENAgTsobYTI5gmur%2B0tzhJOvPjzAA%3D%3D";

        try {
            /* 네트워크로 xml을 받아 올 시 설정*/
            /*URL url= new URL(queryUrl);
            InputStream inputStream= url.openStream();

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser= factory.newPullParser();
            xmlPullParser.setInput( new InputStreamReader(inputStream, "UTF-8") );*/

            XmlPullParser xmlPullParser = resources.getXml(R.xml.prd_main_code);
            int eventType= xmlPullParser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch( eventType ){
                    case XmlPullParser.START_TAG:

                        tag= xmlPullParser.getName();

                        if(tag.equals("item"))
                            item = new NameCodePoliceAPI();
                        else if(tag.equals("prdtCd"))
                            item.code = xmlPullParser.nextText();
                        else if(tag.equals("prdtNm"))
                            item.name = xmlPullParser.nextText();

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

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(onTaskWorkStateListener!= null)
            onTaskWorkStateListener.onTaskPost(resultCode, arrayList.size());
    }

}
