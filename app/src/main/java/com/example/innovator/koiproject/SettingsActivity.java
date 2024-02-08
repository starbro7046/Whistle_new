package com.example.innovator.koiproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class SettingsActivity extends AppCompatActivity {

    private final long FINISH_INTERVAL_TIME = 2000;//뒤로가기 버튼 종료시간
    public long lastTouchedTime=0;
    SharedPreferences settings;//설정 저장소
    SharedPreferences.Editor settingsEditer;//설정 에디터
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        PermissionListener permissionListener=new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };
        TedPermission.with(this)//권한
                .setPermissionListener(permissionListener)
                .setRationaleMessage("다양한 기능을 사용하기 위해 아래의 권한이 필요합니다.\n전화권한 : 긴급전화\n주소록과 SMS권한 : 긴급메세지 이용\n위치권한 :" +
                        " 안전지도 이용\n카메라 권한 : 긴급 플래시 이용")
                .setDeniedMessage("권한을 거부하시면 다양한 기능을 사용하실 수 없습니다.\n[설정] > [권한] 에서 권한을 허용해 주십시오.")
                .setPermissions(Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_CONTACTS,
                        Manifest.permission.SEND_SMS,Manifest.permission.CAMERA,Manifest.permission.SYSTEM_ALERT_WINDOW)
                .check();
        /*
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("안전지도 서비스를 사용하기 위해 위치 권한이 필요합니다.")
                .setDeniedMessage("위치 권한을 거부하시면 안전지도기능을 사용하실 수 없습니다.\n[설정] > [권한] 에서 권한을 허용해 주십시오.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("긴급메세지 기능을 사용하기위해 주소록 권한이 필요합니다.")
                .setDeniedMessage("주소록 권한을 거부하시면 긴급메세지 기능을 사용하실 수 없습니다.\n[설정] > [권한] 에서 권한을 허용해 주십시오.")
                .setPermissions(Manifest.permission.READ_CONTACTS)
                .check();

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("긴급메세지 기능을 사용하기위해 SMS 권한이 필요합니다.")
                .setDeniedMessage("SMS 권한을 거부하시면 긴급메세지기능을 사용하실 수 없습니다.\n[설정] > [권한] 에서 권한을 허용해 주십시오.")
                .setPermissions(Manifest.permission.SEND_SMS)
                .check();
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("긴급플레시 기능을 사용하기위해 카메라 권한이 필요합니다.")
                .setDeniedMessage("카메라 권한을 거부하시면 긴급플레시 기능을 사용하실 수 없습니다.\n[설정] > [권한] 에서 권한을 허용해 주십시오.")
                .setPermissions(Manifest.permission.CAMERA)
                .check();
        */
        String trigger=null;
        String commend=null;
        settings=getSharedPreferences("SETTINGS",Activity.MODE_PRIVATE);//설정불러옴
        settingsEditer=settings.edit();//설정편집
        //settingsEditer.putString("trigger","");//값넣기
        settingsEditer.commit();


        //ui
        Button mainBtn=(Button) findViewById(R.id.mainButton);
        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsEditer=settings.edit();
                settingsEditer.putBoolean("trigger",false);
                settingsEditer.commit();
                Intent settingsIntent=new Intent(SettingsActivity.this,MainActivity.class);
                settingsIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsIntent);
            }
        });
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
}
