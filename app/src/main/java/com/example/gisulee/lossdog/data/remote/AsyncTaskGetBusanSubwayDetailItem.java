package com.example.gisulee.lossdog.data.remote;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.gisulee.lossdog.data.entity.LossPoliceDetailItem;
import com.example.gisulee.lossdog.data.entity.Request;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class AsyncTaskGetBusanSubwayDetailItem extends AsyncTask<Bundle, Void, LossPoliceDetailItem> {

    private OnTaskWorkStateListener onTaskWorkStateListener;

    public AsyncTaskGetBusanSubwayDetailItem setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener) {
        this.onTaskWorkStateListener = onTaskWorkStateListener;
        return this;
    }

    @Override
    protected LossPoliceDetailItem doInBackground(Bundle... bundles) {

        String id = bundles[0].getString(Request.PARAM_ID);
        String url = "https://www.humetro.busan.kr/homepage/default/lost/view.do?menu_no=1001010402";
        LossPoliceDetailItem item = new LossPoliceDetailItem();

        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .data("NO", id).post();


            Elements titles = doc.select("div.board-view-contents");
            for (Element e : titles) {
                item.productCategory = e.select("div.frm").get(8).text();
                item.productName = e.select("div.frm").get(2).text();
                item.pickPlace = e.select("div.frm").get(9).text();
                item.depPlace = "부산광역시 지하철 " + e.select("div.frm").get(7).text();
                item.depPlaceTel = e.select("div.frm").get(6).text();
                item.pickDate = e.select("div.frm").get(5).text();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return item = null;
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
