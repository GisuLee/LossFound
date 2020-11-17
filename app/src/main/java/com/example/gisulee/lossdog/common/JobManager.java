package com.example.gisulee.lossdog.common;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.gisulee.lossdog.data.entity.AlarmItem;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class JobManager {

    private static final String TAG = "JobManager";
    private static Context mContext;

    private JobManager(){
    }

    private static class ClassHolder {
        public static final JobManager Instance = new JobManager();
    }


    public static void init(Context context){
        mContext = context;
    }

    public static JobManager getInstance(){
        return ClassHolder.Instance;
    }


    public void startJob(int jobId){
        final JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(
                Context.JOB_SCHEDULER_SERVICE);

        // The JobService that we want to run
        final ComponentName name = new ComponentName(mContext, AlarmJobService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(jobScheduler.getPendingJob(jobId) !=null){
                return;
            }
        }
        // Schedule the job
        final int result = jobScheduler.schedule(getJobInfo(jobId, 1, name));

        // If successfully scheduled, log this thing
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Scheduled job successfully!");
        }
    }


    public void stopJob(int jobId){
        final JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(
                Context.JOB_SCHEDULER_SERVICE);

        jobScheduler.cancel(jobId);
    }

    public JobInfo getJobInfo(final int id, final long hour, final ComponentName name) {

        final long interval = TimeUnit.HOURS.toMillis(hour); // run every hour
        final boolean isPersistent = true; // persist through boot
        final int networkType = JobInfo.NETWORK_TYPE_ANY; // Requires some sort of connectivity

        final JobInfo jobInfo;

        jobInfo = new JobInfo.Builder(id, name)
                .setPeriodic(1000*60*15)
                .setRequiredNetworkType(networkType)
                .setPersisted(isPersistent)
                .build();


        return jobInfo;
    }


    public void startJobServices(ArrayList<AlarmItem> arrayList){
        for(int i=0; i<arrayList.size(); i++){
            if(arrayList.get(i).getAlarmOn()){
                startJob(arrayList.get(i).getName().hashCode());
            }
        }
    }


    public void stopstartJobServices(ArrayList<AlarmItem> arrayList){
        for(int i=0; i<arrayList.size(); i++){
            if(arrayList.get(i).getAlarmOn()){
                stopJob(arrayList.get(i).getName().hashCode());
                startJob(arrayList.get(i).getName().hashCode());
            }
        }
    }
}
