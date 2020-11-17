package com.example.gisulee.lossdog.data.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationItem implements Serializable, Comparable<NotificationItem> {
    public static enum TYPE{ALARM, DETAIL_ITEM};
    public static final String KEY_NOTIFICATION_ITEM = "KEY_NOTIFICATION_ITEM";
    private String id;
    private long time;
    private String strTime;
    private String title;
    private String content;
    private boolean isReading;
    private TYPE type;

    public NotificationItem() {
    }

    public TYPE getType() {
        return type;
    }

    public NotificationItem setType(TYPE type) {
        this.type = type;
        return this;
    }

    public NotificationItem(String id, long time, String title, String content, boolean isReading, TYPE type) {
        this.id = id;
        this.time = time;
        this.title = title;
        this.content = content;
        this.isReading = isReading;
        this.type = type;
        if (strTime == null) {
            setStrTime();
        }
    }

    public String getStrTime() {
        return strTime;
    }

    public NotificationItem setStrTime() {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        Date date = new Date(time);
        this.strTime = format.format(date);
        return this;
    }

    public String getId() {
        return id;
    }

    public NotificationItem setId(String id) {
        this.id = id;
        return this;
    }

    public long getTime() {
        return time;
    }

    public NotificationItem setTime(long time) {
        this.time = time;
        setStrTime();
        return this;
    }

    public String getTitle() {
        return title;
    }

    public NotificationItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public NotificationItem setContent(String content) {
        this.content = content;
        return this;
    }

    public boolean isReading() {
        return isReading;
    }

    public NotificationItem setReading(boolean reading) {
        isReading = reading;
        return this;
    }

    @Override
    public int compareTo(NotificationItem notificationItem) {
        long dis = this.time - notificationItem.time;
        return (int)-dis;
    }
}
