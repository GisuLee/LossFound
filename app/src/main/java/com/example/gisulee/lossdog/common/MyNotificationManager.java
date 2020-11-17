package com.example.gisulee.lossdog.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.NotificationItem;
import com.example.gisulee.lossdog.data.entity.Request;
import com.example.gisulee.lossdog.view.activity.MainActivity;

import java.util.ArrayList;

public class MyNotificationManager {

    private static Context mContext;

    private MyNotificationManager(){
    }

    private static class ClassHolder {
        public static final MyNotificationManager Instance = new MyNotificationManager();
    }

    public static void init(Context context){
        mContext = context;
    }

    public static MyNotificationManager getInstance(){
        return ClassHolder.Instance;
    }

    public void showNotification(NotificationItem notyItem, int priority){

        int notyid = Integer.parseInt(notyItem.getId());
        String title = notyItem.getTitle();
        String content = notyItem.getContent();

        NotificationCompat.Builder notificationBuilder;
        Intent notificationIntent = new Intent(mContext, MainActivity.class);
        notificationIntent.putExtra(Request.KEY_NOTIFY_SHOW_AUTOMATIC, true);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel mChannel = new NotificationChannel(Integer.toString(notyid), "새 습득물 알림", android.app.NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            notificationBuilder = new NotificationCompat.Builder(mContext,mChannel.getId());
        } else {
            notificationBuilder = new NotificationCompat.Builder(mContext);
        }

        notificationBuilder.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(priority)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        notificationManager.notify(notyid, notificationBuilder.build());

    }

    public void saveNotificationItem(NotificationItem notificationItem) {

        ArrayList<NotificationItem> notificationItems = new ArrayList<>();

        SharedPreferencesManager.loadNotificationItem(notificationItems,
                SharedPreferencesManager.KEY_NOTIFY_FILE,
                NotificationItem.KEY_NOTIFICATION_ITEM);

        notificationItems.add(notificationItem);

        SharedPreferencesManager.saveNotificationItem(notificationItems,
                SharedPreferencesManager.KEY_NOTIFY_FILE,
                NotificationItem.KEY_NOTIFICATION_ITEM);
    }

    public NotificationItem buildNotificationItem(int notyid, String title, String content, NotificationItem.TYPE type){

        long now = System.currentTimeMillis();
        NotificationItem notifyItem = new NotificationItem();
        notifyItem.setId(Integer.toString(notyid))
                .setTime(now)
                .setTitle(title)
                .setContent(content)
                .setReading(false)
                .setType(type);

        return notifyItem;
    }
}
