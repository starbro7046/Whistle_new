package com.example.innovator.koiproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d("aaaaa","alarmrecueved");
        //Toast.makeText(context, "알람.", Toast.LENGTH_SHORT).show();//메세지 띄우기
        Toast.makeText(context,"보호 기능이 작동 중입니다.",Toast.LENGTH_SHORT).show();
        Intent serviceIntent=new Intent(context,CheckService.class);
        Intent wiIntent=new Intent(context,WigetService.class);
        context.startService(wiIntent);
        context.startService(serviceIntent);
    }
}
