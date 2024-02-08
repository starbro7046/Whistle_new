package com.example.innovator.koiproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class TriggerActivity extends AppCompatActivity {

    SharedPreferences settings;//설정 저장소
    SharedPreferences.Editor settingsEditer;//설정 에디터
    SeekBar rateBar, powBar, pocBar;
    boolean power;
    boolean plug;
    boolean shake;
    private final long FINISH_INTERVAL_TIME = 2000;//뒤로가기 버튼 종료시간
    public long lastTouchedTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger);

        settings=getSharedPreferences("SETTINGS",Activity.MODE_PRIVATE);
        settingsEditer=settings.edit();
        //ui
        ImageButton menuBtn=(ImageButton) findViewById(R.id.menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(TriggerActivity.this,MainActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        ImageButton mapBtn=(ImageButton) findViewById(R.id.map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(TriggerActivity.this,MapActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        ImageButton alarmBtn=(ImageButton) findViewById(R.id.alarm);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(TriggerActivity.this,AlarmActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        shake=settings.getBoolean("shake",false);
        power=settings.getBoolean("power",false);
        plug =settings.getBoolean("plug",false);

        rateBar=(SeekBar) findViewById(R.id.rate);
        powBar=(SeekBar) findViewById(R.id.pow);
        pocBar=(SeekBar) findViewById(R.id.poc);
        rateBar.setProgress(settings.getInt("rate",3));
        powBar.setProgress(settings.getInt("pow",3));
        pocBar.setProgress(settings.getInt("poc",2));
        ImageButton saveBtn=(ImageButton) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsEditer.putInt("rate",rateBar.getProgress());
                settingsEditer.putInt("pow",powBar.getProgress());
                settingsEditer.putInt("poc",pocBar.getProgress());
                Toast.makeText(TriggerActivity.this,"저장되었습니다",Toast.LENGTH_SHORT).show();
                settingsEditer.commit();
            }
        });
        final ImageButton shakeBtn=(ImageButton) findViewById(R.id.shake);
        shakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shake)
                {
                    shake=false;
                    shakeBtn.setBackgroundResource(R.drawable.button_unclicked);
                }else
                {
                    shake=true;
                    shakeBtn.setBackgroundResource(R.drawable.button_clicked);
                }
                settingsEditer.putBoolean("shake",shake);
                settingsEditer.commit();
            }
        });
        final ImageButton powerBtn=(ImageButton) findViewById(R.id.power);
        powerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(power)
                {
                    power=false;
                    powerBtn.setBackgroundResource(R.drawable.button_unclicked);
                }else
                {
                    power=true;
                    powerBtn.setBackgroundResource(R.drawable.button_clicked);
                }
                settingsEditer.putBoolean("power",power);
                settingsEditer.commit();
            }
        });
        final ImageButton plugBtn=(ImageButton) findViewById(R.id.plug);
        plugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(plug)
                {
                    plug=false;
                    plugBtn.setBackgroundResource(R.drawable.button_unclicked);
                }else
                {
                    plug=true;
                    plugBtn.setBackgroundResource(R.drawable.button_clicked);
                }
                settingsEditer.putBoolean("plug",plug);
                settingsEditer.commit();
            }
        });
        if(shake)
        {
            shakeBtn.setBackgroundResource(R.drawable.button_clicked);
        }else
        {
            shakeBtn.setBackgroundResource(R.drawable.button_unclicked);
        }
        if(plug)
        {
            plugBtn.setBackgroundResource(R.drawable.button_clicked);
        }else
        {
            plugBtn.setBackgroundResource(R.drawable.button_unclicked);
        }
        if(power)
        {
            powerBtn.setBackgroundResource(R.drawable.button_clicked);
        }else
        {
            powerBtn.setBackgroundResource(R.drawable.button_unclicked);
        }
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
    public void save()
    {
        settingsEditer.putBoolean("shake",shake);
        settingsEditer.putBoolean("power",power);
        settingsEditer.putBoolean("plug",plug);
    }
}
