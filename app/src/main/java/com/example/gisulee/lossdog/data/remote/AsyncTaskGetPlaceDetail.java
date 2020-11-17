package com.example.gisulee.lossdog.data.remote;

import android.os.AsyncTask;
import android.util.Log;

import com.example.gisulee.lossdog.data.entity.PlaceDetail;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class AsyncTaskGetPlaceDetail extends AsyncTask<String, Integer, PlaceDetail> {

    final private String TAG = "AsyncTaskGetPlaceDetail";
    private OnTaskWorkStateListener onTaskWorkStateListener;

    public AsyncTaskGetPlaceDetail setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener){
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @Override
    protected PlaceDetail doInBackground(String... strings) {

        String placeId = strings[0];
        String key = strings[1];
        String queryUrl = "https://maps.googleapis.com/maps/api/place/details/xml?key=" + key +
                "&placeid=" + placeId;
        Log.d(TAG, "doInBackground: queryUrl = " + queryUrl);
        PlaceDetail item = new PlaceDetail("","");
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

                        if(tag.equals("formatted_phone_number")) { // 첫번째 검색결과
                            item.setPhoneNumber(xmlPullParser.nextText());
                        }else if(tag.equals("photo_reference")){
                            item .setPhotoRef(xmlPullParser.nextText());
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xmlPullParser.getName(); //테그 이름 얻어오기
                        break;
                }

                eventType= xmlPullParser.next();
            }

        } catch (Exception e) {

        }


        return item;

    }

    @Override
    protected void onPostExecute(PlaceDetail placeDetail) {
        super.onPostExecute(placeDetail);
        if(onTaskWorkStateListener!= null)
            onTaskWorkStateListener.onTaskPost(placeDetail);
    }

    interface OnTaskWorkStateListener {
        public void onTaskPost(PlaceDetail placeDetail);
    }
}
