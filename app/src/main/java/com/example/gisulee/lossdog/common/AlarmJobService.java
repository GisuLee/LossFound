package com.example.gisulee.lossdog.common;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.gisulee.lossdog.view.listenerinterface.OnTaskWorkStateListener;
import com.example.gisulee.lossdog.data.entity.AlarmItem;
import com.example.gisulee.lossdog.data.entity.LossPolicePreviewItem;
import com.example.gisulee.lossdog.data.entity.NotificationItem;
import com.example.gisulee.lossdog.data.entity.Request;
import com.example.gisulee.lossdog.data.remote.AsyncTaskGetPreviewListPoliceAPI;

import java.util.ArrayList;

public class AlarmJobService extends JobService {

    private final String TAG = "AlarmJobService";


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        ArrayList<AlarmItem> mLoadAlarmItemList = new ArrayList();
        ArrayList<Object> mloadPreviewList = new ArrayList();

        MyNotificationManager.init(this);
        MyNotificationManager notificationManager = MyNotificationManager.getInstance();

        int mLoadAlarmIndex;
        AlarmItem mLoadAlarm;

        Log.d(TAG, "onStartJob: jobid : " + jobParameters.getJobId());

        mLoadAlarmItemList.clear();

        /* Load alarmItemList 알람리스트를 불러온다.  */
        SharedPreferencesManager.init(getApplicationContext());
        SharedPreferencesManager.loadAlarmData(mLoadAlarmItemList,
                SharedPreferencesManager.KEY_ALARM_FILE,
                AlarmItem.KEY_WRITE_ALARM_LIST);


        mLoadAlarmIndex = getMyAlarmIndex(mLoadAlarmItemList, jobParameters.getJobId());
        if (mLoadAlarmIndex == -1) {
            Log.d(TAG, "onStartJob: myAlarmId -1 Error");
            return false;
        }
        mLoadAlarm = mLoadAlarmItemList.get(mLoadAlarmIndex);

        int compareReulst = DateCalculator.compare(DateCalculator.getTodayToString(), mLoadAlarm.getLastUpdate());

        /* compareResult
         * case 0 getToday = LastUpdate
         * case 1 getToday > lastUpdate
         * case -1 getToday < lastUpdate*/
        switch (compareReulst) {

            case 0:
                 break;


            case 1:

                /* 설정된 알람(mLoadAlarm)을 토대로 번들 파라미터를 생성한다 */
                Bundle bundle[] = Request.getBundle(mLoadAlarm, DateCalculator.getConvertFormat("2019-11-17"));
// TODO: 2019-09-11
                /* 새로운 분실물 데이터를 불러온다*/
                new AsyncTaskGetPreviewListPoliceAPI(mloadPreviewList).setOnTaskWorkStateListener(new OnTaskWorkStateListener() {
                    @Override
                    public void onTaskExecute() {

                    }

                    @Override
                    public void onTaskPost(String resultCode, int totalCount) {
                        Log.d(TAG, "onTaskPost: resultCode " + resultCode + " totalCount =" + totalCount);

                        /* ResultCode가 00이면 성공을 의미한다.*/
                        if (resultCode == null || resultCode.equals(Request.KEY_REQUEST_SUCCESS_CODE) == false ) {
                            Log.d(TAG, "onTaskPost: 데이터를 불러오는데 실패핬습니다. resultCode = " + resultCode);

                            /* 두번째 파라미터(retry)가 true면 재실행 */
                            jobFinished(jobParameters, true);
                        }

                        if(totalCount == 0 ){
                            Log.d(TAG, "onTaskPost: 데이터가 없습니다. ," + totalCount);
                            jobFinished(jobParameters, false);
                        }

                        ArrayList<LossPolicePreviewItem> filterLossList = new ArrayList<>();
                        for(Object item : mloadPreviewList){
                            if(((LossPolicePreviewItem)item).fdSbjt.contains(mLoadAlarm.getProductFeature()) || mLoadAlarm.getProductFeature().equals(""))
                                filterLossList.add((LossPolicePreviewItem)item);
                        }

                        if(filterLossList.size() == 0){
                            Log.d(TAG, "filterLossLIst: 필터링된 데이터가 없습니다");
                            jobFinished(jobParameters, false);
                        }

                        /* 알림정보를 최신화한다. (마지막 수정일, 알림카운터 */
                        mLoadAlarm.setLastUpdate(DateCalculator.getTodayToString());
                        mLoadAlarm.setReceivedCount(mLoadAlarm.getReceivedCount() + filterLossList.size());
                        mLoadAlarmItemList.set(mLoadAlarmIndex, mLoadAlarm);

                        /* 최신화한 알림정보를 저장소에 저장.*/
                        SharedPreferencesManager.saveAlarmData(mLoadAlarmItemList,
                                SharedPreferencesManager.KEY_ALARM_FILE,
                                AlarmItem.KEY_WRITE_ALARM_LIST);


                        int notifyId = (int) (System.currentTimeMillis());
                        String title = mLoadAlarm.getName();
                        String subject = "혹시 잃어버린 물건이 이것인가요?\n" +
                                filterLossList.size() + "개의 새로운 습득물이 발견되었습니다!";

                        NotificationItem notiItem =
                                notificationManager.buildNotificationItem(
                                                    notifyId,
                                                    title,
                                                    subject,
                                                    NotificationItem.TYPE.ALARM);

                        /* 노티피케이션을 보여준다*/
                        notificationManager.showNotification(notiItem,NotificationCompat.PRIORITY_MAX);
                        /* 노티피케이션 데이터를 저장소에 최신화한다..*/
                        notificationManager.saveNotificationItem(notiItem);

                        /* 불러온 데이터를 저장한다.*/
                        SharedPreferencesManager.saveLossPreviewData(filterLossList
                                , Integer.toString(notifyId)                        //파일 이름
                                , LossPolicePreviewItem.KEY_WRITE_PREVIEW_LIST);          //키

                        sendBroadCastNotify();

                        /* 작업이 완료 됬으므로 끝*/
                        jobFinished(jobParameters, false);
                    }
                }).execute(bundle);

                break;
            case 2:

                break;

            default:
                Log.d(TAG, "onStartJob: compareReulst Error value = " + compareReulst);
        }

        return true;
    }




    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        Log.d(TAG, "onStopJob: ");
        return false;
    }

    private int getMyAlarmIndex(ArrayList<AlarmItem> arrayList, int jobId) {
        for (int i = 0; i < arrayList.size(); i++) {
            int id = arrayList.get(i).getName().hashCode();
            if (jobId == id) {
                Log.d(TAG, "getMyAlarmId: 선택된 알람은 " + i + "번째 알람");
                return i;
            }
        }
        return -1;
    }

    private void sendBroadCastNotify(){
        Intent intent = new Intent("com.example.gisulee.lossdog");
        sendBroadcast(intent);
    }



}
