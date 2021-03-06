package com.example.gisulee.lossdog.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.common.DataManager;
import com.example.gisulee.lossdog.data.entity.LossPolicePreviewItem;
import com.example.gisulee.lossdog.data.entity.LossPreviewItem;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.Request;
import com.example.gisulee.lossdog.adapter.LossPreviewListRecyclerAdapter;

import java.util.ArrayList;

public class PreviewListSearchActivity extends AppCompatActivity {

    private int GET_ITEM_UNIT = Integer.MAX_VALUE;
    private ArrayList<Object> mLossPreviewList = new ArrayList();
    private ArrayList<Object> mLossSearchPreviewList;
    private int mTotalCount = 0;
    private LossPreviewListRecyclerAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayout mLinearProgress;
    private TextView mLabelEnd;
    private TextView mTxtTitle;
    private SearchView searchView;
    private View mView;

    public class RecyclerViewOnItemClickListener implements LossPreviewListRecyclerAdapter.OnItemClickListener{

        private ArrayList<Object> arrayList;

        public RecyclerViewOnItemClickListener(ArrayList<Object> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public void onItemClick(View v, int position) {
            LossPreviewItem lossPreviewItem = (LossPreviewItem) arrayList.get(position);
            View imgView = (View) v.findViewById(R.id.lostImageView);

            Bundle putData = new Bundle();
            putData.putString(Request.PARAM_ID, lossPreviewItem.id);
            putData.putString(Request.PARAM_SEQUENCE, lossPreviewItem.sequenceNumber);

            Intent intent = new Intent(getApplicationContext(), LossDetailViewActivity.class);
            intent.putExtra(Request.KEY_PREVIEW_ITEM, putData);
            intent.putExtra(Request.KEY_PREVIEW_SEARCH, lossPreviewItem);

            Drawable drawable = ((ImageView) imgView).getDrawable();
            Bitmap bitmap = null;
            if (drawable != null)
                bitmap = ((BitmapDrawable) drawable).getBitmap();

            DataManager.getInstance().setBitmap(bitmap);

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            PreviewListSearchActivity.this, imgView, imgView.getTransitionName());

            startActivity(intent, options.toBundle());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_list_search);

        /* 툴바 설정*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        mLossPreviewList = DataManager.getInstance().getLossPreviewItems();
        mLabelEnd = (TextView) findViewById(R.id.labelEnd);
        mLinearProgress = findViewById(R.id.linear_progress);
        mRecyclerView =  findViewById(R.id.listView);
        mTxtTitle = findViewById(R.id.txtTitle);


        mTxtTitle.setText("물품검색");
      //  mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);
        setLayoutGravity(mLinearProgress,true);
        mLinearProgress.setVisibility(View.GONE);
        mRecyclerAdapter = new LossPreviewListRecyclerAdapter(this, mLossPreviewList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        /* 리스트뷰 아이템 클릭 리스너*/
        mRecyclerAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener(mLossPreviewList));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchbar_menu, menu);

        searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("물품명으로 검색합니다.");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mLabelEnd.setVisibility(View.VISIBLE);
                mLossSearchPreviewList = search(mLossPreviewList,query);
                mRecyclerAdapter = new LossPreviewListRecyclerAdapter(getApplicationContext(), mLossSearchPreviewList);
                mRecyclerView.setAdapter(mRecyclerAdapter);
                runLayoutAnimation(mRecyclerView);
                mRecyclerAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener(mLossSearchPreviewList));
                mRecyclerView.requestFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //mRecyclerAdapter = new LossPreviewListRecyclerAdapter(getApplicationContext(), mLossPreviewList);
                //mRecyclerView.setAdapter(mRecyclerAdapter);
                //mRecyclerAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener(mLossPreviewList));
                return false;
            }
        });

        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocus();
        return true;
    }

    private ArrayList<Object> search(ArrayList<Object> arrayList, String query) {

        ArrayList<Object> result = new ArrayList();
        Object item;

        for(int i=0; i<arrayList.size(); i++){
            item = arrayList.get(i);
            if(item instanceof LossPolicePreviewItem) {
                if (((LossPolicePreviewItem)item).fdSbjt.contains(query)) {
                    result.add(item);
                }
            }
        }

        return result;
    }


    private void setLayoutGravity(ViewGroup progressBar, boolean isCenter){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        progressBar.setLayoutParams(layoutParams);

        if(isCenter) {
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        }else {
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

/*    private void showDataLoadSnackBar(View view, Resources res, int tc){
        Snackbar snackbar = Snackbar.make(view, "총 " + tc + "개의 습득물이 검색되었습니다.", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackground(res.getDrawable(R.drawable.shape_corner_rounding_border_gray));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(50,0,50,130);
        snackbar.getView().setLayoutParams(params);
        TextView tv = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }*/
}

