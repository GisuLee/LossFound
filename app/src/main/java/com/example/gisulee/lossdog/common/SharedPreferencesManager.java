package com.example.gisulee.lossdog.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.gisulee.lossdog.data.entity.AlarmItem;
import com.example.gisulee.lossdog.data.entity.AppSettings;
import com.example.gisulee.lossdog.data.entity.LossPoliceDetailItem;
import com.example.gisulee.lossdog.data.entity.LossPolicePreviewItem;
import com.example.gisulee.lossdog.data.entity.NotificationItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesManager {

    private static final String TAG = "SharedPreferences";
    public static final String KEY_ALARM_FILE = "ALARM_FILE";
    public static final String KEY_NOTIFY_FILE = "NOTIFY_FILE";

    public static Context mContext = null;

    public static void init(Context context) {
        if (mContext == null)
            mContext = context;
    }

    /* 알림리스트 저장하기*/
    public static void saveAlarmData(ArrayList<AlarmItem> arrayList, String keyFile, String keyObject) {

        if (mContext == null) {
            Log.d(TAG, "saveAlarmData: context not init");
            return;
        }

        SharedPreferences preferences = mContext.getSharedPreferences(keyFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<AlarmItem>>() {
        }.getType();
        String json = gson.toJson(arrayList, listType);
        editor.putString(keyObject, json);
        editor.commit();

        Log.d(TAG, "saveAlarmData: commit, array size = " + arrayList.size() + "keyFile = " + keyFile + " keyObject = " + keyObject);
    }

    /* 알림리스트 불러오기*/
    public static void loadAlarmData(ArrayList<AlarmItem> arrayList, String keyFile, String keyObject) {

        if (mContext == null) {
            Log.d(TAG, "loadAlarmData: context not init");
            return;
        }

        ArrayList<AlarmItem> temp;

        SharedPreferences preferences = mContext.getSharedPreferences(keyFile, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(keyObject, "");
        Type listType = new TypeToken<ArrayList<AlarmItem>>() {
        }.getType();
        temp = gson.fromJson(json, listType);
        if (temp != null)
            arrayList.addAll(temp);
        Log.d(TAG, "loadAlarmData size = " + arrayList.size() + " keyFile = " + keyFile + " keyObject = " + keyObject);

    }

    /* 습득물 저장하기 */
    public static void saveLossPreviewData(ArrayList<LossPolicePreviewItem> arrayList, String keyFile, String keyObject) {

        if (mContext == null) {
            Log.d(TAG, "saveLossPreviewData: context not init");
            return;
        }

        SharedPreferences preferences = mContext.getSharedPreferences(keyFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<LossPolicePreviewItem>>() {
        }.getType();
        String json = gson.toJson(arrayList, listType);
        editor.putString(keyObject, json);
        editor.commit();

        Log.d(TAG, "saveLossPreviewData: commit keyFild = " + keyFile + " ,keyObject = " + keyObject);
    }

    /* 습득물 불러오기*/
    public static void loadLossPreviewData(ArrayList<Object> arrayList, String keyFile, String keyObject) {
        if (mContext == null) {
            Log.d(TAG, "loadLossPreviewData: context not init");
            return;
        }

        ArrayList<LossPolicePreviewItem> temp;

        SharedPreferences preferences = mContext.getSharedPreferences(keyFile, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(keyObject, "");
        Type listType = new TypeToken<ArrayList<LossPolicePreviewItem>>() {
        }.getType();
        temp = gson.fromJson(json, listType);
        if (temp != null) {
            arrayList.addAll(temp);
        }

        Log.d(TAG, "loadLossPreviewData size = " + arrayList.size() + " keyFile = " + keyFile + " keyObject = " + keyObject);
    }


    /* 앱 셋팅 저장하기*/
    public static void saveAppSettings(AppSettings settings, String keyFile, String keyObject) {

        if (mContext == null) {
            Log.d(TAG, "saveAppSettings: context not init");
            return;
        }

        SharedPreferences preferences = mContext.getSharedPreferences(keyFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        Type listType = new TypeToken<AppSettings>() {
        }.getType();
        String json = gson.toJson(settings, listType);
        editor.putString(keyObject, json);
        editor.commit();
        Log.d(TAG, "saveAppSettings: json = " + json);
        Log.d(TAG, "saveAppSettings: commit");
    }

    /* 앱 셋팅 불러오기*/
    public static void loadAppSettings(AppSettings settings, String keyFile, String keyObject) {
        if (mContext == null) {
            Log.d(TAG, "loadLossPreviewData: context not init");
            return;
        }

        AppSettings temp;
        SharedPreferences preferences = mContext.getSharedPreferences(keyFile, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(keyObject, "");
        Type listType = new TypeToken<AppSettings>() {
        }.getType();
        temp = gson.fromJson(json, listType);
        if (temp != null) {
            settings.setSelectedPrdList(temp.getSelectedPrdList());
            settings.setSelectedAreaList(temp.getSelectedAreaList());
        }
        Log.d(TAG, "loadAppSettings");
    }


    public static void remove(String keyFile, String keyObject) {
        SharedPreferences preferences = mContext.getSharedPreferences(keyFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(keyObject);
        editor.commit();
    }

    /* 알림 저장하기 */
    public static void saveNotificationItem(ArrayList<NotificationItem> arrayList, String keyFile, String keyObject) {

        if (mContext == null) {
            Log.d(TAG, "saveLossPreviewData: context not init");
            return;
        }

        SharedPreferences preferences = mContext.getSharedPreferences(keyFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<NotificationItem>>() {
        }.getType();
        String json = gson.toJson(arrayList, listType);
        editor.putString(keyObject, json);
        editor.commit();

        Log.d(TAG, "saveNotificationItem: size = " + arrayList.size() + "commit keyFild = " + keyFile + " ,keyObject = " + keyObject);
    }

    /* 알림 불러오기*/
    public static void loadNotificationItem(ArrayList<NotificationItem> arrayList, String keyFile, String keyObject) {
        if (mContext == null) {
            Log.d(TAG, "loadLossPreviewData: context not init");
            return;
        }

        ArrayList<NotificationItem> temp;

        SharedPreferences preferences = mContext.getSharedPreferences(keyFile, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(keyObject, "");
        Type listType = new TypeToken<ArrayList<NotificationItem>>() {
        }.getType();
        temp = gson.fromJson(json, listType);
        if (temp != null) {
            arrayList.addAll(temp);
        }

        Log.d(TAG, "loadNotificationItem size = " + arrayList.size() + " keyFile = " + keyFile + " keyObject = " + keyObject);
    }

    /* 습득물 저장하기*/
    public static void saveLossDetailItem(LossPoliceDetailItem item, String keyFile, String keyObject, Bitmap bitmap) {

        if (mContext == null) {
            Log.d(TAG, "saveAlarmData: context not init");
            return;
        }

        SharedPreferences preferences = mContext.getSharedPreferences(keyFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(item);
        editor.putString(keyObject, json);
        editor.putString("img", encodeTobase64(bitmap));
        editor.commit();
        Log.d(TAG, "saveLossPreviewItem  keyFile = " + keyFile + " keyObject = " + keyObject);
    }

    /* 알림 불러오기*/
    public static LossPoliceDetailItem loadLossDetailItem(String keyFile, String keyObject, ImageView imageView) {
        if (mContext == null) {
            Log.d(TAG, "loadLossDetailItem: context not init");
            return null;
        }

        LossPoliceDetailItem temp;

        SharedPreferences preferences = mContext.getSharedPreferences(keyFile, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(keyObject, "");
        String encodeBitmap = preferences.getString("img", "");
        Bitmap bitmap = decodeBase64(encodeBitmap);
        temp = gson.fromJson(json, LossPoliceDetailItem.class);
        Log.d(TAG, "loadLossDetailItem: tempjson " + json);
        imageView.setImageBitmap(bitmap);
        Log.d(TAG, "loadLossDetailItem keyFile = " + keyFile + " keyObject = " + keyObject);
        return temp;

    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
