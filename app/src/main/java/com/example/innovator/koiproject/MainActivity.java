package com.example.innovator.koiproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class MainActivity extends AppCompatActivity {

    private final long FINISH_INTERVAL_TIME = 2000;//뒤로가기 버튼 종료시간
    public long lastTouchedTime=0;
    SharedPreferences settings;//설정 저장소
    SharedPreferences.Editor settingsEditer;//설정 에디터
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;
    Calendar calendar;
    Context acontext;
    int requestCode=1;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    TimePicker timePicker;
    TextView ser;
    TextView wigg;
    NotificationManager notificationManager;
    final int NOTIFICATION_ID=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);        // ui 설정


        timePicker=(TimePicker)findViewById(R.id.timerset);
        final Intent intent = new Intent(MainActivity.this, AlarmReciever.class);
        acontext=this;
        boolean trigger;
        String commend=null;
        settings=getSharedPreferences("SETTINGS",Activity.MODE_PRIVATE);
        trigger=settings.getBoolean("trigger",true);//트리거
        settingsEditer=settings.edit();

        int hour;
        int min;
        hour=settings.getInt("hour",21);
        min=settings.getInt("min",0);

        calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);
        calendar.set(Calendar.SECOND,0);
        timePicker.setHour(hour);
        timePicker.setMinute(min);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
                calendar.set(Calendar.MINUTE,timePicker.getMinute());
                calendar.set(Calendar.SECOND,0);
                settingsEditer.putInt("hour", timePicker.getHour());
                settingsEditer.putInt("min", timePicker.getMinute());
                settingsEditer.apply();
                //oast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();//메세지 띄우기

            }
        });
        //timePickerDialog=new TimePickerDialog()
        if(trigger)//설정확인
        {
            Intent defaultSettingsIntent=new Intent(MainActivity.this,SettingsActivity.class);
            defaultSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//기록 삭제
            startActivity(defaultSettingsIntent);
        }
        //설정 불러오기-----------------------------------------------------------------------------------------
        /*
        ImageButton menuBtn=(ImageButton) findViewById(R.id.menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(MainActivity.this,MapActivity.class);
                startActivity(settingsIntent);
            }
        });
        */
        alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);


        final Intent emerIntent=new Intent(MainActivity.this,EmerActivity.class);

        ImageButton emerBtn=(ImageButton) findViewById(R.id.emerBtn);
        emerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(emerIntent);
            }
        });
        ImageButton mapBtn=(ImageButton) findViewById(R.id.map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(MainActivity.this,MapActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        ImageButton triggerBtn=(ImageButton) findViewById(R.id.trigger);
        triggerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(MainActivity.this,TriggerActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        ImageButton alarmBtn=(ImageButton) findViewById(R.id.alarm);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent=new Intent(MainActivity.this,AlarmActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
        /*
        Button timerBtn=(Button)findViewById(R.id.timer);
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */
        final Intent intent2=new Intent(MainActivity.this,CheckService.class);
       /* Button stopAlarm=(Button) findViewById(R.id.timerOff) ;
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"알람이 종료되었습니다.",Toast.LENGTH_SHORT).show();
                // 알람매니저 취소
                stopService(intent2);
                //alarmManager.cancel(pendingIntent);
                // 알람취소
            }
        });
        */
        ser=(TextView) findViewById(R.id.ser) ;
        Switch sesw=(Switch)findViewById(R.id.sesw);
        if(isServiceRunningCheck3())
        {
            sesw.toggle();
            ser.setText("제스쳐 인식 실행중");
        }
        sesw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    ser.setText("제스쳐 인식 중지됨");
                    stopService(intent2);
                }else
                {
                    ser.setText("제스쳐 인식 실행중");
                    startService(intent2);
                }
            }
        });
        wigg=(TextView) findViewById(R.id.wigg);
        Switch wgsw=(Switch)findViewById(R.id.wigsw);
        if(isServiceRunningCheck2())
        {
            wgsw.toggle();
            wigg.setText("위젯 실행중");
        }
        wgsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    wigg.setText("위젯 중지됨");
                    stopService(new Intent(MainActivity.this,WigetService.class));
                }else
                {
                    wigg.setText("위젯 실행중");
                    Permission();
                    startService(new Intent(MainActivity.this, WigetService.class));
                }
            }
        });
        final TextView con=(TextView) findViewById(R.id.con);
        Switch switchView=(Switch)findViewById(R.id.switch1);

        final Intent intent4 = new Intent(MainActivity.this.getApplicationContext(),MainActivity.class);
        final PendingIntent pendnoti = PendingIntent.getActivity(MainActivity.this, 0, intent4, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);


        if (isServiceRunningCheck())
        {
            switchView.toggle();
            con.setText("알람 사용중");
        }
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    con.setText("알람 중지됨");
                    //stopService(intent2);
                    pendingIntent=PendingIntent.getBroadcast(
                            MainActivity.this,
                            requestCode,
                            intent,
                            //alarmIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.cancel(pendingIntent);
                    notificationManager.cancel(NOTIFICATION_ID);
                }else
                {
                    con.setText("알람 사용중");
                    Intent alarmIntent=new Intent("ALARM_RECEIVER");

                    pendingIntent=PendingIntent.getBroadcast(
                            MainActivity.this,
                            requestCode,
                            intent,
                            //alarmIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );
                    alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    ); //알림



                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //NotificationChannel notificationChannel=new NotificationChannel("notify","제스쳐 인식",NotificationManager.IMPORTANCE_DEFAULT);
                        //notificationManager.createNotificationChannel(notificationChannel);

                        Context context = MainActivity.this;
                        Notification notify = new Notification.Builder(context)
                                .setChannelId("alarm")
                                .setTicker("알람")
                                .setOngoing(true)
                                .setContentIntent(pendnoti)
                                .setContentTitle("알람이 작동 중입니다")
                                .setContentText("다음 알림은"+timePicker.getHour()+":"+timePicker.getMinute()+"에 작동됩니다")
                                .setSmallIcon(R.drawable.nofi)
                                .setWhen(System.currentTimeMillis())
                                .build();
                        notificationManager.notify(NOTIFICATION_ID, notify);

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationChannel notificationChannel = new NotificationChannel("alarm", "alarm", NotificationManager.IMPORTANCE_DEFAULT);

                        //otificationChannel.setDescription("channel description"); notificationChannel.enableLights(true); notificationChannel.setLightColor(Color.GREEN);
                        //notificationChannel.enableVibration(true); notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200}); notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                        notificationManager.createNotificationChannel(notificationChannel);

                    }else
                    {
                        Context context = MainActivity.this;
                        Notification notify = new Notification.Builder(context)
                                .setTicker("알람")
                                .setOngoing(true)
                                .setContentIntent(pendnoti)
                                .setContentTitle("알람이 작동 중입니다")
                                .setContentText("다음 알림은"+timePicker.getHour()+"시"+timePicker.getMinute()+"분 입니다")
                                .setSmallIcon(R.drawable.nofi)
                                .setWhen(System.currentTimeMillis())
                                .build();
                        notificationManager.notify(NOTIFICATION_ID, notify);

                    }
                    Toast.makeText(getApplicationContext(), "알람이 작동 중입니다.", Toast.LENGTH_SHORT).show();//메세지 띄우기
                    Log.d("aaaaa","alarm stared");
                }
            }
        });
        //Permission();
        // 1. 뷰 생성
        /*
        ImageView floatingView = new ImageView(this);
        floatingView.setImageResource(R.drawable.button_alarm);

        // 2. 윈도우 레이아웃 파라미터 생성 및 설정
        WindowManager.LayoutParams mWindowLp;
        WindowManager mWindowManager;
        mWindowLp = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        mWindowLp.width  = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowLp.setTitle("aabb");

        // 3. 윈도우 생성
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        try
        {
            mWindowManager.addView(floatingView, mWindowLp);
        }catch (WindowManager.BadTokenException e)
        {
            e.printStackTrace();
        }*/
        /*
        Button saveBtn=(Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsEditer.putInt("hour",timePicker.getHour());
                settingsEditer.putInt("min",timePicker.getMinute());
                settingsEditer.commit();
                int hour,min;
                hour=settings.getInt("hour",21);
                min=settings.getInt("min",0);
                calendar.set(Calendar.HOUR_OF_DAY,hour);
                calendar.set(Calendar.MINUTE,min);
                calendar.set(Calendar.SECOND,0);
            }
        });
        */
        /*
        Button wigetBtn=(Button) findViewById(R.id.wigetRun);
        wigetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permission();
                startService(new Intent(MainActivity.this, WigetService.class));
            }
        });
        Button wigetBtnStop=(Button) findViewById(R.id.stopwiget);
        wigetBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this,WigetService.class));
            }
        });
        */
        //ui코드-------------------------------------------------------------------------------------------
    }
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
    public void Permission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M     // M 버전(안드로이드 6.0 마시멜로우 버전) 보다 같거나 큰 API에서만 설정창 이동 가능합니다.,
                && !Settings.canDrawOverlays(this)) {                     //지금 창이 오버레이 설정창이 아니라면 조건 입니다.

            PermissionOverlay();

        } else {
            System.out.println("버전이 낮거나 오버레이설정창이 아니라면");
        }

    }

    @TargetApi(Build.VERSION_CODES.M)  //M 버전 이상 API를 타겟으로,
    public void PermissionOverlay() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {

                // 오버레이 설정창 이동 후 이벤트 처리합니다.

            }
        }
    }
    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if(AlarmReciever.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        return false;
    }

    public boolean isServiceRunningCheck2() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (WigetService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public boolean isServiceRunningCheck3() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (CheckService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
