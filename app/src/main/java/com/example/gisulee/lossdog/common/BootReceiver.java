package com.example.gisulee.lossdog.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.gisulee.lossdog.data.entity.AlarmItem;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // 핸드폰이 재부팅 되었을경우
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals("android.intent.action.ACTION_BOOT_COMPLETED")) {
            Log.d("BootReceived", "onReceive: ");
            ArrayList<AlarmItem> mAlarmList = new ArrayList();

            // 잡매니저(서비스) 인스턴스 구하기
            JobManager.init(context);
            JobManager jobManager = JobManager.getInstance();

            SharedPreferencesManager.init(context);
            SharedPreferencesManager.loadAlarmData(mAlarmList,
                    SharedPreferencesManager.KEY_ALARM_FILE,
                    AlarmItem.KEY_WRITE_ALARM_LIST);

            // 잡매니저(서비스) 실행
            jobManager.stopstartJobServices(mAlarmList);
        }

    }
}
