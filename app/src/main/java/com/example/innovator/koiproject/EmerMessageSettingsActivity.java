package com.example.innovator.koiproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class EmerMessageSettingsActivity extends AppCompatActivity {

    SharedPreferences message;//메세지저장소
    SharedPreferences.Editor messageEditer;//설정 에디터
    ListView mListView;
    String name,number;
    EditText messageEdit;
    int i;
    int c;
    final String key="name";
    final String key2="tel";
    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emer_message_settings);

        mContext=this;
        message=getSharedPreferences("SETTINGS",Activity.MODE_PRIVATE);
        messageEdit=(EditText) findViewById(R.id.editMessage) ;
        messageEdit.setText(message.getString("message","긴급 문자 발송시 전달됩니다."));

        mListView = (ListView) findViewById(R.id.list2) ;
        dataSetting();

        ImageButton addButton=(ImageButton) findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("aaaaa","실행됨");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                //호출 후, 연락처앱에서 전달되는 결과물을 받기 위해 startActivityForResult로 실행한다.
                startActivityForResult(intent, 0);

            }
        });
        ImageButton backButton=(ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                Intent nextIntent=new Intent(EmerMessageSettingsActivity.this,AlarmActivity.class);
                nextIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(nextIntent);
            }
        });

    }
    public  void saveSettings()
    {
        messageEditer=message.edit();
        if ((messageEdit.getText()!=null))
        {
            messageEditer.putString("message",messageEdit.getText().toString());
        }
        messageEditer.commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
            cursor.moveToFirst();
            name = cursor.getString(0);        //0은 이름을 얻어옵니다.
            number = cursor.getString(1);   //1은 번호를 받아옵니다.
            cursor.close();
            setData(name,number);
            dataSetting();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void setData(String name,String number)
    {
        Log.d("aaaaa","called");

        messageEditer=message.edit();
        for(c=1;c<6;c++)
        {
            if(message.getString((key+Integer.toString(c)),"none")=="none")
            {
                Log.d("aaaaa",(key+Integer.toString(c)));
                Log.d("aaaaa",name);
                messageEditer.putString((key+Integer.toString(c)),name);
                messageEditer.putString((key2+Integer.toString(c)),number);
                break;
            }
        }
        messageEditer.commit();
    }
    public void dataSetting(){

        MyAdapter mMyAdapter = new MyAdapter();
        /*
        message.getString("name1","none");
        message.getString("tel1","none");
        message.getString("name2","none");
        message.getString("tel2","none");
        message.getString("name3","none");
        message.getString("tel3","none");
        message.getString("name4","none");
        message.getString("tel4","none");
        message.getString("name5","none");
        message.getString("tel5","none");
        */
        for (i=1; i<6; i++)
        {
            if(message.getString((key+Integer.toString(i)),"none")!="none")
            {
                mMyAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_assignment_ind_24px)
                        , message.getString((key+Integer.toString(i)),""),message.getString((key2+Integer.toString(i)),""));
            }
        }
        /* 리스트뷰에 어댑터 등록 */
        mListView.setAdapter(mMyAdapter);
    }
}
