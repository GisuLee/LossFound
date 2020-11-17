package com.example.gisulee.lossdog.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.example.gisulee.lossdog.data.remote.AsyncTaskGetAreaCategory;
import com.example.gisulee.lossdog.data.remote.AsyncTaskGetSubAreaCategory;
import com.example.gisulee.lossdog.data.entity.NameCode;
import com.example.gisulee.lossdog.data.entity.NameCodeBusanSubway;
import com.example.gisulee.lossdog.data.entity.NameCodePoliceAPI;
import com.example.gisulee.lossdog.data.entity.NameCodeSeoul;
import com.example.gisulee.lossdog.view.listenerinterface.OnTaskWorkStateListener;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.Request;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class AreaAddAtivity extends AppCompatActivity {

    private final String TAG = "AreaAddAtivity";
    private ArrayList<NameCode> mSelectedAreaList;
    private ArrayList<NameCode> mOrginalList = new ArrayList();
    private ArrayList<NameCode> mMainCategoryList = new ArrayList();
    private ArrayList<NameCode> mSubCategoryList = new ArrayList();
    private ArrayList<NameCode> mPlaceCategoryList = new ArrayList();
    private ArrayList<NameCode> mPlaceCategoryTotal = new ArrayList();
    private RadioGroup mRadioGroupMain;
    private RadioGroup mRadioGroupSub;
    private RadioGroup mRadioGroupPlace;
    private int mSelectedMainIndex = 0;
    private int mSelectedSubIndex = 0;
    private int mSelectedPlaceIndex = 0;
    private LinearLayout mContainer;
    private TextView mTxtSelectedFrame;
    private HorizontalScrollView mHorizontalScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add_ativity);

        mHorizontalScrollView = findViewById(R.id.horiz_listview);
        mRadioGroupMain = (RadioGroup) findViewById(R.id.radioGroup_main);
        mRadioGroupSub = (RadioGroup) findViewById(R.id.radioGroup_sub);
        mRadioGroupPlace = (RadioGroup) findViewById(R.id.radioGroup_place);
        mContainer = (LinearLayout) findViewById(R.id.container_selected);
        mTxtSelectedFrame = (TextView) findViewById(R.id.txtSeletectFrame);

        /* 액션바 설정*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        mTxtSelectedFrame.setText("선택된 지역");

        /* 인텐트 불러오기 (선택된 지역을 불러와서 레이아우셍 추가한다 */
        Intent intent = getIntent();
        mSelectedAreaList = (ArrayList<NameCode>) intent.getSerializableExtra(Request.KEY_SELECTED_AREA_LIST);
        loadSelectedItem(mContainer, mSelectedAreaList);

        mSubCategoryList.add(new NameCode("구/군/시", ""));
        mSubCategoryList.add(new NameCode("대중교통", ""));

        mPlaceCategoryTotal.add(new NameCodePoliceAPI("전체", ""));

        /*메인 Area 불러오기*/
        mMainCategoryList.add(new NameCodePoliceAPI("지역 전체", ""));
        new AsyncTaskGetAreaCategory(getResources(), mOrginalList).setOnTaskWorkStateListener(new OnTaskWorkStateListener() {
            @Override
            public void onTaskPost(String resultCode, int totalCount) {
                getFilteringAreaList(mOrginalList, mMainCategoryList);
                addRadioButton(mRadioGroupMain, mMainCategoryList, new MainGroupOnClickListener(), false);
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
                intent.putExtra(Request.KEY_SELECTED_AREA_LIST, mSelectedAreaList);
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

    private void addRadioButton(ViewGroup viewGroup, ArrayList<NameCode> itemList,
                                View.OnClickListener listener, boolean isCenter) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        params.topMargin = (int) convertDpToPixel(-1, this);
        if (isCenter) {
            params.leftMargin = (int) convertDpToPixel(-1, this);
            //params.rightMargin = (int) convertDpToPixel(-1, this);
        }

        for (int i = 0; i < itemList.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i);
            radioButton.setPadding(0, (int) convertDpToPixel(10, this), 0, (int) convertDpToPixel(10, this));
            radioButton.setTextSize(19);
            radioButton.setTextColor(getResources().getColor(R.color.textColor));
            radioButton.setBackgroundResource(R.drawable.selector_radiobutton_background);
            radioButton.setButtonDrawable(null);
            radioButton.setText(itemList.get(i).name);
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

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;

    }

    private void addSelectedItem(ViewGroup container, ArrayList<NameCode> selectedItemList, NameCode item) {
        selectedItemList.add(item);
        //mRadioGroupSub.clearCheck();
        mRadioGroupPlace.clearCheck();
        addSeclectedItemLayout(container, selectedItemList, item.name, item.hashCode());
        mHorizontalScrollView.fullScroll(View.FOCUS_RIGHT);
        Log.d(TAG, "addSelectedItem: instance " + item.instanceCode);
    }

    private void loadSelectedItem(ViewGroup container, ArrayList<NameCode> selectedItemList) {
        for (int i = 0; i < selectedItemList.size(); i++) {
            NameCode nameCode = selectedItemList.get(i);
            addSeclectedItemLayout(container, selectedItemList, nameCode.name, nameCode.hashCode());
        }
    }

    private void removeItem(ArrayList<NameCode> selectedItemList, int hashCode) {

        for (int i = 0; i < selectedItemList.size(); i++) {
            int compareHash = selectedItemList.get(i).hashCode();
            if (compareHash == hashCode) {
                selectedItemList.remove(i);
                return;
            }
        }
    }

    private void addSeclectedItemLayout(ViewGroup viewGroup, ArrayList<NameCode> selectedItemList, String viewName, int hashCode) {
        LinearLayout.LayoutParams LlParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout Ll = new LinearLayout(this);
        LlParams.rightMargin = (int) convertDpToPixel(10, this);
        Ll.setLayoutParams(LlParams);
        Ll.setId(hashCode);
        Ll.setBackground(getDrawable(R.drawable.shape_gray_background_card));

        TextView textView = new TextView(this);
        textView.setPadding(LlParams.rightMargin, (int) convertDpToPixel(5, this), LlParams.rightMargin, (int) convertDpToPixel(5, this));
        textView.setTextColor(getResources().getColor(R.color.textColor));
        textView.setLayoutParams(textviewParams);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(viewName);

        btnParams.height = (int) convertDpToPixel(20, this);
        ;
        btnParams.width = (int) convertDpToPixel(20, this);
        ;
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

            }
        });
        Ll.addView(textView);
        Ll.addView(btnDelete);

        Transition t = new Slide(Gravity.BOTTOM);
        TransitionManager.beginDelayedTransition(viewGroup, t);

        viewGroup.addView(Ll);

    }


    class MainGroupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            mRadioGroupSub.removeAllViews();
            mRadioGroupPlace.removeAllViews();
            mRadioGroupSub.clearCheck();
            mRadioGroupPlace.clearCheck();

            int index = mRadioGroupMain.getCheckedRadioButtonId();
            mSelectedMainIndex = index;

            /* 지역 전체 클릭 */
            if (index == 0) {
                addSelectedItem(mContainer, mSelectedAreaList, new NameCodePoliceAPI("지역 전체", ""));
                mRadioGroupMain.clearCheck();
            } else {

                addRadioButton(mRadioGroupSub, mSubCategoryList, new SubGroupOnClickListener(), true);

               /* mSubCategoryList.clear();

                try {
                    mSubCategoryList = new AsyncTaskGetSubAreaCategory(mOrginalList).execute(mMainCategoryList.get(index).code).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
*/


            }
        }
    }

    class SubGroupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int index = mRadioGroupSub.getCheckedRadioButtonId();
            mSelectedSubIndex = index;
            mRadioGroupPlace.removeAllViews();
            mRadioGroupPlace.clearCheck();

            mPlaceCategoryList.clear();

            /* index == 0 구/군/시 선택 */
            if (index == 0) {

                try {
                    mPlaceCategoryList = new AsyncTaskGetSubAreaCategory(mOrginalList).execute(mMainCategoryList.get(mSelectedMainIndex).code).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                /* index ==1 , 대중교통 선택 */
            } else if (index == 1) {

                mPlaceCategoryList.add(new NameCode("버스", NameCode.BUS));
                mPlaceCategoryList.add(new NameCode("지하철", NameCode.SUBWAY));
                mPlaceCategoryList.add(new NameCode("택시", NameCode.TAXI));
                mPlaceCategoryList.add(new NameCode("코레일", NameCode.KORAIL));

            }
            addRadioButton(mRadioGroupPlace, mPlaceCategoryList, new PlaceGroupOnClickListener(), false);

        }
    }

    class PlaceGroupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int index = mRadioGroupPlace.getCheckedRadioButtonId();
            mSelectedPlaceIndex = index;
            mRadioGroupPlace.clearCheck();

            if (mSelectedSubIndex == 0) {
                if (index == 0) {
                    addSelectedItem(mContainer, mSelectedAreaList, new NameCodePoliceAPI(
                            mMainCategoryList.get(mSelectedMainIndex).name + " 전체",
                            mMainCategoryList.get(mSelectedMainIndex).code));
                } else {
                    String name = mMainCategoryList.get(mSelectedMainIndex).name + " " + mPlaceCategoryList.get(mSelectedPlaceIndex).name;
                    NameCode addNameCode = new NameCode();
                    addNameCode.name = name;
                    addNameCode.instanceCode = mPlaceCategoryList.get(mSelectedPlaceIndex).instanceCode;
                    addNameCode.code = mPlaceCategoryList.get(mSelectedPlaceIndex).code;
                    addSelectedItem(mContainer, mSelectedAreaList, addNameCode);

                }
            } else {

                String name = mMainCategoryList.get(mSelectedMainIndex).name + " " + mPlaceCategoryList.get(mSelectedPlaceIndex).name;
                NameCode addNameCode = new NameCode();
                addNameCode.name = name;
                addNameCode.code = mPlaceCategoryList.get(mSelectedPlaceIndex).code;

                switch (mMainCategoryList.get(mSelectedMainIndex).name) {
                    case "서울특별시":
                        addNameCode.instanceCode = NameCodeSeoul.instance;
                        break;

                    case "부산광역시":
                        if (mPlaceCategoryList.get(index).name.equals("지하철")) {
                            addNameCode.instanceCode = NameCodeBusanSubway.instance;
                        } else {
                            Toast.makeText(AreaAddAtivity.this, "준비중...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;

                    default:
                        Toast.makeText(AreaAddAtivity.this, "준비중...", Toast.LENGTH_SHORT).show();
                        return;
                }

                addSelectedItem(mContainer, mSelectedAreaList, addNameCode);

            }
        }
    }
}
