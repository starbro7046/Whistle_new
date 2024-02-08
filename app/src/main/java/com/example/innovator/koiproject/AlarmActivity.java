package com.example.innovator.koiproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class AlarmActivity extends AppCompatActivity {

    private final long FINISH_INTERVAL_TIME = 2000;//뒤로가기 버튼 종료시간
    public long lastTouchedTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        ImageButton menuBtn=(ImageButton) findViewById(R.id.menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(AlarmActivity.this,MainActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        ImageButton mapBtn=(ImageButton) findViewById(R.id.map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(AlarmActivity.this,MapActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        ImageButton triggerBtn=(ImageButton) findViewById(R.id.trigger);
        triggerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(AlarmActivity.this,TriggerActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        /*
        ImageButton alarmBtn=(ImageButton) findViewById(R.id.alarm);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(AlarmActivity.this,AlarmActivity.class);
                startActivity(settingsIntent);
            }
        });
        */
        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.emermess);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent=new Intent(AlarmActivity.this,EmerMessageSettingsActivity.class);
                nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(nextIntent);
            }
        });
        LinearLayout linearLayout2=(LinearLayout) findViewById(R.id.emerfunc);
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent=new Intent(AlarmActivity.this,EmerFunctionSettingsActivity.class);
                nextIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(nextIntent);
            }
        });

    }
    @Override
    public void onBackPressed() {//뒤로가기 버튼 눌럿을 시

        if(System.currentTimeMillis() < lastTouchedTime + FINISH_INTERVAL_TIME)
        {
            //super.onBackPressed();//꺼짐
            finishAffinity();//종료코드
        }else {
            Toast.makeText(getApplicationContext(), " '뒤로'두번클릭시 종료됩니다", Toast.LENGTH_SHORT).show();//메세지 띄우기
        }
        lastTouchedTime=System.currentTimeMillis();
    }
}
