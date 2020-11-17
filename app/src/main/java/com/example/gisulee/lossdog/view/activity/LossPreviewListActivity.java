package com.example.gisulee.lossdog.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.common.AdsManager;
import com.example.gisulee.lossdog.common.DataManager;
import com.example.gisulee.lossdog.adapter.viewholder.IntegerHolder;
import com.example.gisulee.lossdog.data.entity.LossPolicePreviewItem;
import com.example.gisulee.lossdog.data.entity.LossPreviewItem;
import com.example.gisulee.lossdog.data.entity.NotificationItem;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.Request;
import com.example.gisulee.lossdog.common.SharedPreferencesManager;
import com.example.gisulee.lossdog.adapter.LossPreviewListRecyclerAdapter;

import java.util.ArrayList;

public class LossPreviewListActivity extends AppCompatActivity {

    private final String TAG = "LossPreviewListActivity";
    private ArrayList<NotificationItem> mNotificationList = new ArrayList();
    private ArrayList<Object> mLossPreviewList = new ArrayList();
    private LossPreviewListRecyclerAdapter mRecyclerAdapter;
    private String mFileKey;
    private int mIndex;
    private TextView mLabelEnd;
    private LinearLayout mLinearProgress;
    private TextView mTxtTitle;
    private RecyclerView mRecyclerView;
    private IntegerHolder mLargestLastPostion = new IntegerHolder(0);

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_preview_list);

        /* 툴바 설정*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        Intent intent = getIntent();
        mFileKey = intent.getStringExtra(Request.KEY_FILE_NAME);
        mIndex = intent.getIntExtra(Request.KEY_INDEX, -1);
        if(mIndex == -1){
            Log.d(TAG, "onCreate: mIndex -1 ");
        }

        SharedPreferencesManager.init(this);
        SharedPreferencesManager.loadLossPreviewData( mLossPreviewList
                ,mFileKey
                , LossPolicePreviewItem.KEY_WRITE_PREVIEW_LIST);

        SharedPreferencesManager.loadNotificationItem(mNotificationList,
                SharedPreferencesManager.KEY_NOTIFY_FILE,
                NotificationItem.KEY_NOTIFICATION_ITEM);

        mLabelEnd = (TextView) findViewById(R.id.labelEnd);
        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        mLinearProgress =  findViewById(R.id.linear_progress);

        mLinearProgress.setVisibility(View.GONE);
        mLabelEnd.setVisibility(View.VISIBLE);
        mTxtTitle.setText("알림항목보기");

        mRecyclerAdapter = new LossPreviewListRecyclerAdapter(this, mLossPreviewList);
        mRecyclerView =  findViewById(R.id.listView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)) ;
        mRecyclerView.setAdapter(mRecyclerAdapter);

        /* 리사이클러뷰 애니메이션 설정*/
        runLayoutAnimation(mRecyclerView);

        /* 리사이클러뷰 아이템 클릭 리스너*/
        mRecyclerAdapter.setOnItemClickListener(new LossPreviewListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                LossPreviewItem lossPreviewItem = (LossPreviewItem) mLossPreviewList.get(position);
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
                                LossPreviewListActivity.this, imgView, imgView.getTransitionName());

                startActivity(intent, options.toBundle());

            }
        });

        Log.d(TAG, "onCreate: loadPreviewsize = " + mLossPreviewList.size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdsManager.getInstance().stopExecutor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.remove_alarm, menu);
        menu.findItem(R.id.action_all_remove).setIcon(R.drawable.img_delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_all_remove:
                showRemoveDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRemoveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle("알림 삭제");
        builder.setMessage("알림을 정말로 삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        /* 알림 Preivew 리스트  삭제 */
                        SharedPreferencesManager.remove(
                                mFileKey,
                                LossPolicePreviewItem.KEY_WRITE_PREVIEW_LIST);

                        mNotificationList.remove(mIndex);
                        SharedPreferencesManager.saveNotificationItem(mNotificationList,
                                SharedPreferencesManager.KEY_NOTIFY_FILE,
                                NotificationItem.KEY_NOTIFICATION_ITEM);

                        setResult(RESULT_OK);
                        finish();
                    }
                });

        builder.setNegativeButton("아니오", null);
        builder.show();
    }

    private void setLayoutGravity(ProgressBar progressBar, boolean isCenter){
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
        AdsManager.getInstance().adsLoad(mRecyclerAdapter, mLargestLastPostion,5);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}
