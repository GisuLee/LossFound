package com.example.gisulee.lossdog.data.remote;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.gisulee.lossdog.view.listenerinterface.OnTaskWorkStateListener;
import com.example.gisulee.lossdog.common.AreaUtil;
import com.example.gisulee.lossdog.data.entity.LossPolicePreviewItem;
import com.example.gisulee.lossdog.data.entity.Request;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class AsyncTaskGetPreviewListPoliceAPI extends AsyncTask<Bundle, Integer, Integer> {

    final private String TAG = "AsyncTaskGetPreviewListPoliceAPI";
    private OnTaskWorkStateListener onTaskWorkStateListener;
    private ArrayList<Object> arrayList;
    private String resultCode;

    public AsyncTaskGetPreviewListPoliceAPI(ArrayList<Object> arrayList) {
        this.arrayList = arrayList;
    }

    public AsyncTaskGetPreviewListPoliceAPI setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener) {
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected Integer doInBackground(Bundle... bundles) {

        if (onTaskWorkStateListener != null)
            onTaskWorkStateListener.onTaskExecute();

        boolean isMainArea;
        int totalCount = 0;
        int addCount = 0;
        Bundle bundle = bundles[0];
        ArrayList<LossPolicePreviewItem> addArrays = new ArrayList();

        String orgAreaCode = bundle.getString(Request.PARAM_AREA_CODE);
        String parseAreaCode;
        if (orgAreaCode.contains("000") || orgAreaCode.equals("")) {
            isMainArea = true;
            parseAreaCode = orgAreaCode;
        } else {
            isMainArea = false;
            parseAreaCode = AreaUtil.getInstance().getMainAreaCode(orgAreaCode);
        }
        String prdMainCode = bundle.getString(Request.PARAM_PRD_MAIN_CATEGORY);
        String prdSubCode = bundle.getString(Request.PARAM_PRD_SUB_CATEGORY);
        String pageNum = bundle.getString(Request.PARAM_PAGE);
        String beginDate = bundle.getString(Request.PARAM_BEGIN_DATE);
        String endDate = bundle.getString(Request.PARAM_END_DATE);
        String rows = bundle.getString(Request.PARAM_ROWS);
        String queryUrl = "http://apis.data.go.kr/1320000/LosfundInfoInqireService/getLosfundInfoAccToClAreaPd?" +
                "serviceKey=HUWDE%2FafItJjY%2BE0GvLNrVBH%2FYYibs33wrOk3XtnbqLO7aGXVnhNlaQ1%2FENAgTsobYTI5gmur%2B0tzhJOvPjzAA%3D%3D" +
                "&PRDT_CL_CD_01=" + prdMainCode +   //습득물 대분류
                "&PRDT_CL_CD_02=" + prdSubCode +    //습득물 소분류
                "&FD_COL_CD=" +                     //습득물 컬러
                "&START_YMD=" + beginDate +         //시작일
                "&END_YMD=" + endDate +             //종료일
                "&N_FD_LCT_CD=" + parseAreaCode +        //지역검색
                "&pageNo=" + pageNum +
                "&numOfRows=" + rows;

        Log.i(TAG, "doInBackground: areaCode-" + parseAreaCode);
        Log.i(TAG, "doInBackground: prdMainCode-" + prdMainCode);
        Log.i(TAG, "doInBackground: prdSubCode-" + prdSubCode);
        Log.i(TAG, "doInBackground: pageNum-" + pageNum);
        Log.i(TAG, "doInBackground: mBeginDate-" + beginDate);
        Log.i(TAG, "doInBackground: mEndDate-" + endDate);
        Log.i(TAG, "doInBackground: rows-" + rows);
        Log.i(TAG, "doInBackground: Datamanager.size-" + arrayList.size());
        Log.i(TAG, "doInBackground: URL-" + queryUrl);
        LossPolicePreviewItem item = null;
        String tag;

        try {
            URL url = new URL(queryUrl);
            InputStream inputStream = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new InputStreamReader(inputStream, "UTF-8"));

            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (isCancelled()) break;
                switch (eventType) {
                    case XmlPullParser.START_TAG:

                        tag = xmlPullParser.getName();//테그 이름 얻어오기
                        if (tag.equals("resultCode"))
                            resultCode = xmlPullParser.nextText();
                        else if (tag.equals("item")) // 첫번째 검색결과
                            item = new LossPolicePreviewItem();
                        else if (tag.equals("atcId")) {
                            item.id = xmlPullParser.nextText();
                        } else if (tag.equals("depPlace")) {
                            item.depPlace = xmlPullParser.nextText();
                        } else if (tag.equals("fdFilePathImg")) {
                            item.imageURL = xmlPullParser.nextText();
                        } else if (tag.equals("fdPrdtNm")) {
                            item.productName = xmlPullParser.nextText();
                        } else if (tag.equals("fdSbjt")) {
                            item.fdSbjt = xmlPullParser.nextText();
                        } else if (tag.equals("fdSn")) {
                            item.sequenceNumber = xmlPullParser.nextText();
                        } else if (tag.equals("fdYmd")) {
                            item.pickDate = xmlPullParser.nextText();
                        } else if (tag.equals("prdtClNm")) {
                            item.productCategory = xmlPullParser.nextText();
                        } else if (tag.equals("totalCount")) {
                            totalCount = Integer.parseInt(xmlPullParser.nextText());
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xmlPullParser.getName(); //테그 이름 얻어오기

                        if (tag.equals("item")) {
                            if (isMainArea) {
                                addArrays.add(item);
                                addCount++;
                                //Log.d(TAG, "doInBackground: 이건 대분류");
                            } else {
                                //Log.d(TAG, "doInBackground: 이건 소분류");
                                if (AreaUtil.getInstance().isPlaceContainArea(item.depPlace, orgAreaCode)) {
                                    //Log.d(TAG, "doInBackground: 이건 " + item.depPlace);
                                    addArrays.add(item);
                                    addCount++;
                                }
                            }
                        }
                        break;
                }

                eventType = xmlPullParser.next();
            }

        } catch (Exception e) {

        }

        Collections.sort(addArrays);
        arrayList.addAll(addArrays);
        Request.setBundleTotalCount(bundle, totalCount);
        Request.addBundleCurrentCount(bundle, addCount);

        if (addCount == 0)
            Request.setBundleFinish(bundle, true);


        return totalCount;

    }

    @Override
    protected void onPostExecute(Integer totalCount) {
        super.onPostExecute(totalCount);

        if (onTaskWorkStateListener != null)
            onTaskWorkStateListener.onTaskPost(resultCode, totalCount);
    }


}
