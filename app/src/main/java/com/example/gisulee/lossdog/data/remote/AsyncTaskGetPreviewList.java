package com.example.gisulee.lossdog.data.remote;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.gisulee.lossdog.view.listenerinterface.OnTaskWorkStateListener;
import com.example.gisulee.lossdog.common.DateCalculator;
import com.example.gisulee.lossdog.data.entity.LossBusanSubwayPreviewItem;
import com.example.gisulee.lossdog.data.entity.LossSeoulPreviewItem;
import com.example.gisulee.lossdog.data.entity.NameCode;
import com.example.gisulee.lossdog.data.entity.NameCodeBusanSubway;
import com.example.gisulee.lossdog.data.entity.NameCodeSeoul;
import com.example.gisulee.lossdog.data.entity.Request;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class AsyncTaskGetPreviewList extends AsyncTask<Bundle, Integer, Integer> {

    final private String TAG = "AsyncTaskGetPreviewList";
    private ArrayList<Bundle> bundles;
    private OnTaskWorkStateListener onTaskWorkStateListener;
    private AsyncTaskGetPreviewListPoliceAPI mAsynTaskGetPreivewListPoliceAPI;
    private AsyncTaskGetPreviewListBusanSubway mAsyncTaskGetPreviewListBusanSubway;
    private AsyncTaskGetPreviewListSeoul mAsyncTaskGetPreviewListSeoul;
    private ArrayList<Object> arrayList;
    private String resultCode;

    public AsyncTaskGetPreviewList(ArrayList<Object> arrayList) {
        this.arrayList = arrayList;
        mAsynTaskGetPreivewListPoliceAPI = new AsyncTaskGetPreviewListPoliceAPI(arrayList);
        mAsyncTaskGetPreviewListBusanSubway = new AsyncTaskGetPreviewListBusanSubway(arrayList);
    }

    public AsyncTaskGetPreviewList setBundles(ArrayList<Bundle> bundles) {
        this.bundles = bundles;
        return this;
    }

    public AsyncTaskGetPreviewList setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener) {
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (onTaskWorkStateListener != null)
            onTaskWorkStateListener.onTaskExecute();

        int totalCount = 0;

        for (Bundle bundle : bundles) {

            if (bundle.getBoolean(Request.KEY_FINISHED_DATA_LOAD))
                continue;
            String instance = bundle.getString(Request.PARAM_OBJECT_INSTANCE);
            Log.d(TAG, "execute: instance " + instance);

            switch (instance) {

                case NameCodeBusanSubway.instance:

                    //mAsyncTaskGetPreviewListBusanSubway.cancel(false);
                    mAsyncTaskGetPreviewListBusanSubway = new AsyncTaskGetPreviewListBusanSubway(arrayList);
                    mAsyncTaskGetPreviewListBusanSubway.setOnTaskWorkStateListener(new OnTaskWorkStateListener() {
                        @Override
                        public void onTaskPost(String resultCode, int tc) {
                            //processResponse(tc, arrayList,isDataClear);

                            if (onTaskWorkStateListener != null)
                                onTaskWorkStateListener.onTaskPost();
                        }

                        @Override
                        public void onTaskExecute() {
                        }
                    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bundle);

                    break;

                case NameCodeSeoul.instance:

                    //mAsyncTaskGetPreviewListBusanSubway.cancel(false);
                    mAsyncTaskGetPreviewListSeoul = new AsyncTaskGetPreviewListSeoul(arrayList);
                    mAsyncTaskGetPreviewListSeoul.setOnTaskWorkStateListener(new OnTaskWorkStateListener() {
                        @Override
                        public void onTaskPost(String resultCode, int tc) {
                            //processResponse(tc, arrayList,isDataClear);

                            if (onTaskWorkStateListener != null)
                                onTaskWorkStateListener.onTaskPost();
                        }

                        @Override
                        public void onTaskExecute() {
                        }
                    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bundle);

                    break;

                default:
                    Log.d(TAG, "execute: 2");
                    //mAsynTaskGetPreivewListPoliceAPI.cancel(false);
                    mAsynTaskGetPreivewListPoliceAPI = new AsyncTaskGetPreviewListPoliceAPI(arrayList);
                    mAsynTaskGetPreivewListPoliceAPI.setOnTaskWorkStateListener(new OnTaskWorkStateListener() {
                        @Override
                        public void onTaskPost(String resultCode, int tc) {

                            if (onTaskWorkStateListener != null)
                                onTaskWorkStateListener.onTaskPost();
                            //processResponse(tc, arrayList,isDataClear);
                        }

                        @Override
                        public void onTaskExecute() {
                        }
                    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bundle);

                    break;
            }


        }


    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    @Override
    protected Integer doInBackground(Bundle... bundles) {
        return null;
    }

    public int exeacute(ArrayList<Bundle> bundles) {

        if (onTaskWorkStateListener != null)
            onTaskWorkStateListener.onTaskExecute();

        int totalCount = 0;


        for (Bundle bundle : bundles) {

            String instance = bundle.getString(Request.PARAM_OBJECT_INSTANCE);
            Log.d(TAG, "execute: instance " + instance);
            switch (instance) {

                case NameCodeBusanSubway.instance:

                    mAsyncTaskGetPreviewListBusanSubway.cancel(false);
                    mAsyncTaskGetPreviewListBusanSubway = new AsyncTaskGetPreviewListBusanSubway(arrayList);
                    try {
                        mAsyncTaskGetPreviewListBusanSubway.setOnTaskWorkStateListener(new OnTaskWorkStateListener() {
                            @Override
                            public void onTaskPost(String resultCode, int tc) {
                                //processResponse(tc, arrayList,isDataClear);
                            }

                            @Override
                            public void onTaskExecute() {
                            }
                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bundle).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    break;


                default:
                    Log.d(TAG, "execute: 2");
                    mAsynTaskGetPreivewListPoliceAPI.cancel(false);
                    mAsynTaskGetPreivewListPoliceAPI = new AsyncTaskGetPreviewListPoliceAPI(arrayList);
                    try {
                        mAsynTaskGetPreivewListPoliceAPI.setOnTaskWorkStateListener(new OnTaskWorkStateListener() {
                            @Override
                            public void onTaskPost(String resultCode, int tc) {

                                //processResponse(tc, arrayList,isDataClear);
                            }

                            @Override
                            public void onTaskExecute() {
                            }
                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bundle).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    break;
            }


        }


        if (onTaskWorkStateListener != null)
            onTaskWorkStateListener.onTaskPost();
        return totalCount;
    }


}

class AsyncTaskGetPreviewListBusanSubway extends AsyncTask<Bundle, String, Integer> {

    final private String TAG = "AsyncTaskGetPreviewListBusanSubway";
    private OnTaskWorkStateListener onTaskWorkStateListener;
    private ArrayList<Object> arrayList;
    private String resultCode;

    public AsyncTaskGetPreviewListBusanSubway() {

    }

    public AsyncTaskGetPreviewListBusanSubway(ArrayList<Object> arrayList) {
        this.arrayList = arrayList;
    }

    public AsyncTaskGetPreviewListBusanSubway setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener) {
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected Integer doInBackground(Bundle... bundles) {

        if (onTaskWorkStateListener != null)
            onTaskWorkStateListener.onTaskExecute();

        int totalCount = 0;
        int addCount = 0;
        String url = "http://www.humetro.busan.kr/homepage/default/lost/list.do?menu_no=1001010402";
        String pageNum = bundles[0].getString(Request.PARAM_PAGE);
        String beginDate = bundles[0].getString(Request.PARAM_BEGIN_DATE);
        String endDate = bundles[0].getString(Request.PARAM_END_DATE);
        String prdCategory = bundles[0].getString(Request.PARAM_PRD_MAIN_CATEGORY);

        Log.d(TAG, "doInBackground: prdCate" + prdCategory);
        Log.d(TAG, "doInBackground: " + endDate);

        String[] sdate = new String[6];
        sdate[0] = Integer.toString(DateCalculator.getYearFromString(beginDate));
        sdate[1] = String.format("%02d", DateCalculator.getMonthFromString(beginDate) + 1);
        sdate[2] = String.format("%02d", DateCalculator.getDayFromString(beginDate));
        sdate[3] = Integer.toString(DateCalculator.getYearFromString(endDate));
        sdate[4] = String.format("%02d", DateCalculator.getMonthFromString(endDate) + 1);
        sdate[5] = String.format("%02d", DateCalculator.getDayFromString(endDate));

        Log.d(TAG, "doInBackground: " + Arrays.toString(sdate));

        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .data("s_year", sdate[0])
                    .data("s_month", sdate[1])
                    .data("s_day", sdate[2])
                    .data("e_year", sdate[3])
                    .data("e_month", sdate[4])
                    .data("e_day", sdate[5])
                    .data("SGUBUN",prdCategory)
                    .data("C_PAGE", pageNum)
                    .post();

            totalCount = Integer.parseInt(doc.select("span.pg-num strong").text());
            Elements titles = doc.select("li.li0");

            for (Element e : titles) {
                LossBusanSubwayPreviewItem item = new LossBusanSubwayPreviewItem();
                String id = e.select("a").attr("onClick");
                String img = "http://www.humetro.busan.kr/" + e.select("img").attr("src");
                String title = e.select("p.ptitle").text();
                String date = e.select("p.pdate span").first().text();
                String depPlace = e.select("p.pdate span.plocate").text();

                item.id = id.replaceAll("[^0-9]", "");
                if (img.equals("http://www.humetro.busan.kr/")) {
                    img = "https://www.lost112.go.kr/lostnfs/images/sub/img02_no_img.gif";
                }
                item.imageURL = img;
                item.fdSbjt = title;
                item.pickDate = date;
                item.depPlace = "부산광역시 지하철 " + depPlace;
                arrayList.add(item);
                addCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        resultCode = totalCount == 0 ? "null" : Request.KEY_REQUEST_SUCCESS_CODE;
        if (Request.getBundleTotalCount(bundles[0]) == 0)
            Request.setBundleTotalCount(bundles[0], totalCount);
        Request.addBundleCurrentCount(bundles[0], addCount);

        Log.d(TAG, "doInBackground: curCount" + Request.getBundleCurrentCount(bundles[0]));

        if (addCount == 0)
            Request.setBundleFinish(bundles[0], true);

        return totalCount;

    }

    @Override
    protected void onPostExecute(Integer totalCount) {
        super.onPostExecute(totalCount);
        if (onTaskWorkStateListener != null)
            onTaskWorkStateListener.onTaskPost(resultCode, totalCount);
    }

}


class AsyncTaskGetPreviewListSeoul extends AsyncTask<Bundle, String, Integer> {

    final private String TAG = "AsyncTaskGetPreviewListSeoul";
    private OnTaskWorkStateListener onTaskWorkStateListener;
    private ArrayList<Object> arrayList;
    private String resultCode;

    public AsyncTaskGetPreviewListSeoul() {

    }

    public AsyncTaskGetPreviewListSeoul(ArrayList<Object> arrayList) {
        this.arrayList = arrayList;
    }

    public AsyncTaskGetPreviewListSeoul setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener) {
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected Integer doInBackground(Bundle... bundles) {

        if (onTaskWorkStateListener != null)
            onTaskWorkStateListener.onTaskExecute();

        int totalCount = -1;
        int addCount = 0;
        String areaCodes[] = new String[]{"", "", "", "", "", "", "", ""};

        String url = "http://115.84.165.106/admin/find_list.jsp?like_where=get_name&targetCode=&searchKey=&sort_1=2";
        String pageNum = bundles[0].getString(Request.PARAM_PAGE);
        String beginDate = bundles[0].getString(Request.PARAM_BEGIN_DATE);
        String endDate = bundles[0].getString(Request.PARAM_END_DATE);
        String areaCode = bundles[0].getString(Request.PARAM_AREA_CODE);
        String parmCategory =bundles[0].getString(Request.PARAM_PRD_MAIN_CATEGORY);
        url += parmCategory;

        Log.d(TAG, "doInBackground: get area = " + areaCode);

        switch (areaCode) {
            case NameCode.BUS:
                areaCodes[0] = "b1";
                areaCodes[1] = "b2";
                break;
            case NameCode.SUBWAY:
                areaCodes[4] = "s1";
                areaCodes[5] = "s2";
                areaCodes[7] = "s4";
                break;
            case NameCode.TAXI:
                areaCodes[2] = "t1";
                areaCodes[3] = "t2";
                break;
            case NameCode.KORAIL:
                areaCodes[6] = "s3";
                break;
            default:
                Log.d(TAG, "doInBackground: error 대중교통 분류 실패");
        }
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .data("curPage", pageNum)
                    .data("date_start", beginDate)
                    .data("date_end", endDate)
                    .data("code1", areaCodes[0])
                    .data("code2", areaCodes[1])
                    .data("code3", areaCodes[2])
                    .data("code4", areaCodes[3])
                    .data("code5", areaCodes[4])
                    .data("code6", areaCodes[5])
                    .data("code7", areaCodes[6])
                    .data("code8", areaCodes[7])
                    .data("cate1","")
                    .get();
            Log.d(TAG, "doInBackground: 왜안돼"+doc.text());
            Elements titles = doc.select("tr.btl2");

            for (Element e : titles) {
                if (e.text().startsWith("번호"))
                    continue;

                String stream = e.text();
                StringTokenizer st = new StringTokenizer(stream);

                int tc = Integer.parseInt(st.nextToken());
                if (totalCount == -1 && pageNum.equals("1"))
                    totalCount = tc;

                String category = st.nextToken();
                String id = e.select("a").attr("onClick").replaceAll("[^0-9]", "");

                String img = e.select("img").attr("src");
                Log.d(TAG, "doInBackground: img " + img);
                img = img.replace("n40", "n150");
                if (img.endsWith("c11.gif")) {
                    img = "https://www.lost112.go.kr/lostnfs/images/sub/img02_no_img.gif";
                } else if (img.startsWith("../")) {
                    img = "http://115.84.165.106/" + img;
                } else if (img.startsWith("/upload/")) {
                    img = img.replaceFirst("/upload/", "");
                }


                String title = "";
                String date = "";
                String depPlace = "";

                Iterator<Element> iterator = e.select("span.font_gray8").iterator();

                int index = 0;

                while (iterator.hasNext()) {
                    if (index == 0)
                        iterator.next().text();
                    else if (index == 1)
                        title = iterator.next().text();
                    else if (index == 2)
                        date = iterator.next().text();
                    else if (index == 3)
                        depPlace = iterator.next().text();
                    else if (index > 3)
                        break;
                    index++;
                }
                LossSeoulPreviewItem item = new LossSeoulPreviewItem();
                item.id = id;
                item.productCategory = category;
                item.fdSbjt = title;
                item.pickDate = date;
                item.depPlace = "서울특별시 " + depPlace;
                item.imageURL = img;
                arrayList.add(item);
                addCount++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        resultCode = totalCount == 0 ? "null" : Request.KEY_REQUEST_SUCCESS_CODE;
        if (Request.getBundleTotalCount(bundles[0]) == 0)
            Request.setBundleTotalCount(bundles[0], totalCount);
        Request.addBundleCurrentCount(bundles[0], addCount);

        Log.d(TAG, "doInBackground: totalcount " + Request.getBundleTotalCount(bundles[0]));
        Log.d(TAG, "doInBackground: curCount" + Request.getBundleCurrentCount(bundles[0]));

        if (addCount == 0)
            Request.setBundleFinish(bundles[0], true);

        return totalCount;

    }

    @Override
    protected void onPostExecute(Integer totalCount) {
        super.onPostExecute(totalCount);
        if (onTaskWorkStateListener != null)
            onTaskWorkStateListener.onTaskPost(resultCode, totalCount);
    }

}