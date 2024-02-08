package com.example.innovator.koiproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {


    final String key="name";
    final String key2="tel";
    SharedPreferences settings;//메세지저장소
    SharedPreferences.Editor settingsEditer;//설정 에디터
    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<MyItem> mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public MyItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.call, parent, false);
        }
        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img) ;
        TextView tv_name = (TextView) convertView.findViewById(R.id.name) ;
        TextView tv_contents = (TextView) convertView.findViewById(R.id.content) ;

        settings=context.getSharedPreferences("SETTINGS",Activity.MODE_PRIVATE);
        settingsEditer=settings.edit();
        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        MyItem myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        iv_img.setImageDrawable(myItem.getIcon());
        tv_name.setText(myItem.getName());
        tv_contents.setText(myItem.getContents());
        ImageButton delete=(ImageButton) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("aaaaa",Integer.toString(position));
                int cnt=0;
                for(int b=1;b<6;b++)
                {
                    if(settings.getString(key+Integer.toString(b),"none")!="none")
                    {
                        cnt++;
                    }
                    if(cnt==position+1)
                    {
                        Log.d("aaaaa",Integer.toString(b));
                        settingsEditer.remove(key+Integer.toString(b));
                        settingsEditer.remove(key2+Integer.toString(b));
                        break;
                    }
                }
                settingsEditer.commit();
                ((EmerMessageSettingsActivity)EmerMessageSettingsActivity.mContext).dataSetting();
            }
        });
        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수 */
    public void addItem(Drawable img, String name, String contents) {

        MyItem mItem = new MyItem();

        /* MyItem에 아이템을 setting한다. */
        mItem.setIcon(img);
        mItem.setName(name);
        mItem.setContents(contents);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);
    }
}