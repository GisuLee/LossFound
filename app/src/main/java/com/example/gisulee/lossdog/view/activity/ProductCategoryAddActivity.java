package com.example.gisulee.lossdog.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.example.gisulee.lossdog.data.remote.AsyncTaskGetPrdMainCategory;
import com.example.gisulee.lossdog.data.entity.NameCode;
import com.example.gisulee.lossdog.data.entity.NameCodePoliceAPI;
import com.example.gisulee.lossdog.view.listenerinterface.OnTaskWorkStateListener;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.Request;

import java.util.ArrayList;
import java.util.Iterator;


public class ProductCategoryAddActivity extends AppCompatActivity {

    private final String TAG = "ProductCategoryAddActivity";

    private ArrayList<NameCode> mSelectedPrdCategoryList;
    private ArrayList<NameCode> mMainCategoryList = new ArrayList();
    private ArrayList<NameCode> mSubCategoryList = new ArrayList();
    private RadioGroup mRadioGroupMain;
    private RadioGroup mRadioGroupSub;
    private int mSelectedMainIndex = 0;
    private int mSelectedSubIndex = 0;
    private LinearLayout mContainter;
    private TextView mTxtToolBar;
    private TextView mTxtMainGroup;
    private TextView mTxtSubGroup;
    private TextView mTxtPlaceGroup;
    private TextView mTxtSelectedFrame;
    private HorizontalScrollView mHorizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add_ativity);

        mHorizontalScrollView = findViewById(R.id.horiz_listview);
        mTxtMainGroup = (TextView) findViewById(R.id.txtMainGroup);
        mTxtSubGroup = (TextView) findViewById(R.id.txtSubGroup);
        mTxtPlaceGroup = (TextView) findViewById(R.id.txtPlaceGroup);
        mRadioGroupMain = (RadioGroup) findViewById(R.id.radioGroup_main);
        mRadioGroupSub = (RadioGroup) findViewById(R.id.radioGroup_sub);
        mContainter = (LinearLayout) findViewById(R.id.container_selected);
        mTxtToolBar = (TextView) findViewById(R.id.toolbar_id);
        mTxtSelectedFrame = (TextView) findViewById(R.id.txtSeletectFrame);

        /* 액션바 설정*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        mTxtToolBar.setText("분실물 분류 선택");

        mTxtMainGroup.setText("대분류");
        mTxtSubGroup.setText("소분류");
        mTxtPlaceGroup.setText("");
        mTxtSelectedFrame.setText("선택된 물품");
        /* 인텐트 불러오기 (선택된 지역을 불러와서 레이아우셍 추가한다 */
        Intent intent = getIntent();
        mSelectedPrdCategoryList = (ArrayList<NameCode>) intent.getSerializableExtra(Request.KEY_SELECTED_PRDCATE_LIST);
        loadSelectedItem(mContainter, mSelectedPrdCategoryList);

        /*메인 Area 불러오기*/
        mMainCategoryList.add(new NameCodePoliceAPI("전체", ""));
        new AsyncTaskGetPrdMainCategory(getResources(), mMainCategoryList).setOnTaskWorkStateListener(new OnTaskWorkStateListener() {
            @Override
            public void onTaskPost(String resultCode, int totalCount) {
                addRadioButton(mRadioGroupMain, mMainCategoryList, new MainGroupOnClickListener());
            }

            @Override
            public void onTaskExecute() {
            }
        }).execute();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
            case R.id.action_save: {
                Intent intent = new Intent();
                intent.putExtra(Request.KEY_SELECTED_PRDCATE_LIST, mSelectedPrdCategoryList);
                setResult(RESULT_OK, intent);
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }


    private void getFilteringAreaList(ArrayList<NameCode> mArea, ArrayList<NameCode> mMain) {
        for (Iterator<NameCode> iter = mArea.iterator(); iter.hasNext(); ) {
            NameCode nameCode = iter.next();
            if (nameCode.code.contains("000") == true) {
                mMain.add(nameCode);
            }
        }
    }

    private void addRadioButton(ViewGroup viewGroup, ArrayList<NameCode> array,
                                View.OnClickListener listener) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int) convertDpToPixel(-1, this);
        params.topMargin = (int) convertDpToPixel(-1, this);
        for(int i=0; i<array.size(); i++){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i);
            radioButton.setPadding(0,(int) convertDpToPixel(10,this),0,(int) convertDpToPixel(10,this));
            radioButton.setTextSize(19);
            radioButton.setTextColor(getResources().getColor(R.color.textColor));
            radioButton.setBackgroundResource(R.drawable.selector_radiobutton_background);
            radioButton.setButtonDrawable(null);
            radioButton.setText(array.get(i).name);
            radioButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            radioButton.setLayoutParams(params);
            if (listener != null)
                radioButton.setOnClickListener(listener);
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == true)
                        compoundButton.setTextColor(getResources().getColor(R.color.textIconColor));
                    else
                        compoundButton.setTextColor(getResources().getColor(R.color.textColor));
                }
            });
            viewGroup.addView(radioButton);
        }
    }

    private void addSelectedItem(ViewGroup container, ArrayList<NameCode> selectedAreaList, NameCode area) {
        selectedAreaList.add(area);
        mRadioGroupSub.clearCheck();
        addItemLayout(container, selectedAreaList, area.name, area.hashCode());
        mHorizontalScrollView.fullScroll(View.FOCUS_RIGHT);
    }

    private void loadSelectedItem(ViewGroup container, ArrayList<NameCode> selectedItemList) {
        for (int i = 0; i < selectedItemList.size(); i++) {
            NameCode nameCode = selectedItemList.get(i);
            addItemLayout(container, selectedItemList, nameCode.name, nameCode.hashCode());
        }
    }

    private void removeItem(ArrayList<NameCode> selectedAreaList, int hashCode) {

        for (int i = 0; i < selectedAreaList.size(); i++) {
            int compareHash = selectedAreaList.get(i).hashCode();
            if (compareHash == hashCode) {
                selectedAreaList.remove(i);
                return;
            }
        }
    }

    private void addItemLayout(ViewGroup viewGroup, ArrayList<NameCode> selectedAreaList, String areaName, int hashCode) {
        LinearLayout.LayoutParams LlParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout Ll = new LinearLayout(this);
        LlParams.rightMargin = (int) convertDpToPixel(10,this);
        Ll.setLayoutParams(LlParams);
        Ll.setId(hashCode);
        Ll.setBackground(getDrawable(R.drawable.shape_gray_background_card));

        TextView textView = new TextView(this);
        textView.setPadding(LlParams.rightMargin,(int) convertDpToPixel(5,this),LlParams.rightMargin,(int) convertDpToPixel(5,this));
        textView.setTextColor(getResources().getColor(R.color.textColor));
        textView.setLayoutParams(textviewParams);
        textView.setSingleLine();
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(areaName);

        btnParams.height = (int) convertDpToPixel(20,this); btnParams.width = (int) convertDpToPixel(20,this);
        Button btnDelete = new Button(this);
        btnDelete.setBackground(getDrawable(R.drawable.icon_cancel));
        btnDelete.setLayoutParams(btnParams);
        btnParams.gravity = Gravity.CENTER_VERTICAL;
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout removeView = (LinearLayout) view.getParent();
                LinearLayout removeViewParent = (LinearLayout) removeView.getParent();
                removeItem(selectedAreaList, removeView.getId());
                removeViewParent.removeView(removeView);

            }
        });
        Ll.addView(textView);
        Ll.addView(btnDelete);


        Transition t = new Slide(Gravity.BOTTOM);
        TransitionManager.beginDelayedTransition(viewGroup, t);
        viewGroup.addView(Ll);
    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;

    }

    class MainGroupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int index = mRadioGroupMain.getCheckedRadioButtonId();
            mSelectedMainIndex = index;

            mRadioGroupSub.removeAllViews();
            mRadioGroupSub.clearCheck();

            if (index == 0) {
                addSelectedItem(mContainter, mSelectedPrdCategoryList, new NameCodePoliceAPI("물품 전체", ""));
                mRadioGroupMain.clearCheck();
            } else {
                mSubCategoryList.clear();
                mSubCategoryList.add(new NameCodePoliceAPI("전체", ""));
                /*try {
                    new AsyncTaskGetPrdSubCategory(getResources(), mSubCategoryList).execute(mMainCategoryList.get(index).code).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }*/

                mRadioGroupSub.removeAllViews();
                mRadioGroupSub.clearCheck();

                //서브지역 클릭리스너
                addRadioButton(mRadioGroupSub, mSubCategoryList, new SubGroupOnClickListener());
            }
        }
    }

    class SubGroupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int index = mRadioGroupSub.getCheckedRadioButtonId();
            mSelectedSubIndex = index;

            if (index == 0) {
                addSelectedItem(mContainter, mSelectedPrdCategoryList, new NameCodePoliceAPI(
                        mMainCategoryList.get(mSelectedMainIndex).name + " > 전체",
                        mMainCategoryList.get(mSelectedMainIndex).code));
            } else {
                addSelectedItem(mContainter, mSelectedPrdCategoryList, new NameCodePoliceAPI(
                        mMainCategoryList.get(mSelectedMainIndex).name + " > " + mSubCategoryList.get(mSelectedSubIndex).name,
                        mSubCategoryList.get(mSelectedSubIndex).code));
            }
        }
    }


}


