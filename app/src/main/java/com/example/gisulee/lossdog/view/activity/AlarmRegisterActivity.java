package com.example.gisulee.lossdog.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.gisulee.lossdog.data.entity.AlarmItem;
import com.example.gisulee.lossdog.view.component.ClearEditText;
import com.example.gisulee.lossdog.common.DateCalculator;
import com.example.gisulee.lossdog.data.entity.NameCode;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.Request;

import java.util.ArrayList;

public class AlarmRegisterActivity extends AppCompatActivity {

    private final String TAG = "AlarmRegisterActivity";
    private ArrayList<AlarmItem> mAlarmItemList = new ArrayList();
    private ArrayList<NameCode> mSelectedAreaList = new ArrayList<>();
    private ArrayList<NameCode> mSelectedPrdCategoryList = new ArrayList();
    private int mReceviedIndex = -1;
    private int mReceviedRequestCode;
    private boolean isOverrapAlarmName = true;
    private AlarmItem mReceivedAlarmItem = new AlarmItem();
    private String mAlarmRegisterDate = DateCalculator.getTodayToString();
    private String mAlarmName;
    private String mAlarmProductFeature;
    private CardView mCardViewAlarmName;
    private CardView mCardViewPlace;
    private CardView mCardViewPrdCategory;
    private CardView mCardViewPrdName;
    private SwitchCompat mSwitchAlarm;
    private Button mBtnRegister;
    private ImageView mBtnAddPrdCategory;
    private ImageView mBtnAddArea;
    private LinearLayout mContainerSelecedArea;
    private LinearLayout mContainerSelecedPrdCategory;
    private ClearEditText mEditAlarmName;
    private ClearEditText mEditProductFeature;
    private TextView mTxtAlarmNameCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loss_alarm_register);

        mContainerSelecedArea = (LinearLayout) findViewById(R.id.container_selected_area);
        mContainerSelecedPrdCategory = (LinearLayout) findViewById(R.id.container_product_select);
        mCardViewAlarmName = (CardView) findViewById(R.id.cardView_alarm_name);
        mCardViewPlace = (CardView) findViewById(R.id.cardView_place);
        mCardViewPrdCategory = (CardView) findViewById(R.id.cardView_product_category);
        mCardViewPrdName = (CardView) findViewById(R.id.cardView_product_name);
        mSwitchAlarm = (SwitchCompat) findViewById(R.id.swtich_alarm);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mEditAlarmName = (ClearEditText) findViewById(R.id.txtAlarmName);
        mEditProductFeature = (ClearEditText) findViewById(R.id.txtProductFeature);
        mBtnAddArea = (ImageView) findViewById(R.id.btn_add_area);
        mBtnAddPrdCategory = (ImageView) findViewById(R.id.btn_add_prdcategory);
        mTxtAlarmNameCheck =(TextView) findViewById(R.id.txtAlarmNameCheck);

        /* 툴바 설정*/
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        Intent intent = getIntent();
        mAlarmItemList = (ArrayList<AlarmItem>) intent.getSerializableExtra(Request.KEY_ALARM_LIST);
        mReceviedRequestCode = intent.getIntExtra(Request.KEY_REQUEST_CODE, -1);

        /* requestCode 가 기존의 알림을 수정하는 요청이면*/
        if(mReceviedRequestCode == Request.REQUEST_EDIT_ALARM){
            toolbarTitle.setText("알람 편집");
            mReceviedIndex = intent.getIntExtra(Request.KEY_INDEX, -1);
            mReceivedAlarmItem = (AlarmItem) intent.getSerializableExtra(Request.KEY_ALARM_ITEM);
            mSwitchAlarm.setChecked(mReceivedAlarmItem.getAlarmOn());
            setViewEnabled(mReceivedAlarmItem.getAlarmOn());
            mEditAlarmName.setText(mReceivedAlarmItem.getName());
            mEditProductFeature.setText(mReceivedAlarmItem.getProductFeature());
            mSelectedPrdCategoryList = mReceivedAlarmItem.getPrdCategoryList();
            mSelectedAreaList = mReceivedAlarmItem.getPrdAreaList();
            setViewEnabled(mCardViewAlarmName,false);
            isOverrapAlarmName = false;
            loadSelectedItem(mContainerSelecedArea,mSelectedAreaList);
            loadSelectedItem(mContainerSelecedPrdCategory,mSelectedPrdCategoryList);

        /* requestCode가 새로운 알림 추가이면*/
        }else if(mReceviedRequestCode == Request.REQUEST_ADD_ALARM) {
            toolbarTitle.setText("알람 추가");
            mSwitchAlarm.setChecked(false);
            isOverrapAlarmName = true;
            setViewEnabled(false);
            refreshButtonEnabled(mBtnRegister);
        }else{
            Log.d(TAG, "onCreate: requestCode is " + mReceviedRequestCode );
        }



        /* 에디트 텍스트 키보드 다음 버튼 설정*/
        mEditAlarmName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                boolean handler = false;
                if(actionID == EditorInfo.IME_ACTION_NEXT){
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    handler = true;
                }
                return true;
            }
        });

        /* 에디트 텍스트 키보드 다음 버튼 설정*/
        mEditProductFeature.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                boolean handler = false;
                if(actionID == EditorInfo.IME_ACTION_NEXT){
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    handler = true;
                }
                return true;
            }
        });

        /* 분실지역 추가버튼 리스너 설정*/
        mBtnAddArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AreaAddAtivity.class);
                intent.putExtra(Request.KEY_SELECTED_AREA_LIST, mSelectedAreaList);
                startActivityForResult(intent, Request.REQUEST_ADD_AREA);
            }
        });

        /* 분실물 카테고리 설정버튼 리스너*/
        mBtnAddPrdCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProductCategoryAddActivity.class);
                intent.putExtra(Request.KEY_SELECTED_PRDCATE_LIST, mSelectedPrdCategoryList);
                startActivityForResult(intent, Request.REQUEST_ADD_PRD_CATEGORY);
            }
        });

        /* 새 습득물 알림 스위치 리스너 설정*/
        mSwitchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setViewEnabled(b);
                refreshButtonEnabled(mBtnRegister);
                if(mReceviedRequestCode == Request.REQUEST_EDIT_ALARM)
                    setViewEnabled(mCardViewAlarmName,false);
            }
        });

        /* 등록하기 버튼 스위치 리스너 설정*/
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAlarmName = mEditAlarmName.getText().toString();
                mAlarmProductFeature = mEditProductFeature.getText().toString();

                String areaName = mSelectedAreaList.get(0).name;
                if(mSelectedAreaList.size() > 1){
                    areaName = areaName + " 외 " + Integer.toString(mSelectedAreaList.size() -1) + "곳";
                }

                String prdName = mSelectedPrdCategoryList.get(0).name;
                if(mSelectedPrdCategoryList.size() > 1){
                    prdName = prdName + " 외 " + Integer.toString(mSelectedPrdCategoryList.size() -1) + "개의 분류 (" + mAlarmProductFeature +")";
                }else{
                    prdName = prdName + " (" + mAlarmProductFeature + ")";
                }

                AlarmItem alarmItem = new AlarmItem()
                                    .setAlarmOn(mSwitchAlarm.isChecked())
                                    .setName(mAlarmName)
                                    .setDate(mAlarmRegisterDate)
                                    .setProductText(prdName)
                                    .setPlaceText(areaName)
                                    .setProductFeature(mAlarmProductFeature)
                                    .setPrdCategoryList(mSelectedPrdCategoryList)
                                    .setPrdAreaList(mSelectedAreaList)
                                    .setLastUpdate(DateCalculator.getTodayToString())
                                    .setReceivedCount(mReceivedAlarmItem.getReceivedCount());

                Intent intent = new Intent();

                /* 만약 요청이 알람 편집이었으면 받았던 알림인덱스를 돌려준다. */
                if(mReceviedRequestCode == Request.REQUEST_EDIT_ALARM){
                    intent.putExtra(Request.KEY_INDEX,mReceviedIndex);
                }

                intent.putExtra(Request.KEY_ALARM_ITEM,alarmItem);
                setResult(RESULT_OK,intent);
                onBackPressed();
            }
        });

        mEditAlarmName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence input, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                refreshTxtCheckInformation(mTxtAlarmNameCheck, input);
            }
        });
    }

    private void refreshTxtCheckInformation(TextView mTxtAlarmNameCheck, CharSequence input) {
        if(input.equals("")){
            mTxtAlarmNameCheck.setText("알림을 구별할 수 있는 이름을 입력하세요.");
            mTxtAlarmNameCheck.setTextColor(getResources().getColor(R.color.textColor));
            isOverrapAlarmName = true;
        }else{
            if(!isOverlap(mAlarmItemList, input)){
                mTxtAlarmNameCheck.setText("사용가능한 이름 입니다.");
                mTxtAlarmNameCheck.setTextColor(getResources().getColor(R.color.blue));
                isOverrapAlarmName = false;
            }else{
                mTxtAlarmNameCheck.setText("중복된 이름입니다.");
                mTxtAlarmNameCheck.setTextColor(getResources().getColor(R.color.red));
                isOverrapAlarmName = true;
            }
        }
        refreshButtonEnabled(mBtnRegister);
    }

    private boolean isOverlap(ArrayList<AlarmItem> mAlarmItemList, CharSequence input) {
        for(int i=0; i<mAlarmItemList.size(); i++){
            if(mAlarmItemList.get(i).getName().equals(input)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setViewEnabled(ViewGroup viewGroup,boolean b){
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            setViewAndChildrenEnabled(child,b);
        }
    }

    private void setViewEnabled(boolean b){
        for (int i = 0; i < mCardViewAlarmName.getChildCount(); i++) {
            View child = mCardViewAlarmName.getChildAt(i);
            setViewAndChildrenEnabled(child,b);
        }
        for (int i = 0; i < mCardViewPlace.getChildCount(); i++) {
            View child = mCardViewPlace.getChildAt(i);
            setViewAndChildrenEnabled(child,b);
        }
        for (int i = 0; i < mCardViewPrdCategory.getChildCount(); i++) {
            View child = mCardViewPrdCategory.getChildAt(i);
            setViewAndChildrenEnabled(child,b);
        }
        for (int i = 0; i < mCardViewPrdName.getChildCount(); i++) {
            View child = mCardViewPrdName.getChildAt(i);
            setViewAndChildrenEnabled(child,b);
        }
        mBtnRegister.setEnabled(b);
    }


    private static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1: 0.5f);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* 지역추가 액티비티로부터 데이터를 받는다.*/
        if(requestCode == Request.REQUEST_ADD_AREA) {
            if (resultCode == RESULT_OK) {
                mSelectedAreaList = (ArrayList<NameCode>) data.getSerializableExtra(Request.KEY_SELECTED_AREA_LIST);
                mContainerSelecedArea.removeAllViews();
                loadSelectedItem(mContainerSelecedArea, mSelectedAreaList);
            }
        /* 분실물 카테고리추가 액티비티로부터 데이터를 받는다.*/
        }else if (requestCode == Request.REQUEST_ADD_PRD_CATEGORY){
            if(resultCode == RESULT_OK){
                mSelectedPrdCategoryList = (ArrayList<NameCode>) data.getSerializableExtra(Request.KEY_SELECTED_PRDCATE_LIST);
                mContainerSelecedPrdCategory.removeAllViews();
                loadSelectedItem(mContainerSelecedPrdCategory, mSelectedPrdCategoryList);
            }
        }

        refreshButtonEnabled(mBtnRegister);
    }

    private void loadSelectedItem(ViewGroup container, ArrayList<NameCode> selectedItemList){
        for(int i=0; i<selectedItemList.size(); i++){
            NameCode nameCode = selectedItemList.get(i);
            addItemLayout(container, selectedItemList, nameCode.name, nameCode.hashCode());
        }
    }

    private void addItemLayout(ViewGroup viewGroup, ArrayList<NameCode> selectedItemList, String viewName, int hashCode){

        LinearLayout.LayoutParams LlParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout Ll = new LinearLayout(this);
        LlParams.rightMargin = 40;
        Ll.setLayoutParams(LlParams);
        Ll.setId(hashCode);
        Ll.setBackground(getDrawable(R.drawable.shape_gray_background_card));

        TextView textView = new TextView(this);
        textView.setPadding(20,20,20,20);
        textView.setTextColor(getResources().getColor(R.color.textColor));
        textView.setLayoutParams(textviewParams);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(viewName);

        btnParams.height = 60; btnParams.width = 60;
        Button btnDelete = new Button(this);
        btnDelete.setBackground(getDrawable(R.drawable.icon_cancel));
        btnDelete.setLayoutParams(btnParams);
        btnParams.gravity = Gravity.CENTER_VERTICAL;
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout removeView = (LinearLayout) view.getParent();
                LinearLayout removeViewParent = (LinearLayout) removeView.getParent();
                removeItem(selectedItemList, removeView.getId());
                removeViewParent.removeView(removeView);
                refreshButtonEnabled(mBtnRegister);
            }
        });

        Ll.addView(textView);
        Ll.addView(btnDelete);
        viewGroup.addView(Ll);
    }

    private void removeItem(ArrayList<NameCode> selectedItemList, int hashCode){

        for(int i=0; i<selectedItemList.size(); i++){
            int compareHash = selectedItemList.get(i).hashCode();
            if( compareHash == hashCode){
                Log.d(TAG, "removeItem: " + selectedItemList.get(i).name);
                selectedItemList.remove(i);
                return;
            }
        }
    }

    private void refreshButtonEnabled(Button btn){
        if(mSelectedAreaList.size() != 0 &&
                mSelectedPrdCategoryList.size() != 0 &&
                (!isOverrapAlarmName  ||  mReceviedRequestCode == Request.REQUEST_EDIT_ALARM)){
            btn.setEnabled(true);
        }else {
            btn.setEnabled(false);
        }
    }
}
