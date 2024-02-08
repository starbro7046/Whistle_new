package com.example.innovator.koiproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EmerAction extends AppCompatActivity {
    final String key = "name";
    final String key2 = "tel";
    MediaPlayer m;
    boolean bool = true;
    boolean isPlugged;
    private Camera camera;
    private Camera.Parameters parameters;
    private AudioManager audioManager;
    long rate;
    boolean end;
    String message;
    SharedPreferences settings;
    AudioManager mAudioManager;
    boolean once=true;
    int fCallV,fMideaV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emer_action);

        Log.d("aaaaaaa","eeeeeeeeeeeeeeeeeee 긴급상황 실행");
        String phoneNumber;
        settings = getSharedPreferences("SETTINGS", Activity.MODE_PRIVATE);
        message = settings.getString("message", "긴급 문자 발송시 전달됩니다.");
        final Context context = this;
        final String tel = "112";//전화
        try {
                startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:"+tel)));
        } catch (Exception e) {
            e.printStackTrace();
        }//메세지
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(tel, null, message, null, null);
        for (int a = 1; a < 6; a++) {
            if (settings.getString(key2 + Integer.toString(a), "none") != "none") {
                phoneNumber = settings.getString(key2 + Integer.toString(a), "");
                try {
                    sms.sendTextMessage(phoneNumber, null, message, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        fMideaV=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//처음 미디어
        fCallV=mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);//처음 통화

        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);//스피커로 모드전환
        //mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),0);//통화 최대
        //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);//미디어 최대
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,15,0);//통화 최대
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);//미디어 최대
        mAudioManager.setSpeakerphoneOn(true);//스피커폰 온

        m = MediaPlayer.create(context, R.raw.siren);//소리재생
        m.start();
        end = true;
        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();

                Log.d("aaaa","audiosssssssss");
                mAudioManager.setMode(AudioManager.MODE_NORMAL);//이어폰 사용시 스피커로 소리 전환
                mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,fCallV,0);//전화 되돌리기
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, fMideaV, 0);//미디어 되돌리기
                Log.d("aaaa","audiosssssssss   call backto "+fCallV);
                Log.d("aaaa","audiosssssssss     mideo backto"+fMideaV);
                mAudioManager.setSpeakerphoneOn(false);
                end = false;
            }
        });//후래쉬
        rate = ((settings.getInt("light", 3) + 1) * 250);
        camera = Camera.open();
        parameters = camera.getParameters();
        on();
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {//종료
            @Override
            public void onClick(View v) {
                bool = false;
                if (end) {
                    Log.d("aaaa","audiossssssss button");
                    m.stop();
                    m.release();
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);//이어폰 사용시 스피커로 소리 전환
                    mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,fCallV,0);//전화 되돌리기
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, fMideaV, 0);//미디어 되돌리기
                    mAudioManager.setSpeakerphoneOn(false);
                }
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                camera.stopPreview();
                camera.release();
                finishAffinity();
            }
        });
        //settings.getString()
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final LocationListener mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d("aaaaa","get");
                if(once) {
                    String phoneNumber2;
                    LatLng aL= new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("aaaaaa",getCurrentAddress(aL));
                    String newMes=getCurrentAddress(aL);
                    SmsManager sms = SmsManager.getDefault();
                    for (int a = 1; a < 6; a++) {
                        if (settings.getString(key2 + Integer.toString(a), "none") != "none") {
                            phoneNumber2 = settings.getString(key2 + Integer.toString(a), "");
                            try {
                                sms.sendTextMessage(phoneNumber2, null, "현재위치:"+newMes, null, null);
                                sms.sendTextMessage(tel, null, "현재위치:"+newMes, null, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                once=false;
                double longitude = location.getLongitude(); //경도
                double latitude = location.getLatitude();   //위도

            }

            public void onProviderDisabled(String provider) {
                // Disabled시

            }

            public void onProviderEnabled(String provider) {
                // Enabled시

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // 변경시

            }

        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
        }
    }
    public void on()
    {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
        camera.startPreview();
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(bool) {
                    off();
                }
            }
        }, rate);
    }
    public void off()
    {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
        camera.startPreview();
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(bool) {
                    on();
                }
            }
        }, rate);
    }
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

}
