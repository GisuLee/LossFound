package com.example.gisulee.lossdog.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gisulee.lossdog.common.DateCalculator;
import com.example.gisulee.lossdog.R;

import java.util.Date;

public class Backup_DateSelectorActivity extends AppCompatActivity {
    private Button mBtnClose;
    private Button mBtnConfirm;
    private TextView mTxtBeginDate;
    private TextView mTxtEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //다이얼로그 밖의 화면은 흐리게 만들어줌

        Intent intent = getIntent();

        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.date_selector_dialog);
        // Dialog 사이즈 조절 하기
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.dimAmount = 0.5f;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        mBtnClose = (Button) findViewById(R.id.btnClose);
        mBtnConfirm = (Button) findViewById(R.id.btnConfirm);
        mTxtBeginDate = (TextView) findViewById(R.id.txt_begin_date);
        mTxtEndDate = (TextView) findViewById(R.id.txt_end_date);
        mTxtBeginDate.setText(intent.getStringExtra("BeginDate"));
        mTxtEndDate.setText(intent.getStringExtra("EndDate"));

        mTxtBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = DateCalculator.getYearFromString(((TextView)v).getText().toString());
                int mon = DateCalculator.getMonthFromString(((TextView)v).getText().toString());
                int day = DateCalculator.getDayFromString(((TextView)v).getText().toString());
                DatePickerDialog dialog = new DatePickerDialog(v.getContext(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mTxtBeginDate.setText(year + "-" + String.format("%02d",month+1) + "-" + String.format("%02d",dayOfMonth));
                        Date date = new Date();

                    }
                }, year,mon,day);
                dialog.show();
            }
        });

        mTxtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = DateCalculator.getYearFromString(((TextView)v).getText().toString());
                int mon = DateCalculator.getMonthFromString(((TextView)v).getText().toString());
                int day = DateCalculator.getDayFromString(((TextView)v).getText().toString());
                DatePickerDialog dialog = new DatePickerDialog(v.getContext(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mTxtEndDate.setText(year + "-" + String.format("%02d",month+1) + "-" + String.format("%02d",dayOfMonth));
                        Date date = new Date();

                    }
                }, year,mon,day);
                dialog.show();
            }
        });

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("BeginDate", mTxtBeginDate.getText());
                intent.putExtra("EndDate", mTxtEndDate.getText());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }



}
