package com.example.gisulee.lossdog.data.entity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class Request {

    /* 경찰청 API 파라미터*/
    public static final String PARAM_OBJECT_INSTANCE = "PARAM_OBJECT_INSTANCE";
    public static final String PARAM_ID = "PARAM_ID";
    public static final String PARAM_SEQUENCE = "PARAM_SEQ";
    public static final String PARAM_AREA_CODE = "PARAM_SAREA_CODE";
    public static final String PARAM_PRD_MAIN_CATEGORY = "PARAM_SPRD_MAIN_CATEGORY";
    public static final String PARAM_PRD_SUB_CATEGORY = "PARAM_SPRD_SUB_CATEGORY";
    public static final String PARAM_BEGIN_DATE = "PARAM_SBEGIN_DATE";
    public static final String PARAM_END_DATE = "PARAM_SEND_DATE";
    public static final String PARAM_PAGE = "PARAM_SPAGE";
    public static final String PARAM_ROWS = "PARAM_SROW";
    public static final String PARAM_NAME = "PARAM_NAME";
    /* 액티비티 인텐트 request code */
    public static final int REQUEST_PREVIEW_OBJECT = 50;
    public static final int REQUEST_ADD_ALARM = 10;
    public static final int REQUEST_EDIT_ALARM = 11;
    public static final int REQUEST_VIEW_LIST = 12;
    public static final int REQUEST_ADD_AREA = 20;
    public static final int REQUEST_ADD_PRD_CATEGORY = 30;


    /* 프로젝트 데이터 전달용 파라미터 */
    public static final String KEY_REQUEST_SUCCESS_CODE = "00";
    public static final String KEY_PREVIEW_SEARCH = "KEY_PREVIEW_SEARCH";
    public static final String KEY_PREVIEW_ITEM = "KEY_PREVIEW_TIME";
    public static final String KEY_SELECTED_AREA_LIST = "KEY_SELECTED_AREA_LIST";
    public static final String KEY_ALARM_FILENAME = "KEY_ALARM_FILENAME";
    public static final String KEY_REQUEST_CODE = "KEY_REQUEST_CODE";
    public static final String KEY_INDEX = "KEY_INDEX";
    public static final String KEY_ALARM_ITEM = "KEY_ALARM_ITEM";
    public static final String KEY_SELECTED_PRDCATE_LIST = "KEY_SELCETED_PRDCATE_LIST";
    public static final String KEY_ALARM_LIST = "KEY_ALARM_LIST";
    public static final String KEY_FILE_NAME = "KEY_FILE_NAME";
    public static final String KEY_TOTAL_COUNT = "KEY_TOTAL_COUNT";
    public static final String KEY_CURRENT_COUNT = "KEY_CURRENT_COUNT";
    public static final String KEY_FINISHED_DATA_LOAD = "KEY_FINISHED_DATA_LOAD";
    public static final String KEY_ALARM_ADD_AUTOMATIC = "KEY_ALARM_ADD_AUTOMATIC";
    public static final String KEY_NOTIFY_SHOW_AUTOMATIC = "KEY_NOTIFY_SHOW_AUTOMATIC";


    public static class BundleBuilder {

        private String instance;
        private String areaCode;
        private String prdMainCategory;
        private String prdSubCategory;
        private String beginDate;
        private String endDate;
        private String rows;
        private String page;
        private String totalCount;
        private String currentCount;

        public BundleBuilder setAreaCode(String areaCode) {
            this.areaCode = areaCode;
            return this;
        }

        public BundleBuilder setPrdMainCategory(String prdMainCategory) {
            this.prdMainCategory = prdMainCategory;
            return this;
        }

        public BundleBuilder setPrdSubCategory(String prdSubCategory) {
            this.prdSubCategory = prdSubCategory;
            return this;
        }

        public BundleBuilder setBeginDate(String beginDate) {
            this.beginDate = beginDate;
            return this;
        }

        public BundleBuilder setEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public BundleBuilder setRows(String rows) {
            this.rows = rows;
            return this;
        }

        public BundleBuilder setPage(String page) {
            this.page = page;
            return this;
        }


        public BundleBuilder setInstance(String instance) {
            this.instance = instance;
            return this;
        }

        public Bundle build() {
            Bundle bundle = new Bundle();
            bundle.putString(Request.PARAM_OBJECT_INSTANCE, this.instance);
            bundle.putString(Request.PARAM_AREA_CODE, this.areaCode);
            bundle.putString(Request.PARAM_PRD_MAIN_CATEGORY, this.prdMainCategory);
            bundle.putString(Request.PARAM_PRD_SUB_CATEGORY, this.prdSubCategory);
            bundle.putString(Request.PARAM_BEGIN_DATE, this.beginDate);
            bundle.putString(Request.PARAM_END_DATE, this.endDate);
            bundle.putString(Request.PARAM_PAGE, this.page);
            bundle.putString(Request.PARAM_ROWS, this.rows);
            return bundle;
        }
    }

    public static Bundle[] getBundle(AlarmItem alarmItem, String wantToDay) {

        int bundleSize = alarmItem.getPrdAreaList().size() * alarmItem.getPrdCategoryList().size();
        Bundle bundle[] = new Bundle[bundleSize];

        for (int i = 0; i < alarmItem.getPrdAreaList().size(); i++) {
            for (int j = 0; j < alarmItem.getPrdCategoryList().size(); j++) {
                String mainCode, subCode;

                /* 물품 카테고리가 상위코드이면*/
                if (alarmItem.getPrdCategoryList().get(j).code.contains("000")) {
                    mainCode = alarmItem.getPrdCategoryList().get(j).code;
                    subCode = "";

                    /* 물품 카테고리를 전체("")로 선택했다면*/
                } else if (alarmItem.getPrdCategoryList().get(j).code.equals("")) {
                    mainCode = "";
                    subCode = "";

                } else {  /* 물품 카테고리가 하위코드이면*/
                    mainCode = alarmItem.getPrdCategoryList().get(j).code.substring(0, 3) + "000";
                    subCode = alarmItem.getPrdCategoryList().get(j).code;
                }

                bundle[i * alarmItem.getPrdCategoryList().size() + j] =
                        new Request.BundleBuilder()
                                .setAreaCode(alarmItem.getPrdAreaList().get(i).code)
                                .setPrdMainCategory(mainCode)
                                .setPrdSubCategory(subCode)
                                .setBeginDate(wantToDay)
                                .setEndDate(wantToDay)
                                .setPage("1")
                                .setRows(Integer.toString(Integer.MAX_VALUE))
                                .build();
            }
        }

        return bundle;
    }

    public static void getBundle(AppSettings settings, ArrayList<Bundle> bundles) {

        int bundleSize = settings.getSelectedPrdList().size() * settings.getSelectedAreaList().size();
        int page = 1;


        for (int i = 0; i < settings.getSelectedAreaList().size(); i++) {

            // area 코드가 다형성이 적용된다. 지역(지하철, 경찰청 등..)
            NameCode areaCategoryCode = settings.getSelectedAreaList().get(i);

            for (int j = 0; j < settings.getSelectedPrdList().size(); j++) {

                NameCode prdCategoryCode = settings.getSelectedPrdList().get(j);

                int index = i * settings.getSelectedPrdList().size() + j;

                if (bundles.size() != bundleSize) {

                    Log.d("make", "getBundle: make instance" + areaCategoryCode.instanceCode);
                    Bundle createBundle = areaCategoryCode.createRequestBundle(settings,areaCategoryCode,prdCategoryCode);

                    bundles.add(createBundle);
                }else{
                    if(isFinishDataLoad(bundles.get(index)))
                        setBundleFinish(bundles.get(index), true);
                    else{
                        page = Integer.parseInt(bundles.get(index).getString(Request.PARAM_PAGE)) ;
                        page ++;
                        setBundlePage(bundles.get(index), page);
                    }

                }
            }
        }

    }

    private static boolean isFinishDataLoad(Bundle bundle){
      boolean ret = false;

      int totalCount = bundle.getInt(Request.KEY_TOTAL_COUNT, -1);
      int currentCount = bundle.getInt(Request.KEY_CURRENT_COUNT, -1);

      if (totalCount != -1 && currentCount != -1)
          if(totalCount == currentCount)
              ret = true;

      return ret;
    }

    public static void setBundleTotalCount(Bundle bundle, int count){
        bundle.putInt(Request.KEY_TOTAL_COUNT, count);
    }

    public static int getBundleTotalCount(Bundle bundle){
        return bundle.getInt(Request.KEY_TOTAL_COUNT, 0);
    }

    public static void setBundleCurrentCount(Bundle bundle, int count){
        bundle.putInt(Request.KEY_CURRENT_COUNT, count);
    }

    public static void addBundleCurrentCount(Bundle bundle, int addCount){
        int curCount = getBundleCurrentCount(bundle);
        bundle.putInt(Request.KEY_CURRENT_COUNT, curCount + addCount);
    }

    public static int getBundleCurrentCount(Bundle bundle){
        return bundle.getInt(Request.KEY_CURRENT_COUNT, 0);
    }

    public static void setBundlePage(Bundle bundle, int page){
        bundle.putString(Request.PARAM_PAGE, Integer.toString(page));
    }

    public static void setBundlePage(Bundle bundle, String page){
        bundle.putString(Request.PARAM_PAGE, page);
    }

    public static boolean getBundleFinish(Bundle bundle){
        return bundle.getBoolean(Request.KEY_FINISHED_DATA_LOAD, false);
    }

    public static void setBundleFinish(Bundle bundle, boolean isFinish){
        bundle.putBoolean(Request.KEY_FINISHED_DATA_LOAD, isFinish);
    }
}
