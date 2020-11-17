package com.example.gisulee.lossdog.data.remote;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.example.gisulee.lossdog.view.listenerinterface.OnTaskWorkStateListener;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.DepAddress;
import com.example.gisulee.lossdog.data.entity.Request;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class AsyncTaskGetDepAddress extends AsyncTask<Void, Void, Void> {

    private OnTaskWorkStateListener onTaskWorkStateListener;
    private ArrayList<DepAddress> arrayList;
    private Resources resources;
    private String resultCode = Request.KEY_REQUEST_SUCCESS_CODE;
    private String TAG = "AsyncTaskGetDepAddress";

    public AsyncTaskGetDepAddress(Resources res, ArrayList<DepAddress> arrayList) {
        resources = res;
        this.arrayList = arrayList;
    }

    public AsyncTaskGetDepAddress setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener){
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if(onTaskWorkStateListener!= null)
            onTaskWorkStateListener.onTaskExecute();

        DepAddress item = null;
        String tag;
        String queryUrl="http://apis.data.go.kr/1320000/CmmnCdService/getCmmnCd?serviceKey=HUWDE%2FafItJjY%2BE0GvLNrVBH%2FYYibs33wrOk3XtnbqLO7aGXVnhNlaQ1%2FENAgTsobYTI5gmur%2B0tzhJOvPjzAA%3D%3D&GRP_NM=%EC%A7%80%EC%97%AD%EA%B5%AC%EB%B6%84";

        try {
            /* 네트워크로 xml을 받아 올 시 설정 */
           /* URL url= new URL(queryUrl);
            InputStream inputStream= url.openStream();

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser= factory.newPullParser();
            xmlPullParser.setInput( new InputStreamReader(inputStream, "UTF-8") );*/
            XmlPullParser xmlPullParser = resources.getXml(R.xml.dep_addr);

            int eventType= xmlPullParser.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_TAG:

                        tag= xmlPullParser.getName();

                        if(tag.equals("item"))
                            item = new DepAddress();
                        else if(tag.equals("main")){

                            item.name = xmlPullParser.nextText();
                        }
                        else if(tag.equals("police")){
                            item.police = xmlPullParser.nextText();
                        }
                        else if(tag.equals("subpolice")){
                            item.subPolice = xmlPullParser.nextText();
                        }
                        else if(tag.equals("addr")){
                            item.addr = xmlPullParser.nextText();
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


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(onTaskWorkStateListener!= null)
            onTaskWorkStateListener.onTaskPost(resultCode,arrayList.size());
    }
}
