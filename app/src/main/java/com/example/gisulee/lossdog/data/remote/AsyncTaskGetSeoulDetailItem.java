package com.example.gisulee.lossdog.data.remote;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.gisulee.lossdog.data.entity.LossPoliceDetailItem;
import com.example.gisulee.lossdog.data.entity.Request;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

public class AsyncTaskGetSeoulDetailItem extends AsyncTask<Bundle, Void, LossPoliceDetailItem> {

    private OnTaskWorkStateListener onTaskWorkStateListener;
    private String TAG = "AsyncTaskGetSeoulDetailItem";

    public AsyncTaskGetSeoulDetailItem setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener) {
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @Override
    protected LossPoliceDetailItem doInBackground(Bundle... bundles) {

        String id = bundles[0].getString(Request.PARAM_ID);
        String title = bundles[0].getString(Request.PARAM_NAME);
        String url = "http://115.84.165.106/admin/find_view_0.jsp";
        LossPoliceDetailItem item = new LossPoliceDetailItem();
        item.productName = title;

        Log.d(TAG, "doInBackground: " + id);
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .data("id", id)
                    .post();

        } catch (IOException e) {
            e.printStackTrace();
            return item = null;
        }

        Elements titles = doc.select("tr td");
        for (Element e : titles) {

            if (e.text().length() < 20)
                continue;

            Log.d(TAG, "doInBackground: " + e.text());
            Iterator<Element> iterator = e.select("td").iterator();
            int index = -1;
            while (iterator.hasNext()) {
                index++;
                String stream = iterator.next().text();
                if (index == 0 || index == 1) {
                    continue;
                }

                if (stream.equals("")) {
                    continue;
                }

                switch (index) {
                    case 2:
                        item.pickPlace = stream;
                        break;
                    case 6:
                        item.productCategory = stream;
                        break;
                    case 7:
                        item.pickDate = stream;
                        break;
                    case 9:
                        item.depPlaceTel = stream;
                    case 12:
                        item.depPlace = "서울특별시 " + stream;
                    case 14:
                        item.subject = stream;
                    default:

                }
                Log.d(TAG, "doInBackground it: index " + index + " / " + stream);

            }
            break;
            /*item.productCategory = e.select("div.frm").get(8).text();
            item.productName = e.select("div.frm").get(2).text();
            item.pickPlace = e.select("div.frm").get(9).text();
            item.depPlace = "부산광역시 지하철 " + e.select("div.frm").get(7).text();
            item.depPlaceTel = e.select("div.frm").get(6).text();
            item.pickDate = e.select("div.frm").get(5).text();*/
        }

        return item;

    }

    @Override
    protected void onPostExecute(LossPoliceDetailItem lossDetailItem) {
        super.onPostExecute(lossDetailItem);
        onTaskWorkStateListener.onTaskPost(lossDetailItem);
    }

    public abstract class OnTaskWorkStateListener {
        public abstract void onTaskPost(LossPoliceDetailItem item);
    }

}