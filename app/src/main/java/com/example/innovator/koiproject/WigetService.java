package com.example.innovator.koiproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class WigetService extends Service {

    WindowManager wm;
    View mView;
    private float prevX;
    private float prevY;
    WindowManager.LayoutParams params;
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                /*ViewGroup.LayoutParams.MATCH_PARENT*/300,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.RIGHT | Gravity.TOP;
        mView = inflate.inflate(R.layout.wigetlayouts, null);
        //final TextView textView = (TextView) mView.findViewById(R.id.textView);
        final ImageButton bt =  (ImageButton) mView.findViewById(R.id.wigetbtn);
        final Intent emerIntent=new Intent(WigetService.this,EmerActivity.class);
        emerIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);

        //mView.setOnTouchListener(onTouchListener);
        /*
        mView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.d("aaaaa","afdadsfadfsadfs");
                return false;
            }
        });
        */
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bt.setImageResource(R.mipmap.ic_launcher_round);
                startActivity(emerIntent);
            }
        });
        wm.addView(mView, params);
    }
    private void setCoordinateUpdate(float x, float y) {
        if (params != null) {
            params.x += (int) x;
            params.y += (int) y;

            wm.updateViewLayout(mView, params);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wm != null) {
            if(mView != null) {
                wm.removeView(mView);
                mView = null;
            }
            wm = null;
        }
    }
    private View.OnTouchListener onTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            mView.performClick();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // 처음 위치를 기억해둔다.
                    prevX = event.getRawX();
                    prevY = event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float rawX = event.getRawX(); // 절대 X 좌표 값을 가져온다.
                    float rawY = event.getRawY(); // 절대 Y 좌표값을 가져온다.

                    // 이동한 위치에서 처음 위치를 빼서 이동한 거리를 구한다.
                    float x = rawX - prevX;
                    float y = rawY - prevY;

                    setCoordinateUpdate(x, y);

                    prevX = rawX;
                    prevY = rawY;
                    break;
            }
            return false;
        }

    };

}