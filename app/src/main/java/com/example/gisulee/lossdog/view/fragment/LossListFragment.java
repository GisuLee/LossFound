package com.example.gisulee.lossdog.view.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.common.AdsManager;
import com.example.gisulee.lossdog.data.entity.AppSettings;
import com.example.gisulee.lossdog.view.activity.AreaAddAtivity;
import com.example.gisulee.lossdog.common.AreaUtil;
import com.example.gisulee.lossdog.data.remote.AsyncTaskGetPreviewList;
import com.example.gisulee.lossdog.AsyncTaskGetPreviewListBusanSubway;
import com.example.gisulee.lossdog.common.DataManager;
import com.example.gisulee.lossdog.common.DateCalculator;
import com.example.gisulee.lossdog.adapter.viewholder.IntegerHolder;
import com.example.gisulee.lossdog.data.entity.LossPreviewItem;
import com.example.gisulee.lossdog.data.entity.NameCode;
import com.example.gisulee.lossdog.data.entity.NameCodePoliceAPI;
import com.example.gisulee.lossdog.view.listenerinterface.OnSearchClickListener;
import com.example.gisulee.lossdog.view.listenerinterface.OnTaskWorkStateListener;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.Request;
import com.example.gisulee.lossdog.common.SharedPreferencesManager;
import com.example.gisulee.lossdog.adapter.LossPreviewListRecyclerAdapter;
import com.example.gisulee.lossdog.view.activity.LossDetailViewActivity;
import com.example.gisulee.lossdog.view.activity.MainActivity;
import com.example.gisulee.lossdog.view.activity.PreviewListSearchActivity;
import com.example.gisulee.lossdog.view.activity.ProductCategoryAddActivity;
import com.google.android.gms.ads.MobileAds;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class LossListFragment extends Fragment implements Serializable {

    private final String TAG = "LossListFragment";
    private final static int UNIT_SIZE = Integer.MAX_VALUE;
    private ArrayList<Object> mLossPreviewList = new ArrayList();
    private AppSettings mAppSettings = new AppSettings(); // 초기화 하면 안됌
    private AsyncTaskGetPreviewList mAsyncTaskGetPreviewList = new AsyncTaskGetPreviewList(mLossPreviewList);
    private AsyncTaskGetPreviewListBusanSubway mAsyncTaskGetPreviewListBusanSubway = new AsyncTaskGetPreviewListBusanSubway(mLossPreviewList);
    private LinearLayout mLinearProgress;
    private RecyclerView mRecyclerView;
    private LossPreviewListRecyclerAdapter mRecyclerAdapter;
    private TextView mLabelEnd;
    private TextView mTxtSelectedArea;
    private TextView mTxtSelctedPrd;
    private TextView mTxtBeginDate;
    private TextView mTxtEndDate;
    private LinearLayout mBtnAddArea;
    private LinearLayout mBtnAddPrdCategory;
    private LinearLayout mBtnBeginDate;
    private LinearLayout mBtnEndDate;
    private int mtotalCount = 0;
    private boolean isDataLoding = true;
    private boolean isFinishedLoading = false;

    private IntegerHolder mLargestLastPostion = new IntegerHolder(0);             //가장 마지막까지 스크롤한 위치를 저장
    private ArrayList<Bundle> mRequestBundles = new ArrayList();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //mAsyncTaskGetPreviewList.cancel(true);
        mAsyncTaskGetPreviewListBusanSubway.cancel(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_list, container, false);
        AreaUtil.getInstance().setRes(getResources());
        AreaUtil.getInstance().loadAreaList();

        mRecyclerAdapter = new LossPreviewListRecyclerAdapter(getContext(), mLossPreviewList,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.listView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mLabelEnd = view.findViewById(R.id.labelEnd);
        mTxtSelectedArea = view.findViewById(R.id.txtSelectedArea);
        mTxtSelctedPrd = view.findViewById(R.id.txtSelectedPrd);
        mTxtBeginDate = view.findViewById(R.id.txt_begin_date);
        mTxtEndDate = view.findViewById(R.id.txt_end_date);
        mLinearProgress = view.findViewById(R.id.linear_progress);
        mBtnAddArea = view.findViewById(R.id.btn_add_area);
        mBtnAddPrdCategory = view.findViewById(R.id.btn_add_prdcategory);
        mBtnBeginDate = view.findViewById(R.id.btn_begin_date);
        mBtnEndDate = view.findViewById(R.id.btn_end_date);

        if (loadAppSettings(mAppSettings) == false) {
            Log.d(TAG, "onCreateView: AppSetting NULL");
            mAppSettings = setDefaultMainSettings();
        }

        setDefaultDateSetting(mAppSettings);
        setUIAppSettings(mAppSettings, mTxtSelectedArea, mTxtSelctedPrd, mTxtBeginDate, mTxtEndDate);

        mLinearProgress.setVisibility(View.GONE);

        /* 프리뷰 불러오기 */
        if (mLossPreviewList.size() == 0) {
            Log.d(TAG, "onCreateView: updatePreviewList");
            updatePreviewList(mLossPreviewList, mAppSettings, true,0);
        }

        /* 리스트뷰 아이템 클릭 리스너*/
        mRecyclerAdapter.setOnItemClickListener(new LossPreviewListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if(mLossPreviewList.get(position) == null) return;
                LossPreviewItem lossPreviewItem = (LossPreviewItem) mLossPreviewList.get(position);
                View imgView = (View) v.findViewById(R.id.lostImageView);

                Bundle putData = new Bundle();
                putData.putString(Request.PARAM_ID, lossPreviewItem.id);
                putData.putString(Request.PARAM_SEQUENCE, lossPreviewItem.sequenceNumber);
                putData.putString(Request.PARAM_NAME, lossPreviewItem.fdSbjt);

                Intent intent = new Intent(getContext(), LossDetailViewActivity.class);
                intent.putExtra(Request.KEY_PREVIEW_ITEM, putData);
                intent.putExtra(Request.KEY_PREVIEW_SEARCH, lossPreviewItem);


                Drawable drawable = ((ImageView) imgView).getDrawable();
                Bitmap bitmap = null;
                if (drawable != null)
                    bitmap = ((BitmapDrawable) drawable).getBitmap();

                DataManager.getInstance().setBitmap(bitmap);

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(), imgView, imgView.getTransitionName());
                startActivity(intent, options.toBundle());

                //startActivity(intent);
            }
        });


        /* 리스트뷰 스크롤 시, 스크롤이 마지막 하단이면 새로운 데이터를 불러온다 */
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                final int numOfAds = 5;
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                if (lastVisibleItemPosition >= mLargestLastPostion.value) {
                    AdsManager.getInstance().adsLoad(mRecyclerAdapter, mLargestLastPostion, 5);
                }


                /* 데이터를 다 불러왔다면 그냥 리턴 */
                //if (mtotalCount <= mLossPreviewList.size()) return;
                if(isDataLoding)
                    return;

                if (!mRecyclerView.canScrollVertically(1)) {
                    isDataLoding = true;
                    //mLinearProgress.setVisibility(View.VISIBLE);
                    int progressIndex = mLossPreviewList.size();
                    mLossPreviewList.add(null);
                    mRecyclerAdapter.notifyItemInserted(progressIndex);
                    mRecyclerView.scrollToPosition(progressIndex);
                    updatePreviewList(mLossPreviewList, mAppSettings, false, progressIndex);
                    //mLossPreviewList.remove(mLossPreviewList.size() - 1);

                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        /* 분실지역 추가버튼 리스너 설정*/
        mBtnAddArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AreaAddAtivity.class);
                intent.putExtra(Request.KEY_SELECTED_AREA_LIST, mAppSettings.getSelectedAreaList());
                startActivityForResult(intent, Request.REQUEST_ADD_AREA);
            }
        });

        /* 분실물 카테고리 설정버튼 리스너*/
        mBtnAddPrdCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProductCategoryAddActivity.class);
                intent.putExtra(Request.KEY_SELECTED_PRDCATE_LIST, mAppSettings.getSelectedPrdList());
                startActivityForResult(intent, Request.REQUEST_ADD_PRD_CATEGORY);
            }
        });

        mBtnBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDatePicker(mAppSettings, true);
            }
        });

        mBtnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDatePicker(mAppSettings, false);
            }
        });

        MobileAds.initialize(getContext(), getContext().getString(R.string.admob_app_id));

        return view;
    }


    @Override
    public void onDestroy() {
        saveAppSettings(mAppSettings);
        AdsManager.getInstance().stopExecutor();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* 지역추가 액티비티로부터 데이터를 받는다.*/
        if (requestCode == Request.REQUEST_ADD_AREA) {
            if (resultCode == RESULT_OK) {
                mAppSettings.setSelectedAreaList((ArrayList<NameCode>) data.getSerializableExtra(Request.KEY_SELECTED_AREA_LIST));
                setUIAppSettings(mAppSettings, mTxtSelectedArea, mTxtSelctedPrd, mTxtBeginDate, mTxtEndDate);
            }
            /* 분실물 카테고리추가 액티비티로부터 데이터를 받는다.*/
        } else if (requestCode == Request.REQUEST_ADD_PRD_CATEGORY) {
            if (resultCode == RESULT_OK) {
                mAppSettings.setSelectedPrdList((ArrayList<NameCode>) data.getSerializableExtra(Request.KEY_SELECTED_PRDCATE_LIST));
                setUIAppSettings(mAppSettings, mTxtSelectedArea, mTxtSelctedPrd, mTxtBeginDate, mTxtEndDate);
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setOnSearchClickListener(new OnSearchClickListener() {
                @Override
                public void onClick() {
                    onSearchButtonClick();
                }
            });

            ((MainActivity) getActivity()).setOnRefreshButtonClickListener(new MainActivity.OnRefreshButtonClickListener() {
                @Override
                public void onClick() {
                    onRefeshButtonClick();
                }
            });

            ((MainActivity) getActivity()).setOnMainButtonClickListenr(new MainActivity.OnMainButtonClickListener() {
                @Override
                public void onClick() {
              /*      Log.d("BusanSubway", "시작: ");
                    String response = "";
                    Bundle bundle[] = Request.getBundle(mAppSettings, UNIT_SIZE);

                    new AsyncTaskGetPreviewListBusanSubway(mLossPreviewList).execute(bundle);
                    mRecyclerAdapter.notifyDataSetChanged();*/


                }
            });
        }
    }

    private void onRefeshButtonClick() {
        AdsManager.getInstance().stopExecutor();
        updatePreviewList(mLossPreviewList, mAppSettings, true,0);
    }

    private void onSearchButtonClick() {

        if (isDataLoding) {
            Toast.makeText(getContext(), "데이터를 불러오는 중 입니다..", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(getContext(), PreviewListSearchActivity.class);
        DataManager.getInstance().setLossPreviewItems(mLossPreviewList);
        startActivity(intent);
    }


    /* 스피너 높이 설정 */
    private void setSpinnerHeight(Spinner spinner, int hegith) {
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            ListPopupWindow window = (ListPopupWindow) popup.get(spinner);
            window.setHeight(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePreviewList(ArrayList<Object> arrayList, AppSettings settings, boolean isDataClear, int progressIndex) {

        isDataLoding = true;
        isFinishedLoading = false;

        if (isDataClear) {
            mRequestBundles.clear();
            mLargestLastPostion.value = 0;
            setLayoutGravity(mLinearProgress, true);
            mLabelEnd.setVisibility(View.INVISIBLE);
            mLinearProgress.setVisibility(View.VISIBLE);
            arrayList.clear();
            Log.d(TAG, "updatePreviewList: list clear");
            runLayoutAnimation(mRecyclerView);

        }

        Request.getBundle(settings, mRequestBundles);
        if (isFinishedDataLoad(mRequestBundles)) {
            Log.d(TAG, "updatePreviewList: completed data loading");
            mLinearProgress.setVisibility(View.GONE);
            isDataLoding = false;
            isFinishedLoading = true;
            if(progressIndex != 0) {
                arrayList.remove(progressIndex);
                mRecyclerAdapter.notifyDataSetChanged();
            }
            return;
        } else {

            mAsyncTaskGetPreviewList.cancel(false);
            mAsyncTaskGetPreviewList = new AsyncTaskGetPreviewList(arrayList);
            mAsyncTaskGetPreviewList.setBundles(mRequestBundles);
            mAsyncTaskGetPreviewList.setOnTaskWorkStateListener(new OnTaskWorkStateListener() {
                @Override
                public void onTaskPost() {
                    super.onTaskPost();
                    if(progressIndex != 0)
                        arrayList.remove(progressIndex);
                    processResponse(0,arrayList,isDataClear);
                }
            });

            mAsyncTaskGetPreviewList.execute();

        }
    }

    private  boolean isFinishedDataLoad(ArrayList<Bundle> mRequestBundles) {
        for(Bundle bundle : mRequestBundles){
            if(Request.getBundleFinish(bundle) == false)
                return false;
        }
        return true;
    }

    private void processResponse(int tc, ArrayList<Object> arrayList, boolean isdataClear) {
/*
        mtotalCount = tc;
        if (tc <= arrayList.size()) {
            mLabelEnd.setVisibility(View.VISIBLE);
        }
*/

        setLayoutGravity(mLinearProgress, false);
        mLinearProgress.setVisibility(View.GONE);
        if(!isdataClear)
            mRecyclerAdapter.notifyDataSetChanged();
        else
            //runLayoutAnimation(mRecyclerView);
            mRecyclerAdapter.notifyDataSetChanged();

        AdsManager.getInstance().adsLoad(mRecyclerAdapter, mLargestLastPostion, 5);

        isDataLoding = false;

    }

    private void setLayoutGravity(LinearLayout progressBar, boolean isCenter) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        progressBar.setLayoutParams(layoutParams);

        if (isCenter) {
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        } else {
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_right);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private boolean loadAppSettings(AppSettings settings) {

        SharedPreferencesManager.init(getContext());
        SharedPreferencesManager.loadAppSettings(settings,
                SharedPreferencesManager.KEY_ALARM_FILE,
                AppSettings.KEY_APP_SETTINGS);

        if (settings.getSelectedPrdList() == null) {
            Log.d(TAG, "loadAppSettings: null");
            return false;
        } else
            return true;
    }

    private AppSettings setDefaultMainSettings() {
        AppSettings settings = new AppSettings();
        ArrayList<NameCode> areaList = new ArrayList();
        ArrayList<NameCode> prdList = new ArrayList();

        areaList.add(new NameCodePoliceAPI("지역 전체", ""));
        prdList.add(new NameCodePoliceAPI("물품 전체", ""));

        settings.setSelectedAreaList(areaList)
                .setSelectedPrdList(prdList);

        return settings;
    }

    private void setDefaultDateSetting(AppSettings settings) {
        settings.setBeginDate(DateCalculator.getBeforeMonthToString(1));
        settings.setEndDate(DateCalculator.getTodayToString());
    }

    private void setUIAppSettings(AppSettings settings, TextView txtArea, TextView txtPrd, TextView txtBeginDate, TextView txtEndDate) {

        String strArea = settings.getSelectedAreaList().get(0).name;
        String strPrd = settings.getSelectedPrdList().get(0).name;

        if (settings.getSelectedAreaList().size() > 1) {
            strArea = strArea + "외 " + Integer.toString(settings.getSelectedAreaList().size() - 1) + "곳";
        }

        if (settings.getSelectedPrdList().size() > 1) {
            strPrd = strPrd + " 외 " + Integer.toString(settings.getSelectedPrdList().size() - 1) + "개";
        }

        txtArea.setText(strArea);
        txtPrd.setText(strPrd);
        txtBeginDate.setText(settings.getBeginDate());
        txtEndDate.setText(settings.getEndDate());
    }


    private void saveAppSettings(AppSettings settings) {
        if (settings == null) {
            Log.d(TAG, "saveAppSettings: Appsettins is null");
            return;
        }

        SharedPreferencesManager.saveAppSettings(settings,
                SharedPreferencesManager.KEY_ALARM_FILE,
                AppSettings.KEY_APP_SETTINGS);
        Log.d(TAG, "saveAppSettings: ");
    }

    private void setDatePicker(AppSettings settings, boolean isBeginDate) {
        String str;
        if (isBeginDate)
            str = settings.getBeginDate();
        else
            str = settings.getEndDate();

        int year = DateCalculator.getYearFromString(str);
        int mon = DateCalculator.getMonthFromString(str);
        int day = DateCalculator.getDayFromString(str);
        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (isBeginDate)
                    settings.setBeginDate(year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth));
                else
                    settings.setEndDate(year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth));
                setUIAppSettings(mAppSettings, mTxtSelectedArea, mTxtSelctedPrd, mTxtBeginDate, mTxtEndDate);
            }
        }, year, mon, day);
        dialog.show();
    }


}
