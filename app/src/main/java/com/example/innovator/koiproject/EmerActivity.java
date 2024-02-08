package com.example.innovator.koiproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;


public class EmerActivity extends AppCompatActivity {
    int i;
    boolean is=true;

    public long lastTouchedTime=0;
    private final long FINISH_INTERVAL_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emer);


        final TextView textView = (TextView) findViewById(R.id.count);
        Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is = false;
                finish();
            }
        });
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("2");
                Handler delayHandler2 = new Handler();
                delayHandler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("1");
                        Handler delayHandler3 = new Handler();
                        delayHandler3.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (is) {
                                    Toast.makeText(getApplicationContext(), " 실행", Toast.LENGTH_SHORT).show();//메세지 띄우기
                                    stopService(new Intent(EmerActivity.this, WigetService.class));
                                    Intent intent = new Intent(EmerActivity.this, EmerAction.class);
                                    intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
                                    Log.d("aaaaa", "eeeeee bb");
                                    startActivity(intent);

                                }
                            }
                        }, 1000);
                    }
                }, 1000);
            }
        }, 1000);

    }
    public void onBackPressed() {//뒤로가기 버튼 눌럿을 시

        if(System.currentTimeMillis() < lastTouchedTime + FINISH_INTERVAL_TIME)
        {
            is=false;
            finish();
        }else {
            Toast.makeText(getApplicationContext(), "뒤로 두번 클릭시 취소됩니다.", Toast.LENGTH_SHORT).show();//메세지 띄우기
        }
        lastTouchedTime=System.currentTimeMillis();
    }
}

