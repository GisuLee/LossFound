package com.example.gisulee.lossdog.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gisulee.lossdog.common.DataManager;
import com.example.gisulee.lossdog.data.entity.LossPoliceDetailItem;
import com.example.gisulee.lossdog.data.entity.LossPolicePreviewItem;
import com.example.gisulee.lossdog.data.entity.LossPreviewItem;
import com.example.gisulee.lossdog.common.MyNotificationManager;
import com.example.gisulee.lossdog.data.entity.NotificationItem;
import com.example.gisulee.lossdog.R;
import com.example.gisulee.lossdog.data.entity.Request;
import com.example.gisulee.lossdog.common.SharedPreferencesManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class LossDetailViewActivity extends AppCompatActivity {

    private final String TAG = "LossDetailViewActivity";

    public Toolbar mToolbar;
    public ImageView mImageView;
    public TextView mTxtSubject;
    public TextView mTxtProductName;
    public TextView mTxtProductCategory;
    public TextView mTxtLossPlace;
    public TextView mTxtDepPlace;
    public TextView mTxtPickPlace;
    public LinearLayout mContent;
    public LinearLayout mLinear_progress;
    public FrameLayout btnSave;
    public LossPoliceDetailItem mLossDetailItem;
    public String mFileKey;
    public boolean isLoading = false;
    public ArrayList<NotificationItem> mNotificationList = new ArrayList<>();
    public ProgressDialog progressDialog;
    public int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loss_detail_view_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        //저장하지 않은 디테일뷰는 AsynTask의 파라미터로 readData를 넘긴다
        Bundle readData = getIntent().getBundleExtra(Request.KEY_PREVIEW_ITEM);

        //저장한 디테일뷰는 mfileKey를 통해 읽는다.
        mFileKey = getIntent().getStringExtra(Request.KEY_FILE_NAME);
        mIndex = getIntent().getIntExtra(Request.KEY_INDEX, -1);

        Log.d(TAG, "onCreate: mFileKey" + mFileKey);
        btnSave = findViewById(R.id.btn_save);
        mContent = findViewById(R.id.content_list);
        mLinear_progress = findViewById(R.id.linear_progress);
        mImageView = (ImageView) findViewById(R.id.lostImageView);
        mTxtProductName = (TextView) findViewById(R.id.txtProductFeature);
        mTxtProductCategory = (TextView) findViewById(R.id.txtProductCategory);
        mTxtDepPlace = (TextView) findViewById(R.id.txtDepPlace);
        mTxtPickPlace = (TextView) findViewById(R.id.txtPickPlace);
        mTxtSubject = (TextView) findViewById(R.id.txtSubject);
        progressDialog = new ProgressDialog(this, R.style.ProgressDailogStyle);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PhotoDetailViewActivity.class);
                startActivity(intent);

            }
        });

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isLoading) return;
                if (mImageView.getDrawable() == null) return;

                new saveDetailItem().execute();
                /*progressDialog.setMessage("저장 중...");
                progressDialog.show();

                int notifyId = (int) (System.currentTimeMillis());
                String title = "습득물 저장";
                String subject = mTxtProductName.getText() + " 물건이 저장되었습니다.";

                Thread saveTask = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MyNotificationManager notificationManager = MyNotificationManager.getInstance();

                        NotificationItem notiItem = notificationManager.buildNotificationItem(notifyId, title, subject, NotificationItem.TYPE.DETAIL_ITEM);
                        notificationManager.saveNotificationItem(notiItem);
                        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                        SharedPreferencesManager.init(getApplicationContext());
                        SharedPreferencesManager.saveLossDetailItem(mLossDetailItem, Integer.toString(notifyId), LossPoliceDetailItem.KEY_WRITE, bitmap);

                        Intent intent = new Intent("com.example.gisulee.lossdog");
                        sendBroadcast(intent);
                    }
                });
                saveTask.start();

                try {
                    saveTask.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

               // progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), subject, Toast.LENGTH_SHORT).show();*/

            }
        });
        //mContent.setVisibility(View.VISIBLE);
        //mLinear_progress.setVisibility(View.GONE);
        if (mFileKey != null) {
            SharedPreferencesManager.init(this);
            SharedPreferencesManager.loadNotificationItem(mNotificationList,
                    SharedPreferencesManager.KEY_NOTIFY_FILE,
                    NotificationItem.KEY_NOTIFICATION_ITEM);

            mLossDetailItem = SharedPreferencesManager.loadLossDetailItem(mFileKey, LossPoliceDetailItem.KEY_WRITE, mImageView);
            if (mLossDetailItem == null) {
                Toast.makeText(this, "잘못된 파일입니다", Toast.LENGTH_LONG).show();
                return;
            }

            isLoading = true;
            btnSave.setVisibility(View.GONE);
            setUI(mLossDetailItem);

            return;
        } else {

           LossPreviewItem lossPreviewItem = (LossPreviewItem) getIntent().getSerializableExtra(Request.KEY_PREVIEW_SEARCH);

           lossPreviewItem.setUI(this,readData);
        /*    if (readData == null) {
                Log.d(TAG, "onCreate: intent 전달 에러");
                return;
            }

            Bitmap image = DataManager.getInstance().getBitmap();
            if (image != null)
                mImageView.setImageBitmap(image);

            mContent.setVisibility(View.GONE);
            new GetXMLTask().setOnTaskWorkStateListener(new OnTaskWorkStateListener() {
                @Override
                public void onTaskPost(LossPoliceDetailItem item) {

                    if (item == null) {
                        Log.d(TAG, "onTaskPost: onTaskPost load Error");
                        return;
                    }
                    isLoading = true;
                    mLossDetailItem = item;
                    setUI(mLossDetailItem);

                }

            }).execute(readData);*/
        }


    }

    @Override
    public void onBackPressed() {
        // supportFinishAfterTransition();
        super.onBackPressed();
    }

    public void setUI(LossPoliceDetailItem item) {

        if (mFileKey == null && DataManager.getInstance().getBitmap() == null) {
            Log.d(TAG, "setUI: ddddd");
            Picasso.with(getApplicationContext()).load(item.imageURL).into(mImageView);
        }

        String strPickPlace = item.pickPlace;

        if(item.pickDate != null){
            if(item.pickTime == null)
                strPickPlace += " (" + item.pickDate +")" ;
            else
                strPickPlace += " (" + item.pickDate + " " + item.pickTime + "시 경)";
        }


        if(item.subject == null)
            mTxtSubject.setVisibility(View.GONE);

        mTxtProductName.setText(item.productName);
        mTxtProductCategory.setText(item.productCategory);
        mTxtDepPlace.setText(item.depPlace);
        mTxtPickPlace.setText(strPickPlace);
        mTxtSubject.setText(item.subject);

        mContent.setVisibility(View.VISIBLE);
        mLinear_progress.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        //저장하기 한 디테일뷰면 메뉴버튼을 만들지 않는다.
        if (mFileKey != null)
            menuInflater.inflate(R.menu.remove_alarm, menu);

        menuInflater.inflate(R.menu.call_place, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_all_remove:
                showRemoveDialog();
                return true;

            case R.id.action_call:
                Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mLossDetailItem.depPlaceTel));
                startActivity(tt);
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

    private void showDataLoadSnackBar(View view, Resources res, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackground(res.getDrawable(R.drawable.shape_corner_rounding_border_gray));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(50, 0, 50, 130);
        snackbar.getView().setLayoutParams(params);
        TextView tv = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }

    class saveDetailItem extends AsyncTask<Void, Void, Void> {

        private int notifyId;
        private String title;
        private String subject;
        private Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("저장 중...");
            progressDialog.show();

            bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
            notifyId = (int) (System.currentTimeMillis());
            title = "습득물 저장";
            subject = mTxtProductName.getText() + " 물건이 저장되었습니다.";
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), subject, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MyNotificationManager notificationManager = MyNotificationManager.getInstance();

            NotificationItem notiItem = notificationManager.buildNotificationItem(notifyId, title, subject, NotificationItem.TYPE.DETAIL_ITEM);
            notificationManager.saveNotificationItem(notiItem);
            SharedPreferencesManager.init(getApplicationContext());
            SharedPreferencesManager.saveLossDetailItem(mLossDetailItem, Integer.toString(notifyId), LossPoliceDetailItem.KEY_WRITE, bitmap);

            Intent intent = new Intent("com.example.gisulee.lossdog");
            sendBroadcast(intent);
            return null;
        }
    }


    class GetXMLTask extends AsyncTask<Bundle, Integer, LossPoliceDetailItem> {

        private OnTaskWorkStateListener onTaskWorkStateListener;

        public GetXMLTask setOnTaskWorkStateListener(OnTaskWorkStateListener onTaskWorkStateListener) {
            this.onTaskWorkStateListener = onTaskWorkStateListener;
            return this;
        }

        @Override
        protected LossPoliceDetailItem doInBackground(Bundle... bundles) {

            String id = bundles[0].getString(Request.PARAM_ID);
            String sequenceNumber = bundles[0].getString(Request.PARAM_SEQUENCE);
            String queryUrl = "http://apis.data.go.kr/1320000/LosfundInfoInqireService/getLosfundDetailInfo?serviceKey=HUWDE%2FafItJjY%2BE0GvLNrVBH%2FYYibs33wrOk3XtnbqLO7aGXVnhNlaQ1%2FENAgTsobYTI5gmur%2B0tzhJOvPjzAA%3D%3D&ATC_ID=" + id + "&FD_SN=" + sequenceNumber;
            LossPoliceDetailItem item = null;
            try {
                URL url = new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
                InputStream inputStream = url.openStream(); //url위치로 입력스트림 연결

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new InputStreamReader(inputStream, "UTF-8")); //inputstream 으로부터 xml 입력받기3333
                String tag;


                int eventType = xmlPullParser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:

                            tag = xmlPullParser.getName();//테그 이름 얻어오기

                            if (tag.equals("item")) // 첫번째 검색결과
                                item = new LossPoliceDetailItem();
                            else if (tag.equals("atcId")) {
                                item.id = xmlPullParser.nextText();
                                Log.i("TAG", item.id);
                            } else if (tag.equals("depPlace")) {
                                item.depPlace = xmlPullParser.nextText();
                                Log.i("TAG", item.depPlace);
                            } else if (tag.equals("fdFilePathImg")) {
                                item.imageURL = xmlPullParser.nextText();
                                Log.i("TAG", item.depPlace);
                            } else if (tag.equals("fdHor")) {
                                item.pickTime = xmlPullParser.nextText();
                                Log.i("TAG", item.pickTime);
                            } else if (tag.equals("fdPlace")) {
                                item.pickPlace = xmlPullParser.nextText();
                                Log.i("TAG", item.pickPlace);
                            } else if (tag.equals("fdPrdtNm")) {
                                item.productName = xmlPullParser.nextText();
                                Log.i("TAG", item.productName);
                            } else if (tag.equals("fdSn")) {
                                item.sequenceNumber = xmlPullParser.nextText();
                                Log.i("TAG", item.depPlace);
                            } else if (tag.equals("fdYmd")) {
                                item.pickDate = xmlPullParser.nextText();
                                Log.i("TAG", item.pickDate);
                            } else if (tag.equals("prdtClNm")) {
                                item.productCategory = xmlPullParser.nextText();
                                Log.i("TAG", item.productCategory);
                            } else if (tag.equals("tel")) {
                                item.depPlaceTel = xmlPullParser.nextText();
                                Log.i("TAG", item.depPlaceTel);
                            } else if (tag.equals("uniq")) {
                                item.subject = xmlPullParser.nextText();
                                Log.i("TAG", item.subject);
                            }
                            break;

                        case XmlPullParser.TEXT:
                            break;

                        case XmlPullParser.END_TAG:
                            tag = xmlPullParser.getName(); //테그 이름 얻어오기

                    }
                    eventType = xmlPullParser.next();
                }

            } catch (Exception e) {

            }

            return item;
        }

        @Override
        protected void onPostExecute(LossPoliceDetailItem lossDetailItem) {
            super.onPostExecute(lossDetailItem);
            onTaskWorkStateListener.onTaskPost(lossDetailItem);
        }

    }

    abstract class OnTaskWorkStateListener {
        abstract void onTaskPost(LossPoliceDetailItem item);
    }
}