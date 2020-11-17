package com.example.gisulee.lossdog.view.activity;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.data.entity.AlarmItem;
import com.example.gisulee.lossdog.common.JobManager;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.Request;
import com.example.gisulee.lossdog.common.SharedPreferencesManager;
import com.example.gisulee.lossdog.SwipeController;
import com.example.gisulee.lossdog.view.component.SwipeControllerActions;
import com.example.gisulee.lossdog.adapter.AlarmListRecyclerAdapter;

import java.util.ArrayList;

public class AlarmListActivity extends AppCompatActivity {

    private final String TAG = "AlarmListActivity";
    private final String SHARED_PREF = "shard_pef";
    private BroadcastReceiver mReceiver;
    private ArrayList<AlarmItem> mAlarmItemList = new ArrayList();
    private RecyclerView mRecyclerView;
    private AlarmListRecyclerAdapter mRecyclerAdapter;
    private ViewGroup mContainer;
    private View mViewNoAlarm;
    private JobManager jobManager = JobManager.getInstance();

    class AdapterDataObserver extends RecyclerView.AdapterDataObserver{

        @Override
        public void onChanged() {
            setInfoView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            setInfoView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            setInfoView();
        }

        void setInfoView(){
            Log.d(TAG, "onChanged: ");
            if(mAlarmItemList.size() == 0) {
                mViewNoAlarm.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }else{
                mViewNoAlarm.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jobManager.startJobServices(mAlarmItemList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AdapterDataObserver().setInfoView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        mContainer = findViewById(R.id.container);
        mViewNoAlarm = getNoAlarmView();
        mContainer.addView(mViewNoAlarm);

        mRecyclerAdapter = new AlarmListRecyclerAdapter(this, mAlarmItemList);
        mRecyclerAdapter.registerAdapterDataObserver(new AdapterDataObserver());

        /* 툴바 설정*/
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        //리사이클뷰 초기화 및 어댑터와 연결
        mRecyclerView = findViewById(R.id.recycleView) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mRecyclerAdapter) ;

        /* 리사이클러뷰 온클릭리스너, 새로운 분실물을 보여준다.*/
        mRecyclerAdapter.setOnItemClickListener(new AlarmListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                startEditActivity(position);
            }

            @Override
            public void onSwitchClick(View view, boolean b, int pos) {

                mAlarmItemList.get(pos).setAlarmOn(b);
                Log.d(TAG, "onSwitchClick: " + mAlarmItemList.get(pos).getAlarmOn());
                SharedPreferencesManager.saveAlarmData(mAlarmItemList,
                        SharedPreferencesManager.KEY_ALARM_FILE,
                        AlarmItem.KEY_WRITE_ALARM_LIST);

                mRecyclerAdapter.notifyItemChanged(pos);

                if(!b){
                    jobManager.stopJob(mAlarmItemList.get(pos).getName().hashCode());
                }
            }
        });

        /* 스와이프 컨트롤러 온클릭 리스너*/
        SwipeController swipeController = new SwipeController(this,new SwipeControllerActions() {

            /* 삭제하기 버튼*/
            @Override
            public void onRightClicked(int index) {
                showDeleteDialog(index);
            }

            /*편집하기 버튼*/
            @Override
            public void onLeftClicked(int index) {
                startEditActivity(index);
            }
        });

        /* 리사이클러뷰 아이템 스와이프 터치이벤트 설정*/
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        //저장된 알림리스트 불러오기
        refreshSharedAlarmList(mRecyclerAdapter, mAlarmItemList,false);

        Intent intent = getIntent();
        boolean isAutomaticAdd = intent.getBooleanExtra(Request.KEY_ALARM_ADD_AUTOMATIC,false);

        if(isAutomaticAdd){
            startAddActivity();
        }

        Log.d(TAG, "onCreateView: itemsize = " + mAlarmItemList.size());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.alarmlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
            case R.id.btn_add: {
                startAddActivity();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private View getNoAlarmView(){
        final View view = getLayoutInflater().inflate(R.layout.info_no_alarm_reg, mContainer, false);
        final TextView btnAlarmAdd = view.findViewById(R.id.btn_add_alarm);
        btnAlarmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddActivity();
            }
        });

        return view;
    }
    private void refreshSharedAlarmList(AlarmListRecyclerAdapter mRecyclerAdapter, ArrayList<AlarmItem> mAlarmItemList, boolean isEndScroll) {
        mAlarmItemList.clear();
        SharedPreferencesManager.init(this);
        SharedPreferencesManager.loadAlarmData(mAlarmItemList,
                SharedPreferencesManager.KEY_ALARM_FILE,
                AlarmItem.KEY_WRITE_ALARM_LIST);

        mRecyclerAdapter.notifyDataSetChanged();
        if(isEndScroll)
            mRecyclerView.scrollToPosition(mRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        /* 알람추가 요청에 대한 응답이이 OK이면 */
        if(requestCode== Request.REQUEST_ADD_ALARM){

            if(resultCode== RESULT_OK){
                AlarmItem alarmItem = (AlarmItem) intent.getSerializableExtra(Request.KEY_ALARM_ITEM);
                Log.d(TAG, "onActivityResult: alarmItem"+ alarmItem.getAlarmOn());
                mAlarmItemList.add(alarmItem);
                mRecyclerAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mRecyclerAdapter.getItemCount() - 1);

                SharedPreferencesManager.saveAlarmData(mAlarmItemList,
                        SharedPreferencesManager.KEY_ALARM_FILE,
                        AlarmItem.KEY_WRITE_ALARM_LIST);

                if(!alarmItem.getAlarmOn())
                    jobManager.stopJob(alarmItem.getName().hashCode());
            }

            /* 알림 수정 요청에 대한 응답이 OK이면*/
        }else if(requestCode == Request.REQUEST_EDIT_ALARM){

            if(resultCode == RESULT_OK){
                AlarmItem alarmItem = (AlarmItem) intent.getSerializableExtra(Request.KEY_ALARM_ITEM);
                int index = intent.getIntExtra(Request.KEY_INDEX, -1);
                if(index == -1){
                    Log.d(TAG, "onActivityResult: REQUEST_EDIT_ALARM, RESULT_CODE = 0");
                    return ;
                }

                mAlarmItemList.set(index, alarmItem);
                mRecyclerAdapter.notifyDataSetChanged();
                SharedPreferencesManager.saveAlarmData(mAlarmItemList,
                        SharedPreferencesManager.KEY_ALARM_FILE,
                        AlarmItem.KEY_WRITE_ALARM_LIST);

                if(!alarmItem.getAlarmOn())
                    jobManager.stopJob(alarmItem.getName().hashCode());
            }
        }
    }


    private void startAddActivity() {
        Intent intent = new Intent(this, AlarmRegisterActivity.class);
        intent.putExtra(Request.KEY_REQUEST_CODE, Request.REQUEST_ADD_ALARM);
        intent.putExtra(Request.KEY_ALARM_LIST, mAlarmItemList);
        startActivityForResult(intent, Request.REQUEST_ADD_ALARM);
    }

    private void startEditActivity(int index) {
        Intent intent = new Intent(this, AlarmRegisterActivity.class);
        intent.putExtra(Request.KEY_REQUEST_CODE, Request.REQUEST_EDIT_ALARM);
        intent.putExtra(Request.KEY_INDEX, index);
        intent.putExtra(Request.KEY_ALARM_LIST, mAlarmItemList);
        intent.putExtra(Request.KEY_ALARM_ITEM, mAlarmItemList.get(index));
        startActivityForResult(intent, Request.REQUEST_EDIT_ALARM);
    }

    private void startPreviewActivity(int index) {
        /* 인텐트에 알람 아이템의 알람이름을 해쉬코드로 보낸다 */
        Intent intent = new Intent(this, AlarmPreviewListActivity.class);
        intent.putExtra(Request.KEY_ALARM_LIST, mAlarmItemList);
        intent.putExtra(Request.KEY_ALARM_FILENAME, mAlarmItemList.get(index).getName().hashCode());
        startActivityForResult(intent, Request.REQUEST_VIEW_LIST);
        mAlarmItemList.get(index).setReceivedCount(0);
    }


    private void showDeleteDialog(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle("알림 삭제");
        builder.setMessage("알림을 정말로 삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        /* 설정된 잡을 캔슬한다*/
                        jobManager.stopJob(mAlarmItemList.get(index).getName().hashCode());

                        /* 알림 Preivew 리스트  삭제 */
                        SharedPreferencesManager.remove(
                                Integer.toString(mAlarmItemList.get(index).getName().hashCode()),
                                AlarmItem.KEY_WRITE_ALARM_PREVIEW_LIST);

                        /* 데이터를 지우고 commit한다 */
                        mAlarmItemList.remove(index);
                        mRecyclerAdapter.notifyItemRemoved(index);
                        //mRecyclerAdapter.notifyItemRangeChanged(index,mAlarmItemList.size());

                        /* 삭제되어 변경된 알림리스트 저장*/
                        SharedPreferencesManager.saveAlarmData(mAlarmItemList,
                                SharedPreferencesManager.KEY_ALARM_FILE,
                                AlarmItem.KEY_WRITE_ALARM_LIST);
                    }
                });

        builder.setNegativeButton("아니오", null);
        builder.show();
    }


}
