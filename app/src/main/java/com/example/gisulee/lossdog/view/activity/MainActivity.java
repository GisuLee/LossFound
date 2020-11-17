package com.example.gisulee.lossdog.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.gisulee.lossdog.common.AdsManager;
import com.example.gisulee.lossdog.common.JobManager;
import com.example.gisulee.lossdog.common.MyNotificationManager;
import com.example.gisulee.lossdog.view.component.NonSwipeViewPager;
import com.example.gisulee.lossdog.view.listenerinterface.OnBackPressedCustom;
import com.example.gisulee.lossdog.view.listenerinterface.OnSearchClickListener;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.Request;
import com.example.gisulee.lossdog.common.SharedPreferencesManager;
import com.example.gisulee.lossdog.adapter.PagerAdapter;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Context mContext;
    private TabLayout mTabLayout;
    private PagerAdapter mPagerAdapter;
    private Menu mMenu = null;
    private OnMenuButtonClickListener onMenuButtonClickListener;
    private OnSearchClickListener onSearchClickListener;
    private OnRefreshButtonClickListener onRefreshButtonClickListener;
    private OnAlarmAddClickListener onAlarmAddClickListener;
    private OnMainButtonClickListener onMainButtonClickListener;
    private Toolbar toolbar;
    private TextView mToolbarTitle;
    private NonSwipeViewPager mViewPager;
    private AppBarLayout appbarlayout;
    private FloatingActionButton mBtnFloating;
    private int VIEWPAGER_DURATION = 500;
    private Animation fadeIn;

    @Override
    public void onBackPressed() {
        int position = mViewPager.getCurrentItem();

        Fragment fragment = (Fragment) mPagerAdapter.getRegisteredFragment(position);
        if (!(fragment instanceof OnBackPressedCustom) || !((OnBackPressedCustom) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        AdsManager adsManager = AdsManager.getInstance();
        adsManager.init(this);
        JobManager.init(mContext);
        MyNotificationManager.init(mContext);
        SharedPreferencesManager.init(mContext);

        mBtnFloating = findViewById(R.id.btn_floating);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_id);
        appbarlayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.icon_dog_paw));
        setActionBarTitle("습득물 조회");

        mTabLayout = (TabLayout) findViewById(R.id.bottom_tab);
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("습득물 조회",getDrawable(R.drawable.icon_search_white))));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("주변 검색",getDrawable(R.drawable.icon_near_me_white))));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("알림",getDrawable(R.drawable.icon_notification_white))));


        mViewPager =  findViewById(R.id.pager_content);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setOffscreenPageLimit(2);


        Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        fadeOut.setDuration(VIEWPAGER_DURATION);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.rise_up);
        fadeIn.setInterpolator(new OvershootInterpolator(1.5f));
        fadeIn.setDuration(VIEWPAGER_DURATION);
        ScaleAnimation anim = new ScaleAnimation(0,1,0,1);
        anim.setFillEnabled(true);
        anim.setDuration(VIEWPAGER_DURATION);
        anim.setInterpolator(new OvershootInterpolator(1.5f));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Fragment oldFrag = mPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
                if(oldFrag.getView() != null) {
                    oldFrag.getView().startAnimation(fadeOut);
                }

                mViewPager.setCurrentItem(tab.getPosition(),false);
                tab.select();
                Fragment newFrag =  mPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());

                if(newFrag.getView() != null) {
                    newFrag.getView().startAnimation(fadeIn);
                }


                if(tab.getPosition() == 0){
                    mBtnFloating.startAnimation(anim);
                    mBtnFloating.setVisibility(View.VISIBLE);
                    mBtnFloating.setImageResource(R.drawable.icon_search_white);
                    mMenu.findItem(R.id.btn_refresh).setVisible(true);
                    mMenu.findItem(R.id.btn_refresh).setIcon(R.drawable.icon_refresh_white).setTitle("새로고침");
                    setActionBarTitle("습득물 조회");
                    showToolbar(toolbar,true);
                    setViewBehavior(mViewPager,toolbar,true);
                }else if(tab.getPosition() == 1){
                    mBtnFloating.setVisibility(View.GONE);
                    showToolbar(toolbar,false);
                    setViewBehavior(mViewPager,toolbar,false);
                } else{
                    mBtnFloating.setVisibility(View.GONE);
                    mBtnFloating.setImageResource(R.drawable.icon_setting);
                    mMenu.findItem(R.id.btn_refresh).setIcon(R.drawable.icon_setting).setTitle("설정");
                    setActionBarTitle("내 알림");
                    showToolbar(toolbar,true);
                    setViewBehavior(mViewPager,toolbar,false );
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mBtnFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMenu.findItem(R.id.btn_refresh).getTitle().equals("새로고침")) {
                    if (onSearchClickListener != null)
                        onSearchClickListener.onClick();
                }else{
                    if(onAlarmAddClickListener !=null)
                        onAlarmAddClickListener.onClick();
                }
           }
        });

    }

    public void setActionBarTitle(String str){
        mToolbarTitle.setText(str);
    }

    public void setTabLayoutNotify(int index, int count){

        View view = mTabLayout.getTabAt(index).getCustomView();
        TextView txtNoti = view.findViewById(R.id.txt_noticount);
        txtNoti.setText(Integer.toString(count));
        if(count == 0){
            txtNoti.setVisibility(View.GONE);
        }else{
            txtNoti.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                onMainButtonClickListener.onClick();
                return true;
            }
            case R.id.btn_refresh:{

                if(item.getTitle().equals("새로고침")) {
                    mViewPager.startAnimation(fadeIn);
                    if(onRefreshButtonClickListener != null)
                        onRefreshButtonClickListener.onClick();
                    return true;
                }else{
                    if(onMenuButtonClickListener != null)
                        onMenuButtonClickListener.onClick();
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        mMenu = menu;
        processIntent();
        return true;
    }


    private void processIntent(){
        Intent intent = getIntent();
        boolean isAutomaicNotifyShow = intent.getBooleanExtra(Request.KEY_NOTIFY_SHOW_AUTOMATIC, false);
        if(isAutomaicNotifyShow) {
            TabLayout.Tab tab = mTabLayout.getTabAt(2);
            tab.select();
        }
    }

    private View createTabView(String tabName, Drawable res) {
        View tabView = LayoutInflater.from(mContext).inflate(R.layout.tab_item, null);
        TextView txtName = (TextView) tabView.findViewById(R.id.txtTitle);
        txtName.setText(tabName);
        txtName.setCompoundDrawablesWithIntrinsicBounds(null, res,null,null);
        return tabView;
    }


    public interface OnMenuButtonClickListener {
        public void onClick();
    }

    public interface OnAlarmAddClickListener {
        public void onClick();
    }

    public interface OnRefreshButtonClickListener{
        public void onClick();
    }

    public interface OnMainButtonClickListener{
        public void onClick();
    }

    public void setOnMainButtonClickListenr(OnMainButtonClickListener onMainButtonClickListenr){
        this.onMainButtonClickListener = onMainButtonClickListenr;
    }

    public void setOnRefreshButtonClickListener(OnRefreshButtonClickListener onRefreshButtonClickListener){
        this.onRefreshButtonClickListener = onRefreshButtonClickListener;
    }

    public void setOnMenuButtonClickListener(OnMenuButtonClickListener onMenuButtonClickListener){
        this.onMenuButtonClickListener = onMenuButtonClickListener;
    }

    public void setOnSearchClickListener(OnSearchClickListener onSearchClickListener){
        this.onSearchClickListener = onSearchClickListener;
    }

    public void setOnAlarmAddClickListener(OnAlarmAddClickListener onAlarmAddClickListener){
        this.onAlarmAddClickListener = onAlarmAddClickListener;
    }

    public void showToolbar(Toolbar toolbar, boolean show){
        if (show) {
            toolbar.animate()
                    .translationY(0)
                    .alpha(1)
                    .setDuration(500)
                    .setInterpolator(new DecelerateInterpolator());
            toolbar.setVisibility(View.VISIBLE);
        } else {

            toolbar.setVisibility(View.GONE);
        }
    }

    private void setViewBehavior(ViewPager viewPager, Toolbar toolbar, Boolean isSet){
        if(isSet){
            AppBarLayout.LayoutParams params =
                    (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            appbarlayout.setExpanded(true);

        }else{
            AppBarLayout.LayoutParams params =
                    (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(0);
            appbarlayout.setExpanded(false);
        }

        /* 뷰페이저 프레임 레이아웃 비헤이버설정*/
//        CoordinatorLayout.LayoutParams params2 =
//                (CoordinatorLayout.LayoutParams) viewPager.getLayoutParams();
//        params2.setBehavior((new AppBarLayout.ScrollingViewBehavior()))
//        viewPager.requestLayout();
    }
}
