package com.example.innovator.koiproject;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class CheckService  extends Service implements SensorEventListener {
    private static IntentFilter plugIntentFilter;
    private static IntentFilter screenFilter;
    private static BroadcastReceiver plugStateChangeReceiver;
    private static BroadcastReceiver screenOffReceiver;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    SharedPreferences settings;//설정 저장소
    int FINISH_INTERVAL_TIME=1200;
    long lastTouchedTime;
    boolean isPlugged;
    boolean charge=false;
    AudioManager audio;
    public static int cnt;
    boolean plug,power,shake;
    private static int SHAKE_THRESHOLD = 3000;//강도
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;
    int NOTIFICATION_ID=1;
    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;
    public int limit;
    public int cntv;
    int cnts=0;
    int ofcnts=0;
    NotificationManager notificationManager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("aaaaaaa","bbbbbbbbbbbbbbbbb started b");

        plugStateChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                audio = (AudioManager) getSystemService(CheckService.this.AUDIO_SERVICE);
                isPlugged = (intent.getIntExtra("state", 0) > 0) ? true : false;
                if (isPlugged) {
                    Log.d("aaaaa", "Earphone is plugged");
                    charge=true;
                }else {
                    Log.d("aaaaaa", "Earphone is unPlugged");
                    if (charge) {
                        charge = false;
                        Intent emerIntent = new Intent(CheckService.this, EmerActivity.class);
                        emerIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(emerIntent);
                    }
                }
                }
            };

        screenOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                    Log.d("aaaaa","hahaha");
                    Log.d("aaaaa",Integer.toString(cnt));
                    if(System.currentTimeMillis() < lastTouchedTime + FINISH_INTERVAL_TIME)
                    {
                        cnt++;
                        if(cnt>=limit)
                        {
                            Intent emerIntent = new Intent(CheckService.this, EmerActivity.class);
                            emerIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(emerIntent);
                            cnt=0;
                        }
                    }else {
                        cnt=0;
                    }
                    lastTouchedTime=System.currentTimeMillis();
                }
            }
        };
        if(plug) {
            registerReceiver(plugStateChangeReceiver, plugIntentFilter);
        }
        if(power)
        {
            registerReceiver(screenOffReceiver, screenFilter);
        }
        if(shake){
            if (accelerormeterSensor != null)
                sensorManager.registerListener(this, accelerormeterSensor,
                        SensorManager.SENSOR_DELAY_GAME);

        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        screenFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        settings=getSharedPreferences("SETTINGS",Activity.MODE_PRIVATE);
        plugIntentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        Log.d("aaaaaa","bbbbbbbbbb started a");
        cnt=0;
        power=settings.getBoolean("power",false);
        shake=settings.getBoolean("shake",false);
        plug=settings.getBoolean("plug",false);
        SHAKE_THRESHOLD=(1000+(settings.getInt("pow",3)*200));
        limit=(settings.getInt("poc",3)+3);
        cntv=(settings.getInt("rate",2)+2);


        final Intent intent4 = new Intent(CheckService.this.getApplicationContext(),MainActivity.class);
        PendingIntent pendnoti = PendingIntent.getActivity(CheckService.this, 0, intent4, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //NotificationChannel notificationChannel=new NotificationChannel("notify","제스쳐 인식",NotificationManager.IMPORTANCE_DEFAULT);
            //notificationManager.createNotificationChannel(notificationChannel);

            Context context = CheckService.this;
            Notification notify = new Notification.Builder(context)
                    .setChannelId("zes")
                    .setTicker("제스쳐 작동")
                    .setOngoing(true)
                    .setContentIntent(pendnoti)
                    .setContentTitle("제스쳐 인식이 작동 중입니다")
                    .setContentText("안전하게 보호되고 있습니다")
                    .setSmallIcon(R.drawable.nofi)
                    .setWhen(System.currentTimeMillis())
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notify);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("zes", "zes", NotificationManager.IMPORTANCE_DEFAULT);

            //otificationChannel.setDescription("channel description"); notificationChannel.enableLights(true); notificationChannel.setLightColor(Color.GREEN);
            //notificationChannel.enableVibration(true); notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200}); notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            notificationManager.createNotificationChannel(notificationChannel);

        }else
        {
            Context context = CheckService.this;
            Notification notify = new Notification.Builder(context)
                    .setTicker("제스쳐 작동")
                    .setOngoing(true)
                    .setContentIntent(pendnoti)
                    .setContentTitle("제스쳐 인식이 작동 중입니다")
                    .setContentText("안전하게 보호되고 있습니다")
                    .setSmallIcon(R.drawable.nofi)
                    .setWhen(System.currentTimeMillis())
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notify);

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(plug) {
            unregisterReceiver(plugStateChangeReceiver); //메모리 누수 방지
        }
        if(power) {
            unregisterReceiver(screenOffReceiver);
        }
        notificationManager.cancel(NOTIFICATION_ID);
        //notificationManager.deleteNotificationChannel(this, NotificationManager.Channel.COMMENT);
        Log.d("aaaaaa","bbbbbbbbbbbb deleted");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 200) {

                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    cnts++;
                    if(cnts>=cntv) {
                        Intent emerIntent = new Intent(CheckService.this, EmerActivity.class);
                        emerIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(emerIntent);
                        cnts=0;
                    }
                }else
                {
                    ofcnts++;
                    if(ofcnts>=4)
                    {
                        ofcnts=0;
                        cnts=0;
                    }
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
