package com.example.gisulee.lossdog.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gisulee.lossdog.data.entity.AlarmItem;
import com.example.gisulee.lossdog.common.JobManager;
import com.example.gisulee.lossdog.data.entity.LossPolicePreviewItem;
import com.example.gisulee.lossdog.data.entity.NotificationItem;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.Request;
import com.example.gisulee.lossdog.common.SharedPreferencesManager;
import com.example.gisulee.lossdog.view.component.SwipeToDeleteCallback;
import com.example.gisulee.lossdog.adapter.NotificationItemRecyclerAdapter;
import com.example.gisulee.lossdog.view.activity.AlarmListActivity;
import com.example.gisulee.lossdog.view.activity.LossDetailViewActivity;
import com.example.gisulee.lossdog.view.activity.LossPreviewListActivity;
import com.example.gisulee.lossdog.view.activity.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;

public class NotificationListFragment extends Fragment {

    private final String TAG = "NotificationList";
    private ViewGroup mContainer;
    private BroadcastReceiver mReceiver;
    private ArrayList<AlarmItem> mAlarmList = new ArrayList<>();
    private ArrayList<NotificationItem> mNotificationList = new ArrayList();
    private RecyclerView mRecyclerView;
    private NotificationItemRecyclerAdapter mRecyclerAdapter;
    private View mViewNoAlarm;
    private View mViewNoNotify;
    private JobManager jobManager = JobManager.getInstance();

    class AdapterDataObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            setInfoView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            setInfoView();
        }

        void setInfoView() {
            Log.d(TAG, "onChanged: ");
            if (mNotificationList.size() == 0) {
                Log.d(TAG, "onChanged: 0");
                mRecyclerView.setVisibility(View.GONE);
                if (mAlarmList.size() == 0) {
                    mViewNoNotify.setVisibility(View.GONE);
                    mViewNoAlarm.setVisibility(View.VISIBLE);
                    return;
                }
                mViewNoAlarm.setVisibility(View.GONE);
                mViewNoNotify.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mViewNoAlarm.setVisibility(View.GONE);
                mViewNoNotify.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void onDestroy() {

        /* 노티피케이션 데이터 저장*/
        SharedPreferencesManager.saveNotificationItem(mNotificationList,
                SharedPreferencesManager.KEY_NOTIFY_FILE,
                NotificationItem.KEY_NOTIFICATION_ITEM);

        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.notification_list, container, false);
        mContainer = view.findViewById(R.id.container);

        mViewNoAlarm = getNoAlarmView();
        mViewNoNotify = getNoNotifyView();
        mContainer.addView(mViewNoAlarm);
        mContainer.addView(mViewNoNotify);

        //어탭터와 알람리스트 연결
        mRecyclerAdapter = new NotificationItemRecyclerAdapter(getContext(), mNotificationList);

        //리사이클뷰 초기화 및 어댑터와 연결
        mRecyclerView = view.findViewById(R.id.recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mRecyclerAdapter);

        /* 리사이클러뷰 온클릭리스너, 새로운 분실물을 보여준다.*/
        mRecyclerAdapter.setOnItemClickListener(new NotificationItemRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String fileKey = mNotificationList.get(position).getId();
                mNotificationList.get(position).setReading(true);

                if (mNotificationList.get(position).getType() == NotificationItem.TYPE.ALARM)
                    startLossPreviewListActivity(fileKey, position);
                else
                    startLossDetailActivity(fileKey, position);

                /* 변경된 노티피케이션 데이터 저장*/
                SharedPreferencesManager.saveNotificationItem(mNotificationList,
                        SharedPreferencesManager.KEY_NOTIFY_FILE,
                        NotificationItem.KEY_NOTIFICATION_ITEM);
            }

        });


        loadSharedNotificationItem(mRecyclerAdapter, mNotificationList);

        enableSwipeToDeleteAndUndo(mRecyclerView, mRecyclerAdapter);

        mRecyclerAdapter.registerAdapterDataObserver(new AdapterDataObserver());

        Log.d(TAG, "onCreateView:  size : " + mNotificationList.size());
        return view;
    }


    private View getNoNotifyView() {
        final View view = getLayoutInflater().inflate(R.layout.info_no_notify, mContainer, false);
        return view;
    }

    private View getNoAlarmView() {
        final View view = getLayoutInflater().inflate(R.layout.info_no_alarm_reg, mContainer, false);
        final TextView btnAlarmAdd = view.findViewById(R.id.btn_add_alarm);
        btnAlarmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAlarmAddActivity();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setOnMenuButtonClickListener(new MainActivity.OnMenuButtonClickListener() {

                /* 알람추가 버튼이 눌리면 알람등록액티비티를 띄운다. */
                @Override
                public void onClick() {
                    startAlarmActivity();
                }
            });

            ((MainActivity) getActivity()).setOnAlarmAddClickListener(new MainActivity.OnAlarmAddClickListener() {

                /* 알람추가 버튼이 눌리면 알람등록액티비티를 띄운다. */
                @Override
                public void onClick() {
                    startAlarmActivity();
                }
            });

        }
    }

    @Override
    public void onResume() {
        mAlarmList.clear();
        SharedPreferencesManager.loadAlarmData(mAlarmList,
                SharedPreferencesManager.KEY_ALARM_FILE,
                AlarmItem.KEY_WRITE_ALARM_LIST);
        new AdapterDataObserver().setInfoView();

        /* 브로드 캐스트 리시버 등록*/
        registerBroadCastReicever();
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Request.REQUEST_VIEW_LIST) {
            if (resultCode == RESULT_OK) {
                //loadSharedNotificationItem(mRecyclerAdapter,mNotificationList);
            }

        }
        loadSharedNotificationItem(mRecyclerAdapter, mNotificationList);
    }

    private boolean isAlarmEmpty(ArrayList<AlarmItem> arrayList) {
        arrayList.clear();
        SharedPreferencesManager.init(getContext());
        SharedPreferencesManager.loadAlarmData(arrayList,
                SharedPreferencesManager.KEY_ALARM_FILE,
                AlarmItem.KEY_WRITE_ALARM_LIST);

        if (arrayList.size() == 0) {
            if (mNotificationList.size() == 0)
                return true;
            else
                return false;
        } else
            return false;
    }

    private void startAlarmActivity() {
        Intent intent = new Intent(getActivity(), AlarmListActivity.class);
        startActivity(intent);
    }

    private void startAlarmAddActivity() {
        Intent intent = new Intent(getActivity(), AlarmListActivity.class);
        intent.putExtra(Request.KEY_ALARM_ADD_AUTOMATIC, true);
        startActivity(intent);
    }

    private void startLossPreviewListActivity(String fileKey, int index) {
        Intent intent = new Intent(getActivity(), LossPreviewListActivity.class);
        intent.putExtra(Request.KEY_FILE_NAME, fileKey);
        intent.putExtra(Request.KEY_INDEX, index);
        startActivityForResult(intent, Request.REQUEST_VIEW_LIST);
    }

    private void startLossDetailActivity(String fileKey, int index) {

        Intent intent = new Intent(getActivity(), LossDetailViewActivity.class);
        intent.putExtra(Request.KEY_FILE_NAME, fileKey);
        intent.putExtra(Request.KEY_INDEX, index);
        startActivityForResult(intent, 0);
    }

    private void showDeleteDialog(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
        builder.setTitle("알림 삭제");
        builder.setMessage("알림을 정말로 삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        /* 알림 Preivew 리스트  삭제 */
                        SharedPreferencesManager.remove(
                                mNotificationList.get(index).getId(),
                                LossPolicePreviewItem.KEY_WRITE_PREVIEW_LIST);

                        /* 데이터를 지우고 commit한다 */
                        mNotificationList.remove(index);

                        /* 삭제되어 변경된 노티피케이션 데이터 저장*/
                        SharedPreferencesManager.saveNotificationItem(mNotificationList,
                                SharedPreferencesManager.KEY_NOTIFY_FILE,
                                NotificationItem.KEY_NOTIFICATION_ITEM);

                        loadSharedNotificationItem(mRecyclerAdapter, mNotificationList);
                    }
                });

        builder.setNegativeButton("아니오", null);
        builder.show();
    }


    private void registerBroadCastReicever() {
        if(mReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.example.gisulee.lossdog");
            intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    switch (intent.getAction()) {
                        case "com.example.gisulee.lossdog":
                            Log.d(TAG, "onReceive: lossdog notify");
                            loadSharedNotificationItem(mRecyclerAdapter, mNotificationList);
                            break;
                        case Intent.ACTION_BOOT_COMPLETED:
                            Log.d(TAG, "onReceive: ACTION_BOOT_COMPLETED notify");
                            jobManager.startJobServices(mAlarmList);
                            break;

                        default:
                            break;

                    }

                }
            };

            getActivity().registerReceiver(mReceiver, intentFilter);
        }
    }

    private void loadSharedNotificationItem(NotificationItemRecyclerAdapter adapter, ArrayList<NotificationItem> arrayList) {
        arrayList.clear();
        SharedPreferencesManager.init(getContext());
        SharedPreferencesManager.loadNotificationItem(arrayList,
                SharedPreferencesManager.KEY_NOTIFY_FILE,
                NotificationItem.KEY_NOTIFICATION_ITEM);

        Collections.sort(arrayList);
        adapter.notifyDataSetChanged();

        ((MainActivity) getActivity()).setTabLayoutNotify(2, getNotifyCount(arrayList));
    }


    private int getNotifyCount(ArrayList<NotificationItem> arrayList) {
        int count = 0;

        for (int i = 0; i < arrayList.size(); i++) {
            if (!arrayList.get(i).isReading())
                count += 1;
        }
        return count;
    }

    private void enableSwipeToDeleteAndUndo(RecyclerView recyclerView, NotificationItemRecyclerAdapter mAdapter) {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final NotificationItem item = mAdapter.getData().get(position);
                final boolean[] isRemoved = {true};
                View view = getActivity().getWindow().getDecorView();
                CoordinatorLayout ftnBtn = view.findViewById(R.id.coordinator);
                isRemoved[0] = true;
                mAdapter.removeItem(position);
                Snackbar snackbar = Snackbar
                        .make(ftnBtn, "알림을 삭제하였습니다.", Snackbar.LENGTH_LONG);
                TextView tv = snackbar.getView().findViewById(R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);

                snackbar.setAction("취소", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isRemoved[0] = false;
                        mAdapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {

                        if (isRemoved[0]) {
                            /* 알림 Preivew 리스트  삭제 */
                            SharedPreferencesManager.remove(
                                    item.getId(),
                                    LossPolicePreviewItem.KEY_WRITE_PREVIEW_LIST);


                            /* 삭제되어 변경된 노티피케이션 데이터 저장*/
                            SharedPreferencesManager.saveNotificationItem(mNotificationList,
                                    SharedPreferencesManager.KEY_NOTIFY_FILE,
                                    NotificationItem.KEY_NOTIFICATION_ITEM);

                            ((MainActivity) getActivity()).setTabLayoutNotify(2, getNotifyCount(mNotificationList));
                        }
                    }
                });
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

}
