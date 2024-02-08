package com.example.innovator.koiproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;


public class EmerFunctionSettingsActivity extends AppCompatActivity {
    SeekBar seekbar;
    SharedPreferences settings;//설정저장소
    SharedPreferences.Editor settingsEditer;//설정 에디터
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emer_function_settings);

        settings=getSharedPreferences("SETTINGS",Activity.MODE_PRIVATE);

        seekbar=(SeekBar) findViewById(R.id.light);
        seekbar.setProgress(settings.getInt("light",4));
        /*
        ImageButton lightButton=(ImageButton) findViewById(R.id.lightButton);
        lightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ImageButton soundButton=(ImageButton) findViewById(R.id.soundButton);
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */
        ImageButton backButton=(ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                Intent nextIntent=new Intent(EmerFunctionSettingsActivity.this,AlarmActivity.class);
                nextIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(nextIntent);
            }
        });
    }
    public void saveSettings()
    {
        settingsEditer=settings.edit();
        settingsEditer.putInt("light",seekbar.getProgress());
        Log.d("aaaaa",Integer.toString(seekbar.getProgress()));
        settingsEditer.commit();
    }
}
